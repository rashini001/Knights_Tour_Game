package game;

public class TimerUtil {
    private long start, end;

    public void start() {
        start = System.nanoTime();
    }

    public void stop() {
        end = System.nanoTime();
    }

    public long getElapsedMillis() {
        return (end - start) / 1_000_000;
    }
}

