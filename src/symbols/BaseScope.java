package symbols;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseScope implements Scope{
    protected Scope enclosingScope;
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    protected List<Scope> nestedScopes = new ArrayList<>();
    protected String scopeName;

    public BaseScope(Scope enclosingScope, String scopeName) {
        this.enclosingScope = enclosingScope;
        this.scopeName = scopeName;
    }

    @Override
    public void define(Symbol newSymbol){
        symbols.put(newSymbol.name, newSymbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol s = symbols.get(name);
        if (s != null) return s;
        if (enclosingScope != null) return enclosingScope.resolve(name);
        return null;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

    @Override
    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void addNestedScope(Scope scope) {
        nestedScopes.add(scope);
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "{");
        System.out.println(indent + "  \"scope\": \"" + getScopeName() + "\",");

        System.out.println(indent + "  \"symbols\": [");
        int count = 0;
        for (Symbol s : symbols.values()) {
            System.out.print(indent + "    { \"name\": \"" + s.getName() + "\", \"type\": \"" + s.getType() + "\" }");
            count++;
            System.out.println(count < symbols.size() ? "," : "");
        }
        System.out.println(indent + "  ]");

        // Print nested scopes (both from symbols AND anonymous (conditional and loop))
        if (!nestedScopes.isEmpty() || containsSymbolScopes()) {
            System.out.println(indent + "  ,\"nestedScopes\": [");

            int nestedPrinted = 0;
            int totalNested = nestedScopes.size() + symbolScopeCount();

            // Print anonymous/nested block scopes
            for (Scope nested : nestedScopes) {
                nested.print(indent + "    ");
                nestedPrinted++;
                if (nestedPrinted < totalNested) {
                    System.out.println(indent + "    ,");
                }
            }

            // Print scopes attached to symbols (like function bodies)
            for (Symbol s : symbols.values()) {
                if (s.getScope() != null) {
                    s.getScope().print(indent + "    ");
                    nestedPrinted++;
                    if (nestedPrinted < totalNested) {
                        System.out.println(indent + "    ,");
                    }
                }
            }

            System.out.println(indent + "  ]");
        }

        System.out.println(indent + "}");
    }

    private boolean containsSymbolScopes() {
        for (Symbol s : symbols.values()) {
            if (s.getScope() != null) return true;
        }
        return false;
    }

    private int symbolScopeCount() {
        int count = 0;
        for (Symbol s : symbols.values()) {
            if (s.getScope() != null) count++;
        }
        return count;
    }
}
