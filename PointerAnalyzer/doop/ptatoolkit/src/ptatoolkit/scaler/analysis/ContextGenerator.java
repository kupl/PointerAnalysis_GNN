package ptatoolkit.scaler.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextGenerator<E> {

    private final Map<List<E>, Context<E>> contextMap = new HashMap<>();

    public Context<E> getContext(List<E> list) {
        if (!contextMap.containsKey(list)) {
            contextMap.put(list, new Context<>(list));
        }
        return contextMap.get(list);
    }
}
