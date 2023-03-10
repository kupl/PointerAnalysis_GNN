package ptatoolkit.util.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MergedNode<N> {

    private Set<MergedNode<N>> preds = Collections.emptySet();
    private Set<MergedNode<N>> succs = Collections.emptySet();
    private final Set<N> content;

    public MergedNode(Collection<N> content) {
        assert !content.isEmpty();
        this.content = content.size() == 1
                ? Collections.singleton(content.iterator().next())
                : new HashSet<>(content);
    }

    public void addPred(MergedNode<N> pred) {
        if (preds.isEmpty()) {
            preds = new HashSet<>(4);
        }
        preds.add(pred);
    }

    public Set<MergedNode<N>> getPreds() {
        return preds;
    }

    public void addSucc(MergedNode<N> succ) {
        if (succs.isEmpty()) {
            succs = new HashSet<>(4);
        }
        succs.add(succ);
    }

    public Set<MergedNode<N>> getSuccs() {
        return succs;
    }

    public Set<N> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
