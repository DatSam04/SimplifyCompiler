package jvm_bytecode;

import antlr.SimplifyBaseListener;
import antlr.SimplifyParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SimplifyCompiler extends SimplifyBaseListener {
    //Initialize field for generate bytecode using Javassist
    private ClassPool cp = ClassPool.getDefault();
    private CtClass ctClass = cp.makeClass("Main");
    private ClassFile cf = ctClass.getClassFile();
    private ConstPool constPool = cf.getConstPool();
    private MethodInfo mainMethodInfo;
    private Bytecode code = new Bytecode(constPool, 1, 1);

    //Field for managing variable
    private Map<String, Integer> localVarIndex = new HashMap<>();
    private Map<String, String> localVarType = new HashMap<>();
    private Map<String, Integer> localVarStartPc = new HashMap<>();
    private Map<String, Object> varValues = new HashMap<>();
    private int nextLocalIndex = 1;

    //Field for conditional statement flow control
    private boolean canEmit = true;
    private boolean skipBlock = false;
    private boolean hasCode = false;
    private Stack<Integer> condPosition = new Stack<>();
    private final Stack<Integer> endJumpOffsets = new Stack<>();

    //Field for loop statement flow control
    Stack<Integer> loopStartOffsets = new Stack<>();
    Stack<Integer> loopExitJumps = new Stack<>();

    //Main method to generate the java class file
    public void generateClass(){
        try{
            if(code.getSize() == 0){
                code = defaultProgram();
            }else{
                code.addOpcode(Bytecode.RETURN);
                code.setMaxStack(10);
                code.setMaxLocals(nextLocalIndex);
            }

            //Create the header of the main method and add it to class
            MethodInfo mainMethod = new MethodInfo(constPool, "main", "([Ljava/lang/String;)V");
            mainMethod.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC);
            cf.addMethod(mainMethod);

            CodeAttribute codeAttr = code.toCodeAttribute();
            codeAttr.getAttributes().add(createLocalVarTableAttribute(codeAttr));

            mainMethod.setCodeAttribute(codeAttr);

            ctClass.writeFile("out/jvm_output/");
            System.out.println("Generated Main.class to disk successfully.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Generate the localVarAttributeTable
    private LocalVariableAttribute createLocalVarTableAttribute(CodeAttribute codeAttr) {
        LocalVariableAttribute table = new LocalVariableAttribute(constPool);
        table.addEntry(0, code.length(),
                constPool.addUtf8Info("args"),
                constPool.addUtf8Info("[Ljava/lang/String;"),
                0);

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(localVarIndex.entrySet());
        sortedEntries.sort(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String name = entry.getKey();
            int index = entry.getValue();
            String descriptor = getDescriptor(localVarType.get(name));
            int startPc = localVarStartPc.getOrDefault(name, 0);

            table.addEntry(startPc, code.length() - startPc,
                    constPool.addUtf8Info(name),
                    constPool.addUtf8Info(descriptor),
                    index);
        }
        return table;
    }

    //Specify the data type when converting and store in localVarAttrTable for reference in bytecode
    private String getDescriptor(String simplifyType){
        return switch(simplifyType){
            case "num" -> "I";
            case "dec" -> "D";
            case "bool" -> "Z";
            case "str" -> "Ljava/lang/String;";
            default -> "Ljava/lang/Object;";
        };
    }

    @Override
    public void enterDeclaration(SimplifyParser.DeclarationContext ctx) {
        if(!canEmit) return; //Prevent emit bytecode in conditional statement
        String type = ctx.type().getText();
        String id = ctx.ID().getText();
        SimplifyParser.ExpressionContext exprCtx = ctx.expression();
        //Generate expr bytecode if existed
        if(exprCtx != null){
            //Check if the assigned value has same type as variable
            String exprType = getExprType(exprCtx);
            if(!type.equals(exprType)){
                System.err.println("Type mismatch in declaration of '" + id + "': expected " + type + ", found " + exprType);
                return;
            }

            varValues.put(id, evaluateExpr(exprCtx));
            generateExpr(exprCtx);
        }else{
            varValues.put(id, null);
        }
        //Generate the store var in bytecode even with or without expr
        int index = nextLocalIndex;
        localVarType.put(id, type);
        localVarIndex.put(id, index);
        switch(type){
            case "num", "bool" -> code.addIstore(index);
            case "dec" -> code.addDstore(index);
            case "str" -> code.addAstore(index);
            default -> System.err.println("Unsupported type in declaration: " + type);
        }
        if(type.equals("dec")){
            nextLocalIndex += 2;
        }else{
            nextLocalIndex++;
        }
    }

    @Override
    public void enterAssignment(SimplifyParser.AssignmentContext ctx) {
        if(!canEmit) return; //Prevent emit bytecode in conditional statement
        String id = ctx.ID().getText();
        String type = localVarType.get(id);
        SimplifyParser.ExpressionContext exprValue = ctx.expression().size() == 1 ?
                ctx.expression(0) : ctx.expression(1);
        String exprType = getExprType(exprValue);

        if(!type.equals(exprType)){
            System.err.println("Type mismatch in assignment of '" + id + "': expected " + type + ", found " + exprType);
            return;
        }

        //Generate bytecode for expr and store them in the associate var
        int index = localVarIndex.get(id);
        varValues.put(id, evaluateExpr(exprValue));
        generateExpr(exprValue);

        switch(type){
            case "num", "bool" -> code.addIstore(index);
            case "dec" -> code.addDstore(index);
            case "str" -> code.addAstore(index);
            default -> System.err.println("Unsupported type in declaration: " + type);
        }
    }

    @Override
    public void enterIfBlock(SimplifyParser.IfBlockContext ctx) {
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
            condPosition.push(code.currentPc());
            code.addIndex(0);
        }
    }

    private void handleConditionJumpEnd(SimplifyParser.ExpressionContext condExpr) {
        if(condExpr instanceof SimplifyParser.CompOpExprContext
        || condExpr instanceof SimplifyParser.BoolOpExprContext) {
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
        if (ctx instanceof SimplifyParser.IdExprContext) {
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
        //Declare value manually
        String type = "num";
        String id = ctx.ID().getText();
        int varValue = Integer.parseInt(ctx.Number().getText());
        int index;
        if(!localVarIndex.containsKey(id)){
            index = nextLocalIndex++;
            localVarIndex.put(id, index);
            localVarType.put(id, type);
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
        code.add(Bytecode.IF_ICMPGT);
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
        String id = ctx.ID().getText();
        if(!localVarIndex.containsKey(id)){
            System.err.println("Variable not found: " + id);
        }
        String type = localVarType.get(id);
        if(!type.equals("num")){
            System.err.println("Variable used in condition must be 'num', but found: '" + type + "'");
        }
        int index = localVarIndex.get(id);

        //Record the loop starting point
        int loopStart = code.currentPc();
        loopStartOffsets.push(loopStart);
        code.addIload(index);
        generateExpr(ctx.expression());

        //Compare var > expr, exit loop if false
        code.add(Bytecode.IF_ICMPGT);
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
    }

    @Override
    public void enterResult(SimplifyParser.ResultContext ctx) {
        if(!canEmit) return;
        //Add getstatic to use print statement
        code.addGetstatic("java/lang/System", "out", "Ljava/io/PrintStream;");

        //add bytecode base on type
        generateExpr(ctx.expression());

        // Invokevirtual for writing print statement base on expression type
        String exprType = getExprType(ctx.expression());
        switch (exprType) {
            case "num" -> code.addInvokevirtual("java/io/PrintStream", "println", "(I)V");
            case "dec" -> code.addInvokevirtual("java/io/PrintStream", "println", "(D)V");
            case "bool" -> code.addInvokevirtual("java/io/PrintStream", "println", "(Z)V");
            case "str" -> code.addInvokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            default -> System.err.println("Unsupported type for result: " + exprType);
        }
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
            int index = localVarIndex.get(idName);
            String type = localVarType.get(idName);
            switch(type){
                case "num", "bool" -> code.addIload(index);
                case "dec" -> code.addDload(index);
                case "str" -> code.addAload(index);
                default -> System.err.println("Unsupported type for expression: " + type);
            }
        }else if(ctx instanceof SimplifyParser.NumOpExprContext numOpCtx){
            //Check if Expr type is match
            String leftType = getExprType(numOpCtx.expression(0));
            String rightType = getExprType(numOpCtx.expression(1));
            if(!leftType.equals(rightType)){
                System.err.println("Type mismatch numerical operation: " + leftType + " " + numOpCtx.getChild(1).getText() + " " + rightType);
            }

            //Load both expression
            generateExpr(numOpCtx.expression(0));
            generateExpr(numOpCtx.expression(1));

            //Load operator
            String op = numOpCtx.getChild(1).getText();
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
            String op = boolCtx.getChild(1).getText(); // & or |
            switch (op) {
                case "&" -> {
                    //generate code for && conditionally
                    int jumpToFalse1 = 0;
                    int jumpToFalse2 = 0;
                    int jumpToEnd = 0;
                    if(boolCtx.expression(0) instanceof SimplifyParser.IdExprContext){
                        generateExpr(boolCtx.expression(0));
                        code.add(Bytecode.IFEQ);
                        code.addIndex(0);
                        jumpToFalse1 = code.currentPc() - 2;
                    }else{ //If first expr is not var, just write 1 bytecode for result
                        printBoolBytecode(boolCtx);
                        return;
                    }

                    if(boolCtx.expression(1) instanceof SimplifyParser.IdExprContext){
                        generateExpr(boolCtx.expression(1));
                        code.add(Bytecode.IFEQ);
                        code.addIndex(0);
                        jumpToFalse2 = code.currentPc() - 2;
                    }

                    if(boolCtx.expression(0) instanceof SimplifyParser.IdExprContext
                    || boolCtx.expression(1) instanceof SimplifyParser.IdExprContext){
                        code.add(Bytecode.ICONST_1);
                        code.add(Bytecode.GOTO);
                        code.addIndex(0);
                        jumpToEnd = code.currentPc() - 2;
                    }

                    int falsePC = code.currentPc();
                    code.add(Bytecode.ICONST_0);
                    int endPC = code.currentPc();

                    code.write16bit(jumpToFalse1, falsePC - jumpToFalse1 + 1);
                    code.write16bit(jumpToFalse2, falsePC - jumpToFalse2 + 1);
                    code.write16bit(jumpToEnd, endPC - jumpToEnd + 1);
                }
                case "|" -> {
                    //generate code for || conditionally
                    int jumpToTrue1 = 0;
                    int jumpToTrue2 = 0;
                    int jumpToEnd = 0;
                    if(boolCtx.expression(0) instanceof SimplifyParser.IdExprContext){
                        generateExpr(boolCtx.expression(0));
                        code.add(Bytecode.IFNE);
                        code.addIndex(0);
                        jumpToTrue1 = code.currentPc() - 2;
                    }else{ //If first expr is not var, just write 1 bytecode for result
                        printBoolBytecode(boolCtx);
                        return;
                    }
                    if((boolCtx.expression(1) instanceof SimplifyParser.IdExprContext)){
                        generateExpr(boolCtx.expression(1));
                        code.add(Bytecode.IFNE);
                        code.addIndex(0);
                        jumpToTrue2 = code.currentPc() - 2;
                    }

                    if(boolCtx.expression(0) instanceof SimplifyParser.IdExprContext
                    && boolCtx.expression(1) instanceof SimplifyParser.IdExprContext){
                        code.add(Bytecode.ICONST_0);
                        code.add(Bytecode.GOTO);
                        code.addIndex(0);
                        jumpToEnd = code.currentPc() - 2;
                    }

                    int truePC = code.currentPc();
                    if(evaluateExpr(boolCtx.expression(0)).equals("false")
                        && evaluateExpr(boolCtx.expression(1)).equals("false")){
                        code.add(Bytecode.ICONST_0);
                    }else{
                        code.add(Bytecode.ICONST_1);
                    }
                    int endPC = code.currentPc();

                    //Controlled jump
                    if(jumpToTrue1 != 0){
                        code.write16bit(jumpToTrue1, truePC - jumpToTrue1 + 1);
                    }
                    if(jumpToTrue2 != 0){
                        code.write16bit(jumpToTrue2, truePC - jumpToTrue2 + 1);
                    }

                    if(jumpToTrue1 != 0 && jumpToTrue2 != 0){
                        code.write16bit(jumpToEnd, endPC - jumpToEnd + 1);
                    }
                }
                default -> System.err.println("Invalid boolean op: " + op);
            }
        }else if(ctx instanceof SimplifyParser.ReadExprContext readCtx){
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
        }
    }

    //generate 1 byte for result in compute involve '|' operator
    private void printBoolBytecode(SimplifyParser.BoolOpExprContext boolCtx){
        Object leftVal = evaluateExpr(boolCtx.expression(0));
        Object rightVal = evaluateExpr(boolCtx.expression(1));
        if(leftVal instanceof Boolean && rightVal instanceof Boolean){
            boolean leftBool = (Boolean) leftVal;
            boolean rightBool = (Boolean) rightVal;
            code.add(leftBool || rightBool ? Bytecode.ICONST_1 : Bytecode.ICONST_0);
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
            if(localVarIndex.containsKey(idName)){
                return varValues.get(idName);
            }
            return null;
        }else if(ctx instanceof SimplifyParser.NumOpExprContext numOpCtx){
            String leftType = getExprType(numOpCtx.expression(0));
            String rightType = getExprType(numOpCtx.expression(1));
            if(!leftType.equals(rightType)){
                throw new RuntimeException("Type mismatch numerical operation: " + leftType + " " + numOpCtx.getChild(1).getText() + " " + rightType);
            }
            Object leftVal = evaluateExpr(numOpCtx.expression(0));
            Object rightVal = evaluateExpr(numOpCtx.expression(1));
            String op = numOpCtx.getChild(1).getText();
            if(leftVal instanceof Number && rightVal instanceof Number){
                return switch(op){
                    case "+" -> (leftVal instanceof Integer) ? (Integer) leftVal + (Integer) rightVal
                                                             : (Double) leftVal + (Double) rightVal;
                    case "-" -> (leftVal instanceof Integer) ? (Integer) leftVal - (Integer) rightVal
                                                             : (Double) leftVal - (Double) rightVal;
                    case "*" -> (leftVal instanceof Integer) ? (Integer) leftVal * (Integer) rightVal
                                                             : (Double) leftVal * (Double) rightVal;
                    case "/" -> (leftVal instanceof Integer) ? (Integer) leftVal / (Integer) rightVal
                                                             : (Double) leftVal / (Double) rightVal;
                    default -> throw new RuntimeException("Invalid numerical operation: " + op);
                };
            }else{
                throw new RuntimeException("Numerical Operation requires numeric types");
            }
        }else if(ctx instanceof SimplifyParser.CompOpExprContext compOpCtx){
            Object left = evaluateExpr(compOpCtx.expression(0));
            Object right = evaluateExpr(compOpCtx.expression(1));
            String op = compOpCtx.getChild(1).getText();

            if (left == null || right == null) return null;

            // Type check
            if (!left.getClass().equals(right.getClass())) {
                System.err.println("Cannot compare values of different types: " + left + " and " + right);
                return null;
            }

            return switch (op) {
                case "==" -> left.equals(right);
                case "!=" -> !left.equals(right);
                case "<" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l < r;
                    if (left instanceof Double l && right instanceof Double r) yield l < r;
                    throw new RuntimeException("Invalid < comparison on type: " + left.getClass());
                }
                case "<=" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l <= r;
                    if (left instanceof Double l && right instanceof Double r) yield l <= r;
                    throw new RuntimeException("Invalid <= comparison on type: " + left.getClass());
                }
                case ">" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l > r;
                    if (left instanceof Double l && right instanceof Double r) yield l > r;
                    throw new RuntimeException("Invalid > comparison on type: " + left.getClass());
                }
                case ">=" -> {
                    if (left instanceof Integer l && right instanceof Integer r) yield l >= r;
                    if (left instanceof Double l && right instanceof Double r) yield l >= r;
                    throw new RuntimeException("Invalid >= comparison on type: " + left.getClass());
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
                throw new RuntimeException("Invalid comparable expression: " + ctx.getText());
            }
        }else if(ctx instanceof SimplifyParser.ReadExprContext){
            return "";
        }else{
            throw new RuntimeException("Unsupported expression type: " + ctx.getClass());
        }
    }

    private boolean isComparableType(SimplifyParser.ExpressionContext ctx){
        if(ctx instanceof SimplifyParser.IdExprContext){
            String varType = localVarType.get(ctx.getText());
            return varType.equals("num") || varType.equals("dec") || varType.equals("bool") || varType.equals("str");
        }
        return (ctx instanceof SimplifyParser.NumberExprContext
                || ctx instanceof SimplifyParser.DecimalExprContext
                || ctx instanceof SimplifyParser.StringExprContext
                || ctx instanceof SimplifyParser.TrueExprContext
                || ctx instanceof SimplifyParser.FalseExprContext);
    }

    //Return Simplify type of expr
    private String getExprType(SimplifyParser.ExpressionContext ctx){
        if (ctx instanceof SimplifyParser.NumberExprContext) {
            return "num";
        } else if (ctx instanceof SimplifyParser.DecimalExprContext) {
            return "dec";
        } else if (ctx instanceof SimplifyParser.StringExprContext || ctx instanceof SimplifyParser.ReadExprContext) {
            return "str";
        } else if (ctx instanceof SimplifyParser.ArrExprContext) {
            return "arr";
        } else if (ctx instanceof SimplifyParser.DictExprContext) {
            return "dict";
        }else if (ctx instanceof SimplifyParser.TrueExprContext
                || ctx instanceof SimplifyParser.FalseExprContext
                || ctx instanceof SimplifyParser.BoolOpExprContext
                || ctx instanceof SimplifyParser.CompOpExprContext) {
            return "bool";
        } else if (ctx instanceof SimplifyParser.IdExprContext) {
            String varName = ctx.getText();
            String varType = localVarType.get(varName);
            return varType != null ? varType : "undefined";
        } else if(ctx instanceof SimplifyParser.NumOpExprContext){
            SimplifyParser.NumOpExprContext curExpr = (SimplifyParser.NumOpExprContext) ctx;
            String leftType = getExprType(curExpr.expression(0));
            String rightType = getExprType(curExpr.expression(1));
            if(!leftType.equals(rightType)){
                return "Invalid";
            }else{
                return leftType;
            }
        }else {
            return "Invalid";
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
