    import ErrorListener.*;
    import antlr.SimplifyLexer;
    import antlr.SimplifyParser;
    import org.antlr.v4.runtime.*;
    import org.antlr.v4.runtime.tree.*;
    import symbols.SymbolTableListener;

    import java.io.IOException;

    public class Main {
        public static void main(String[] args) throws IOException {
            /* Compile program:
            1. java -jar lib/antlr-4.13.2-complete.jar -o src/antlr -package antlr src/antlr/Simplify.g4
            2. javac -cp "lib/antlr-4.13.2-complete.jar" -d out src/antlr/*.java src/Main.java src/ErrorListener/* src/symbols/*.java
            3. For MacOS/Linux: use : instead of ; between .jar;out
               Window: java -cp "lib/antlr-4.13.2-complete.jar;out" Main stage input.txt
               MacOS/Linux: java -cp "lib/antlr-4.13.2-complete.jar:out" Main stage input.txt
               Ex: java -cp "lib/antlr-4.13.2-complete.jar;out" Main lex src\Language_Definition\Sem_analytic.txt
             */
            if(args.length != 2){
                System.err.println("Invalid Command");
                System.out.println("Usage: java Main <stage> <input_file>");
                System.out.println("Stages: lex, parse, sem");
                return;
            }

            String stage = args[0];
            String input_file = args[1];
            if (!stage.matches("lex|parse|sem")) {
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
//            parser.setErrorHandler(new CustomErrorStrategy());

            if (stage.equals("lex")) {
                printTokens(simplifyLexer);
                if (LexerErrorListener.hasErrors()) {
                    System.err.println("Lexer Errors Found:");
                    LexerErrorListener.printErrors();
                }
                return;
            }else if (stage.equals("parse")) {
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
            }else if(stage.equals("sem")){
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
                return;
            }
        }

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
