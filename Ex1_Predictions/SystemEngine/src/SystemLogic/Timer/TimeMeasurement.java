package SystemLogic.Timer;

public class TimeMeasurement {
    private long startTimeInSeconds;

    public TimeMeasurement() {
        this.startTimeInSeconds = 0;
    }

    public void start() {
        startTimeInSeconds = getCurrentTimeInSeconds();
    }

    public long checkElapsedTime() {
        if (startTimeInSeconds == 0) {
            throw new IllegalStateException("The timer has not been started.");
        }
        long currentTimeInSeconds = getCurrentTimeInSeconds();
        return currentTimeInSeconds - startTimeInSeconds;
    }

    private long getCurrentTimeInSeconds() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis / 1000; // Convert milliseconds to seconds
    }
}