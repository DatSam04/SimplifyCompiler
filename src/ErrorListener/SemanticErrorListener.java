package ErrorListener;

import java.util.ArrayList;
import java.util.List;

public class SemanticErrorListener {
    private static final List<String> semanticErrors = new ArrayList<>();

    public static boolean hasErrors(){
        return !semanticErrors.isEmpty();
    }

    public static void report(String message) {
        semanticErrors.add(message);
    }

    public static void printErrors() {
        if(!semanticErrors.isEmpty()) {
            for (String error : semanticErrors) {
                System.err.println(error);
            }
        }
    }
}
