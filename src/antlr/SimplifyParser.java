// Generated from src/antlr/Simplify.g4 by ANTLR 4.13.2
package antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SimplifyParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, Cond_If=12, Cond_Else=13, Cond_Or=14, F_Loop=15, W_Loop=16, 
		Func=17, Class=18, StrMethod=19, ArrMethod=20, DictMethod=21, Str=22, 
		Num=23, Dec=24, Bool=25, Arr=26, Dict=27, Output=28, Input=29, Exist=30, 
		To=31, Return=32, Size=33, True=34, False=35, ID=36, Number=37, Decimal=38, 
		String=39, Ws=40, SEMI=41, EQ=42, NE=43, GT=44, GE=45, LT=46, LE=47, ASSIGN=48, 
		MUL=49, DIV=50, PLUS=51, MINUS=52, AND=53, OR=54, SingleLineComment=55, 
		MultiLineComment=56;
	public static final int
		RULE_program = 0, RULE_comments = 1, RULE_statement = 2, RULE_innerStatement = 3, 
		RULE_type = 4, RULE_declaration = 5, RULE_assignment = 6, RULE_expression = 7, 
		RULE_methodName = 8, RULE_compOp = 9, RULE_boolOp = 10, RULE_conditional = 11, 
		RULE_ifBlock = 12, RULE_orBlock = 13, RULE_elseBlock = 14, RULE_loop = 15, 
		RULE_forLoop = 16, RULE_whileLoop = 17, RULE_block = 18, RULE_funcExpr = 19, 
		RULE_function = 20, RULE_argumentList = 21, RULE_argument = 22, RULE_returnStatement = 23, 
		RULE_classInit = 24, RULE_classProperty = 25, RULE_result = 26, RULE_readInputExpr = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "comments", "statement", "innerStatement", "type", "declaration", 
			"assignment", "expression", "methodName", "compOp", "boolOp", "conditional", 
			"ifBlock", "orBlock", "elseBlock", "loop", "forLoop", "whileLoop", "block", 
			"funcExpr", "function", "argumentList", "argument", "returnStatement", 
			"classInit", "classProperty", "result", "readInputExpr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'['", "']'", "'.'", "'('", "')'", "':'", "'[]'", "','", "'{}'", 
			"'{'", "'}'", "'if'", "'else'", "'or'", "'f_loop'", "'w_loop'", "'func'", 
			"'class'", "'length'", null, null, "'str'", "'num'", "'dec'", "'bool'", 
			"'arr'", "'dict'", "'result'", "'readInput'", "'exist'", "'to'", "'return'", 
			"'size'", "'True'", "'False'", null, null, null, null, null, "';'", "'=='", 
			"'!='", "'>'", "'>='", "'<'", "'<='", null, "'*'", "'/'", "'+'", "'-'", 
			"'&'", "'|'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"Cond_If", "Cond_Else", "Cond_Or", "F_Loop", "W_Loop", "Func", "Class", 
			"StrMethod", "ArrMethod", "DictMethod", "Str", "Num", "Dec", "Bool", 
			"Arr", "Dict", "Output", "Input", "Exist", "To", "Return", "Size", "True", 
			"False", "ID", "Number", "Decimal", "String", "Ws", "SEMI", "EQ", "NE", 
			"GT", "GE", "LT", "LE", "ASSIGN", "MUL", "DIV", "PLUS", "MINUS", "AND", 
			"OR", "SingleLineComment", "MultiLineComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Simplify.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SimplifyParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SimplifyParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(56);
				statement();
				}
				}
				setState(59); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 108087474458695314L) != 0) );
			setState(61);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommentsContext extends ParserRuleContext {
		public TerminalNode SingleLineComment() { return getToken(SimplifyParser.SingleLineComment, 0); }
		public TerminalNode MultiLineComment() { return getToken(SimplifyParser.MultiLineComment, 0); }
		public CommentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterComments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitComments(this);
		}
	}

	public final CommentsContext comments() throws RecognitionException {
		CommentsContext _localctx = new CommentsContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_comments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			_la = _input.LA(1);
			if ( !(_la==SingleLineComment || _la==MultiLineComment) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ClassInitContext classInit() {
			return getRuleContext(ClassInitContext.class,0);
		}
		public InnerStatementContext innerStatement() {
			return getRuleContext(InnerStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statement);
		try {
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Func:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				function();
				}
				break;
			case Class:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				classInit();
				}
				break;
			case T__0:
			case T__3:
			case T__6:
			case T__8:
			case T__9:
			case Cond_If:
			case F_Loop:
			case W_Loop:
			case Str:
			case Num:
			case Dec:
			case Bool:
			case Arr:
			case Dict:
			case Output:
			case Input:
			case True:
			case False:
			case ID:
			case Number:
			case Decimal:
			case String:
			case SingleLineComment:
			case MultiLineComment:
				enterOuterAlt(_localctx, 3);
				{
				setState(67);
				innerStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InnerStatementContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public CommentsContext comments() {
			return getRuleContext(CommentsContext.class,0);
		}
		public ConditionalContext conditional() {
			return getRuleContext(ConditionalContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public LoopContext loop() {
			return getRuleContext(LoopContext.class,0);
		}
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public InnerStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_innerStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterInnerStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitInnerStatement(this);
		}
	}

	public final InnerStatementContext innerStatement() throws RecognitionException {
		InnerStatementContext _localctx = new InnerStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_innerStatement);
		try {
			setState(79);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				comments();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(72);
				conditional();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(73);
				declaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(74);
				loop();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(75);
				result();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(76);
				expression(0);
				setState(77);
				match(SEMI);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DecTypeContext extends TypeContext {
		public TerminalNode Dec() { return getToken(SimplifyParser.Dec, 0); }
		public DecTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterDecType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitDecType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrTypeContext extends TypeContext {
		public TerminalNode Arr() { return getToken(SimplifyParser.Arr, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ArrTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterArrType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitArrType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolTypeContext extends TypeContext {
		public TerminalNode Bool() { return getToken(SimplifyParser.Bool, 0); }
		public BoolTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterBoolType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitBoolType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumTypeContext extends TypeContext {
		public TerminalNode Num() { return getToken(SimplifyParser.Num, 0); }
		public NumTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterNumType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitNumType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DictTypeContext extends TypeContext {
		public TerminalNode Dict() { return getToken(SimplifyParser.Dict, 0); }
		public DictTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterDictType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitDictType(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StrTypeContext extends TypeContext {
		public TerminalNode Str() { return getToken(SimplifyParser.Str, 0); }
		public StrTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterStrType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitStrType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_type);
		try {
			setState(91);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Str:
				_localctx = new StrTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				match(Str);
				}
				break;
			case Num:
				_localctx = new NumTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				match(Num);
				}
				break;
			case Dec:
				_localctx = new DecTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(83);
				match(Dec);
				}
				break;
			case Bool:
				_localctx = new BoolTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(84);
				match(Bool);
				}
				break;
			case Arr:
				_localctx = new ArrTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(85);
				match(Arr);
				setState(86);
				match(T__0);
				setState(87);
				type();
				setState(88);
				match(T__1);
				}
				break;
			case Dict:
				_localctx = new DictTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(90);
				match(Dict);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public TerminalNode ASSIGN() { return getToken(SimplifyParser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			type();
			setState(94);
			match(ID);
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(95);
				match(ASSIGN);
				setState(96);
				expression(0);
				}
			}

			setState(99);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(SimplifyParser.ASSIGN, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(ID);
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(102);
				match(T__0);
				setState(103);
				expression(0);
				setState(104);
				match(T__1);
				}
			}

			setState(108);
			match(ASSIGN);
			setState(109);
			expression(0);
			setState(110);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AddDictItemExprContext extends ExpressionContext {
		public TerminalNode String() { return getToken(SimplifyParser.String, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AddDictItemExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterAddDictItemExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitAddDictItemExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CompOpExprContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public CompOpContext compOp() {
			return getRuleContext(CompOpContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public CompOpExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterCompOpExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitCompOpExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EmptyArrExprContext extends ExpressionContext {
		public EmptyArrExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterEmptyArrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitEmptyArrExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DecimalExprContext extends ExpressionContext {
		public TerminalNode Decimal() { return getToken(SimplifyParser.Decimal, 0); }
		public DecimalExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterDecimalExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitDecimalExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TrueExprContext extends ExpressionContext {
		public TerminalNode True() { return getToken(SimplifyParser.True, 0); }
		public TrueExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterTrueExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitTrueExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumberExprContext extends ExpressionContext {
		public TerminalNode Number() { return getToken(SimplifyParser.Number, 0); }
		public NumberExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterNumberExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitNumberExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolOpExprContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public BoolOpContext boolOp() {
			return getRuleContext(BoolOpContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public BoolOpExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterBoolOpExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitBoolOpExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReadExprContext extends ExpressionContext {
		public ReadInputExprContext readInputExpr() {
			return getRuleContext(ReadInputExprContext.class,0);
		}
		public ReadExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterReadExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitReadExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EmptyDictExprContext extends ExpressionContext {
		public EmptyDictExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterEmptyDictExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitEmptyDictExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExprContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParenExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterParenExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitParenExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumOpExprContext extends ExpressionContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode MUL() { return getToken(SimplifyParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(SimplifyParser.DIV, 0); }
		public TerminalNode PLUS() { return getToken(SimplifyParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(SimplifyParser.MINUS, 0); }
		public NumOpExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterNumOpExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitNumOpExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionExprContext extends ExpressionContext {
		public FuncExprContext funcExpr() {
			return getRuleContext(FuncExprContext.class,0);
		}
		public FunctionExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterFunctionExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitFunctionExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringExprContext extends ExpressionContext {
		public TerminalNode String() { return getToken(SimplifyParser.String, 0); }
		public StringExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterStringExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitStringExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MethodCallExprContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public MethodNameContext methodName() {
			return getRuleContext(MethodNameContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public MethodCallExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterMethodCallExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitMethodCallExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IndexAccessExprContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public IndexAccessExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterIndexAccessExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitIndexAccessExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArrExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterArrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitArrExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DictExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public DictExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterDictExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitDictExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FalseExprContext extends ExpressionContext {
		public TerminalNode False() { return getToken(SimplifyParser.False, 0); }
		public FalseExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterFalseExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitFalseExpr(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdExprContext extends ExpressionContext {
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public IdExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterIdExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitIdExpr(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				_localctx = new IndexAccessExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(113);
				match(ID);
				setState(114);
				match(T__0);
				setState(115);
				expression(0);
				setState(116);
				match(T__1);
				}
				break;
			case 2:
				{
				_localctx = new MethodCallExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(118);
				match(ID);
				setState(119);
				match(T__2);
				setState(120);
				methodName();
				setState(121);
				match(T__3);
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1082868631186L) != 0)) {
					{
					setState(122);
					expression(0);
					}
				}

				setState(125);
				match(T__4);
				}
				break;
			case 3:
				{
				_localctx = new AddDictItemExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(127);
				match(String);
				setState(128);
				match(T__5);
				setState(129);
				expression(19);
				}
				break;
			case 4:
				{
				_localctx = new EmptyArrExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(130);
				match(T__6);
				}
				break;
			case 5:
				{
				_localctx = new ArrExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(131);
				match(T__0);
				setState(132);
				expression(0);
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(133);
					match(T__7);
					setState(134);
					expression(0);
					}
					}
					setState(139);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(140);
				match(T__1);
				}
				break;
			case 6:
				{
				_localctx = new EmptyDictExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(142);
				match(T__8);
				}
				break;
			case 7:
				{
				_localctx = new DictExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(143);
				match(T__9);
				setState(144);
				expression(0);
				setState(145);
				match(T__5);
				setState(146);
				expression(0);
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(147);
					match(T__7);
					setState(148);
					expression(0);
					setState(149);
					match(T__5);
					setState(150);
					expression(0);
					}
					}
					setState(156);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(157);
				match(T__10);
				}
				break;
			case 8:
				{
				_localctx = new IdExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(159);
				match(ID);
				}
				break;
			case 9:
				{
				_localctx = new NumberExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(160);
				match(Number);
				}
				break;
			case 10:
				{
				_localctx = new DecimalExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(161);
				match(Decimal);
				}
				break;
			case 11:
				{
				_localctx = new StringExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(162);
				match(String);
				}
				break;
			case 12:
				{
				_localctx = new FunctionExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(163);
				funcExpr();
				}
				break;
			case 13:
				{
				_localctx = new ReadExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(164);
				readInputExpr();
				}
				break;
			case 14:
				{
				_localctx = new TrueExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(165);
				match(True);
				}
				break;
			case 15:
				{
				_localctx = new FalseExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(166);
				match(False);
				}
				break;
			case 16:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(167);
				match(T__3);
				setState(168);
				expression(0);
				setState(169);
				match(T__4);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(192);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(190);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						_localctx = new NumOpExprContext(new ExpressionContext(_parentctx, _parentState));
						((NumOpExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(173);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(174);
						_la = _input.LA(1);
						if ( !(_la==MUL || _la==DIV) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(175);
						((NumOpExprContext)_localctx).right = expression(9);
						}
						break;
					case 2:
						{
						_localctx = new NumOpExprContext(new ExpressionContext(_parentctx, _parentState));
						((NumOpExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(176);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(177);
						match(PLUS);
						setState(178);
						((NumOpExprContext)_localctx).right = expression(8);
						}
						break;
					case 3:
						{
						_localctx = new NumOpExprContext(new ExpressionContext(_parentctx, _parentState));
						((NumOpExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(179);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(180);
						match(MINUS);
						setState(181);
						((NumOpExprContext)_localctx).right = expression(7);
						}
						break;
					case 4:
						{
						_localctx = new CompOpExprContext(new ExpressionContext(_parentctx, _parentState));
						((CompOpExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(182);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(183);
						compOp();
						setState(184);
						((CompOpExprContext)_localctx).right = expression(6);
						}
						break;
					case 5:
						{
						_localctx = new BoolOpExprContext(new ExpressionContext(_parentctx, _parentState));
						((BoolOpExprContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(186);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(187);
						boolOp();
						setState(188);
						((BoolOpExprContext)_localctx).right = expression(5);
						}
						break;
					}
					} 
				}
				setState(194);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MethodNameContext extends ParserRuleContext {
		public TerminalNode ArrMethod() { return getToken(SimplifyParser.ArrMethod, 0); }
		public TerminalNode DictMethod() { return getToken(SimplifyParser.DictMethod, 0); }
		public TerminalNode StrMethod() { return getToken(SimplifyParser.StrMethod, 0); }
		public MethodNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterMethodName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitMethodName(this);
		}
	}

	public final MethodNameContext methodName() throws RecognitionException {
		MethodNameContext _localctx = new MethodNameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_methodName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3670016L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompOpContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(SimplifyParser.EQ, 0); }
		public TerminalNode NE() { return getToken(SimplifyParser.NE, 0); }
		public TerminalNode GT() { return getToken(SimplifyParser.GT, 0); }
		public TerminalNode GE() { return getToken(SimplifyParser.GE, 0); }
		public TerminalNode LT() { return getToken(SimplifyParser.LT, 0); }
		public TerminalNode LE() { return getToken(SimplifyParser.LE, 0); }
		public CompOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterCompOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitCompOp(this);
		}
	}

	public final CompOpContext compOp() throws RecognitionException {
		CompOpContext _localctx = new CompOpContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_compOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 277076930199552L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BoolOpContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(SimplifyParser.AND, 0); }
		public TerminalNode OR() { return getToken(SimplifyParser.OR, 0); }
		public BoolOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterBoolOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitBoolOp(this);
		}
	}

	public final BoolOpContext boolOp() throws RecognitionException {
		BoolOpContext _localctx = new BoolOpContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_boolOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalContext extends ParserRuleContext {
		public IfBlockContext ifBlock() {
			return getRuleContext(IfBlockContext.class,0);
		}
		public ElseBlockContext elseBlock() {
			return getRuleContext(ElseBlockContext.class,0);
		}
		public List<OrBlockContext> orBlock() {
			return getRuleContexts(OrBlockContext.class);
		}
		public OrBlockContext orBlock(int i) {
			return getRuleContext(OrBlockContext.class,i);
		}
		public ConditionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterConditional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitConditional(this);
		}
	}

	public final ConditionalContext conditional() throws RecognitionException {
		ConditionalContext _localctx = new ConditionalContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_conditional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			ifBlock();
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Cond_Or) {
				{
				{
				setState(202);
				orBlock();
				}
				}
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(208);
			elseBlock();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfBlockContext extends ParserRuleContext {
		public TerminalNode Cond_If() { return getToken(SimplifyParser.Cond_If, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Exist() { return getToken(SimplifyParser.Exist, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public IfBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterIfBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitIfBlock(this);
		}
	}

	public final IfBlockContext ifBlock() throws RecognitionException {
		IfBlockContext _localctx = new IfBlockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_ifBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(Cond_If);
			setState(211);
			match(T__3);
			setState(212);
			expression(0);
			setState(213);
			match(T__4);
			setState(214);
			match(Exist);
			setState(215);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OrBlockContext extends ParserRuleContext {
		public TerminalNode Cond_Or() { return getToken(SimplifyParser.Cond_Or, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Exist() { return getToken(SimplifyParser.Exist, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public OrBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterOrBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitOrBlock(this);
		}
	}

	public final OrBlockContext orBlock() throws RecognitionException {
		OrBlockContext _localctx = new OrBlockContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_orBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(Cond_Or);
			setState(218);
			match(T__3);
			setState(219);
			expression(0);
			setState(220);
			match(T__4);
			setState(221);
			match(Exist);
			setState(222);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElseBlockContext extends ParserRuleContext {
		public TerminalNode Cond_Else() { return getToken(SimplifyParser.Cond_Else, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ElseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterElseBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitElseBlock(this);
		}
	}

	public final ElseBlockContext elseBlock() throws RecognitionException {
		ElseBlockContext _localctx = new ElseBlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_elseBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(Cond_Else);
			setState(225);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LoopContext extends ParserRuleContext {
		public ForLoopContext forLoop() {
			return getRuleContext(ForLoopContext.class,0);
		}
		public WhileLoopContext whileLoop() {
			return getRuleContext(WhileLoopContext.class,0);
		}
		public LoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_loop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitLoop(this);
		}
	}

	public final LoopContext loop() throws RecognitionException {
		LoopContext _localctx = new LoopContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_loop);
		try {
			setState(229);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case F_Loop:
				enterOuterAlt(_localctx, 1);
				{
				setState(227);
				forLoop();
				}
				break;
			case W_Loop:
				enterOuterAlt(_localctx, 2);
				{
				setState(228);
				whileLoop();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForLoopContext extends ParserRuleContext {
		public TerminalNode F_Loop() { return getToken(SimplifyParser.F_Loop, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(SimplifyParser.ASSIGN, 0); }
		public TerminalNode Number() { return getToken(SimplifyParser.Number, 0); }
		public TerminalNode To() { return getToken(SimplifyParser.To, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Exist() { return getToken(SimplifyParser.Exist, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ForLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forLoop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterForLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitForLoop(this);
		}
	}

	public final ForLoopContext forLoop() throws RecognitionException {
		ForLoopContext _localctx = new ForLoopContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_forLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			match(F_Loop);
			setState(232);
			match(T__3);
			setState(233);
			type();
			setState(234);
			match(ID);
			setState(235);
			match(ASSIGN);
			setState(236);
			match(Number);
			setState(237);
			match(To);
			setState(238);
			expression(0);
			setState(239);
			match(T__4);
			setState(240);
			match(Exist);
			setState(241);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileLoopContext extends ParserRuleContext {
		public TerminalNode W_Loop() { return getToken(SimplifyParser.W_Loop, 0); }
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public TerminalNode To() { return getToken(SimplifyParser.To, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Exist() { return getToken(SimplifyParser.Exist, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public WhileLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileLoop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterWhileLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitWhileLoop(this);
		}
	}

	public final WhileLoopContext whileLoop() throws RecognitionException {
		WhileLoopContext _localctx = new WhileLoopContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_whileLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(W_Loop);
			setState(244);
			match(T__3);
			setState(245);
			match(ID);
			setState(246);
			match(To);
			setState(247);
			expression(0);
			setState(248);
			match(T__4);
			setState(249);
			match(Exist);
			setState(250);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public List<InnerStatementContext> innerStatement() {
			return getRuleContexts(InnerStatementContext.class);
		}
		public InnerStatementContext innerStatement(int i) {
			return getRuleContext(InnerStatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			match(T__9);
			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108087474458302098L) != 0)) {
				{
				{
				setState(253);
				innerStatement();
				}
				}
				setState(258);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(259);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncExprContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public FuncExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterFuncExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitFuncExpr(this);
		}
	}

	public final FuncExprContext funcExpr() throws RecognitionException {
		FuncExprContext _localctx = new FuncExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_funcExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(ID);
			setState(262);
			match(T__3);
			setState(271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1082868631186L) != 0)) {
				{
				setState(263);
				expression(0);
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(264);
					match(T__7);
					setState(265);
					expression(0);
					}
					}
					setState(270);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(273);
			match(T__4);
			setState(275);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(274);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode Func() { return getToken(SimplifyParser.Func, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public List<InnerStatementContext> innerStatement() {
			return getRuleContexts(InnerStatementContext.class);
		}
		public InnerStatementContext innerStatement(int i) {
			return getRuleContext(InnerStatementContext.class,i);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitFunction(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(277);
			match(Func);
			setState(278);
			type();
			setState(279);
			match(ID);
			setState(280);
			match(T__3);
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 264241152L) != 0)) {
				{
				setState(281);
				argumentList();
				}
			}

			setState(284);
			match(T__4);
			setState(285);
			match(T__9);
			setState(289);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 108087474458302098L) != 0)) {
				{
				{
				setState(286);
				innerStatement();
				}
				}
				setState(291);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(292);
			returnStatement();
			setState(293);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentListContext extends ParserRuleContext {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295);
			argument();
			setState(300);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(296);
				match(T__7);
				setState(297);
				argument();
				}
				}
				setState(302);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			type();
			setState(304);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode Return() { return getToken(SimplifyParser.Return, 0); }
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitReturnStatement(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			match(Return);
			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1082868631186L) != 0)) {
				{
				setState(307);
				expression(0);
				}
			}

			setState(310);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassInitContext extends ParserRuleContext {
		public TerminalNode Class() { return getToken(SimplifyParser.Class, 0); }
		public TerminalNode ID() { return getToken(SimplifyParser.ID, 0); }
		public ClassPropertyContext classProperty() {
			return getRuleContext(ClassPropertyContext.class,0);
		}
		public ClassInitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classInit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterClassInit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitClassInit(this);
		}
	}

	public final ClassInitContext classInit() throws RecognitionException {
		ClassInitContext _localctx = new ClassInitContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_classInit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(312);
			match(Class);
			setState(313);
			match(ID);
			setState(314);
			match(T__9);
			setState(315);
			classProperty();
			setState(316);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassPropertyContext extends ParserRuleContext {
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public ClassPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterClassProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitClassProperty(this);
		}
	}

	public final ClassPropertyContext classProperty() throws RecognitionException {
		ClassPropertyContext _localctx = new ClassPropertyContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_classProperty);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68983717888L) != 0)) {
				{
				setState(320);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Str:
				case Num:
				case Dec:
				case Bool:
				case Arr:
				case Dict:
					{
					setState(318);
					declaration();
					}
					break;
				case ID:
					{
					setState(319);
					assignment();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Func) {
				{
				{
				setState(325);
				function();
				}
				}
				setState(330);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ResultContext extends ParserRuleContext {
		public TerminalNode Output() { return getToken(SimplifyParser.Output, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public ResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitResult(this);
		}
	}

	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_result);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(Output);
			setState(332);
			match(T__3);
			setState(333);
			expression(0);
			setState(334);
			match(T__4);
			setState(335);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReadInputExprContext extends ParserRuleContext {
		public TerminalNode Input() { return getToken(SimplifyParser.Input, 0); }
		public TerminalNode String() { return getToken(SimplifyParser.String, 0); }
		public TerminalNode SEMI() { return getToken(SimplifyParser.SEMI, 0); }
		public ReadInputExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_readInputExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).enterReadInputExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SimplifyListener ) ((SimplifyListener)listener).exitReadInputExpr(this);
		}
	}

	public final ReadInputExprContext readInputExpr() throws RecognitionException {
		ReadInputExprContext _localctx = new ReadInputExprContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_readInputExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(Input);
			setState(338);
			match(T__3);
			setState(339);
			match(String);
			setState(340);
			match(T__4);
			setState(342);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				setState(341);
				match(SEMI);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 7:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u00018\u0159\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0001\u0000\u0004\u0000:\b\u0000\u000b\u0000\f\u0000;\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003"+
		"\u0002E\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003P\b"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\\\b"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005b\b"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0003\u0006k\b\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007|\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0005\u0007\u0088\b\u0007\n\u0007\f\u0007\u008b\t\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007"+
		"\u0099\b\u0007\n\u0007\f\u0007\u009c\t\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003"+
		"\u0007\u00ac\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0005\u0007\u00bf\b\u0007\n\u0007\f\u0007\u00c2\t\u0007\u0001\b"+
		"\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0005"+
		"\u000b\u00cc\b\u000b\n\u000b\f\u000b\u00cf\t\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0003\u000f\u00e6\b\u000f\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001"+
		"\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0005\u0012\u00ff\b\u0012\n"+
		"\u0012\f\u0012\u0102\t\u0012\u0001\u0012\u0001\u0012\u0001\u0013\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u010b\b\u0013\n"+
		"\u0013\f\u0013\u010e\t\u0013\u0003\u0013\u0110\b\u0013\u0001\u0013\u0001"+
		"\u0013\u0003\u0013\u0114\b\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0003\u0014\u011b\b\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0005\u0014\u0120\b\u0014\n\u0014\f\u0014\u0123\t\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005\u0015"+
		"\u012b\b\u0015\n\u0015\f\u0015\u012e\t\u0015\u0001\u0016\u0001\u0016\u0001"+
		"\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u0135\b\u0017\u0001\u0017\u0001"+
		"\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0018\u0001\u0019\u0001\u0019\u0005\u0019\u0141\b\u0019\n\u0019\f\u0019"+
		"\u0144\t\u0019\u0001\u0019\u0005\u0019\u0147\b\u0019\n\u0019\f\u0019\u014a"+
		"\t\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003"+
		"\u001b\u0157\b\u001b\u0001\u001b\u0000\u0001\u000e\u001c\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.0246\u0000\u0005\u0001\u000078\u0001\u000012\u0001\u0000\u0013\u0015"+
		"\u0001\u0000*/\u0001\u000056\u0171\u00009\u0001\u0000\u0000\u0000\u0002"+
		"?\u0001\u0000\u0000\u0000\u0004D\u0001\u0000\u0000\u0000\u0006O\u0001"+
		"\u0000\u0000\u0000\b[\u0001\u0000\u0000\u0000\n]\u0001\u0000\u0000\u0000"+
		"\fe\u0001\u0000\u0000\u0000\u000e\u00ab\u0001\u0000\u0000\u0000\u0010"+
		"\u00c3\u0001\u0000\u0000\u0000\u0012\u00c5\u0001\u0000\u0000\u0000\u0014"+
		"\u00c7\u0001\u0000\u0000\u0000\u0016\u00c9\u0001\u0000\u0000\u0000\u0018"+
		"\u00d2\u0001\u0000\u0000\u0000\u001a\u00d9\u0001\u0000\u0000\u0000\u001c"+
		"\u00e0\u0001\u0000\u0000\u0000\u001e\u00e5\u0001\u0000\u0000\u0000 \u00e7"+
		"\u0001\u0000\u0000\u0000\"\u00f3\u0001\u0000\u0000\u0000$\u00fc\u0001"+
		"\u0000\u0000\u0000&\u0105\u0001\u0000\u0000\u0000(\u0115\u0001\u0000\u0000"+
		"\u0000*\u0127\u0001\u0000\u0000\u0000,\u012f\u0001\u0000\u0000\u0000."+
		"\u0132\u0001\u0000\u0000\u00000\u0138\u0001\u0000\u0000\u00002\u0142\u0001"+
		"\u0000\u0000\u00004\u014b\u0001\u0000\u0000\u00006\u0151\u0001\u0000\u0000"+
		"\u00008:\u0003\u0004\u0002\u000098\u0001\u0000\u0000\u0000:;\u0001\u0000"+
		"\u0000\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0001"+
		"\u0000\u0000\u0000=>\u0005\u0000\u0000\u0001>\u0001\u0001\u0000\u0000"+
		"\u0000?@\u0007\u0000\u0000\u0000@\u0003\u0001\u0000\u0000\u0000AE\u0003"+
		"(\u0014\u0000BE\u00030\u0018\u0000CE\u0003\u0006\u0003\u0000DA\u0001\u0000"+
		"\u0000\u0000DB\u0001\u0000\u0000\u0000DC\u0001\u0000\u0000\u0000E\u0005"+
		"\u0001\u0000\u0000\u0000FP\u0003\f\u0006\u0000GP\u0003\u0002\u0001\u0000"+
		"HP\u0003\u0016\u000b\u0000IP\u0003\n\u0005\u0000JP\u0003\u001e\u000f\u0000"+
		"KP\u00034\u001a\u0000LM\u0003\u000e\u0007\u0000MN\u0005)\u0000\u0000N"+
		"P\u0001\u0000\u0000\u0000OF\u0001\u0000\u0000\u0000OG\u0001\u0000\u0000"+
		"\u0000OH\u0001\u0000\u0000\u0000OI\u0001\u0000\u0000\u0000OJ\u0001\u0000"+
		"\u0000\u0000OK\u0001\u0000\u0000\u0000OL\u0001\u0000\u0000\u0000P\u0007"+
		"\u0001\u0000\u0000\u0000Q\\\u0005\u0016\u0000\u0000R\\\u0005\u0017\u0000"+
		"\u0000S\\\u0005\u0018\u0000\u0000T\\\u0005\u0019\u0000\u0000UV\u0005\u001a"+
		"\u0000\u0000VW\u0005\u0001\u0000\u0000WX\u0003\b\u0004\u0000XY\u0005\u0002"+
		"\u0000\u0000Y\\\u0001\u0000\u0000\u0000Z\\\u0005\u001b\u0000\u0000[Q\u0001"+
		"\u0000\u0000\u0000[R\u0001\u0000\u0000\u0000[S\u0001\u0000\u0000\u0000"+
		"[T\u0001\u0000\u0000\u0000[U\u0001\u0000\u0000\u0000[Z\u0001\u0000\u0000"+
		"\u0000\\\t\u0001\u0000\u0000\u0000]^\u0003\b\u0004\u0000^a\u0005$\u0000"+
		"\u0000_`\u00050\u0000\u0000`b\u0003\u000e\u0007\u0000a_\u0001\u0000\u0000"+
		"\u0000ab\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0005)\u0000"+
		"\u0000d\u000b\u0001\u0000\u0000\u0000ej\u0005$\u0000\u0000fg\u0005\u0001"+
		"\u0000\u0000gh\u0003\u000e\u0007\u0000hi\u0005\u0002\u0000\u0000ik\u0001"+
		"\u0000\u0000\u0000jf\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000"+
		"kl\u0001\u0000\u0000\u0000lm\u00050\u0000\u0000mn\u0003\u000e\u0007\u0000"+
		"no\u0005)\u0000\u0000o\r\u0001\u0000\u0000\u0000pq\u0006\u0007\uffff\uffff"+
		"\u0000qr\u0005$\u0000\u0000rs\u0005\u0001\u0000\u0000st\u0003\u000e\u0007"+
		"\u0000tu\u0005\u0002\u0000\u0000u\u00ac\u0001\u0000\u0000\u0000vw\u0005"+
		"$\u0000\u0000wx\u0005\u0003\u0000\u0000xy\u0003\u0010\b\u0000y{\u0005"+
		"\u0004\u0000\u0000z|\u0003\u000e\u0007\u0000{z\u0001\u0000\u0000\u0000"+
		"{|\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000\u0000}~\u0005\u0005\u0000"+
		"\u0000~\u00ac\u0001\u0000\u0000\u0000\u007f\u0080\u0005\'\u0000\u0000"+
		"\u0080\u0081\u0005\u0006\u0000\u0000\u0081\u00ac\u0003\u000e\u0007\u0013"+
		"\u0082\u00ac\u0005\u0007\u0000\u0000\u0083\u0084\u0005\u0001\u0000\u0000"+
		"\u0084\u0089\u0003\u000e\u0007\u0000\u0085\u0086\u0005\b\u0000\u0000\u0086"+
		"\u0088\u0003\u000e\u0007\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0088"+
		"\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089"+
		"\u008a\u0001\u0000\u0000\u0000\u008a\u008c\u0001\u0000\u0000\u0000\u008b"+
		"\u0089\u0001\u0000\u0000\u0000\u008c\u008d\u0005\u0002\u0000\u0000\u008d"+
		"\u00ac\u0001\u0000\u0000\u0000\u008e\u00ac\u0005\t\u0000\u0000\u008f\u0090"+
		"\u0005\n\u0000\u0000\u0090\u0091\u0003\u000e\u0007\u0000\u0091\u0092\u0005"+
		"\u0006\u0000\u0000\u0092\u009a\u0003\u000e\u0007\u0000\u0093\u0094\u0005"+
		"\b\u0000\u0000\u0094\u0095\u0003\u000e\u0007\u0000\u0095\u0096\u0005\u0006"+
		"\u0000\u0000\u0096\u0097\u0003\u000e\u0007\u0000\u0097\u0099\u0001\u0000"+
		"\u0000\u0000\u0098\u0093\u0001\u0000\u0000\u0000\u0099\u009c\u0001\u0000"+
		"\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000"+
		"\u0000\u0000\u009b\u009d\u0001\u0000\u0000\u0000\u009c\u009a\u0001\u0000"+
		"\u0000\u0000\u009d\u009e\u0005\u000b\u0000\u0000\u009e\u00ac\u0001\u0000"+
		"\u0000\u0000\u009f\u00ac\u0005$\u0000\u0000\u00a0\u00ac\u0005%\u0000\u0000"+
		"\u00a1\u00ac\u0005&\u0000\u0000\u00a2\u00ac\u0005\'\u0000\u0000\u00a3"+
		"\u00ac\u0003&\u0013\u0000\u00a4\u00ac\u00036\u001b\u0000\u00a5\u00ac\u0005"+
		"\"\u0000\u0000\u00a6\u00ac\u0005#\u0000\u0000\u00a7\u00a8\u0005\u0004"+
		"\u0000\u0000\u00a8\u00a9\u0003\u000e\u0007\u0000\u00a9\u00aa\u0005\u0005"+
		"\u0000\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000\u00abp\u0001\u0000\u0000"+
		"\u0000\u00abv\u0001\u0000\u0000\u0000\u00ab\u007f\u0001\u0000\u0000\u0000"+
		"\u00ab\u0082\u0001\u0000\u0000\u0000\u00ab\u0083\u0001\u0000\u0000\u0000"+
		"\u00ab\u008e\u0001\u0000\u0000\u0000\u00ab\u008f\u0001\u0000\u0000\u0000"+
		"\u00ab\u009f\u0001\u0000\u0000\u0000\u00ab\u00a0\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a1\u0001\u0000\u0000\u0000\u00ab\u00a2\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a3\u0001\u0000\u0000\u0000\u00ab\u00a4\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a5\u0001\u0000\u0000\u0000\u00ab\u00a6\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a7\u0001\u0000\u0000\u0000\u00ac\u00c0\u0001\u0000\u0000\u0000"+
		"\u00ad\u00ae\n\b\u0000\u0000\u00ae\u00af\u0007\u0001\u0000\u0000\u00af"+
		"\u00bf\u0003\u000e\u0007\t\u00b0\u00b1\n\u0007\u0000\u0000\u00b1\u00b2"+
		"\u00053\u0000\u0000\u00b2\u00bf\u0003\u000e\u0007\b\u00b3\u00b4\n\u0006"+
		"\u0000\u0000\u00b4\u00b5\u00054\u0000\u0000\u00b5\u00bf\u0003\u000e\u0007"+
		"\u0007\u00b6\u00b7\n\u0005\u0000\u0000\u00b7\u00b8\u0003\u0012\t\u0000"+
		"\u00b8\u00b9\u0003\u000e\u0007\u0006\u00b9\u00bf\u0001\u0000\u0000\u0000"+
		"\u00ba\u00bb\n\u0004\u0000\u0000\u00bb\u00bc\u0003\u0014\n\u0000\u00bc"+
		"\u00bd\u0003\u000e\u0007\u0005\u00bd\u00bf\u0001\u0000\u0000\u0000\u00be"+
		"\u00ad\u0001\u0000\u0000\u0000\u00be\u00b0\u0001\u0000\u0000\u0000\u00be"+
		"\u00b3\u0001\u0000\u0000\u0000\u00be\u00b6\u0001\u0000\u0000\u0000\u00be"+
		"\u00ba\u0001\u0000\u0000\u0000\u00bf\u00c2\u0001\u0000\u0000\u0000\u00c0"+
		"\u00be\u0001\u0000\u0000\u0000\u00c0\u00c1\u0001\u0000\u0000\u0000\u00c1"+
		"\u000f\u0001\u0000\u0000\u0000\u00c2\u00c0\u0001\u0000\u0000\u0000\u00c3"+
		"\u00c4\u0007\u0002\u0000\u0000\u00c4\u0011\u0001\u0000\u0000\u0000\u00c5"+
		"\u00c6\u0007\u0003\u0000\u0000\u00c6\u0013\u0001\u0000\u0000\u0000\u00c7"+
		"\u00c8\u0007\u0004\u0000\u0000\u00c8\u0015\u0001\u0000\u0000\u0000\u00c9"+
		"\u00cd\u0003\u0018\f\u0000\u00ca\u00cc\u0003\u001a\r\u0000\u00cb\u00ca"+
		"\u0001\u0000\u0000\u0000\u00cc\u00cf\u0001\u0000\u0000\u0000\u00cd\u00cb"+
		"\u0001\u0000\u0000\u0000\u00cd\u00ce\u0001\u0000\u0000\u0000\u00ce\u00d0"+
		"\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000\u0000\u0000\u00d0\u00d1"+
		"\u0003\u001c\u000e\u0000\u00d1\u0017\u0001\u0000\u0000\u0000\u00d2\u00d3"+
		"\u0005\f\u0000\u0000\u00d3\u00d4\u0005\u0004\u0000\u0000\u00d4\u00d5\u0003"+
		"\u000e\u0007\u0000\u00d5\u00d6\u0005\u0005\u0000\u0000\u00d6\u00d7\u0005"+
		"\u001e\u0000\u0000\u00d7\u00d8\u0003$\u0012\u0000\u00d8\u0019\u0001\u0000"+
		"\u0000\u0000\u00d9\u00da\u0005\u000e\u0000\u0000\u00da\u00db\u0005\u0004"+
		"\u0000\u0000\u00db\u00dc\u0003\u000e\u0007\u0000\u00dc\u00dd\u0005\u0005"+
		"\u0000\u0000\u00dd\u00de\u0005\u001e\u0000\u0000\u00de\u00df\u0003$\u0012"+
		"\u0000\u00df\u001b\u0001\u0000\u0000\u0000\u00e0\u00e1\u0005\r\u0000\u0000"+
		"\u00e1\u00e2\u0003$\u0012\u0000\u00e2\u001d\u0001\u0000\u0000\u0000\u00e3"+
		"\u00e6\u0003 \u0010\u0000\u00e4\u00e6\u0003\"\u0011\u0000\u00e5\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e5\u00e4\u0001\u0000\u0000\u0000\u00e6\u001f"+
		"\u0001\u0000\u0000\u0000\u00e7\u00e8\u0005\u000f\u0000\u0000\u00e8\u00e9"+
		"\u0005\u0004\u0000\u0000\u00e9\u00ea\u0003\b\u0004\u0000\u00ea\u00eb\u0005"+
		"$\u0000\u0000\u00eb\u00ec\u00050\u0000\u0000\u00ec\u00ed\u0005%\u0000"+
		"\u0000\u00ed\u00ee\u0005\u001f\u0000\u0000\u00ee\u00ef\u0003\u000e\u0007"+
		"\u0000\u00ef\u00f0\u0005\u0005\u0000\u0000\u00f0\u00f1\u0005\u001e\u0000"+
		"\u0000\u00f1\u00f2\u0003$\u0012\u0000\u00f2!\u0001\u0000\u0000\u0000\u00f3"+
		"\u00f4\u0005\u0010\u0000\u0000\u00f4\u00f5\u0005\u0004\u0000\u0000\u00f5"+
		"\u00f6\u0005$\u0000\u0000\u00f6\u00f7\u0005\u001f\u0000\u0000\u00f7\u00f8"+
		"\u0003\u000e\u0007\u0000\u00f8\u00f9\u0005\u0005\u0000\u0000\u00f9\u00fa"+
		"\u0005\u001e\u0000\u0000\u00fa\u00fb\u0003$\u0012\u0000\u00fb#\u0001\u0000"+
		"\u0000\u0000\u00fc\u0100\u0005\n\u0000\u0000\u00fd\u00ff\u0003\u0006\u0003"+
		"\u0000\u00fe\u00fd\u0001\u0000\u0000\u0000\u00ff\u0102\u0001\u0000\u0000"+
		"\u0000\u0100\u00fe\u0001\u0000\u0000\u0000\u0100\u0101\u0001\u0000\u0000"+
		"\u0000\u0101\u0103\u0001\u0000\u0000\u0000\u0102\u0100\u0001\u0000\u0000"+
		"\u0000\u0103\u0104\u0005\u000b\u0000\u0000\u0104%\u0001\u0000\u0000\u0000"+
		"\u0105\u0106\u0005$\u0000\u0000\u0106\u010f\u0005\u0004\u0000\u0000\u0107"+
		"\u010c\u0003\u000e\u0007\u0000\u0108\u0109\u0005\b\u0000\u0000\u0109\u010b"+
		"\u0003\u000e\u0007\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010b\u010e"+
		"\u0001\u0000\u0000\u0000\u010c\u010a\u0001\u0000\u0000\u0000\u010c\u010d"+
		"\u0001\u0000\u0000\u0000\u010d\u0110\u0001\u0000\u0000\u0000\u010e\u010c"+
		"\u0001\u0000\u0000\u0000\u010f\u0107\u0001\u0000\u0000\u0000\u010f\u0110"+
		"\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u0113"+
		"\u0005\u0005\u0000\u0000\u0112\u0114\u0005)\u0000\u0000\u0113\u0112\u0001"+
		"\u0000\u0000\u0000\u0113\u0114\u0001\u0000\u0000\u0000\u0114\'\u0001\u0000"+
		"\u0000\u0000\u0115\u0116\u0005\u0011\u0000\u0000\u0116\u0117\u0003\b\u0004"+
		"\u0000\u0117\u0118\u0005$\u0000\u0000\u0118\u011a\u0005\u0004\u0000\u0000"+
		"\u0119\u011b\u0003*\u0015\u0000\u011a\u0119\u0001\u0000\u0000\u0000\u011a"+
		"\u011b\u0001\u0000\u0000\u0000\u011b\u011c\u0001\u0000\u0000\u0000\u011c"+
		"\u011d\u0005\u0005\u0000\u0000\u011d\u0121\u0005\n\u0000\u0000\u011e\u0120"+
		"\u0003\u0006\u0003\u0000\u011f\u011e\u0001\u0000\u0000\u0000\u0120\u0123"+
		"\u0001\u0000\u0000\u0000\u0121\u011f\u0001\u0000\u0000\u0000\u0121\u0122"+
		"\u0001\u0000\u0000\u0000\u0122\u0124\u0001\u0000\u0000\u0000\u0123\u0121"+
		"\u0001\u0000\u0000\u0000\u0124\u0125\u0003.\u0017\u0000\u0125\u0126\u0005"+
		"\u000b\u0000\u0000\u0126)\u0001\u0000\u0000\u0000\u0127\u012c\u0003,\u0016"+
		"\u0000\u0128\u0129\u0005\b\u0000\u0000\u0129\u012b\u0003,\u0016\u0000"+
		"\u012a\u0128\u0001\u0000\u0000\u0000\u012b\u012e\u0001\u0000\u0000\u0000"+
		"\u012c\u012a\u0001\u0000\u0000\u0000\u012c\u012d\u0001\u0000\u0000\u0000"+
		"\u012d+\u0001\u0000\u0000\u0000\u012e\u012c\u0001\u0000\u0000\u0000\u012f"+
		"\u0130\u0003\b\u0004\u0000\u0130\u0131\u0005$\u0000\u0000\u0131-\u0001"+
		"\u0000\u0000\u0000\u0132\u0134\u0005 \u0000\u0000\u0133\u0135\u0003\u000e"+
		"\u0007\u0000\u0134\u0133\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000"+
		"\u0000\u0000\u0135\u0136\u0001\u0000\u0000\u0000\u0136\u0137\u0005)\u0000"+
		"\u0000\u0137/\u0001\u0000\u0000\u0000\u0138\u0139\u0005\u0012\u0000\u0000"+
		"\u0139\u013a\u0005$\u0000\u0000\u013a\u013b\u0005\n\u0000\u0000\u013b"+
		"\u013c\u00032\u0019\u0000\u013c\u013d\u0005\u000b\u0000\u0000\u013d1\u0001"+
		"\u0000\u0000\u0000\u013e\u0141\u0003\n\u0005\u0000\u013f\u0141\u0003\f"+
		"\u0006\u0000\u0140\u013e\u0001\u0000\u0000\u0000\u0140\u013f\u0001\u0000"+
		"\u0000\u0000\u0141\u0144\u0001\u0000\u0000\u0000\u0142\u0140\u0001\u0000"+
		"\u0000\u0000\u0142\u0143\u0001\u0000\u0000\u0000\u0143\u0148\u0001\u0000"+
		"\u0000\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0145\u0147\u0003(\u0014"+
		"\u0000\u0146\u0145\u0001\u0000\u0000\u0000\u0147\u014a\u0001\u0000\u0000"+
		"\u0000\u0148\u0146\u0001\u0000\u0000\u0000\u0148\u0149\u0001\u0000\u0000"+
		"\u0000\u01493\u0001\u0000\u0000\u0000\u014a\u0148\u0001\u0000\u0000\u0000"+
		"\u014b\u014c\u0005\u001c\u0000\u0000\u014c\u014d\u0005\u0004\u0000\u0000"+
		"\u014d\u014e\u0003\u000e\u0007\u0000\u014e\u014f\u0005\u0005\u0000\u0000"+
		"\u014f\u0150\u0005)\u0000\u0000\u01505\u0001\u0000\u0000\u0000\u0151\u0152"+
		"\u0005\u001d\u0000\u0000\u0152\u0153\u0005\u0004\u0000\u0000\u0153\u0154"+
		"\u0005\'\u0000\u0000\u0154\u0156\u0005\u0005\u0000\u0000\u0155\u0157\u0005"+
		")\u0000\u0000\u0156\u0155\u0001\u0000\u0000\u0000\u0156\u0157\u0001\u0000"+
		"\u0000\u0000\u01577\u0001\u0000\u0000\u0000\u001a;DO[aj{\u0089\u009a\u00ab"+
		"\u00be\u00c0\u00cd\u00e5\u0100\u010c\u010f\u0113\u011a\u0121\u012c\u0134"+
		"\u0140\u0142\u0148\u0156";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}