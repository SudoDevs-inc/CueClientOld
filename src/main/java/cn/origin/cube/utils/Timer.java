package cn.origin.cube.utils;

public class Timer {
    private long time = this.getCurrentTime();

    long delay;
    boolean paused;

    public Timer() {
        this.delay = 0L;
        this.paused = false;
    }


    protected final long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public final long getTime() {
        return this.time;
    }

    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.time >= this.delay;
    }

    protected final void setTime(long l2) {
        this.time = l2;
    }

    private final long current = System.currentTimeMillis();

    public boolean isPaused() {
        return this.paused;
    }

    public boolean passed(long ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public boolean passed(double ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - convertToNS(ms);
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public boolean hasReached(long var1) {
        return System.currentTimeMillis() - this.current >= var1;
    }

    public boolean hasReached(long var1, boolean var3) {
        if (var3) {
            this.reset();
        }
        return System.currentTimeMillis() - this.current >= var1;
    }

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public long timePassed(long n) {
        return System.currentTimeMillis() - n;
    }

    public long getPassedTimeMs() {
        return System.currentTimeMillis() - this.time;
    }

    public final boolean passedTicks(int ticks) {
        return this.passed(ticks * 50);
    }

    public void resetTimeSkipTo(final long p_MS) {
        this.time = System.currentTimeMillis() + p_MS;
    }

    public boolean passed(float ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public boolean passed(int ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public void setDelay(final long delay) {
        this.delay = delay;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
}

