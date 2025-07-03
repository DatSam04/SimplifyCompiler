package ErrorListener;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.*;

public class CustomErrorStrategy extends DefaultErrorStrategy {
    private static final ParserErrorListener parserErrorListener = new ParserErrorListener();

    @Override
    public void reportError(Parser recognizer, RecognitionException e) {
        // Report the error but don't throw exceptions or halt the parser
        if (inErrorRecoveryMode(recognizer)) {
            return; // Already handled
        }
        beginErrorCondition(recognizer);

        String msg;
        if (e instanceof NoViableAltException) {
            Token token = e.getOffendingToken();
            String text = token.getText();
            msg = "no viable alternative at input '" + escapeWSAndQuote(text) + "'";
        } else if (e instanceof InputMismatchException) {
            Token token = e.getOffendingToken();
            String expected = e.getExpectedTokens().toString(recognizer.getVocabulary());
            msg = "mismatched input '" + escapeWSAndQuote(token.getText()) + "' expecting " + expected;
        } else if (e instanceof FailedPredicateException) {
            msg = "rule " + recognizer.getRuleNames()[recognizer.getContext().getRuleIndex()]
                    + " failed predicate: {" + ((FailedPredicateException)e).getPredicate() + "}?";
        } else {
            msg = "unknown recognition error: " + e.getClass().getSimpleName();
        }

        Token offendingToken = e.getOffendingToken();
        recognizer.notifyErrorListeners(offendingToken, msg, e);
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        // Skip over erroneous token and continue parsing
        InputMismatchException e = new InputMismatchException(recognizer);
        reportError(recognizer, e);
        return getMissingSymbol(recognizer);
    }

    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        // Skip the erroneous token and continue
        reportError(recognizer, e);
        super.recover(recognizer, e);
    }

    @Override
    protected String escapeWSAndQuote(String text) {
        if (text == null) return "<no text>";
        text = text.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        return "'" + text + "'";
    }
}
