package symbols;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalScope extends BaseScope{
    public GlobalScope() {
        super(null, "global");
    }

    @Override
    public String getScopeName() {
        return "Global Scope";
    }
}
