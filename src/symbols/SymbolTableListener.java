package symbols;

import antlr.SimplifyBaseListener;
import antlr.SimplifyParser;
import ErrorListener.SemanticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SymbolTableListener extends SimplifyBaseListener {
    private Scope currentScope;
    private final GlobalScope globalScope = new GlobalScope();
    private final Set<String> validTypes = new java.util.HashSet<>(Set.of("num", "dec", "str", "bool", "arr", "dict"));

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
                if(!(ctx.expression() instanceof SimplifyParser.ArrExprContext)){
                    SemanticErrorListener.report("Semantic Error at line " + line + ": value mismatch type, array variable only accepts array value \"[value]\"");
                    return;
                }
                SimplifyParser.ArrExprContext elemList = (SimplifyParser.ArrExprContext) ctx.expression();
                for(SimplifyParser.ExpressionContext elem : elemList.expression()){
                    String curElemType = expressionType(elem);
                    if(!curElemType.equals(elementType)){
                        SemanticErrorListener.report("Semantic Error at line " + line + ": Type mismatch in array declaration: can not initialize " + curElemType + " to a " + varType);
                    }
                }
            }
        } else if (varType.equals("dict")) { // Perform semantic analysis with dictionary variables
            if(ctx.expression() != null){
                SimplifyParser.DictExprContext itemList = (SimplifyParser.DictExprContext) ctx.expression();
                int index = 0;
                for(SimplifyParser.ExpressionContext item : itemList.expression()){
                    if(index == 0){
                        String keyType = expressionType(item);
                        if(!keyType.equals("str")){
                            SemanticErrorListener.report("Semantic Error at line " + line + ": Key type mismatch in dictionary declaration: key of dictionary must be string");
                        }
                        index = 1;
                    }else if(index == 1){
                        String valueType = expressionType(item);
                        if(!valueType.matches("str|num|dec|bool")){
                            SemanticErrorListener.report("Semantic Error at line " + line + ": Value type mismatch in dictionary declaration: value must be these type str|num|dec|bool");
                        }
                        index = 0;
                    }
                }
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
            if(!valType.equals(type)){
                SemanticErrorListener.report("Semantic Error at line " + line + ": cannot assign '" + valType + "' to " + "'" + type + "' variable");
                return;
            }
        }

        currentScope.define(new Symbol(name, type));
    }

    private boolean isValidType(String type) {
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
//        if (!(ctx.getParent() instanceof SimplifyParser.NumOpExprContext)) {
//            evalNumCompExpr(ctx, "NumOp");
//        }
        String leftType = expressionType(ctx.expression(0));
        String rightType = expressionType(ctx.expression(1));
        boolean isConcatenate = false;
        if(ctx.getText().indexOf("+") != -1 && (leftType.equals("str") || rightType.equals("str"))){
            isConcatenate = true;
        }

        if((!isNumericType(leftType) || !isNumericType(rightType)) && !isConcatenate){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in numerical operation: Expected 'num' and 'dec' but found '" + leftType + "' and '" + rightType + "'");
        }

        if(!leftType.equals(rightType)){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in numerical operation : can't perform operation between " + leftType + " and " + rightType);
        }
    }

    @Override
    public void enterCompOpExpr(SimplifyParser.CompOpExprContext ctx) {
//        if (!(ctx.getParent() instanceof SimplifyParser.CompOpExprContext)) {
//            evalNumCompExpr(ctx, "CompOp");
//        }
        String leftType = expressionType(ctx.expression(0));
        String rightType = expressionType(ctx.expression(1));
        String operator = ctx.compOp().getText();
        if(operator.equals("<") || operator.equals(">") || operator.equals("<=") || operator.equals(">=")){
            if(!isComparableType(leftType) || !isComparableType(rightType)){
                SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in comparison operation: CompOperator('" + operator + "') can only compare 'num', 'dec', or 'str' but found '" + leftType + "' and '" + rightType + "'");
            }
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

//    //Evaluate both NumOp and CompOp expression and output related errors
//    public String evalNumCompExpr(SimplifyParser.ExpressionContext expr, String OpExpr) {
//        List<SimplifyParser.ExpressionContext> curExpr = getSubExpr(expr);
//        if(curExpr.size() == 0){
//            return expressionType(expr);
//        }
//        String leftType = evalNumCompExpr(curExpr.get(0), OpExpr);
//        String rightType = evalNumCompExpr(curExpr.get(1), OpExpr);
//
//        if(OpExpr.equals("NumOp")){
//            boolean isConcatenate = false;
//            if(expr.getText().indexOf("+") != -1 && (leftType.equals("str") || rightType.equals("str"))){
//                isConcatenate = true;
//            }
//            if((!isNumericType(leftType) || !isNumericType(rightType)) && !isConcatenate){
//                SemanticErrorListener.report("Semantic Error at line " + expr.start.getLine() + ": Type mismatch in numerical operation: Expected 'num' and 'dec' but found '" + leftType + "' and '" + rightType + "'");
//                return "Invalid";
//            }
//            if(!leftType.equals(rightType)){
//                SemanticErrorListener.report("Semantic Error at line " + expr.start.getLine() + ": Type mismatch in " + OpExpr + ": can't perform operation between " + leftType + " and " + rightType);
//            }
//            return rightType;
//        }else if (OpExpr.equals("CompOp")){
//            if (!isComparableType(leftType) || !isComparableType(rightType)) {
//                SemanticErrorListener.report("Semantic Error at line " + expr.start.getLine() + ": Type mismatch in comparison operation: Expected 'num', 'dec', or 'str' but found '" + leftType + "' and '" + rightType + "'");
//                return "Invalid";
//            }
//            if(!leftType.equals(rightType)){
//                SemanticErrorListener.report("Semantic Error at line " + expr.start.getLine() + ": Type mismatch in " + OpExpr + ": can't perform operation between " + leftType + " and " + rightType);
//                return rightType;
//            }
//        }
//        return rightType;
//    }

    private boolean isNumericType(String type){
        return (type.equals("num") || type.equals("dec"));
    }

    private boolean isComparableType(String type){
        return (type.equals("num") || type.equals("dec") || type.equals("str"));
    }

    private List<SimplifyParser.ExpressionContext> getSubExpr(SimplifyParser.ExpressionContext expr){
        if(expr instanceof SimplifyParser.NumOpExprContext){
            return ((SimplifyParser.NumOpExprContext) expr).expression();
        }else if(expr instanceof SimplifyParser.CompOpExprContext){
            return ((SimplifyParser.CompOpExprContext) expr).expression();
        }
        return Collections.emptyList();
    }

    @Override
    public void enterIndexAccessExpr(SimplifyParser.IndexAccessExprContext ctx) {
        String arrayName = ctx.expression(0).getText();  // Array variable
        Symbol arraySymbol = findSymbol( arrayName);

        if (arraySymbol == null) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Array variable '" + arrayName + "' used before declaration");
        } else if (!arraySymbol.getType().startsWith("arr")) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Variable '" + arrayName + "' is not an array");
        }

        // Check the type of index accessing
        String indexType = expressionType(ctx.expression(1));
        if (!indexType.equals("num")) {
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Indexing requires a 'num' type but found '" + indexType);
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
        if(ctx.expression().size() == 1){
            SimplifyParser.ExpressionContext rhs = ctx.expression(0);
            if(declaredType.startsWith("arr[")){
                String elemType = declaredType.substring(4, declaredType.length() - 1); //extract element type
                SimplifyParser.ArrExprContext arrExpr = (SimplifyParser.ArrExprContext) rhs; //convert expression to array expression
                List<SimplifyParser.ExpressionContext> elements = arrExpr.expression();
                for (SimplifyParser.ExpressionContext elem : elements) { //Loops through element list in array
                    if (!elemType.equals(expressionType(elem))) {
                        SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment: cannot assign " + expressionType(elem) + " to array with type " + elemType);
                        return;
                    }
                }
            }else if(!declaredType.equals("dict")){ //Check assignment for other data type(num, str, bool, dec)
                String valueType = expressionType(ctx.expression(0));
                if(!declaredType.equals(valueType)){
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment: cannot assigned " + valueType + " to variable with type " + declaredType);
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
            return;
        }

        String idType = ctx.type().getText();
        if(!(ctx.type() instanceof SimplifyParser.NumTypeContext)){
            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ":wrong type declaration in for loop. Variable must be of type 'num'");
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
            return;
        }

        Symbol curSymbol = currentScope.resolve(idNode.getText());
        System.out.println(curSymbol);
        if(curSymbol != null){
            System.out.println(curSymbol.getType());
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
        Symbol funcSymbol = new Symbol(functionName, "function");
        LocalScope functionScope = new LocalScope(currentScope, functionName);
        funcSymbol.setScope(functionScope); // 💡 store the local scope in the symbol

        currentScope.define(funcSymbol);    // define the function in the current scope (global)
        currentScope = functionScope;

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