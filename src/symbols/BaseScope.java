package symbols;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseScope implements Scope{
    protected Scope enclosingScope;
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
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

    @Override
    public void print(String indent) {
        System.out.println(indent + "{");
        System.out.println(indent + "  \"scope\": \"" + getScopeName() + "\",");
        int maxNameLen = 4;
        int maxTypeLen = 4;
        for (Symbol s : symbols.values()) {
            maxNameLen = Math.max(maxNameLen, s.getName().length());
            maxTypeLen = Math.max(maxTypeLen, s.getType().length());
        }

        System.out.println(indent + "  \"symbols\": [");
        int count = 0;
        int size = symbols.size();
        for (Symbol s : symbols.values()) {
            String nameStr = String.format("\"%-" + maxNameLen + "s\"", s.getName());
            String typeStr = String.format("\"%-" + maxTypeLen + "s\"", s.getType());
            System.out.print(indent + "    { \"name\": " + nameStr + ", \"type\": " + typeStr + " }");
            count++;
            System.out.println(count < size ? "," : "");
        }

        System.out.println(indent + "  ]");

        boolean hasNested = false;
        for (Symbol s : symbols.values()) {
            if (s.getScope() != null) {
                hasNested = true;
                break;
            }
        }

        if (hasNested) {
            System.out.println(indent + "  ,\"nestedScopes\": [");
            int nestedCount = 0;
            for (Symbol s : symbols.values()) {
                if (s.getScope() != null) {
                    s.getScope().print(indent + "    ");
                    nestedCount++;
                    if (nestedCount < nestedScopeCount()) {
                        System.out.println(indent + "    ,");
                    }
                }
            }
            System.out.println(indent + "  ]");
        }

        System.out.println(indent + "}");
    }

    private int nestedScopeCount() {
        int count = 0;
        for (Symbol s : symbols.values()) {
            if (s.getScope() != null) count++;
        }
        return count;
    }
}
