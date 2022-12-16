package ptatoolkit.util;

import java.util.function.Supplier;

public class Timer {

    private final String name;
    private long elapsedTime = 0;
    private long startTime;
    private boolean inCounting = false;

    /**
     * Execute a task, output the elapsed time, and return the result.
     * @param message message of the task
     * @param task task to be executed
     */
    public static <T> T executeAndCount(String message, Supplier<T> task) {
        Timer timer = new Timer(message);
        timer.start();
        T result = task.get();
        timer.stop();
         System.out.println(/*timer*/);
        return result;
    }

    /**
     * Execute a task and output the elapsed time.
     * @param message message of the task
     * @param task task to be executed
     */
    public static void executeAndCount(String message, Runnable task) {
        Timer timer = new Timer(message);
        timer.start();
        task.run();
        timer.stop();
        System.out.println(timer);
    }

    public Timer(String name) {
        this.name = name;
    }

    public void start() {
        if (!inCounting) {
            inCounting = true;
            startTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        if (inCounting) {
            elapsedTime += System.currentTimeMillis() - startTime;
            inCounting = false;
        }
    }

    public float inSecond() {
        return elapsedTime / 1000F;
    }

    public void clear() {
        elapsedTime = 0;
        inCounting = false;
    }

    @Override
    public String toString() {
        return String.format("%s elapsed time: %.2fs",
                name, inSecond());
    }
}
