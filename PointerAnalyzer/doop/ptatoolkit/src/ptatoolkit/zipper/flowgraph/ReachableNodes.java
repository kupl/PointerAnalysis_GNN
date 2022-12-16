package ptatoolkit.zipper.flowgraph;

import ptatoolkit.util.graph.MergedNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReachableNodes {

    private static final int MERGED_NODE_THRESHOLD = 100;

    private final Set<Node> nodes = new HashSet<>();
    private Set<MergedNode<Node>> mergedNodes = Collections.emptySet();

    public void add(MergedNode<Node> mergedNode) {
        if (mergedNode.getContent().size() > MERGED_NODE_THRESHOLD) {
            if (mergedNodes.isEmpty()) {
                mergedNodes = new HashSet<>();
            }
            mergedNodes.add(mergedNode);
        } else {
            nodes.addAll(mergedNode.getContent());
        }
    }

    public void addAll(ReachableNodes other) {
        other.mergedNodes.forEach(this::add);
        nodes.addAll(other.nodes);
    }

    public void addAll(Collection<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    public boolean contains(Node node) {
        return nodes.contains(node)
                || mergedNodes.stream()
                .anyMatch(mn -> mn.getContent().contains(node));
    }

    public int size() {
        return mergedNodes.stream()
                .mapToInt(mn -> mn.getContent().size())
                .sum() + nodes.size();
    }
}
