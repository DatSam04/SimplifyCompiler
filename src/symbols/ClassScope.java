package symbols;

public class ClassScope extends BaseScope{
    public ClassScope(Scope enclosingScope, String className) {
        super(enclosingScope, className);
    }

    @Override
    public String getScopeName() {
        return super.getScopeName();
    }
}
