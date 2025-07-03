package jvm_bytecode;

import antlr.SimplifyBaseListener;
import antlr.SimplifyParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.*;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import symbols.GlobalScope;
import symbols.LocalScope;
import symbols.Scope;
import symbols.Symbol;

import javax.naming.NameNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class SimplifyCompiler extends SimplifyBaseListener {
    //Initialize field for generate bytecode using Javassist
    private ClassPool cp = ClassPool.getDefault();
    private CtClass ctClass = cp.makeClass("Main");
    private ClassFile cf = ctClass.getClassFile();
    private ConstPool constPool = cf.getConstPool();
    private Bytecode code = new Bytecode(constPool, 1, 1);

    //Field for managing variable
    private Map<String, Integer> localVarIndex = new HashMap<>();
    private Map<String, String> localVarType = new HashMap<>();
    private Map<String, Integer> localVarStartPc = new HashMap<>();
    private Map<String, Object> varValues = new HashMap<>();
    private int nextLocalIndex = 1;

    //Field for managing local variable across different scope
    private Map<String, Map<String, Integer>> localVarIndexByScope = new HashMap<>();
    private Map<String, Map<String, String>> localVarTypeByScope = new HashMap<>();
    private Map<String, Map<String, Integer>> localVarStartPcByScope = new HashMap<>();
    private Map<String, Map<String, Object>> varValuesByScope = new HashMap<>();
    private Map<String, Integer> nextLocalIndexByScope = new HashMap<>();

    //Field for conditional statement flow control
    private boolean canEmit = true;
    private boolean skipBlock = false;
    private boolean hasCode = false;
    private Stack<Integer> condPosition = new Stack<>();
    private final Stack<Integer> endJumpOffsets = new Stack<>();

    //Field for loop statement flow control
    Stack<Integer> loopStartOffsets = new Stack<>();
    Stack<Integer> loopExitJumps = new Stack<>();
    private boolean skipLoop = false;

    //Field for generate bytecode base on array type
    String curElementType = "str";
    int curIndex = 0;

    //Field for generate bytecode for dictionary item
    String dictVar = "";
    String ArrayVariable = "";

    //Field for accessing Symbol Table and Scope
    private GlobalScope globalScope;
    private Scope currentScope;

    //Field for interacting with function
    private Bytecode funcCode = null;
    private MethodInfo funcMethod = null;
    private boolean inFunction = false;

    //Manage class and bytecode in function
    private Bytecode clinitCode = new Bytecode(constPool); // for static init
    private Bytecode mainCode = new Bytecode(constPool, 1, 1);
    private final List<MethodInfo> generatedFunctionMethods = new ArrayList<>();

    //Constructor for initialize the scope from symbol table listener
    public SimplifyCompiler(GlobalScope globalScope) {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }

    //Main method to generate the java class file
    public void generateClass(){
        try{
            //Initialize the static bytecode for global field/variable
            if (clinitCode.getSize() > 0) {
                clinitCode.addOpcode(Bytecode.RETURN);
                clinitCode.setMaxStack(2);
                clinitCode.setMaxLocals(0);

                MethodInfo clinitMethod = new MethodInfo(constPool, "<clinit>", "()V");
                clinitMethod.setAccessFlags(AccessFlag.STATIC);

                CodeAttribute initAttr = clinitCode.toCodeAttribute();
                initAttr.getAttributes().add(createLocalVarTableAttribute(initAttr, "<clinit>", "Global Scope"));
                clinitMethod.setCodeAttribute(initAttr);
                cf.addMethod(clinitMethod);
            }

            //Initialize a default program if no code existed or initialize the main method
            if(clinitCode.getSize() == 0 && mainCode.getSize() == 0 && generatedFunctionMethods.size() == 0){
                mainCode = defaultProgram();
            }else{
                mainCode.addOpcode(Bytecode.RETURN);
                mainCode.setMaxStack(10);
                mainCode.setMaxLocals(nextLocalIndex);
            }

            //Create the header of the main method and add it to class
            MethodInfo mainMethod = new MethodInfo(constPool, "main", "([Ljava/lang/String;)V");
            mainMethod.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC);

            CodeAttribute codeAttr = mainCode.toCodeAttribute();
            codeAttr.getAttributes().add(createLocalVarTableAttribute(codeAttr, "main", "main"));
            mainMethod.setCodeAttribute(codeAttr);
            cf.addMethod(mainMethod);

            //add all custom methods to class file
            for (MethodInfo funcMethod : generatedFunctionMethods) {
                cf.addMethod(funcMethod);
            }

            // Use specific API to write class bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            cf.write(dos);
            byte[] classBytes = baos.toByteArray();
            Path outputPath = Paths.get("out/jvm_output/Main.class");
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, classBytes);
            System.out.println("Generated Main.class to disk successfully.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Generate the localVarAttributeTable
    private LocalVariableAttribute createLocalVarTableAttribute(CodeAttribute codeAttr, String methodName, String scopeName) {
        LocalVariableAttribute table = new LocalVariableAttribute(constPool);
        if(methodName.equals("main")){
            code = mainCode;
            table.addEntry(0, code.length(),
                    constPool.addUtf8Info("args"),
                    constPool.addUtf8Info("[Ljava/lang/String;"),
                    0);
        }else if(methodName.equals("<clinit>")){
            code = clinitCode;
        }else{
            code = funcCode;
        }
        Map<String, Integer> scopedVarIndex = new HashMap<>();
        Map<String, String> scopedVarType = new HashMap<>();
        Map<String, Integer> scopedVarStartPC = new HashMap<>();
        if(localVarIndexByScope.get(scopeName) != null){
            scopedVarIndex = localVarIndexByScope.get(scopeName);
            scopedVarType = localVarTypeByScope.get(scopeName);
            scopedVarStartPC = localVarStartPcByScope.get(scopeName);
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scopedVarIndex.entrySet());
        sortedEntries.sort(Comparator.comparingInt(Map.Entry::getValue));
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String name = entry.getKey();
            int index = entry.getValue();

            String descriptor = getDescriptor(scopedVarType.get(name));
            int startPc = Integer.parseInt(scopedVarStartPC.get(name).toString());

            table.addEntry(startPc, code.length() - startPc,
                    constPool.addUtf8Info(name),
                    constPool.addUtf8Info(descriptor),
                    index);
        }
        return table;
    }

    //Specify the data type when converting and store in localVarAttrTable for reference in bytecode
    private String getDescriptor(String simplifyType){
        if (simplifyType.startsWith("arr[")) {
            String elementType = simplifyType.substring(4, simplifyType.length() - 1).trim();
            return "[" + getDescriptor(elementType); // recursive
        }

        // Handle dictionary type
        if (simplifyType.equals("dict")) {
            return "Ljava/util/HashMap;";
        }

        return switch(simplifyType){
            case "num" -> "I";
            case "dec" -> "D";
            case "bool" -> "Z";
            case "str" -> "Ljava/lang/String;";
            default -> "Ljava/lang/Object;";
        };
    }

    //Compile method call expression
    @Override
    public void enterInnerStatement(SimplifyParser.InnerStatementContext ctx) {
        if(ctx.expression() != null){
            SimplifyParser.ExpressionContext curExpr = ctx.expression();
            if(curExpr instanceof SimplifyParser.MethodCallExprContext methodExpr){
                code = getActiveCode(); //ensure the bytecode generate to correct scope
                if(currentScope.getScopeName().equals("Global Scope")){
                    code = mainCode;
                }
                getExprType(methodExpr); //Check if the current type has this built-in method
                evaluateExpr(methodExpr); //Perform action to local variable
                generateExpr(methodExpr); //Generate JVM bytecode
            }
        }
    }

    @Override
    public void enterDeclaration(SimplifyParser.DeclarationContext ctx) {
        code = getActiveCode();
        if(!canEmit) return; //Prevent emit bytecode in conditional statement
        String type = ctx.type().getText();
        String id = ctx.ID().getText();
        curIndex = nextLocalIndex;
        //Check if variable exist
        ArrayList<Object> varInfo = getVariableInfo(currentScope, id);
        if(varInfo != null){
            System.err.println("Variable " + id + " has been declared.");
            return;
        }

        SimplifyParser.ExpressionContext exprCtx = ctx.expression();
        //Generate expr bytecode if existed
        if(exprCtx != null){
            //Compile Array variable
            String exprType = getExprType(exprCtx);
            if(exprType.equals("Invalid")){
                return;
            }
            if(exprCtx instanceof SimplifyParser.EmptyArrExprContext){
                //compare declared variable type with expr type
                if(!CompareType(type, exprType, id)){
                    return;
                }
                curElementType = type.substring(4, type.length() - 1);
                localVarType.put(id, type);
                localVarIndex.put(id, nextLocalIndex++);
                localVarStartPc.put(id, code.currentPc());
                varValues.put(id, new ArrayList<>());
                generateExpr(exprCtx);
                return;
            }else if(exprCtx instanceof SimplifyParser.ArrExprContext arrExprCtx){
                //compare declared variable type with expr type
                if(!CompareType(type, exprType, id)){
                    return;
                }
                //Check if assigned value match with declared element type
                String elemType = type.substring(4, type.length() - 1);
                curElementType = elemType;
                ArrayList<Object> values = new ArrayList<>();
                boolean isValidExpr = true;
                for(SimplifyParser.ExpressionContext element : arrExprCtx.expression()){
                    if(!elemType.equals(getExprType(element))){
                        System.err.println("Semantic Error at line " + ctx.start.getLine() + ":Type mismatch in declaration of '" + id + "' array: expected " + elemType + ", found " + getExprType(element));
                        isValidExpr = false;
                    }
                    values.add(parseExprAsObject(element, elemType));
                }
                if(!isValidExpr){
                    return;
                }
                //Add variable to localVarTable
                localVarType.put(id, type);
                localVarIndex.put(id, nextLocalIndex++);
                localVarStartPc.put(id, code.currentPc());
                varValues.put(id, values);
                ArrayVariable = id;

                //generate Bytecode
                generateExpr(exprCtx);
                return;
            }else if(exprCtx instanceof SimplifyParser.EmptyDictExprContext){
                localVarType.put(id, type);
                localVarIndex.put(id, nextLocalIndex++);
                localVarStartPc.put(id, code.currentPc());
                varValues.put(id, new HashMap<>());
                //Generate bytecode for empty dictionary
                generateExpr(exprCtx);
                return;
            }else if(exprCtx instanceof SimplifyParser.DictExprContext dictExpr){
                if(storeDictValue(dictExpr, id)){
                    localVarType.put(id, type);
                    localVarIndex.put(id, nextLocalIndex++);
                    localVarStartPc.put(id, code.currentPc());
                    dictVar = id;
                    generateExpr(dictExpr);
                }
                return;
            }
            //Check if the assigned value has same type as variable
            if(!CompareType(type, exprType, id)){
                return;
            }
            //Compile constant variable
            Object value = evaluateExpr(exprCtx);
            if(value.equals("Invalid")){
                return;
            }
            varValues.put(id, value);
            generateExpr(exprCtx);
        }else{
            varValues.put(id, null);
        }
        //Generate the store var in bytecode even with or without expr
        int index = nextLocalIndex;
        localVarType.put(id, type);
        localVarIndex.put(id, index);
        localVarStartPc.put(id, code.currentPc());
        if(exprCtx != null){
            if(currentScope.getScopeName().equals("Global Scope")){
                code.addPutstatic("Main", id,getDescriptor(type));
            }else{
                switch(type){
                    case "num", "bool" -> code.addIstore(index);
                    case "dec" -> code.addDstore(index);
                    case "str" -> code.addAstore(index);
                    case "arr[str]", "arr[num]", "arr[dec]", "arr[bool]" -> code.addAstore(index);
                    default -> System.err.println("Unsupported type in declaration: " + type);
                }
            }
        }
        if(type.equals("dec")){
            nextLocalIndex += 2;
        }else{
            nextLocalIndex++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void enterAssignment(SimplifyParser.AssignmentContext ctx) {
        if(!canEmit) return; //Prevent emit bytecode in conditional statement
        code = getActiveCode();
        if(currentScope.getScopeName().equals("Global Scope")){
            code = mainCode;
        }
        String id = ctx.ID().getText();
        if(!isVarExist(id, ctx.start.getLine())){
            return;
        }
        ArrayList<Object> curVar = getVariableInfo(currentScope, id);
        int idIndex = Integer.parseInt(curVar.get(2).toString());
        curIndex = idIndex;
        String type = curVar.get(3).toString();
        SimplifyParser.ExpressionContext exprValue = ctx.expression().size() == 1 ?
                ctx.expression(0) : ctx.expression(1);
        String exprType = getExprType(exprValue);

        //Compile assingment via indexing first
        if(ctx.expression().size() == 2){
            processIndexing(ctx);
            return;
        }

        if(exprValue instanceof SimplifyParser.EmptyArrExprContext){
            //compare declared variable type with expr type
            if(!CompareType(type, exprType, id)){
                return;
            }
            curElementType = type.substring(4, type.length() - 1);
            setVariableValue(currentScope, id, new ArrayList<>());
            generateExpr(exprValue);
            return;
        }else if(exprValue instanceof SimplifyParser.ArrExprContext arrExprCtx){
            //compare declared variable type with expr type
            if(!CompareType(type, exprType, id)){
                return;
            }
            //Check if assigned value match with declared element type
            String elemType = type.substring(4, type.length() - 1);
            curElementType = elemType;
            ArrayList<Object> values = new ArrayList<>();
            boolean isValidExpr = true;
            for(SimplifyParser.ExpressionContext element : arrExprCtx.expression()){
                if(!elemType.equals(getExprType(element))){
                    System.err.println("Type mismatch in assignment of '" + id + "' array: expected " + elemType + ", found " + getExprType(element));
                    isValidExpr = false;
                }
                values.add(parseExprAsObject(element, elemType));
            }
            if(!isValidExpr){
                return;
            }

            //Add variable to localVarTable
            setVariableValue(currentScope, id, values);
            ArrayVariable = id;

            //generate Bytecode
            generateExpr(exprValue);
            return;
        }else if(exprValue instanceof SimplifyParser.EmptyDictExprContext){
            //compare declared variable type with expr type
            if(!CompareType(type, exprType, id)){
                return;
            }
            setVariableValue(currentScope, id, new HashMap<>());
            generateExpr(exprValue);
            return;
        }else if(exprValue instanceof SimplifyParser.DictExprContext dictExpr){
            if(storeDictValue(dictExpr, id)){
                localVarType.put(id, type);
                dictVar = id;
                generateExpr(dictExpr);
            }
            return;
        }

        if(!type.equals(exprType)){
            System.err.println("Type mismatch in assignment of '" + id + "': expected " + type + ", found " + exprType);
            return;
        }

        //Generate bytecode for expr and store them in the associate var
        int index = localVarIndex.get(id);
        Object value = evaluateExpr(exprValue);
        if(value.equals("Invalid")){
            return;
        }
        setVariableValue(currentScope, id, value);
        generateExpr(exprValue);

        switch(type){
            case "num", "bool" -> code.addIstore(index);
            case "dec" -> code.addDstore(index);
            case "str" -> code.addAstore(index);
            default -> System.err.println("Unsupported type in assignment: " + type);
        }
    }

    @Override
    public void enterIfBlock(SimplifyParser.IfBlockContext ctx) {
        //Ensure conditional statement (if, or, and else block will be written to the correct scope)
        code = getActiveCode();
        if(currentScope.getScopeName().equals("Global Scope")){
            code = mainCode;
        }
        canEmit = false; //Prevent emit bytecode inside if first and only turn it on if condition is true
        SimplifyParser.ExpressionContext condExpr = ctx.expression();
        Boolean evalCond = (Boolean) evaluateExpr(condExpr);
        //Bypass printing epxr condition if the expr contains only constant
        if(!containsVariable(condExpr)){
            if(evalCond){
                skipBlock = true;
                canEmit = true;
            }
            return;
        }
        canEmit = true;
        if(evalCond){
            skipBlock = true;
        }
        hasCode = true;
        handleConditionJumpStart(condExpr);
    }
    @Override
    public void exitIfBlock(SimplifyParser.IfBlockContext ctx){
        canEmit = true; //Allow emit again
        handleConditionJumpEnd(ctx.expression());
    }

    @Override
    public void enterOrBlock(SimplifyParser.OrBlockContext ctx) {
        hasCode = false;
        canEmit = false; //Prevent emit bytecode inside if first and only turn it on if condition is true
        if(skipBlock){
            return;
        }

        SimplifyParser.ExpressionContext condExpr = ctx.expression();
        Boolean evalCond = (Boolean) evaluateExpr(condExpr);
        //Bypass printing epxr condition if the expr contains only constant
        if(!containsVariable(condExpr)){
            if(evalCond){
                skipBlock = true;
                canEmit = true;
            }
            return;
        }
        canEmit = true;
        if(evalCond){
            skipBlock = true;
        }
        hasCode = true;
        handleConditionJumpStart(condExpr);
    }

    @Override
    public void exitOrBlock(SimplifyParser.OrBlockContext ctx){
        canEmit = true;
        handleConditionJumpEnd(ctx.expression());
    }

    @Override
    public void enterElseBlock(SimplifyParser.ElseBlockContext ctx) {
        canEmit = false;
        //hasCode determine if statement inside else branch is allowed to emit
        if(!skipBlock || hasCode){
            canEmit = true;
        }
    }

    @Override
    public void exitElseBlock(SimplifyParser.ElseBlockContext ctx){
        canEmit = true;
        skipBlock = false;

        while(!endJumpOffsets.isEmpty()){ //specify the jump pos for goto in other branches
            int endCondStatement = code.currentPc();
            int endJumpPos = endJumpOffsets.pop();
            code.write16bit(endJumpPos, (endCondStatement + 1) - endJumpPos);
        }
    }

    private void handleConditionJumpStart(SimplifyParser.ExpressionContext condExpr){
        //Only generate bytecode for checking condition when it is CompOp or BoolOp expression
        if(condExpr instanceof SimplifyParser.CompOpExprContext
        || condExpr instanceof SimplifyParser.BoolOpExprContext){
            SimplifyParser.CompOpExprContext curExpr = (SimplifyParser.CompOpExprContext) condExpr;
            generateExpr(curExpr.expression(0));
            generateExpr(curExpr.expression(1));

            String op = curExpr.getChild(1).getText();
            switch(op){
                case "!=" -> code.add(Bytecode.IF_ICMPEQ);
                case "==" -> code.add(Bytecode.IF_ICMPNE);
                case "<"  -> code.add(Bytecode.IF_ICMPGE);
                case "<=" -> code.add(Bytecode.IF_ICMPGT);
                case ">"  -> code.add(Bytecode.IF_ICMPLE);
                case ">=" -> code.add(Bytecode.IF_ICMPLT);
                default   -> System.err.println("Unsupported comparison operation: " + op);
            }
        }else if(condExpr instanceof SimplifyParser.IndexAccessExprContext indexCtx){
            generateExpr(indexCtx);
            code.add(Bytecode.IFEQ);
        }
        condPosition.push(code.currentPc());
        code.addIndex(0);
    }

    private void handleConditionJumpEnd(SimplifyParser.ExpressionContext condExpr) {
        if(condExpr instanceof SimplifyParser.CompOpExprContext
        || condExpr instanceof SimplifyParser.BoolOpExprContext
        ||  condExpr instanceof SimplifyParser.IndexAccessExprContext) {
            code.add(Bytecode.GOTO);
            int endJumpPos = code.currentPc();
            code.addIndex(0);
            endJumpOffsets.push(endJumpPos);

            // Patch false jump to here if generated compare operator in condition header
            if (!condPosition.isEmpty()) {
                int prevCondPos = condPosition.pop();
                code.write16bit(prevCondPos, (endJumpPos + 3) - prevCondPos); // patch operand of jump
            }
        }
    }
    //Helper method for verify if condition contains ID
    private boolean containsVariable(SimplifyParser.ExpressionContext ctx) {
        if (ctx instanceof SimplifyParser.IdExprContext
        || ctx instanceof SimplifyParser.IndexAccessExprContext
        || ctx instanceof SimplifyParser.MethodCallExprContext) {
            return true;
        }

        // For binary/unary expressions, recurse into children
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SimplifyParser.ExpressionContext exprChild) {
                if (containsVariable(exprChild)) return true;
            }
        }
        return false;
    }

    @Override
    public void enterForLoop(SimplifyParser.ForLoopContext ctx) {
        if(currentScope.getScopeName().equals("Global Scope")){
            code = mainCode;
        }else{
            code = getActiveCode();
        }
        //Declare value manually
        String type = "num";
        String id = ctx.ID().getText();
        int varValue = Integer.parseInt(ctx.Number().getText());
        int index;
        //Initialize a new variable for tracking loop
        if(!localVarIndex.containsKey(id)){
            index = nextLocalIndex++;
            localVarIndex.put(id, index);
            localVarType.put(id, type);
            localVarStartPc.put(id, code.currentPc());
            varValues.put(id, varValue);
        }else{
            index = localVarIndex.get(id);
            varValue = (int) varValues.get(id);
        }

        code.addIconst(varValue);
        code.addIstore(index);

        // Label loop start
        int loopStart = code.currentPc();
        loopStartOffsets.push(loopStart);

        // Load loop variable
        code.addIload(index);
        generateExpr(ctx.expression());

        // Compare: if loopVar > endExpr, exit loop
        code.add(Bytecode.IF_ICMPGE);
        int exitJumpPos = code.currentPc();
        code.addIndex(0);
        loopExitJumps.push(exitJumpPos);
    }

    @Override
    public void exitForLoop(SimplifyParser.ForLoopContext ctx) {
        String varName = ctx.ID().getText();
        int varIndex = localVarIndex.get(varName);
        int varValue = (int) varValues.get(varName);
        varValue++;

        // Increment loop variable
        varValues.put(varName, varValue);
        code.add(Bytecode.IINC);
        code.add(varIndex);
        code.add(1);

        // Jump back to loop start
        int loopStart = loopStartOffsets.pop();
        code.add(Bytecode.GOTO);
        int jumpBackPos = code.currentPc();
        code.addIndex(loopStart - (jumpBackPos - 1));

        // Patch the exit jump
        int exitJump = loopExitJumps.pop();
        code.write16bit(exitJump, (jumpBackPos + 3) - exitJump );
    }

    @Override
    public void enterWhileLoop(SimplifyParser.WhileLoopContext ctx) {
        if(currentScope.getScopeName().equals("Global Scope")){
            code = mainCode;
        }else{
            code = getActiveCode();
        }
        String id = ctx.ID().getText();
        boolean isValid = true;
        ArrayList<Object> curVar = getVariableInfo(currentScope, id);
        if(curVar == null){
            System.err.println("Variable not found: " + id);
            isValid = false;
        }
        //Check if variable used in condition is num
        String type = curVar.get(3).toString();
        if(!type.equals("num")){
            System.err.println("Variable used in condition must be 'num', but found: '" + type + "'");
            isValid = false;
        }

        if(!isValid){
            canEmit = false;
            return;
        }

        int index = Integer.parseInt(curVar.get(2).toString());

        //Record the loop starting point
        int loopStart = code.currentPc();
        loopStartOffsets.push(loopStart);
        if(curVar.get(0).equals("Global Scope")){
            code.addGetstatic("Main", id, getDescriptor(curVar.get(3).toString()));
        }else{
            code.addIload(index);
        }
        generateExpr(ctx.expression());

        //Compare var > expr, exit loop if false
        code.add(Bytecode.IF_ICMPGE);
        int exitJumpPos = code.currentPc();
        code.addIndex(0);
        loopExitJumps.push(exitJumpPos);
    }

    @Override
    public void exitWhileLoop(SimplifyParser.WhileLoopContext ctx) {
        // Jump back to loop start
        int loopStart = loopStartOffsets.pop();
        code.add(Bytecode.GOTO);
        int jumpBackPos = code.currentPc();
        code.addIndex(loopStart - (jumpBackPos - 1));

        // Patch the exit jump
        int exitJump = loopExitJumps.pop();
        code.write16bit(exitJump, (jumpBackPos + 3) - exitJump );
        canEmit = true;
    }

    @Override
    public void enterResult(SimplifyParser.ResultContext ctx) {
        if(!canEmit) return;
        if(ctx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
            String id = idExprCtx.getText();
            if(getVariableInfo(currentScope, id) == null){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": variable " + id + " hasn't declared");
                return;
            }
        }
        code = getActiveCode();
        if(currentScope.getScopeName().equals("Global Scope")){
            code = mainCode;
        }

        //Add getstatic to use print statement
        code.addGetstatic("java/lang/System", "out", "Ljava/io/PrintStream;");

        //add bytecode base on type
        generateExpr(ctx.expression());

        // Invokevirtual for writing print statement base on expression type
        String exprType = getExprType(ctx.expression());
        if(ctx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
            exprType = getVariableInfo(currentScope, idExprCtx.getText()).get(3).toString();
        }
        switch (exprType) {
            case "num" -> code.addInvokevirtual("java/io/PrintStream", "println", "(I)V");
            case "dec" -> code.addInvokevirtual("java/io/PrintStream", "println", "(D)V");
            case "bool" -> code.addInvokevirtual("java/io/PrintStream", "println", "(Z)V");
            case "str" -> code.addInvokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            case "arr[str]",
                 "arr[num]",
                 "arr[dec]",
                 "arr[bool]",
                 "dict" -> code.addInvokevirtual("java/io/PrintStream", "println", "(Ljava/lang/Object;)V");
            default -> System.err.println("Unsupported type for result: " + exprType);
        }
    }

    @Override
    public void enterFunction(SimplifyParser.FunctionContext ctx) {
        String id = ctx.ID().getText();
        //Return if function already existed in bytecode
        if (generatedFunctionMethods.stream().anyMatch(m -> m.getName().equals(id))) {
            System.err.println("Semantic Error at line " + ctx.start.getLine() + ": function " + id + " has already existed");
            return;
        }

        Symbol sym = currentScope.resolve(id);
        //Ensure no semantic error on parse tree and function declared successfully
        if(sym != null && sym.getScope() instanceof LocalScope){

            //Change to local scopep
            currentScope = (LocalScope) sym.getScope();
            inFunction = true;

            //Create new bytecode object for function body
            funcCode = new Bytecode(constPool, 2, 3); // adjust stack/locals later
            funcMethod = new MethodInfo(constPool, id, getFunctionDescriptor(ctx));
            funcMethod.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC);

            //Change current local variable table
            ChangeLocalVarTable(currentScope,"Enter");
            if(ctx.argumentList() != null){
                for(SimplifyParser.ArgumentContext argCtx : ctx.argumentList().argument()){
                    String argType = argCtx.type().getText();
                    String argID = argCtx.ID().getText();
                    localVarIndex.put(argID, nextLocalIndex);
                    localVarType.put(argID, argType);
                    localVarStartPc.put(argID, funcCode.currentPc());
                    varValues.put(argID, null);
                    if(argType.equals("dec")){
                        nextLocalIndex += 2;
                    }else{
                        nextLocalIndex++;
                    }
                }
            }
        }
    }

    @Override
    public void exitFunction(SimplifyParser.FunctionContext ctx) {
        //Get the parent scope local table
        ChangeLocalVarTable(currentScope,"Exit");
        SimplifyParser.ReturnStatementContext returnCtx = ctx.returnStatement();
        generateExpr(returnCtx.expression());
        String returnType = ctx.type().getText();
        switch (returnType) {
            case "str":
                funcCode.addOpcode(Bytecode.ARETURN);
                break;
            case "int":
                funcCode.addOpcode(Bytecode.IRETURN);
                break;
            case "double":
                funcCode.addOpcode(Bytecode.DRETURN);
                break;
            case "bool":
                funcCode.addOpcode(Bytecode.IRETURN);
                break;
            default:
                funcCode.addOpcode(Bytecode.RETURN);
                break;
        }

        funcCode.setMaxStack(10);
        funcCode.setMaxLocals(nextLocalIndex);

        // Add function method to class
        CodeAttribute funcAttr = funcCode.toCodeAttribute();
        funcAttr.getAttributes().add(createLocalVarTableAttribute(funcAttr, ctx.ID().getText(), currentScope.getScopeName()));
        funcMethod.setCodeAttribute(funcAttr);
        generatedFunctionMethods.add(funcMethod);
        // Reset state
        inFunction = false;
        funcCode = null;
        funcMethod = null;
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void exitProgram(SimplifyParser.ProgramContext ctx) {
        String scopeName = currentScope.getScopeName();
        localVarIndexByScope.put(scopeName, new HashMap<>(localVarIndex));
        localVarTypeByScope.put(scopeName, new HashMap<>(localVarType));
        localVarStartPcByScope.put(scopeName, new HashMap<>(localVarStartPc));
        varValuesByScope.put(scopeName, new HashMap<>(varValues));
        nextLocalIndexByScope.put(scopeName, nextLocalIndex);
    }

    //Handle generating bytecode for expression
    private void generateExpr(SimplifyParser.ExpressionContext ctx) {
        if(ctx instanceof SimplifyParser.NumberExprContext){
            int num = Integer.parseInt(ctx.getText());
            code.addIconst(num);
        }else if(ctx instanceof SimplifyParser.DecimalExprContext){
            double num = Double.parseDouble(ctx.getText());
            code.addDconst(num);
        }else if(ctx instanceof SimplifyParser.StringExprContext){
            String str = ctx.getText();
            String actualVal = str.substring(1, str.length() - 1);
            code.addLdc(actualVal);
        }else if(ctx instanceof SimplifyParser.TrueExprContext){
            code.addIconst(1);
        }else if(ctx instanceof SimplifyParser.FalseExprContext){
            code.addIconst(0);
        }else if(ctx instanceof SimplifyParser.IdExprContext idCtx){
            String idName = idCtx.getText();
            ArrayList<Object> varInfo = getVariableInfo(currentScope, idName);
            int index = Integer.parseInt(varInfo.get(2).toString());
            String type = varInfo.get(3).toString();
            String curScope = varInfo.get(0).toString();
            if(curScope.equals("Global Scope")){
                code.addGetstatic("Main", idName, getDescriptor(type));
            }else{
                switch(type){
                    case "num", "bool" -> code.addIload(index);
                    case "dec" -> code.addDload(index);
                    case "str", "dict" -> code.addAload(index);
                    case "arr[str]", "arr[num]", "arr[dec]", "arr[bool]" -> code.addAload(index);
                    default -> System.err.println("Unsupported type for expression: " + type);
                }
            }
        }else if(ctx instanceof SimplifyParser.NumOpExprContext numOpCtx){
            //Check if Expr type is match
            String leftType = getExprType(numOpCtx.expression(0));
            String rightType = getExprType(numOpCtx.expression(1));
            if(!leftType.equals(rightType)){
                System.err.println("Type mismatch numerical operation: " + leftType + " " + numOpCtx.getChild(1).getText() + " " + rightType);
                return;
            }

            String op = numOpCtx.getChild(1).getText();
            //Prevent string concatenation with other type
            if(op.equals("+")) {
                if((numOpCtx.expression(0) instanceof SimplifyParser.IdExprContext
                || numOpCtx.expression(1) instanceof SimplifyParser.IdExprContext)
                && (leftType.equals("str") || rightType.equals("str")) ){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Simplify doesn't support concatenation");
                    return;
                }
            }
            //Load both expression
            generateExpr(numOpCtx.expression(0));
            generateExpr(numOpCtx.expression(1));

            //Load operator
            if (leftType.equals("num")) {
                switch (op) {
                    case "+" -> code.add(Bytecode.IADD);
                    case "-" -> code.add(Bytecode.ISUB);
                    case "*" -> code.add(Bytecode.IMUL);
                    case "/" -> code.add(Bytecode.IDIV);
                }
            } else if (leftType.equals("dec")) {
                switch (op) {
                    case "+" -> code.add(Bytecode.DADD);
                    case "-" -> code.add(Bytecode.DSUB);
                    case "*" -> code.add(Bytecode.DMUL);
                    case "/" -> code.add(Bytecode.DDIV);
                }
            }
        }else if(ctx instanceof SimplifyParser.CompOpExprContext compOpCtx){
            SimplifyParser.ExpressionContext leftExpr = compOpCtx.expression(0);
            SimplifyParser.ExpressionContext rightExpr = compOpCtx.expression(1);
            String leftType = getExprType(leftExpr);
            String rightType = getExprType(rightExpr);

            if(!leftType.equals(rightType)){
                System.err.println("Type mismatch comparison operation: " + leftType + " " + compOpCtx.getChild(1).getText() + " " + rightType);
                return;
            }else if(!isComparableType(leftExpr)){
                System.err.println("Comparison Operation only support int, double, str, and bool type: not " + leftType);
                return;
            }
            if(leftExpr instanceof SimplifyParser.IdExprContext){
                generateExpr(leftExpr);
            }
            if(rightExpr instanceof SimplifyParser.IdExprContext){
                generateExpr(rightExpr);
            }
            Object leftVal = evaluateExpr(leftExpr);
            Object rightVal = evaluateExpr(rightExpr);
            String op = compOpCtx.getChild(1).getText();
            boolean result = switch(op){
                case "!=" -> !compareEquals(leftVal, rightVal);
                case "==" -> compareEquals(leftVal, rightVal);
                case "<"  -> compareNumbers(leftVal, rightVal, "<") < 0;
                case "<=" -> compareNumbers(leftVal, rightVal, "<=") <= 0;
                case ">"  -> compareNumbers(leftVal, rightVal, ">") > 0;
                case ">=" -> compareNumbers(leftVal, rightVal, ">=") >= 0;
                default   -> throw new RuntimeException("Invalid comparison operation: " + op);
            };
            //Check if expr is in if block or or block
            ParserRuleContext parent = ctx.getParent();
            while (parent != null) {
                if (parent instanceof SimplifyParser.IfBlockContext || parent instanceof SimplifyParser.OrBlockContext) {
                    if(!(leftExpr instanceof SimplifyParser.IdExprContext)){
                        generateExpr(leftExpr);
                    }else if(!(rightExpr instanceof SimplifyParser.IdExprContext)){
                        generateExpr(rightExpr);
                    }
                    break;
                }
                parent = parent.getParent();
            }
            code.add(result ? Bytecode.ICONST_1 : Bytecode.ICONST_0);
        }else if(ctx instanceof SimplifyParser.BoolOpExprContext boolCtx) {
            printBoolBytecode(boolCtx);
        }else if(ctx instanceof SimplifyParser.ReadExprContext readCtx){
            code = getActiveCode();
            if(currentScope.getScopeName().equals("Global Scope")){
                code = mainCode;
            }
            //Initialize scanner for input
            String scanner = "scanner";
            if(!localVarIndex.containsKey(scanner)){
                code.addNew("java/util/Scanner");
                code.addOpcode(Opcode.DUP);
                code.addGetstatic("java/lang/System", "in", "Ljava/io/InputStream;");
                code.addInvokespecial("java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V");
                code.addAstore(nextLocalIndex);
                localVarIndex.put(scanner, nextLocalIndex++);
                localVarType.put(scanner, "str");
                localVarStartPc.put(scanner, code.currentPc());
                varValues.put(scanner, "");
            }
            if(localVarIndex.containsKey(scanner)){
                //Simplify requires readInput(String), so prompt string before getting user input
                SimplifyParser.ReadInputExprContext readInput = readCtx.readInputExpr();
                String prompt = readInput.String().getText().substring(1, readInput.String().getText().length() - 1);
                code.addGetstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
                code.addLdc(prompt);
                code.addInvokevirtual("java/io/PrintStream", "print", "(Ljava/lang/String;)V");
                code.addAload(localVarIndex.get(scanner));
                code.addInvokevirtual("java/util/Scanner", "nextLine", "()Ljava/lang/String;");
            }
        }else if(ctx instanceof SimplifyParser.EmptyArrExprContext || ctx instanceof SimplifyParser.ArrExprContext){
            generateArray(ctx);
        }else if(ctx instanceof SimplifyParser.EmptyDictExprContext || ctx instanceof SimplifyParser.DictExprContext){
            generateDictionary(ctx);
        }else if(ctx instanceof SimplifyParser.MethodCallExprContext methodCtx){
            generateMethodExpr(methodCtx);
        }else if(ctx instanceof SimplifyParser.IndexAccessExprContext indexCtx){
            generateIndexExpr(indexCtx);
        }
    }

    @SuppressWarnings("unchecked")
    private void generateIndexExpr(SimplifyParser.IndexAccessExprContext indexCtx){
        String id = indexCtx.ID().getText();
        if(!isVarExist(id, indexCtx.start.getLine())){
            return;
        }
        ArrayList<Object> curVar = getVariableInfo(currentScope, id);
        int idIndex = Integer.parseInt(curVar.get(2).toString());
        String type = curVar.get(3).toString();
        String indexType = getExprType(indexCtx.expression());
        if(type.startsWith("arr")){
            ArrayList<Object> curArr = (ArrayList<Object>) curVar.get(4);
            if(!indexType.equals("num")){
                System.err.println("Semantic Error at line " + indexCtx.start.getLine() + ": Wrong index type. Index of array must be 'num', but found " + indexType);
                return;
            }
            int index = 0;
            if(indexCtx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
                ArrayList<Object> value = getVariableInfo(currentScope, idExprCtx.getText());
                index = Integer.parseInt(value.get(4).toString());
            }else{
                index = Integer.parseInt(indexCtx.expression().getText());
            }
            if(index > curArr.size() - 1){
                System.err.println("Runtime Error at line " + indexCtx.start.getLine() + ": can't evaluate Expression. Index " + index + " our of range array '" + id + "'");
                return;
            }

            String elemType = type.substring(4, type.length() - 1);
            if(curVar.get(0).equals("Global Scope")){
                code.addGetstatic("Main", id, getDescriptor(type));
            }else{
                code.addAload(idIndex);
            }
            if(indexCtx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
                ArrayList<Object> value = getVariableInfo(currentScope, idExprCtx.getText());
                if(value.get(0).equals("Global Scope")){
                    code.addGetstatic("Main", idExprCtx.getText(), getDescriptor(value.get(3).toString()));
                }else{
                    int idLocalIndex = Integer.parseInt(value.get(2).toString());
                    code.addIload(idLocalIndex);
                }
            }else{
                code.addIconst(index);
            }
            code.addInvokevirtual("java/util/ArrayList","get","(I)Ljava/lang/Object;");
            switch(elemType){
                case "str":
                    code.addCheckcast("java/lang/String");
                    break;
                case "num":
                    code.addCheckcast("java/lang/Integer");
                    code.addInvokevirtual("java/lang/Integer","intValue","()I");
                    break;
                case "dec":
                    code.addCheckcast("java/lang/Double");
                    code.addInvokevirtual("java/lang/Double","doubleValue","()D");
                    break;
                case "bool":
                    code.addCheckcast("java/lang/Boolean");
                    code.addInvokevirtual("java/lang/Boolean","booleanValue","()Z");
                    break;
            }
        }else if(type.equals("dict")){
            HashMap<String, Object> curDict = (HashMap<String, Object>) curVar.get(4);
            if(!indexType.equals("str")){
                System.err.println("Semantic Error at line " + indexCtx.start.getLine() + ": Wrong index type. Index of dictionary must be 'str', but found " + indexType);
                return;
            }
            String indexString = indexCtx.expression().getText();
            if(indexCtx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
                ArrayList<Object> indexVar = getVariableInfo(currentScope, idExprCtx.getText());
                indexString = indexVar.get(4).toString();
            }
            if(!curDict.containsKey(indexString)){
                System.err.println("Invalid index in line " + indexCtx.start.getLine() + ": " + indexString + " key doesn't exist in '" + id + "'");
                return;
            }
            Object value =  curDict.get(indexString);
            if(curVar.get(0).equals("Global Scope")){
                code.addGetstatic("Main", id, getDescriptor(type));
            }else{
                code.addAload(idIndex);
            }
            code.addLdc(indexString.substring(1,  indexString.length() - 1));
            code.addInvokevirtual("java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
            code.addInvokevirtual("java/lang/Object", "toString", "()Ljava/lang/String;");
            if(value instanceof Integer){
                code.addInvokestatic("java/lang/Integer","parseInt","(Ljava/lang/String;)I");
            }else if(value instanceof Double){
                code.addInvokestatic("java/lang/Double","parseDouble","(Ljava/lang/String;)D");
                code.addInvokestatic("java/lang/Double","valueOf","(D)Ljava/lang/Double;");
            }else if(value instanceof Boolean){
                code.addInvokestatic("java/lang/Boolean","parseBoolean","(Ljava/lang/String;)Z");
                code.addInvokestatic("java/lang/Boolean","valueOf","(Z)Ljava/lang/Boolean;");
            }
        }
    }

    //Generate bytecode for declare an array without values
    private void generateArray(SimplifyParser.ExpressionContext ctx){
        code.addNew("java/util/ArrayList");
        code.addOpcode(Opcode.DUP);
        if(ctx instanceof SimplifyParser.EmptyArrExprContext){
            code.addIconst(0);
            generateArrayType();
        }else{
            SimplifyParser.ArrExprContext arrExpr = (SimplifyParser.ArrExprContext) ctx;
            String elemType = getExprType(arrExpr.expression(0));
            code.addIconst(arrExpr.expression().size());
            generateArrayType();
            int i = 0;
            for(SimplifyParser.ExpressionContext expr : arrExpr.expression()){
                code.addOpcode(Opcode.DUP);
                code.addIconst(i++);
                switch(elemType){
                    case "num":
                        code.addIconst(Integer.parseInt(expr.getText()));
                        code.addInvokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        break;
                    case "dec":
                        code.addLdc2w(Double.parseDouble(expr.getText()));
                        code.addInvokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        break;
                    case "str":
                        String exprStr = expr.getText().substring(1, expr.getText().length() - 1);
                        code.addLdc(exprStr);
                        break;
                    case "bool":
                        int value = 0;
                        if(expr.getText().equals("True")){
                            value = 1;
                        }
                        code.addIconst(value);
                        code.addInvokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                        break;
                    default:
                        System.err.println("Unsupported type for element in array: " + elemType);
                        return;
                }
                code.addOpcode(Opcode.AASTORE);
            }
        }
        code.addInvokestatic("java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;");
        code.addInvokespecial("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        if(currentScope.getScopeName().equals("Global Scope")){
            String type = getVariableInfo(currentScope, ArrayVariable).get(3).toString();
            code.addPutstatic("Main", ArrayVariable, getDescriptor(type));
        }else{
            code.addAstore(curIndex);
        }
    }

    private void generateArrayType(){
        switch(curElementType){
            case "str":
                code.addAnewarray("java/lang/String");
                break;
            case "num":
                code.addAnewarray("java/lang/Integer");
                break;
            case "dec":
                code.addAnewarray("java/lang/Double");
                break;
            case "bool":
                code.addAnewarray("java/lang/Boolean");
                break;
        }
    }

    private void generateDictionary(SimplifyParser.ExpressionContext ctx){
        code.addNew("java/util/HashMap");
        code.addOpcode(Opcode.DUP);
        code.addInvokespecial("java/util/HashMap", "<init>", "()V");
        boolean isGlobal = false;
        if(currentScope.getScopeName().equals("Global Scope")){
            code.addPutstatic("Main", dictVar, getDescriptor("dict"));
            isGlobal = true;
            code = mainCode;
        }else{
            code.addAstore(curIndex);
        }
        if(ctx instanceof SimplifyParser.DictExprContext dictExpr){
            int varIndex = localVarIndex.get(dictVar);
            boolean isKey = true;
            for(SimplifyParser.ExpressionContext expr : dictExpr.expression()){
                if(isKey){
                    isKey = false;
                    if(isGlobal){
                        code.addGetstatic("Main", dictVar, getDescriptor("dict"));
                    }else{
                        code.addAload(varIndex);
                    }
                    code.addLdc(expr.getText().substring(1, expr.getText().length() - 1));
                }else{
                    isKey = true;
                    String exprType = getExprType(expr);
                    switch (exprType){
                        case "str":
                            code.addLdc(expr.getText().substring(1, expr.getText().length() - 1));
                            break;
                        case "num":
                            code.addIconst(Integer.parseInt(expr.getText()));
                            code.addInvokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                            break;
                        case "dec":
                            code.addLdc2w(Double.parseDouble(expr.getText()));
                            code.addInvokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                            break;
                        case "bool":
                            int isTrue = expr.getText().equals("True") ? 1 : 0;
                            code.addIconst(isTrue);
                            code.addInvokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                            break;
                    }
                    code.addInvokevirtual("java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
                    code.addOpcode(Opcode.POP);
                }
            }
        }
    }

    private void generateMethodExpr(SimplifyParser.MethodCallExprContext ctx){
        //Check if method call valid
        if(isMethodCallValid(ctx).size() > 0){
            return;
        }

        String idName = ctx.ID().getText();
        ArrayList<Object> curVar = getVariableInfo(currentScope, idName);
        String idType = curVar.get(3).toString();
        String methodName = ctx.methodName().getText();

        int index = Integer.parseInt(curVar.get(2).toString());
        if(curVar.get(0).equals("Global Scope")){
            code.addGetstatic("Main", idName, getDescriptor(curVar.get(3).toString()));
        }else{
            code.addAload(index);
        }
        if(methodName.equals("length")){ //String method
            code.addInvokevirtual("java/lang/String", "length", "()I");
        }else if(methodName.equals("size") && idType.equals("dict")){
            code.addInvokevirtual("java/util/HashMap", "size", "()I");
        }else if(methodName.equals("key")){
            code.addInvokeinterface("java/util/Map", "keySet", "()Ljava/util/Set", 1);
            code.addInvokespecial("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        }else if(methodName.equals("size") && idType.startsWith("arr")){
            code.addInvokevirtual("java/util/ArrayList", "size", "()I");
        }else if(methodName.equals("addItem")){
            SimplifyParser.AddDictItemExprContext addCtx = (SimplifyParser.AddDictItemExprContext) ctx.expression();
            SimplifyParser.ExpressionContext expr = addCtx.expression();
            String exprType = getExprType(expr);
            String indexStr = addCtx.String().getText();
            code.addLdc(indexStr.substring(1, indexStr.length() - 1));
            switch (exprType){
                case "str":
                    code.addLdc(expr.getText().substring(1, expr.getText().length() - 1));
                    break;
                case "num":
                    code.addIconst(Integer.parseInt(expr.getText()));
                    code.addInvokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                    break;
                case "dec":
                    code.addLdc2w(Double.parseDouble(expr.getText()));
                    code.addInvokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                    break;
                case "bool":
                    int isTrue = expr.getText().equals("True") ? 1 : 0;
                    code.addIconst(isTrue);
                    code.addInvokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                    break;
            }
            code.addInvokevirtual("java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            code.addOpcode(Opcode.POP);
        }else if(methodName.equals("rmItem")){
            String indexStr = ctx.expression().getText();
            code.addLdc(indexStr.substring(1, indexStr.length() - 1));
            code.addInvokevirtual("java/util/HashMap","remove","(Ljava/lang/Object;)Ljava/lang/Object;");
            code.addOpcode(Opcode.POP);
        }else if(methodName.equals("add")){
            if(ctx.expression() instanceof SimplifyParser.IdExprContext iDExpr){
                generateExpr(iDExpr);
            }else {
                String exprType = getExprType(ctx.expression());
                String exprText = ctx.expression().getText();
                switch (exprType) {
                    case "str":
                        code.addLdc(exprText.substring(1, exprText.length() - 1));
                        break;
                    case "num":
                        int number = Integer.parseInt(exprText);
                        code.addIconst(number);
                        code.addInvokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        break;
                    case "dec":
                        double decimal = Double.parseDouble(exprText);
                        code.addLdc2w(decimal);
                        code.addInvokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        break;
                    case "bool":
                        boolean boolValue = Boolean.parseBoolean(ctx.expression().getText());
                        code.addIconst(boolValue ? 1 : 0);
                        code.addInvokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                        break;
                }
            }
            code.addInvokevirtual("java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
            code.addOpcode(Opcode.POP);
        }else if(methodName.equals("rm")){
            if(ctx.expression() instanceof SimplifyParser.IdExprContext iDExpr){
                generateExpr(iDExpr);
            }else{
                int elemIndex = Integer.parseInt((ctx.expression().getText()));
                code.addIconst(elemIndex);
            }
            code.addInvokevirtual("java/util/ArrayList","remove","(I)Ljava/lang/Object;");
            code.addOpcode(Opcode.POP);
        }
    }

    //generate 1 byte for result in compute involve '|' operator
    private void printBoolBytecode(SimplifyParser.BoolOpExprContext boolCtx){
        Object leftVal = evaluateExpr(boolCtx.expression(0));
        Object rightVal = evaluateExpr(boolCtx.expression(1));
        if(leftVal instanceof Boolean && rightVal instanceof Boolean){
            boolean leftBool = (Boolean) leftVal;
            boolean rightBool = (Boolean) rightVal;
            //Print bytecode for left expression
            if(boolCtx.expression(0) instanceof SimplifyParser.IdExprContext idExpr){
                ArrayList<Object> curVar = getVariableInfo(currentScope, idExpr.getText());
                if(curVar.get(0).equals("Global Scope")){
                    code.addGetstatic("Main", idExpr.getText(), getDescriptor(curVar.get(3).toString()));
                }else{
                    int index = Integer.parseInt(curVar.get(2).toString());
                    code.addIload(index);
                }
            }else if(leftBool){
                code.addIconst(1);
            }else{
                code.addIconst(0);
            }

            //Print bytecode for right expression
            if(boolCtx.expression(1) instanceof SimplifyParser.IdExprContext idExpr){
                ArrayList<Object> curVar = getVariableInfo(currentScope, idExpr.getText());
                if(curVar.get(0).equals("Global Scope")){
                    code.addGetstatic("Main", idExpr.getText(), getDescriptor(curVar.get(3).toString()));
                }else{
                    int index = Integer.parseInt(curVar.get(2).toString());
                    code.addIload(index);
                }
            }else if(rightBool){
                code.addIconst(1);
            }else{
                code.addIconst(0);
            }

            //Print booleana operator
            if(boolCtx.boolOp().getText().equals("|")){
                code.addOpcode(Bytecode.IOR);
            }else{
                code.addOpcode(Bytecode.IAND);
            }
        }
    }

    private boolean compareEquals(Object left, Object right){
        if(left instanceof Number && right instanceof Number){
            return ((Number) left).doubleValue() == ((Number) right).doubleValue();
        }
        return left.equals(right);
    }

    private int compareNumbers(Object left, Object right, String op){
        if(!(left instanceof Number) || !(right instanceof Number)){
            throw new RuntimeException("Comparison with '" + op + "' requires numeric types");
        }
        double leftVal = ((Number) left).doubleValue();
        double rightVal = ((Number) right).doubleValue();
        return Double.compare(leftVal, rightVal);
    }

    //Generate actual value of expr not writing bytecode
    @SuppressWarnings("unchecked") //Suppress unchecked warning in copy value from varValues<String,Object>
    private Object evaluateExpr(SimplifyParser.ExpressionContext ctx){
        if(ctx instanceof SimplifyParser.NumberExprContext){
            return Integer.parseInt(ctx.getText());
        }else if(ctx instanceof SimplifyParser.DecimalExprContext){
            return Double.parseDouble(ctx.getText());
        }else if(ctx instanceof SimplifyParser.StringExprContext){
            return ctx.getText().substring(1, ctx.getText().length() - 1);
        }else if(ctx instanceof SimplifyParser.TrueExprContext){
            return true;
        }else if(ctx instanceof SimplifyParser.FalseExprContext){
            return false;
        }else if(ctx instanceof SimplifyParser.IdExprContext){
            String idName = ctx.getText();
            ArrayList<Object> curVar = getVariableInfo(currentScope, idName);
            if(curVar != null){
                return curVar.get(4);
            }
            return "Invalid";
        }else if(ctx instanceof SimplifyParser.NumOpExprContext numOpCtx){
            String leftType = getExprType(numOpCtx.expression(0));
            String rightType = getExprType(numOpCtx.expression(1));
            if(!leftType.equals(rightType)){
                System.err.println("Semantic error at line " + numOpCtx.start.getLine() + ": Type mismatch numerical operation: " + leftType + " " + numOpCtx.getChild(1).getText() + " " + rightType);
                return "Invalid";
            }
            Object leftVal;
            if(numOpCtx.expression(0) instanceof SimplifyParser.IdExprContext iDExpr){
                ArrayList<Object> curVar = getVariableInfo(currentScope, iDExpr.getText());
                leftVal = curVar.get(4);
            }else{
                leftVal = evaluateExpr(numOpCtx.expression(0));
            }

            Object rightVal;
            if(numOpCtx.expression(1) instanceof SimplifyParser.IdExprContext iDExpr){
                ArrayList<Object> curVar = getVariableInfo(currentScope, iDExpr.getText());
                rightVal = curVar.get(4);
            }else{
                rightVal = evaluateExpr(numOpCtx.expression(1));
            }

            String op = numOpCtx.getChild(1).getText();
            if(leftVal instanceof Number && rightVal instanceof Number){
                switch (op){
                    case "+":
                        if(leftType.equals("num")){
                            return(Integer) leftVal + (Integer) rightVal;
                        }else{
                            return(Double) leftVal + (Double) rightVal;
                        }
                    case "-":
                        if(leftType.equals("num")){
                            return (Integer) leftVal - (Integer) rightVal;
                        }else{
                            return (Double) leftVal - (Double) rightVal;
                        }
                    case "*":
                        if(leftType.equals("num")){
                            return (Integer) leftVal * (Integer) rightVal;
                        }else{
                            return (Double) leftVal * (Double) rightVal;
                        }
                    case "/":
                        if(leftType.equals("num")){
                            return (Integer) leftVal / (Integer) rightVal;
                        }else{
                            return (Double) leftVal / (Double) rightVal;
                        }
                    default:
                        System.err.println("Invalid numerical operation: " + op);
                        return("Invalid");
                }
            }else{
                System.err.println("Semantic error at line " + numOpCtx.start.getLine() + ": Numerical Operation requires numeric types");
                return "Invalid";
            }
        }else if(ctx instanceof SimplifyParser.CompOpExprContext compOpCtx){
            Object left = evaluateExpr(compOpCtx.expression(0));
            Object right = evaluateExpr(compOpCtx.expression(1));
            String op = compOpCtx.getChild(1).getText();
            if (left == null || right == null) return null;
            String leftType = getExprType(compOpCtx.expression(0));
            String rightType = getExprType(compOpCtx.expression(1));

            if(leftType.equals("num")){
                left = Integer.parseInt(left.toString());
                right = Integer.parseInt(right.toString());
            }else if(leftType.equals("dec")){
                left = Double.parseDouble(left.toString());
                right = Double.parseDouble(right.toString());
            }
            // Type check
            if (!leftType.equals(rightType)) {
                System.err.println("Semantic error at line " + compOpCtx.start.getLine() + ": Cannot compare values of different types: " + left + " and " + right);
                return "Invalid";
            }

            return switch (op) {
                case "==" -> left.equals(right);
                case "!=" -> !left.equals(right);
                case "<" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l < r;
                    if (left instanceof Double l && right instanceof Double r) yield l < r;
                    System.err.println("Semantic error at line " + compOpCtx.start.getLine() + ": Invalid < comparison on type: " + left.getClass());
                    yield "Invalid";
                }
                case "<=" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l <= r;
                    if (left instanceof Double l && right instanceof Double r) yield l <= r;
                    System.err.println("Semantic error at line " + compOpCtx.start.getLine() + ": Invalid <= comparison on type: " + left.getClass());
                    yield "Invalid";
                }
                case ">" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l > r;
                    if (left instanceof Double l && right instanceof Double r) yield l > r;
                    System.err.println("Semantic error at line " + compOpCtx.start.getLine() + ": Invalid > comparison on type: " + left.getClass());
                    yield "Invalid";
                }
                case ">=" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l >= r;
                    if (left instanceof Double l && right instanceof Double r) yield l >= r;
                    System.err.println("Semantic error at line " + compOpCtx.start.getLine() + ": Invalid >= comparison on type: " + left.getClass());
                    yield "Invalid";
                }
                default -> throw new RuntimeException("Unsupported comparison operator: " + op);
            };
        }else if(ctx instanceof SimplifyParser.BoolOpExprContext boolOpCtx){
            Object left = evaluateExpr(boolOpCtx.expression(0));
            Object right = evaluateExpr(boolOpCtx.expression(1));
            String op = boolOpCtx.getChild(1).getText(); // should be "&" or "|"

            if (left instanceof Boolean && right instanceof Boolean) {
                boolean leftBool = (Boolean) left;
                boolean rightBool = (Boolean) right;

                return switch (op) {
                    case "&" -> leftBool && rightBool;
                    case "|" -> leftBool || rightBool;
                    default  -> throw new RuntimeException("Invalid boolean op: " + op);
                };
            }else{
                System.err.println("Semantic error at line " + boolOpCtx.start.getLine() + ": Invalid comparable expression: " + ctx.getText());
                return "Invalid";
            }
        }else if(ctx instanceof SimplifyParser.ReadExprContext){
            return "";
        }else if(ctx instanceof SimplifyParser.MethodCallExprContext methodCtx){
            //Check if method call valid
            ArrayList<String> errorList = isMethodCallValid(methodCtx);
            if(errorList.size() > 0){
                for(String error : errorList){
                    System.err.println(error);
                }
                return "Invalid";
            }
            String varName = methodCtx.ID().getText();
            ArrayList<Object> curVar = getVariableInfo(currentScope, varName);
            String idType = curVar.get(3).toString();
            String methodName = methodCtx.methodName().getText();

            Object curValue = curVar.get(4);

            if(methodName.equals("length") && idType.equals("str")){
                return curValue.toString().length();
            }else if(methodName.equals("size")){
                //Return array or dict size
                if (idType.startsWith("arr")) {
                    ArrayList<Object> curArr = curValue != null ? (ArrayList<Object>) curValue : new ArrayList<>();
                    return curArr.size();
                } else if (idType.equals("dict")) {
                    HashMap<String, Object> curDict = curValue != null ? (HashMap<String, Object>) curValue : new HashMap<>();
                    return curDict.size();
                }
            }else if(methodName.equals("add")){
                String elemType = idType.substring(4, idType.length() - 1);
                String exprType = getExprType(methodCtx.expression());
                if(!exprType.equals(elemType)){
                    System.err.println("Semantic error at line " + methodCtx.start.getLine() + ": Type mismatch in 'add' method: you can only add '" + elemType + "' to '" + varName + "'");
                    return "Invalid";
                }
                ArrayList<Object> value;
                if(curValue == null){
                    value = new ArrayList<>();
                }else{
                    value = new ArrayList<>((ArrayList<Object>) curValue);
                }
                value.add(parseExprAsObject(methodCtx.expression(), elemType));
                setVariableValue(currentScope, varName, value);
            }else if(methodName.equals("rm")){
                ArrayList<Object> value;
                if(curValue.equals(null)){
                    value = new ArrayList<>();
                }else{
                    value = new ArrayList<>((ArrayList<Object>) curValue);
                }
                int index;
                if(methodCtx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
                    ArrayList<Object> curArrVar = getVariableInfo(currentScope, idExprCtx.getText());
                    Object exprValue = curArrVar.get(4);
                    index = Integer.parseInt(exprValue.toString());
                }else{
                    index = Integer.parseInt(methodCtx.expression().getText());
                }
                if(index >= 0 && index < value.size()){
                    value.remove(index);
                }else{
                    System.err.println("Semantic error at line " + methodCtx.start.getLine() + ": Index " + index + " is out of bounds");
                    return "Invalid";
                }
                setVariableValue(currentScope, varName, value);
            }else if(methodName.equals("key")){
                Map<String, Object> curDict = (Map<String, Object>) curValue;
                ArrayList<String> keys = new ArrayList<>(curDict.keySet());
                return keys;
            }else if(methodName.equals("addItem")){
                if(methodCtx.expression() instanceof SimplifyParser.AddDictItemExprContext addExprCtx){
                    Map<String, Object> curDict = (Map<String, Object>) curValue;
                    String valueType = getExprType(addExprCtx.expression());
                    curDict.put(addExprCtx.String().getText(), parseExprAsObject(addExprCtx.expression(), valueType));
                    setVariableValue(currentScope, varName, curDict);
                }
                return "Invalid";
            }else if(methodName.equals("rmItem")){
                Map<String, Object> curDict = (Map<String, Object>) curValue;
                String indexStr;
                if(methodCtx.expression() instanceof SimplifyParser.IdExprContext idExprContext){
                    ArrayList<Object> curDictVar = getVariableInfo(currentScope, idExprContext.getText());
                    indexStr = curDictVar.get(4).toString();
                }else{
                    indexStr = methodCtx.expression().getText();
                }
                if(!curDict.containsKey(indexStr)){
                    System.err.println("Semantic error at line " + methodCtx.start.getLine() + ": '" + indexStr + "' key does not exist in '" + varName + "'");
                    return "Invalid";
                }
                curDict.remove(indexStr);
                setVariableValue(currentScope, varName, curDict);
            }
            return "Invalid";
        }else if(ctx instanceof SimplifyParser.IndexAccessExprContext indexCtx){
            String id = indexCtx.ID().getText();
            if(!isVarExist(id, indexCtx.start.getLine())){
                return "Invalid";
            }
            ArrayList<Object> curVar = getVariableInfo(currentScope, id);
            String type = curVar.get(3).toString();
            boolean isValid = true;
            if(type.startsWith("arr")){
                String indexType = getExprType(indexCtx.expression());
                ArrayList<Object> curArr = (ArrayList<Object>)  curVar.get(4);
                int index = 0;
                if(!indexType.equals("num")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Wrong index type. Index of array must be 'num', but found " + indexType);
                    isValid = false;
                }else{
                    if(indexCtx.expression() instanceof SimplifyParser.IdExprContext idExprCtx){
                        ArrayList<Object> curVariable = getVariableInfo(currentScope, idExprCtx.getText());
                        Object value = curVariable.get(4);
                        index = Integer.parseInt(value.toString());
                    }else{
                        index = Integer.parseInt(indexCtx.expression().getText());
                    }
                    if(index > curArr.size() - 1){
                        System.err.println("Runtime Error at line " + ctx.start.getLine() + ": can't evaluate Expression. Index " + index + " our of range array '" + id + "'");
                        isValid = false;
                    }
                }

                if(!isValid){
                    return "Invalid";
                }

                Object value = curArr.get(index);
                if(value instanceof String){
                    return value;
                }else if(value instanceof Integer){
                    return Integer.parseInt(value.toString());
                }else if(value instanceof Double){
                    return Double.parseDouble(value.toString());
                }else if(value instanceof Boolean){
                    return Boolean.parseBoolean(value.toString());
                }else{
                    return "Invalid";
                }
            }else if(type.equals("dict")){
                HashMap<String, Object> curDict = (HashMap<String, Object>) curVar.get(4);
                String indexType = getExprType(indexCtx.expression());
                String indexStr = "";
                if(!indexType.equals("str")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Wrong index type. Index of dictionary must be 'str', but found " + indexType);
                    isValid = false;
                }else{
                    indexStr = indexCtx.expression().getText();
                    if(!curDict.containsKey(indexStr)){
                        System.err.println("Runtime Error at line " + ctx.start.getLine() + ": can't evaluate Expression. '" + indexStr + "' key doesn't exist in '" + id + "'");
                        isValid = false;
                    }
                }
                if(!isValid){
                    return "Invalid";
                }
                Object value = curDict.get(indexStr);
                if(value instanceof String){
                    return value;
                }else if(value instanceof Integer){
                    return Integer.parseInt(value.toString());
                }else if(value instanceof Double){
                    return Double.parseDouble(value.toString());
                }else if(value instanceof Boolean){
                    return Boolean.parseBoolean(value.toString());
                }else{
                    return "Invalid";
                }
            }else{
                return "Invalid";
            }
        }else{
            System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Unsupported expression type: " + ctx.getClass());
            return "Invalid";
        }
    }

    //return true if it is constant type
    private boolean isComparableType(SimplifyParser.ExpressionContext ctx){
        if(ctx instanceof SimplifyParser.IdExprContext idExprCtx){
            ArrayList<Object> curVar = getVariableInfo(currentScope, idExprCtx.getText());
            String varType = curVar.get(3).toString();
            return varType.equals("num") || varType.equals("dec") || varType.equals("bool") || varType.equals("str");
        }
        return (ctx instanceof SimplifyParser.NumberExprContext
                || ctx instanceof SimplifyParser.DecimalExprContext
                || ctx instanceof SimplifyParser.StringExprContext
                || ctx instanceof SimplifyParser.TrueExprContext
                || ctx instanceof SimplifyParser.FalseExprContext);
    }

    //Return Simplify type of expr
    @SuppressWarnings("unchecked")
    private String getExprType(SimplifyParser.ExpressionContext ctx){
        if (ctx instanceof SimplifyParser.NumberExprContext) {
            return "num";
        } else if (ctx instanceof SimplifyParser.DecimalExprContext) {
            return "dec";
        } else if (ctx instanceof SimplifyParser.StringExprContext || ctx instanceof SimplifyParser.ReadExprContext) {
            return "str";
        } else if (ctx instanceof SimplifyParser.ArrExprContext || ctx instanceof SimplifyParser.EmptyArrExprContext) {
            return "arr";
        } else if (ctx instanceof SimplifyParser.DictExprContext || ctx instanceof SimplifyParser.EmptyDictExprContext) {
            return "dict";
        }else if (ctx instanceof SimplifyParser.TrueExprContext
                || ctx instanceof SimplifyParser.FalseExprContext
                || ctx instanceof SimplifyParser.BoolOpExprContext
                || ctx instanceof SimplifyParser.CompOpExprContext) {
            return "bool";
        } else if (ctx instanceof SimplifyParser.IdExprContext) {
            String varName = ctx.getText();
            ArrayList<Object> curVar = getVariableInfo(currentScope, varName);
            String varType = curVar.get(3).toString();
            return varType != null ? varType : "undefined";
        } else if(ctx instanceof SimplifyParser.NumOpExprContext numCtx){
            String leftType = getExprType(numCtx.expression(0));
            String rightType = getExprType(numCtx.expression(1));
            if(!leftType.equals(rightType)){
                return "Invalid";
            }else{
                return leftType;
            }
        }else if(ctx instanceof SimplifyParser.MethodCallExprContext methodCtx){
            //Check if method call is valid
            if(isMethodCallValid(methodCtx).size() > 0){
                return "Invalid";
            }
            String varName = methodCtx.ID().getText();
            String method = methodCtx.methodName().getText();
            ArrayList<Object> curVar = getVariableInfo(currentScope, varName);
            String type = curVar.get(3).toString();
            if(method.equals("length")){ //String method
                if(!type.equals("str")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": String Method 'length' cannot be called on variable '" + varName + "' of type " + type);
                    return "Invalid";
                }
                return "num";
            }else if(method.equals("size")){ //process size method for array and dictionary
                if(!type.equals("dict") || !type.startsWith("arr")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Complex type Method '" + method +"' cannot be called on variable '" + varName + "' of type " + type);
                    return "Invalid";
                }
                return "num";
            }else if(method.equals("key")){//process key method for dictionary
                if(!type.equals("dict")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Dictionary Method '" + method +"' cannot be called on variable '" + varName + "' of type " + type);
                    return "Invalid";
                }
                return "arr[str]";
            }else{ //Return invalid for other methods because they didn't return value
                ParserRuleContext parent = methodCtx.getParent();
                if(parent instanceof SimplifyParser.InnerStatementContext){
                    return "Invalid";
                }
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": '" + method + "' method doesn't return a value");
                return "Invalid";
            }
        }else if(ctx instanceof SimplifyParser.IndexAccessExprContext indexCtx){
            String id = indexCtx.ID().getText();
            if(!isVarExist(id, indexCtx.start.getLine())){
                return "Invalid";
            }
            ArrayList<Object> curVar = getVariableInfo(currentScope, id);
            String type =  curVar.get(3).toString();
            if(type.startsWith("arr")){
                String elemType = type.substring(4, type.length() - 1);
                return elemType;
            }else{
                HashMap<String, Object> curDict = (HashMap<String, Object>) curVar.get(4);
                String indexType = getExprType(indexCtx.expression());
                if(!indexType.equals("str")){
                    System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Wrong index type. Index of dictionary must be 'str', but found " + indexType);
                    return "Invalid";
                }
                String indexStr = indexCtx.expression().getText();
                if(!curDict.containsKey(indexStr)){
                    System.err.println("Runtime Error at line " + ctx.start.getLine() + ": can't define type of non-exist key '" + indexStr + "' in dictionary '" + id + "'");
                    return "Invalid";
                }
                //Return value type of specific key
                Object value = curDict.get(indexStr);
                if(value instanceof String){
                    return "str";
                }else if(value instanceof Integer){
                    return "num";
                }else if(value instanceof Double){
                    return "dec";
                }else if(value instanceof Boolean){
                    return "bool";
                }else{
                    return "Invalid";
                }
            }
        }else {
            return "Invalid";
        }
    }

    //Check if variable is declared before
    private boolean isVarExist(String id, int line){
        ArrayList<Object> curVar = getVariableInfo(currentScope, id);
        if(curVar == null){
            System.err.println("Semantic Error at line " + line + ": Undeclared variable '" + id + "' before used");
            return false;
        }
        return true;
    }

    //Check if method call expression has errors
    private ArrayList<String> isMethodCallValid(SimplifyParser.MethodCallExprContext methodCtx){
        ArrayList<String> errorList = new ArrayList<>();
        String idName = methodCtx.ID().getText();
        //Check if variable exist
        if(!isVarExist(idName, methodCtx.start.getLine())){
            return errorList;
        }
        ArrayList<Object> curVar = getVariableInfo(currentScope, idName);
        //Check if var type is either str, dict, or arr
        String idType = curVar.get(3).toString();
        if(!idType.equals("str") && !idType.equals("dict") && !idType.startsWith("arr")){
            errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": '" + idType + "' type does not have built-in method");
        }

        //Check if built-in method exist
        String methodName = methodCtx.methodName().getText();
        List<String> arrMethod = new ArrayList<>(Arrays.asList("add", "rm", "size"));
        List<String> dictMethod = new ArrayList<>(Arrays.asList("addItem", "rmItem", "key", "size"));
        if(!methodName.equals("length") && !arrMethod.contains(methodName) && !dictMethod.contains(methodName)){
            errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Unsupported built-in method '" +  methodName + "'");
        }

        //Check if method call associate with correct type
        if(!idType.equals("str") && methodName.equals("length")){
            errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": String Method 'length' cannot be called on variable '" + idName + "' of type " + idType);
        }else if(methodName.equals("size")){
            if(!idType.equals("dict") && !idType.startsWith("arr")){
                errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": 'size' Method cannot be called on variable '" + idName + "' of type " + idType);
            }
        }else if(!idType.startsWith("arr") && arrMethod.contains(methodName)){
            errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Array Method '" + methodName +"' cannot be called on variable '" + idName + "' of type " + idType);
        }else if(!idType.equals("dict") && dictMethod.contains(methodName)){
            errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Dictionary Method '" + methodName +"' cannot be called on variable '" + idName + "' of type " + idType);
        }else if(idType.equals("dict") && methodName.equals("addItem")){
            if(!(methodCtx.expression() instanceof SimplifyParser.AddDictItemExprContext)){
                errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Dictionary Method '" + methodName +"' expect two arguments (String, Object)");
            }
        }

        //Check if index type is correct for rm value from dict and array
        if(methodCtx.expression() != null && !(methodCtx.expression() instanceof SimplifyParser.AddDictItemExprContext)){
            String exprType = getExprType(methodCtx.expression());
            if(idType.equals("dict") && !exprType.equals("str") && methodName.equals("rmItem")){
                errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Wrong index type. Remove dictionary item required 'str' key, but found " + exprType);
            }else if(idType.startsWith("arr") && !exprType.equals("num") && methodName.equals("rm")){
                errorList.add("Semantic Error at line " + methodCtx.start.getLine() + ": Wrong index type. Index of array must be 'num', but found " + exprType);
            }
        }
        return errorList;
    }

    //Generate bytecode for modify value in arr or dict via indexing
    @SuppressWarnings("unchecked")
    private void processIndexing(SimplifyParser.AssignmentContext ctx){
        String id = ctx.ID().getText();
        ArrayList<Object> curVar = getVariableInfo(currentScope, id);
        int idIndex = Integer.parseInt(curVar.get(2).toString());
        String type = curVar.get(3).toString();
        SimplifyParser.ExpressionContext exprValue = ctx.expression(1);
        String exprType = getExprType(ctx.expression(1));
        boolean isValid = true;
        String indexType = getExprType(ctx.expression(0));
        ArrayList<String> supportType =  new ArrayList<>(Arrays.asList("str", "num", "dec", "bool"));
        if(type.startsWith("arr")){
            ArrayList<Object> values = (ArrayList<Object>) curVar.get(4);
            int index = 0;
            //Check if index is num type
            if(!indexType.equals("num")){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Wrong index type. Index of array must be 'num', but found " + indexType);
                isValid = false;
            }else{
                //Check if index our of range
                if(ctx.expression(0) instanceof  SimplifyParser.IdExprContext IdExprCtx){
                    Object value = varValues.get(ctx.expression(0).getText());
                    index = Integer.parseInt(value.toString());
                }else{
                    index = Integer.parseInt(ctx.expression(0).getText());
                }
                if(index > values.size() - 1){
                    System.err.println("Index Error at line " + ctx.start.getLine() + ". array index out of range");
                    isValid = false;
                }
            }

            //Check if expression is constant type (str, num, dec, bool)
            if(!supportType.contains(exprType)){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Unsupported type in array. Expected 'str', 'num', 'dec', or 'bool', but found " + exprType);
                isValid = false;
            }

            //Check if expression type match with element type
            String elemType = type.substring(4, type.length() - 1);
            if(!elemType.equals(exprType)){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment. Can't assign '" + exprType +"' value to array of type '" + elemType + "'");
                isValid = false;
            }
            //Stop compiling for this line if there is error in either lhs or rhs
            if(!isValid && exprType.equals("Invalid")){
                return;
            }

            //Modify value in local variable
            values.set(index, parseExprAsObject(exprValue, elemType));
            setVariableValue(currentScope, id, values);

            //Generate bytecode
            code.addAload(idIndex);
            if(ctx.expression(0) instanceof  SimplifyParser.IdExprContext){
                int idLocalIndex = localVarIndex.get(ctx.expression(0).getText());
                code.addIload(idLocalIndex);
            }else{
                code.addIconst(index);
            }
            if(ctx.expression(1) instanceof SimplifyParser.IdExprContext){
                int idLocalIndex = localVarIndex.get(ctx.expression(1).getText());
                switch (exprType){
                    case "str"  -> code.addAload(idLocalIndex);
                    case "num", "bool"  -> code.addIload(idLocalIndex);
                    case "dec"  -> code.addDload(idLocalIndex);
                }
            }else{
                switch(exprType){
                    case "str":
                        String value = exprValue.getText().substring(1, exprValue.getText().length() - 1);
                        code.addLdc(value);
                        break;
                    case "num":
                        int num = Integer.parseInt(exprValue.getText());
                        code.addIconst(num);
                        code.addInvokestatic("java/lang/Integer","valueOf","(I)Ljava/lang/Integer;");
                        break;
                    case "dec":
                        double dec = Double.parseDouble(exprValue.getText());
                        code.addLdc2w(dec);
                        code.addInvokestatic("java/lang/Double","valueOf","(D)Ljava/lang/Double;");
                        break;
                    case "bool":
                        int isTrue =  exprValue.getText().equals("True") ? 1 : 0;
                        code.addIconst(isTrue);
                        code.addInvokestatic("java/lang/Boolean","valueOf","(Z)Ljava/lang/Boolean;");
                        break;
                }
            }
            code.addInvokevirtual("java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;");
            code.addOpcode(Opcode.POP);
        }else if(type.equals("dict")){
            HashMap<String, Object> curDict =  (HashMap<String, Object>) varValues.get(id);
            String keyIndex = "";
            //Check if the index of dictionary is string
            if(!indexType.equals("str")){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Wrong index type. Index of dictionary must be 'str', but found " + indexType);
                isValid = false;
            }else{
                //Check if the key existed in dictionary
                if(ctx.expression(0) instanceof SimplifyParser.IdExprContext idExprCtx){
                    Object value = varValues.get(idExprCtx.ID().getText());
                    keyIndex = value.toString();
                }else{
                    keyIndex = ctx.expression(0).getText();
                }
                if(!curDict.containsKey(keyIndex)){
                    System.err.println("Index Error at line " + ctx.start.getLine() + ": " + keyIndex + " key does not exist in '" + id + "'");
                    isValid = false;
                }
            }

            //Check if value expression is constant type (str, num, dec, bool)
            if(!supportType.contains(exprType)){
                System.err.println("Semantic Error at line " + ctx.start.getLine() + ": Unsupported type in dictionary. Expected 'str', 'num', 'dec', or 'bool', but found " + exprType);
                isValid = false;
            }

            //Skip the rest if have error
            if(!isValid && exprType.equals("Invalid")){
                return;
            }

            //Modify values in local Var
            curDict.put(keyIndex, parseExprAsObject(ctx.expression(1), exprType));
            setVariableValue(currentScope, id, curDict);

            //Generate bytecode
            String indexStr = keyIndex.substring(1, ctx.expression(0).getText().length() - 1);
            code.addAload(idIndex);
            if(ctx.expression(0) instanceof SimplifyParser.IdExprContext idExprCtx){
                int idIndexStr = localVarIndex.get(ctx.expression(0).getText());
                code.addAload(idIndexStr);
            }else{
                code.addLdc(indexStr);
            }
            if(ctx.expression(1) instanceof SimplifyParser.IdExprContext){
                int idLocalIndex = localVarIndex.get(ctx.expression(1).getText());
                switch (exprType){
                    case "str"  -> code.addAload(idLocalIndex);
                    case "num", "bool"  -> code.addIload(idLocalIndex);
                    case "dec"  -> code.addDload(idLocalIndex);
                }
            }else{
                switch (exprType){
                    case "str":
                        code.addLdc(exprValue.getText().substring(1, exprValue.getText().length() - 1));
                        break;
                    case "num":
                        code.addIconst(Integer.parseInt(exprValue.getText()));
                        code.addInvokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
                        break;
                    case "dec":
                        code.addLdc2w(Double.parseDouble(exprValue.getText()));
                        code.addInvokestatic("java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
                        break;
                    case "bool":
                        int isTrue = exprValue.getText().equals("True") ? 1 : 0;
                        code.addIconst(isTrue);
                        code.addInvokestatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
                        break;
                }
            }
            code.addInvokevirtual("java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            code.addOpcode(Opcode.POP);
        }else{
            System.err.println("Indexing Error at line " + ctx.start.getLine() + ": Expected 'array' and 'dict' variable but found '" + type + "' variable");
        }
    }

    private boolean CompareType(String varType, String exprType, String varName){
        if(varType.startsWith("arr")) {
            String type = varType.substring(0, 3);
            if(type.equals(exprType)) {
                return true;
            }
        }else if(varType.equals(exprType)){
            return true;
        }
        System.err.println("Type mismatch in declaration of '" + varName + "': expected " + varType + ", found " + exprType);
        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean storeDictValue(SimplifyParser.DictExprContext ctx, String id){
        boolean isKey = true;
        boolean hasError = false;
        int line = ctx.start.getLine();
        String curKey = "";
        HashMap<String, Object> dictItems = new HashMap<>();
        for(SimplifyParser.ExpressionContext curExpr : ctx.expression()){
            String exprType = getExprType(curExpr);
            if(isKey){
                isKey = false;
                if(!exprType.equals("str")){
                    System.err.println("Semantic Error at line " + line + ": Invalid key type in dictionary. Allowed 'str' type only, but found '" + exprType + "'");
                    hasError = true;
                }
                curKey = curExpr.getText();
            }else{
                isKey = true;
                ArrayList<String> validType = new ArrayList<>(Arrays.asList("str", "num", "dec", "bool"));
                if(!validType.contains(exprType)){
                    System.err.println("Semantic Error at line " + line + ": Invalid value type in dictionary. Allowed types are str, num, dec, or bool, but found '" + exprType + "'");
                    hasError = true;
                }
                dictItems.put(curKey, parseExprAsObject(curExpr, exprType));
            }
        }
        if(hasError){
            return false;
        }
        if(getVariableInfo(currentScope, id) != null){
            setVariableValue(currentScope, id, dictItems);
        }else{
            varValues.put(id, dictItems);
        }
        return true;
    }
    //store the actual value of expression to variable values tracking
    @SuppressWarnings("unchecked")
    private Object parseExprAsObject(SimplifyParser.ExpressionContext ctx, String elemType){
        String text = ctx.getText();
        //Modify text if involve ID
        if(ctx instanceof  SimplifyParser.IndexAccessExprContext indexCtx){ //Retrieve value via indexing
            String id = indexCtx.ID().getText();
            ArrayList<Object> curVar = getVariableInfo(currentScope, id);
            String type = curVar.get(3).toString();
            if(type.startsWith("arr")){
                ArrayList<Object> values = (ArrayList<Object>) curVar.get(4);
                int index = Integer.parseInt(indexCtx.expression().getText());
                text = values.get(index).toString();
            }else if(type.equals("dict")){
                HashMap<String, Object> dictItems = (HashMap<String, Object>) curVar.get(4);
                String indexStr = indexCtx.expression().getText();
                text = dictItems.get(indexStr).toString();
            }
        }else if(ctx instanceof SimplifyParser.IdExprContext iDCtx){//Find ID in local var table
            ArrayList<Object> curVar = getVariableInfo(currentScope, iDCtx.getText());
            if(curVar.get(4) == null){
                switch(elemType){
                    case "str" -> text = "";
                    case "num" -> text = "0";
                    case "dec" -> text = "0.0";
                    case "bool" -> text = "True";
                }
            }else{
                text = curVar.get(4).toString();
            }
        }else if(ctx instanceof SimplifyParser.MethodCallExprContext methodCtx){
            text = evaluateExpr(methodCtx.expression()).toString();
            if(text.equals("Invalid")){
                return "Invalid";
            }
        }
        int line = ctx.start.getLine();
        switch(elemType){
            case "str":
                return text;
            case "num":
                return Integer.parseInt(text);
            case "dec":
                return Double.parseDouble(text);
            case "bool":
                return Boolean.parseBoolean(text);
            default:
                System.err.println("Semantic Error at line " + line + ": Unsupported element type in array/dict value: " + elemType);
                return "Invalid";
        }
    }

    //Return array containing [scopeName, idName, idIndex, idType, idValues]
    private ArrayList<Object> getVariableInfo(Scope curScope, String id) {
        ArrayList<Object> varInfo = new ArrayList<>();
        Scope scope = curScope;
        while (scope != null) {
            if(scope == curScope){ //Track current scope
                if(localVarIndex != null && localVarIndex.containsKey(id)){
                    varInfo.add(scope.getScopeName());
                    varInfo.add(id);
                    varInfo.add(localVarIndex.get(id));
                    varInfo.add(localVarType.get(id));
                    varInfo.add(varValues.get(id));
                    return varInfo;
                }
            }else{ //Track all parents scope if exist
                Map<String, Integer> curlocalVarIndex =  localVarIndexByScope.get(scope.getScopeName());
                if(curlocalVarIndex != null && curlocalVarIndex.containsKey(id)) {
                    Map<String, String> curlocalVarType = localVarTypeByScope.get(scope.getScopeName());
                    Map<String, Object> curVarValues =  varValuesByScope.get(scope.getScopeName());
                    varInfo.add(scope.getScopeName());
                    varInfo.add(id);
                    varInfo.add(curlocalVarIndex.get(id));
                    varInfo.add(curlocalVarType.get(id));
                    varInfo.add(curVarValues.get(id));
                    return varInfo;
                }
            }

            // Move to enclosing scope
            if (scope.getEnclosingScope() == null || scope.getScopeName().equals("Global Scope")) {
                break;
            }
            scope = scope.getEnclosingScope();
        }
        return null;
    }

    private void setVariableValue(Scope curScope, String id, Object newValue) {
        Scope scope = curScope;

        while (scope != null) {
            if (scope == curScope) { //Check if variable exist in current scope
                if (localVarIndex != null && localVarIndex.containsKey(id)) {
                    varValues.put(id, newValue);
                    return;
                }
            }else { //Check if variable exist in higher scope
                Map<String, Integer> curlocalVarIndex = localVarIndexByScope.get(scope.getScopeName());
                if (curlocalVarIndex != null && curlocalVarIndex.containsKey(id)) {
                    Map<String, Object> curVarValues = varValuesByScope.get(scope.getScopeName());
                    if (curVarValues != null) {
                        curVarValues.put(id, newValue);
                        return;
                    }
                }
            }

            //Base case to break the loop
            if (scope.getEnclosingScope() == null || scope.getScopeName().equals("Global Scope")) {
                break;
            }
            scope = scope.getEnclosingScope();
        }
    }

    //Return function descriptor of the current function
    private String getFunctionDescriptor(SimplifyParser.FunctionContext ctx) {
        StringBuilder paramTypes = new StringBuilder();

        if (ctx.argumentList() != null) {
            for (SimplifyParser.ArgumentContext arg : ctx.argumentList().argument()) {
                //Adding descriptor for each argument
                paramTypes.append(getDescriptor(arg.type().getText()));
            }
        }

        String returnType = getDescriptor(ctx.type().getText());
        return "(" + paramTypes + ")" + returnType;
    }

    //Modify the current local variable tracking structures when enter and exit function
    private void ChangeLocalVarTable(Scope scope, String status){
        String parentScope = scope.getEnclosingScope().getScopeName();
        if(status.equals("Enter")){
            //Store current local var tracking structure to main stack
            localVarIndexByScope.put(parentScope, new HashMap<>(localVarIndex));
            localVarTypeByScope.put(parentScope, new HashMap<>(localVarType));
            localVarStartPcByScope.put(parentScope, new HashMap<>(localVarStartPc));
            varValuesByScope.put(parentScope, new HashMap<>(varValues));
            nextLocalIndexByScope.put(parentScope, nextLocalIndex);
            //Initialize a new local var tracking structure for the method scope
            localVarIndex = new HashMap<>();
            localVarType = new HashMap<>();
            localVarStartPc = new HashMap<>();
            varValues = new HashMap<>();
            nextLocalIndex = 0;
        }else if(status.equals("Exit")){
            //cope current tracking structure and store to the stack
            String currentScope = scope.getScopeName();
            localVarIndexByScope.put(currentScope, new HashMap<>(localVarIndex));
            localVarTypeByScope.put(currentScope, new HashMap<>(localVarType));
            localVarStartPcByScope.put(currentScope, new HashMap<>(localVarStartPc));
            varValuesByScope.put(currentScope, new HashMap<>(varValues));
            nextLocalIndexByScope.put(currentScope, nextLocalIndex);

            //Copy the parent scope and retrieve it from main stack to prevent pass by reference
            localVarIndex = new HashMap<>(localVarIndexByScope.get(parentScope));
            localVarType = new HashMap<>(localVarTypeByScope.get(parentScope));
            localVarStartPc = new HashMap<>(localVarStartPcByScope.get(parentScope));
            varValues = new HashMap<>(varValuesByScope.get(parentScope));
            nextLocalIndex = nextLocalIndexByScope.get(parentScope);
        }
    }

    //Return the current scope that bytecode should be written to
    private Bytecode getActiveCode() {
        if (currentScope.getScopeName().equals("Global Scope")) {
            return clinitCode;
        } else if (inFunction) {
            return funcCode;
        } else {
            return mainCode;
        }
    }

    //Generate a Main class that print hello world if no parse tree
    public Bytecode defaultProgram(){
        Bytecode bc = new Bytecode(constPool, 2, 1);
        bc.addGetstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
        bc.addLdc("hello world");
        bc.addInvokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        bc.addOpcode(Bytecode.RETURN);
        bc.setMaxStack(2);
        bc.setMaxLocals(1);
        return bc;
    }
}
