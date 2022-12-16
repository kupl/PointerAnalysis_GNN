package ptatoolkit.alias;

import ptatoolkit.Global;
import ptatoolkit.Options;
import ptatoolkit.pta.basic.Variable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compute the alias paris based on the given pointer analysis results.
 */
class AliasComputer {

    private static final int EST_CAPACITY = 2000000;

    private final PointsToAnalysis pta;

    private final Options options;

    AliasComputer(PointsToAnalysis pta, Options options) {
        this.pta = pta;
        this.options = options;
    }

    Map<Variable, Set<Variable>> computeAliases() {
        Map<Variable, Set<Variable>> aliases =
                new ConcurrentHashMap<>(EST_CAPACITY);
        pta.allVariables().parallelStream()
                .forEach(v ->
                        pta.pointsToSetOf(v).pointers().forEach(w -> {
                            if (v.getID() < w.getID()
                                    && !pta.assignedTo(v).contains(w)
                                    && !pta.assignedTo(w).contains(v)
                            ) {
                                aliases.computeIfAbsent(v,
                                        var -> ConcurrentHashMap.newKeySet())
                                        .add(w);
                            }
                        }));
        return aliases;
    }

    int computeAliasNumber() {
        Map<Variable, Set<Variable>> aliases = computeAliases();
        return aliases.values().stream()
                .mapToInt(Set::size)
                .sum();
    }

    /**
     * Memory-efficient version of alias computation.
     * @return number of alias pairs.
     */
    int computeAliasNumberME() {
        Map<Variable, Integer> aliasNums = computeAliasNumbers();
        return aliasNums.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    Map<Variable, Integer> computeAliasNumbers() {
        Map<Variable, Integer> aliasNums =
                new ConcurrentHashMap<>(EST_CAPACITY);
        pta.allVariables().parallelStream()
                .forEach(v -> {
                    Set<Variable> aliases = new HashSet<>();
                    pta.pointsToSetOf(v).pointers().forEach(w -> {
                        if (v.getID() < w.getID()
                                && !pta.assignedTo(v).contains(w)
                                && !pta.assignedTo(w).contains(v)
                        ) {
                            aliases.add(w);
                        }
                    });
                    aliasNums.put(v, aliases.size());
                });
        return aliasNums;
    }

    void dumpAliases() {
        File outFile = new File(options.getOutPath(), options.getApp() + ".ALIAS");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)))) {
            System.out.printf("Dumping aliases to %s ...%n", outFile);
            AtomicInteger counter = new AtomicInteger(0);
            computeAliases().forEach((v, vs) ->
                    vs.forEach(v2 -> {
                        writer.write(v.toString());
                        writer.write(Global.getSep());
                        writer.write(v2.toString());
                        writer.println();
                        counter.incrementAndGet();
                    })
            );
            System.out.printf("Dumped %d alias pairs%n", counter.get());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to dump aliases");
        }
    }
}
