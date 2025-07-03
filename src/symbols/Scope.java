package symbols;

import java.util.Map;

public interface Scope {
    String getScopeName();
    Scope getEnclosingScope();
    void define(Symbol sym);
    Symbol resolve(String name);
    Map<String, Symbol> getSymbols();
    void print(String indent);
}
