package symbols;

import antlr.SimplifyBaseListener;
import antlr.SimplifyParser;
import ErrorListener.SemanticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class SymbolTableListener extends SimplifyBaseListener {
    private Scope currentScope;
    private final GlobalScope globalScope = new GlobalScope();
    private final Set<String> validTypes = new java.util.HashSet<>(Set.of("num", "dec", "str", "bool", "arr", "dict"));
    private Map<String, Map<String, String>> dictTable = new HashMap<>();
    private Map<String, String> funcReturnType = new HashMap<>();

    public SymbolTableListener() {
        this.currentScope = globalScope;
    }

    @Override
    public void enterDeclaration(SimplifyParser.DeclarationContext ctx) {
        int line = ctx.start.getLine();

        // Check for missing identifier
        TerminalNode idNode = ctx.ID();
        String name = (idNode != null) ? idNode.getText() : null;

        String varType = ctx.type().getText();
        // Perform semantic analysis with array variables
        if(varType.startsWith("arr")) {
            String elementType = varType.substring(4, varType.length() - 1);;
            if (!validTypes.contains(elementType)) {
                SemanticErrorListener.report("Semantic Error at line " + line + ": Invalid element type in array declaration: " + elementType);
                return;
            }
            if(!(ctx.expression() == null)){
                if(!(ctx.expression() instanceof SimplifyParser.ArrExprContext)
                && !(ctx.expression() instanceof SimplifyParser.EmptyArrExprContext)
                && !(ctx.expression() instanceof SimplifyParser.MethodCallExprContext)){
                    SemanticErrorListener.report("Semantic Error at line " + line + ": value mismatch type, array variable only accepts array value \"[value]\"");
                    return;
                }else if(ctx.expression() instanceof SimplifyParser.ArrExprContext){
                    SimplifyParser.ArrExprContext elemList = (SimplifyParser.ArrExprContext) ctx.expression();
                    for(SimplifyParser.ExpressionContext elem : elemList.expression()){
                        String curElemType = expressionType(elem);
                        if(!curElemType.equals(elementType)){
                            SemanticErrorListener.report("Semantic Error at line " + line + ": Type mismatch in array declaration: can not initialize " + curElemType + " to a " + varType);
                        }
                    }
                }
            }
        } else if (varType.equals("dict")) { // Perform semantic analysis with dictionary variables
            Map<String, String> dictItems = new HashMap<>();
            String curKey = "";
            if(ctx.expression() != null){
                if(!(ctx.expression() instanceof SimplifyParser.DictExprContext)
                && !(ctx.expression() instanceof SimplifyParser.EmptyDictExprContext)) {
                    SemanticErrorListener.report("Semantic Error at line " + line + ": value mismatch type, dict variable only accepts dict value \"{item}\"");
                    return;
                }else if(ctx.expression() instanceof SimplifyParser.DictExprContext){
                    SimplifyParser.DictExprContext itemList = (SimplifyParser.DictExprContext) ctx.expression();
                    int index = 0;
                    for(SimplifyParser.ExpressionContext item : itemList.expression()){
                        if(index == 0){
                            String keyType = expressionType(item);
                            if(!keyType.equals("str")){
                                SemanticErrorListener.report("Semantic Error at line " + line + ": Key type mismatch in dictionary declaration: key of dictionary must be string");
                                return;
                            }
                            curKey = item.getText();
                            index = 1;
                        }else if(index == 1){
                            String valueType = expressionType(item);
                            if(!valueType.matches("str|num|dec|bool")){
                                SemanticErrorListener.report("Semantic Error at line " + line + ": Value type mismatch in dictionary declaration: value must be these type str|num|dec|bool");
                                return;
                            }
                            dictItems.put(curKey, valueType);
                            index = 0;
                        }
                    }
                    dictTable.put(name, dictItems);
                }
            }
        }
        if(ctx.ASSIGN() != null){
            String assignSymbol = ctx.ASSIGN().getText();
            if(!assignSymbol.equals("=")){
                SemanticErrorListener.report("Semantic Error at line " + line + ": invalid assigned symbol('" + assignSymbol +"') in declaration,");
                return;
            }
        }
        handleDeclaration(name, varType, line, ctx.expression());
    }

    private void handleDeclaration(String name, String type, int line, SimplifyParser.ExpressionContext expr) {
        if (name == null || name.isEmpty()) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Missing variable identifier during declaration");
            return;
        }

        if (!isValidType(type)) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Unresolved reference type '" + type);
            return;
        }
        Symbol symbol = findSymbol(name);
        if (symbol != null) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Variable '" + name + "' already declared with type " + "'" + symbol.type + "'");
            return;
        }
        if(expr != null){
            String valType = expressionType(expr);
            if(!valType.equals(type) && !(type.startsWith("arr"))){
                SemanticErrorListener.report("Semantic Error at line " + line + ": cannot assign '" + valType + "' to " + "'" + type + "' variable");
                return;
            }
        }

        currentScope.define(new Symbol(name, type));
    }

    private boolean isValidType(String type) {
        //Only supported storing const type inside array
        if(type.matches("arr\\[(num|dec|str|bool)\\]")){
            return true;
        }
        for(String curType : validTypes){
            if(curType.equals(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void enterIdExpr(SimplifyParser.IdExprContext ctx) {
        String name = ctx.ID().getText();
        Symbol symbol = findSymbol(name);

        // Check if the variable is declared
        if (symbol == null) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Variable '" + name + "' used before declaration");
        } else {
            // Check if the variable is being used correctly with the right type
            String expectedType = symbol.getType();
            String actualType = expressionType(ctx);

            if (!expectedType.equals("undefined") && !expectedType.equals(actualType)) {
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch: Variable '" + name + "' is of type '" + expectedType
                        + "' but is used as type '" + actualType);
            }
        }
    }

    @Override
    public void enterNumOpExpr(SimplifyParser.NumOpExprContext ctx) {
        String leftType = expressionType(ctx.expression(0));
        String rightType = expressionType(ctx.expression(1));
        boolean isConcatenate = false;
        if(ctx.getText().indexOf("+") != -1 && (leftType.equals("str") || rightType.equals("str"))){
            isConcatenate = true;
        }

        if (leftType.equals("Invalid") || rightType.equals("Invalid")) {
            return;
        }

        if((!isNumericType(leftType) || !isNumericType(rightType)) && !isConcatenate){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in numerical operation: Expected 'num' and 'dec' but found '" + leftType + "' and '" + rightType + "'");
        }

        if(!leftType.equals(rightType) && !isConcatenate){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in numerical operation : can't perform operation between " + leftType + " and " + rightType);
        }
    }

    @Override
    public void enterCompOpExpr(SimplifyParser.CompOpExprContext ctx) {
        String leftType = expressionType(ctx.expression(0));
        String rightType = expressionType(ctx.expression(1));
        String operator = ctx.compOp().getText();
        if(operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")){
            if(!isComparableType(leftType) || !isComparableType(rightType)){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in comparison operation: CompOperator('" + operator + "') can only compare 'num', 'dec', or 'str' but found '" + leftType + "' and '" + rightType + "'");
            }
        }

        if (leftType.equals("Invalid") || rightType.equals("Invalid")) {
            return;
        }

        if(!leftType.equals(rightType)){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in comparison operation : can't perform operation between " + leftType + " and " + rightType);
        }
    }

    @Override
    public void enterBoolOpExpr(SimplifyParser.BoolOpExprContext ctx) {
        String leftType = expressionType(ctx.expression(0));
        String rightType = expressionType(ctx.expression(1));
        if(!leftType.equals("bool") || !rightType.equals("bool")){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in boolean operation: Expected 'bool' but found '" + leftType + "' and '" + rightType + "'");
        }
    }

    private boolean isNumericType(String type){
        return (type.equals("num") || type.equals("dec"));
    }

    private boolean isComparableType(String type){
        return (type.equals("num") || type.equals("dec") || type.equals("str"));
    }

    @Override
    public void enterIndexAccessExpr(SimplifyParser.IndexAccessExprContext ctx) {
        String arrayName = ctx.ID().getText();  // Array variable
        Symbol arraySymbol = findSymbol( arrayName);

        if (arraySymbol == null) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": variable '" + arrayName + "' used before declaration");
            return;
        }
        String indexType = expressionType(ctx.expression());
        if (arraySymbol.getType().startsWith("arr")) {
            if (!indexType.equals("num")) {
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Indexing arr requires a 'num' type but found '" + indexType);
            }
        }else{
            if(!indexType.equals("str")){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Indexing dict requires a 'str' type but found '" + indexType);
            }
        }
    }

    @Override
    public void enterAssignment(SimplifyParser.AssignmentContext ctx) {
        String varName = ctx.ID().getText();
        Symbol symbol = findSymbol(varName);

        if (symbol == null) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Variable '" + varName + "' used before declaration at line " + ctx.start.getLine());
            return;
        }
        String declaredType = symbol.type;
        String valaueType = declaredType;

        if(symbol.type.startsWith("arr")){
            valaueType = declaredType.substring(4, declaredType.length() - 1);
        }

        String assignSymbol = ctx.ASSIGN().getText();
        if(!assignSymbol.equals("=") && !(valaueType.equals("num") || valaueType.equals("dec"))) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": assigned symbol('" + assignSymbol +"') only support 'num' or 'dec' variable");
            return;
        }

        if(ctx.expression().size() == 1){
            SimplifyParser.ExpressionContext rhs = ctx.expression(0);
            if(declaredType.startsWith("arr[")){
                if(!(rhs instanceof SimplifyParser.EmptyArrExprContext)){
                    String elemType = declaredType.substring(4, declaredType.length() - 1); //extract element type
                    SimplifyParser.ArrExprContext arrExpr = (SimplifyParser.ArrExprContext) rhs; //convert expression to array expression
                    List<SimplifyParser.ExpressionContext> elements = arrExpr.expression();
                    for (SimplifyParser.ExpressionContext elem : elements) { //Loops through element list in array
                        if (!elemType.equals(expressionType(elem))) {
                            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment: cannot assign " + expressionType(elem) + " to array with type " + elemType);
                            return;
                        }
                    }
                }
            }else if(!declaredType.equals("dict")){ //Check assignment for other data type(num, str, bool, dec)
                String valueType = expressionType(ctx.expression(0));
                if(!declaredType.equals(valueType)){
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment: cannot assigned " + valueType + " to variable with type " + declaredType);
                    return;
                }

                if(!assignSymbol.equals("=") && !(valueType.equals("num") || valueType.equals("dec"))) {
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": assigned symbol('" + assignSymbol +"') only support 'num' or 'dec' value");
                    return;
                }
            }
        }else if(ctx.expression().size() == 2){ //analyze assignment with an index
            String indexType = expressionType(ctx.expression(0));
            if(declaredType.startsWith("arr[")){
                String valueType = expressionType(ctx.expression(1));
                String elemType = declaredType.substring(4, declaredType.length() - 1);
                if(!indexType.equals("num")){
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in indexing of an array: expect 'num' but found " + indexType);
                    return;
                }
                if(!valueType.equals(elemType)){
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in indexing: cannot assign " + valueType + " to element in an array with type " + elemType);
                    return;
                }
            }else{
                if(!indexType.equals("str")){
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in indexing of a dictionary: expect 'string' but found " + indexType);
                    return;
                }
            }
        }
    }

    private String expressionType(SimplifyParser.ExpressionContext ctx) {
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
            Symbol sym = findSymbol(varName);
            return sym != null ? sym.getType() : "undefined";
        } else if(ctx instanceof SimplifyParser.NumOpExprContext){
            SimplifyParser.NumOpExprContext curExpr = (SimplifyParser.NumOpExprContext) ctx;
            String leftType = expressionType(curExpr.expression(0));
            String rightType = expressionType(curExpr.expression(1));
            if(!leftType.equals(rightType)){
                return "Invalid";
            }else{
                return leftType;
            }
        }else if(ctx instanceof SimplifyParser.IndexAccessExprContext){
            SimplifyParser.IndexAccessExprContext curExpr = (SimplifyParser.IndexAccessExprContext) ctx;
            String varName = curExpr.ID().getText();
            Symbol sym = findSymbol(varName);
            if(sym != null){
                if(sym.type.startsWith("arr")){
                    return sym.type.substring(4, sym.type.length() - 1);
                }
                Map<String, String> curDict = dictTable.get(varName);
                String valueType = curDict.get(curExpr.expression().getText());
                if(valueType == null){
                    return "Invalid";
                }
                return valueType;
            }
            return "undefined";
        }else if(ctx instanceof SimplifyParser.MethodCallExprContext){
            System.out.println("Method: " + ctx.getText());
            SimplifyParser.MethodCallExprContext curExpr = (SimplifyParser.MethodCallExprContext) ctx;
            String varName = curExpr.ID().getText();
            if(findSymbol(varName) == null){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Variable '" + varName + "' used before declaration at line " + ctx.start.getLine());
                return "undefined";
            }
            Symbol sym = findSymbol(varName);
            String method = curExpr.methodName().getText();
            if(sym.type.equals("str")){
                if(method.equals("length")){
                    return "num";
                }
            }else if(sym.type.startsWith("arr")){
                if(method.equals("size")){
                    return "num";
                }else{
                    return "Invalid";
                }
            }else if(sym.type.equals("dict")){
                System.out.println("Dict Method: " + sym);
                String exprType = switch(method){
                    case "size" -> "num";
                    case "key", "value" -> "arr";
                    default -> "Invalid";
                };
                System.out.println("Type: " + exprType);
                return exprType;
            }else{
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": there is not built-in method for '" + sym.type + "'");
            }
            return "Invalid";
        }else if(ctx instanceof SimplifyParser.FunctionExprContext){
            SimplifyParser.FuncExprContext curExpr = ((SimplifyParser.FunctionExprContext) ctx).funcExpr();
            String funcName = curExpr.ID().getText();
            String returnType = funcReturnType.get(funcName);
            return returnType;
        }else {
            return "Invalid";
        }
    }

    private Symbol findSymbol(String idName){
        Symbol symbol = null;
        Scope originalScope = currentScope;

        Scope traversedScope = currentScope;
        while(traversedScope != null){
            symbol = traversedScope.resolve(idName);
            if (symbol != null) {
                break;
            }
            if (traversedScope.getEnclosingScope() == null || traversedScope.getScopeName().equals("Global Scope")) {
                break;
            }
            traversedScope = traversedScope.getEnclosingScope();
        }
        currentScope = originalScope;
        return symbol;
    }

    @Override
    public void enterIfBlock(SimplifyParser.IfBlockContext ctx) {
        LocalScope ifScope = new LocalScope(currentScope, "if@" + ctx.getStart().getLine());
        ((BaseScope) currentScope).addNestedScope(ifScope);
        currentScope = ifScope;

        SimplifyParser.ExpressionContext condition = ctx.expression();
        if(!expressionType(condition).equals("bool")){
            SemanticErrorListener.report("Semantic Error at line " + condition.start.getLine() + ": Condition requires a 'bool' type but found '" + expressionType(condition) + "'");
        }
    }

    @Override
    public void exitIfBlock(SimplifyParser.IfBlockContext ctx){
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterOrBlock(SimplifyParser.OrBlockContext ctx) {
        LocalScope orScope = new LocalScope(currentScope, "or@" + ctx.getStart().getLine());
        ((BaseScope) currentScope).addNestedScope(orScope);
        currentScope = orScope;

        SimplifyParser.ExpressionContext condition = ctx.expression();
        if(!expressionType(condition).equals("bool")){
            SemanticErrorListener.report("Semantic Error at line " + condition.start.getLine() + ": Condition requires a 'bool' type but found '" + expressionType(condition) + "'");
        }
    }

    @Override
    public void exitOrBlock(SimplifyParser.OrBlockContext ctx){
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterElseBlock(SimplifyParser.ElseBlockContext ctx) {
        LocalScope elseScope = new LocalScope(currentScope, "else@" + ctx.getStart().getLine());
        ((BaseScope) currentScope).addNestedScope(elseScope);
        currentScope = elseScope;
    }

    @Override
    public void exitElseBlock(SimplifyParser.ElseBlockContext ctx){
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterForLoop(SimplifyParser.ForLoopContext ctx) {
        LocalScope forLoopScope = new LocalScope(currentScope, "forLoop@" + ctx.getStart().getLine());
        ((BaseScope) currentScope).addNestedScope(forLoopScope);
        currentScope = forLoopScope;

        //Ensure ID is not null before continuing analytic
        TerminalNode idNode = ctx.ID();
        if(idNode == null){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Missing variable identifier in for loop initialization");
            return;
        }

        String idType = ctx.type().getText();
        if(!(ctx.type() instanceof SimplifyParser.NumTypeContext)){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ":wrong type declaration in for loop. Variable must be of type 'num'");
            return;
        }
        String assignSymbol = ctx.ASSIGN().getText();
        if(!assignSymbol.equals("=")){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": invalid assigned symbol('" + assignSymbol +"') in for loop initialization,");
            return;
        }

        String idName = idNode.getText() != null? idNode.getText() : "";
        Symbol initVar = new Symbol(idName, idType);
        currentScope.define(initVar);

        if(!expressionType(ctx.expression()).equals("num")){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": For loop requires a 'num' value for end condition but found '" + expressionType(ctx.expression()) + "'");
        }
    }

    @Override
    public void exitForLoop(SimplifyParser.ForLoopContext ctx){
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterWhileLoop(SimplifyParser.WhileLoopContext ctx) {
        LocalScope whileLoopScope = new LocalScope(currentScope, "whileLoop@" + ctx.getStart().getLine());
        ((BaseScope) currentScope).addNestedScope(whileLoopScope);
        currentScope = whileLoopScope;

        TerminalNode idNode = ctx.ID();
        if(idNode == null){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Missing variable for while loop condition");
            return;
        }

        Symbol curSymbol = currentScope.resolve(idNode.getText());
        if(curSymbol != null){
            if(!curSymbol.getType().equals("num")){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": While loop requires a 'num' value for condition but found '" + curSymbol.getType() + "'" + " variable");
            }
        }

        if(!expressionType(ctx.expression()).equals("num")){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": while loop requires a 'num' value for end condition but found '" + expressionType(ctx.expression()) + "'");
        }
    }

    @Override
    public void exitWhileLoop(SimplifyParser.WhileLoopContext ctx){
        currentScope = currentScope.getEnclosingScope();
    }

    @Override
    public void enterFunction(SimplifyParser.FunctionContext ctx) {
        // Ensure ID is not null before accessing it
        TerminalNode idNode = ctx.ID();

        if(idNode == null){
            return;
        }

        String functionName = idNode.getText() != null? idNode.getText() : "";
        int line = ctx.start.getLine();

        // define the function in global scope
        if (currentScope.resolve(functionName) != null) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Function '" + functionName + "' re-declared in the same scope");
        }

        // Create a function symbol and attach a new local scope
        Symbol funcSymbol = new Symbol(functionName,"function");
        LocalScope functionScope = new LocalScope(currentScope, functionName);
        funcSymbol.setScope(functionScope); // 💡 store the local scope in the symbol

        currentScope.define(funcSymbol);    // define the function in the current scope (global)
        currentScope = functionScope;
        funcReturnType.put(functionName, ctx.type().getText());

        // Handle function parameters (if any)
        if (ctx.argumentList() != null) {
            for (SimplifyParser.ArgumentContext argCtx : ctx.argumentList().argument()) {
                String argName = argCtx.ID().getText();
                String argType = argCtx.type().getText();
                currentScope.define(new Symbol(argName, argType));  // Add parameter to local scope
            }
        }
    }

    @Override
    public void exitFunction(SimplifyParser.FunctionContext ctx) {
        SimplifyParser.ReturnStatementContext returnStatement = ctx.returnStatement();
        if(returnStatement.expression() != null){
            String returnExprType = expressionType(returnStatement.expression());
            String returnType = ctx.type().getText();
            if(!returnType.equals(returnExprType)){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Return type mismatch: expected '" + returnType + "' but found '" + returnExprType + "'");
            }
        }
        currentScope = currentScope.getEnclosingScope(); // Exit function scope
    }

    @Override
    public void enterClassInit(SimplifyParser.ClassInitContext ctx) {
        String className = ctx.ID().getText();
        Symbol classSymbol = new Symbol(className, "class");

        validTypes.add(className);

        ClassScope classScope = new ClassScope(currentScope, className);
        classSymbol.setScope(classScope);

        currentScope.define(classSymbol);
        currentScope = classScope;
    }

    @Override
    public void exitClassInit(SimplifyParser.ClassInitContext ctx) {
        currentScope = currentScope.getEnclosingScope();
    }

    public void printSymbolTables() {
        globalScope.print(""); // Define a recursive print method on Scope
    }

    public void printSemanticErrors() {
        SemanticErrorListener.printErrors();
    }
}