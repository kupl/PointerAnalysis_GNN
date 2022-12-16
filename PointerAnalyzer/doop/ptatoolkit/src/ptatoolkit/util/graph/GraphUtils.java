package ptatoolkit.util.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GraphUtils {

    private GraphUtils() {
    }

    /**
     * Search the shortest path on a directed graph between given
     * source and destination nodes
     */
    public static <N> List<N> findShortestPath(DirectedGraph<N> graph,
                                               N src, N dest) {
        // The predecessor of the nodes in the shortest path
        Map<N, N> from = new HashMap<>();
        from.put(src, src); // start condition
        Queue<N> queue = new LinkedList<>();
        queue.add(src);
        boolean found = false;
        outerLoop:
        while (!queue.isEmpty()) {
            N node = queue.poll();
            for (N succ : graph.succsOf(node)) {
                if (!from.containsKey(succ)) {
                    from.put(succ, node);
                    queue.add(succ);
                    if (succ.equals(dest)) {
                        found = true;
                        break outerLoop;
                    }
                }
            }
        }
        if (found) {
            LinkedList<N> path = new LinkedList<>();
            N node = dest;
            while (true) {
                path.addFirst(node);
                if (node.equals(src)) {
                    break;
                }
                node = from.get(node);
            }
            return path;
        } else {
            return Collections.emptyList();
        }
    }
}
