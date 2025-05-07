package symbols;

public class Symbol {
    public final String name;
    public final String type;
    private Scope scope;

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }

    public String getType() { return type; }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public String toString() {
        return name + ": " + type;
    }
}
