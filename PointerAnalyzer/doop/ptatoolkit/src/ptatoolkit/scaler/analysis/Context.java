package ptatoolkit.scaler.analysis;

import java.util.List;

public class Context<E> {

    private final List<E> context;

    public Context(List<E> context) {
        this.context = context;
    }

    public List<E> getContext() {
        return context;
    }

    @Override
    public int hashCode() {
        return context.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Context) {
            Context<?> anoContext = (Context<?>) other;
            return context.equals(anoContext.context);
        }
        return false;
    }

    @Override
    public String toString() {
        return context.toString();
    }
}
