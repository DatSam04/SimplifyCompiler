package symbols;

import antlr.SimplifyBaseListener;
import antlr.SimplifyParser;
import ErrorListener.SemanticErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.beans.Expression;
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
        if(varType.startsWith("arr")) {
            String elementType = varType.substring(4, varType.length() - 1);;
            if (!validTypes.contains(elementType)) {
                SemanticErrorListener.report("Semantic Error at line " + line + ": Invalid element type in array declaration: " + elementType);
                return;
            }
            if(ctx.initializer().getText().length() > 0){
                for(SimplifyParser.ExpressionContext elem : ctx.initializer().elementList().expression()){
                    String curElemType = expressionType(elem);
                    if(!curElemType.equals(elementType)){
                        SemanticErrorListener.report("Semantic Error at line " + line + ": Type mismatch in array declaration: can not initialize " + curElemType + " to a " + varType);
                    }
                }
            }
        }
        handleDeclaration(name, varType, line);
    }

    private void handleDeclaration(String name, String type, int line) {
        if (name == null || name.isEmpty()) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Missing variable identifier during declaration");
            return;
        }

        if (!isValidType(type)) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Unresolved reference type '" + type);
            return;
        }

        if (currentScope.resolve(name) != null) {
            SemanticErrorListener.report("Semantic Error at line " + line + ": Variable '" + name + "' re-declared in the same scope");
            return;
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
        Symbol symbol = currentScope.resolve(name);

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

//    @Override
//    public void enterNumOpExpr(SimplifyParser.NumOpExprContext ctx) {
//        String leftType = expressionType(ctx.expression(0));
//        String rightType = expressionType(ctx.expression(1));
//
//        // Check for type mismatch
//        if (!leftType.equals("num") || !rightType.equals("num")) {
//            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in multiplication/division: Expected 'num' but found '"
//                    + leftType + "' and '" + rightType);
//        }
//    }
//
//    @Override
//    public void enterCompOpExpr(SimplifyParser.CompOpExprContext ctx) {
//        String leftType = expressionType(ctx.expression(0));
//        String rightType = expressionType(ctx.expression(1));
//
//        // Check for type mismatch
//        if (!leftType.equals("num") || !rightType.equals("num")) {
//            SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in comparison: Expected 'num' but found '"
//                    + leftType + "' and '" + rightType);
//        }
//    }

    @Override
    public void enterIndexAccessExpr(SimplifyParser.IndexAccessExprContext ctx) {
        String arrayName = ctx.expression(0).getText();  // Array variable
        Symbol arraySymbol = currentScope.resolve(arrayName);

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

    private String expressionType(SimplifyParser.ExpressionContext ctx) {
        if (ctx instanceof SimplifyParser.NumberExprContext) {
            return "num";
        } else if (ctx instanceof SimplifyParser.DecimalExprContext) {
            return "dec";
        } else if (ctx instanceof SimplifyParser.StringExprContext) {
            return "str";
        } else if (ctx instanceof SimplifyParser.ArrExprContext) {
            return "arr";
        } else if (ctx instanceof SimplifyParser.DictExprContext) {
            return "dict";
        } else if (ctx instanceof SimplifyParser.IndexAccessExprContext) {
            return "index";
        }else if (ctx instanceof SimplifyParser.TrueExprContext || ctx instanceof SimplifyParser.FalseExprContext) {
            return "bool";
        } else if (ctx instanceof SimplifyParser.IdExprContext) {
            String varName = ctx.getText();
            Symbol sym = currentScope.resolve(varName);
            return sym != null ? sym.getType() : "undefined";
        } else {
            return "Invalid";
        }
    }

    @Override
    public void enterItemList(SimplifyParser.ItemListContext ctx) {
        for (SimplifyParser.KeyValuePairContext pair : ctx.keyValuePair()) {
            String key = pair.String().getText();
            SimplifyParser.ExpressionContext valueExpr = pair.expression();

            // Check if the value expression is valid
            String valueType = expressionType(valueExpr);
            if (!valueType.equals("str") && !valueType.equals("num") && !valueType.equals("dec") && !valueType.equals("bool")) {
                SemanticErrorListener.report("Semantic Error at line " + valueExpr.start.getLine() + ": Invalid value type for key '" + key + "': Expected 'str', 'num', 'dec', or 'bool' but found '" + valueType);
            }
        }
    }

    @Override
    public void enterAssignment(SimplifyParser.AssignmentContext ctx) {
        String varName = ctx.ID().getText();
        Symbol symbol = currentScope.resolve(varName);

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
                    SemanticErrorListener.report("Semantic Error at line " + ctx.start.getLine() + ": Type mismatch in assignment: cannot assigned " + valueType + " to variable with type " + symbol.type);
                    return;
                }
            }
        }else if(ctx.expression().size() == 2){
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