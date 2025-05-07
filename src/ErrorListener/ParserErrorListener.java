package ErrorListener;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class ParserErrorListener extends BaseErrorListener {
    public static final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        errors.add(String.format("Parser error at line %d:%d - %s", line, charPositionInLine, msg));
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static void printErrors() {
        errors.forEach(System.err::println);
    }
}
