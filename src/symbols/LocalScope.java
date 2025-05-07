package symbols;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocalScope extends BaseScope{
    public LocalScope(Scope enclosingScope, String name) {
        super(enclosingScope, name);
    }

    @Override
    public String getScopeName() {
        return super.getScopeName();
    }
}
