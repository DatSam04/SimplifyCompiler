// Generated from src/antlr/Simplify.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SimplifyParser}.
 */
public interface SimplifyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SimplifyParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SimplifyParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#comments}.
	 * @param ctx the parse tree
	 */
	void enterComments(SimplifyParser.CommentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#comments}.
	 * @param ctx the parse tree
	 */
	void exitComments(SimplifyParser.CommentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(SimplifyParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(SimplifyParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#innerStatement}.
	 * @param ctx the parse tree
	 */
	void enterInnerStatement(SimplifyParser.InnerStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#innerStatement}.
	 * @param ctx the parse tree
	 */
	void exitInnerStatement(SimplifyParser.InnerStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StrType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterStrType(SimplifyParser.StrTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StrType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitStrType(SimplifyParser.StrTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterNumType(SimplifyParser.NumTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitNumType(SimplifyParser.NumTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DecType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterDecType(SimplifyParser.DecTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DecType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitDecType(SimplifyParser.DecTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BoolType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterBoolType(SimplifyParser.BoolTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitBoolType(SimplifyParser.BoolTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterArrType(SimplifyParser.ArrTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitArrType(SimplifyParser.ArrTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DictType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void enterDictType(SimplifyParser.DictTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DictType}
	 * labeled alternative in {@link SimplifyParser#type}.
	 * @param ctx the parse tree
	 */
	void exitDictType(SimplifyParser.DictTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(SimplifyParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(SimplifyParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(SimplifyParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(SimplifyParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addDictItemExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddDictItemExpr(SimplifyParser.AddDictItemExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addDictItemExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddDictItemExpr(SimplifyParser.AddDictItemExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCompOpExpr(SimplifyParser.CompOpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCompOpExpr(SimplifyParser.CompOpExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyArrExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEmptyArrExpr(SimplifyParser.EmptyArrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyArrExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEmptyArrExpr(SimplifyParser.EmptyArrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decimalExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterDecimalExpr(SimplifyParser.DecimalExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decimalExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitDecimalExpr(SimplifyParser.DecimalExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code trueExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTrueExpr(SimplifyParser.TrueExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code trueExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTrueExpr(SimplifyParser.TrueExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumberExpr(SimplifyParser.NumberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumberExpr(SimplifyParser.NumberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBoolOpExpr(SimplifyParser.BoolOpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBoolOpExpr(SimplifyParser.BoolOpExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code readExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterReadExpr(SimplifyParser.ReadExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code readExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitReadExpr(SimplifyParser.ReadExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyDictExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEmptyDictExpr(SimplifyParser.EmptyDictExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyDictExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEmptyDictExpr(SimplifyParser.EmptyDictExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(SimplifyParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(SimplifyParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNumOpExpr(SimplifyParser.NumOpExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numOpExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNumOpExpr(SimplifyParser.NumOpExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionExpr(SimplifyParser.FunctionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionExpr(SimplifyParser.FunctionExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStringExpr(SimplifyParser.StringExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStringExpr(SimplifyParser.StringExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code methodCallExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMethodCallExpr(SimplifyParser.MethodCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code methodCallExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMethodCallExpr(SimplifyParser.MethodCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indexAccessExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIndexAccessExpr(SimplifyParser.IndexAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indexAccessExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIndexAccessExpr(SimplifyParser.IndexAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrExpr(SimplifyParser.ArrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrExpr(SimplifyParser.ArrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dictExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterDictExpr(SimplifyParser.DictExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dictExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitDictExpr(SimplifyParser.DictExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code falseExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFalseExpr(SimplifyParser.FalseExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code falseExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFalseExpr(SimplifyParser.FalseExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIdExpr(SimplifyParser.IdExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code idExpr}
	 * labeled alternative in {@link SimplifyParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIdExpr(SimplifyParser.IdExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#methodName}.
	 * @param ctx the parse tree
	 */
	void enterMethodName(SimplifyParser.MethodNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#methodName}.
	 * @param ctx the parse tree
	 */
	void exitMethodName(SimplifyParser.MethodNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#compOp}.
	 * @param ctx the parse tree
	 */
	void enterCompOp(SimplifyParser.CompOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#compOp}.
	 * @param ctx the parse tree
	 */
	void exitCompOp(SimplifyParser.CompOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#boolOp}.
	 * @param ctx the parse tree
	 */
	void enterBoolOp(SimplifyParser.BoolOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#boolOp}.
	 * @param ctx the parse tree
	 */
	void exitBoolOp(SimplifyParser.BoolOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterConditional(SimplifyParser.ConditionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitConditional(SimplifyParser.ConditionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void enterIfBlock(SimplifyParser.IfBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#ifBlock}.
	 * @param ctx the parse tree
	 */
	void exitIfBlock(SimplifyParser.IfBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#orBlock}.
	 * @param ctx the parse tree
	 */
	void enterOrBlock(SimplifyParser.OrBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#orBlock}.
	 * @param ctx the parse tree
	 */
	void exitOrBlock(SimplifyParser.OrBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#elseBlock}.
	 * @param ctx the parse tree
	 */
	void enterElseBlock(SimplifyParser.ElseBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#elseBlock}.
	 * @param ctx the parse tree
	 */
	void exitElseBlock(SimplifyParser.ElseBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#loop}.
	 * @param ctx the parse tree
	 */
	void enterLoop(SimplifyParser.LoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#loop}.
	 * @param ctx the parse tree
	 */
	void exitLoop(SimplifyParser.LoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(SimplifyParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(SimplifyParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoop(SimplifyParser.WhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoop(SimplifyParser.WhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(SimplifyParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(SimplifyParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#funcExpr}.
	 * @param ctx the parse tree
	 */
	void enterFuncExpr(SimplifyParser.FuncExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#funcExpr}.
	 * @param ctx the parse tree
	 */
	void exitFuncExpr(SimplifyParser.FuncExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(SimplifyParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(SimplifyParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(SimplifyParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(SimplifyParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(SimplifyParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(SimplifyParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(SimplifyParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(SimplifyParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#classInit}.
	 * @param ctx the parse tree
	 */
	void enterClassInit(SimplifyParser.ClassInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#classInit}.
	 * @param ctx the parse tree
	 */
	void exitClassInit(SimplifyParser.ClassInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#classProperty}.
	 * @param ctx the parse tree
	 */
	void enterClassProperty(SimplifyParser.ClassPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#classProperty}.
	 * @param ctx the parse tree
	 */
	void exitClassProperty(SimplifyParser.ClassPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#result}.
	 * @param ctx the parse tree
	 */
	void enterResult(SimplifyParser.ResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#result}.
	 * @param ctx the parse tree
	 */
	void exitResult(SimplifyParser.ResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link SimplifyParser#readInputExpr}.
	 * @param ctx the parse tree
	 */
	void enterReadInputExpr(SimplifyParser.ReadInputExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SimplifyParser#readInputExpr}.
	 * @param ctx the parse tree
	 */
	void exitReadInputExpr(SimplifyParser.ReadInputExprContext ctx);
}