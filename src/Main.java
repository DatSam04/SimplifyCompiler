    import ErrorListener.*;
    import antlr.SimplifyLexer;
    import antlr.SimplifyParser;
    import jvm_bytecode.SimplifyCompiler;
    import org.antlr.v4.runtime.*;
    import org.antlr.v4.runtime.tree.*;
    import symbols.SymbolTableListener;

    import java.io.IOException;

/* Compile program:
1. java -jar lib/antlr-4.13.2-complete.jar -o src/antlr -package antlr src/antlr/Simplify.g4
2. javac -cp "lib/antlr-4.13.2-complete.jar;lib/javassist.jar" -d out src/antlr/*.java src/Main.java src/ErrorListener/* src/symbols/*.java src/jvm_bytecode/*
3. Remove-Item -Path out/jvm_output/Main.class -Force (Window)
4. java -cp "lib/antlr-4.13.2-complete.jar;lib/javassist.jar;out" Main stage src/SingleFuncComp/Test.txt
5. javap -c -v -classpath out/jvm_output Main  (for reading bytecode and localVarTable)
Note: if no stage in command line flag, default stage is "codegen"
 */
    public class Main {
        public static void main(String[] args) throws IOException {
            if(args.length < 1 || args.length > 2){
                System.err.println("Invalid Command");
                System.out.println("Usage: java Main <stage> <input_file>");
                System.out.println("Stages: lex, parse, sem");
                return;
            }

            String stage;
            String input_file;

            if (args.length == 1) {
                stage = "codegen"; // default
                input_file = args[0];
            } else {
                stage = args[0];
                input_file = args[1];
            }

            if (!stage.matches("lex|parse|sem|codegen")) {
                System.err.println("Error: Invalid stage '" + stage + "'. Must be one of: lex, parse, sem");
                System.exit(1);
            } else if (!input_file.endsWith(".txt")) {
                System.err.println("Error: Input file must be a text file");
                System.exit(1);
            }

            CharStream charStream = CharStreams.fromFileName(input_file);
            SimplifyLexer simplifyLexer = new SimplifyLexer(charStream);
            simplifyLexer.removeErrorListeners();
            simplifyLexer.addErrorListener(new LexerErrorListener());

            CommonTokenStream commonTokens = new CommonTokenStream(simplifyLexer);
            SimplifyParser parser = new SimplifyParser(commonTokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new ParserErrorListener());

            if (stage.equals("lex")) {//Lexer stage
                printTokens(simplifyLexer);
                if (LexerErrorListener.hasErrors()) {
                    System.err.println("Lexer Errors Found:");
                    LexerErrorListener.printErrors();
                }
                return;
            }else if (stage.equals("parse")) { //Parser stage
                ParseTree tree = parser.program();
                System.out.println(printParseTree(tree, parser));
                if (LexerErrorListener.hasErrors()) {
                    System.err.println("Lexer Errors Found:");
                    LexerErrorListener.printErrors();
                }
                if (ParserErrorListener.hasErrors()) {
                    System.err.println("Parser Errors Found:");
                    ParserErrorListener.printErrors();
                }
                return;
            }else if(stage.equals("sem")){ //Sem stage
                ParseTree tree = parser.program();
                ParseTreeWalker walker = new ParseTreeWalker();
                SymbolTableListener listener = new SymbolTableListener();
                walker.walk(listener, tree);

                listener.printSymbolTables();
                if (LexerErrorListener.hasErrors()) {
                    System.err.println("\nLexer Errors:");
                    LexerErrorListener.printErrors();
                }

                if (ParserErrorListener.hasErrors()) {
                    System.err.println("\nParser Errors:");
                    ParserErrorListener.printErrors();
                }

                if (SemanticErrorListener.hasErrors()) {
                    System.err.println("\nSemantic Errors:");
                    listener.printSemanticErrors();
                }
            }else if(stage.equals("codegen")){ //Default stage
                SimplifyCompiler simplify = new SimplifyCompiler();
                ParseTree tree = parser.program();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(simplify, tree);

                simplify.generateClass();
            }
        }

        /// Format print tokens for lexer
        private static void printTokens(SimplifyLexer lexer) {
            Token token;
            while ((token = lexer.nextToken()).getType() != Token.EOF) {
                String tokenName = SimplifyLexer.VOCABULARY.getSymbolicName(token.getType());
                if (tokenName == null) {
                    tokenName = "INVALID";
                }
                System.out.printf(
                        "Token: %-15s Text: %-15s [start=%d, stop=%d]%n",
                        tokenName,
                        token.getText(),
                        token.getStartIndex(),
                        token.getStopIndex()
                );
            }
        }

        //Call printParserTree helper method
        private static String printParseTree(ParseTree tree, Parser parser){
            return printHelper(tree, parser, 0);
        }

        //Format ParseTree
        private static String printHelper(ParseTree tree, Parser parser, int indent){
            StringBuilder sb = new StringBuilder();
            String indentation = "  ".repeat(indent);

            if (tree instanceof TerminalNode) {
                Token token = ((TerminalNode) tree).getSymbol();
                String tokenName = parser.getVocabulary().getSymbolicName(token.getType());
                sb.append(indentation)
                        .append(tokenName != null ? tokenName : "INVALID")
                        .append(": ").append(token.getText()).append("\n");
            } else {
                String ruleName = parser.getRuleNames()[((RuleContext) tree).getRuleIndex()];
                sb.append(indentation).append(ruleName).append("\n");

                for (int i = 0; i < tree.getChildCount(); i++) {
                    sb.append(printHelper(tree.getChild(i), parser, indent + 1));
                }
            }

            return sb.toString();
        }
    }
