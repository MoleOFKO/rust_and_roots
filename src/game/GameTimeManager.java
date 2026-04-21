package game;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import java.time.LocalTime;

public final class GameTimeManager {
    private static final GameTimeManager INSTANCE = new GameTimeManager();
    public static GameTimeManager get() { return INSTANCE; }

    // ---- Config ----
    private static final double SECONDS_PER_DAY = 24.0 * 60.0 * 60.0;
    private static final double GAME_SECONDS_PER_REAL_SECOND = 24.0;
    private static final int NIGHT_START_HOUR = 18; // 18:00
    private static final int NIGHT_END_HOUR = 6;    // 06:00

    // ---- State ----
    private final DoubleProperty timeScale = new SimpleDoubleProperty(1.0); // debug speed
    /**
     * Gameplay-driven world-time multiplier (e.g., "bullet time").
     * This should be kept at 1.0 for normal play and reduced (< 1.0) when the world needs to slow down.
     *
     * Kept separate from {@link #timeScale} so debug/sleep/farm-table speed controls remain intact.
     */
    private final DoubleProperty worldFlowScale = new SimpleDoubleProperty(1.0);
    private final BooleanProperty paused = new SimpleBooleanProperty(false);

    // total simulated seconds since day 1 start (00:00)
    private final DoubleProperty totalSeconds = new SimpleDoubleProperty(0);
    private final ReadOnlyDoubleWrapper realWorldElapsedSeconds = new ReadOnlyDoubleWrapper(0);

    private final ReadOnlyIntegerWrapper dayNumber = new ReadOnlyIntegerWrapper(1);
    private final ReadOnlyObjectWrapper<LocalTime> timeOfDay = new ReadOnlyObjectWrapper<>(LocalTime.MIDNIGHT);
    private final ReadOnlyBooleanWrapper night = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyStringWrapper formatted = new ReadOnlyStringWrapper("Day 1 00:00");

    private AnimationTimer timer;
    private long lastNanos = 0;
    private boolean started = false;

    private GameTimeManager() {
        // Recompute derived properties whenever totalSeconds changes
        totalSeconds.addListener((obs, oldV, newV) -> recomputeDerived());
        realWorldElapsedSeconds.addListener((obs, oldV, newV) -> recomputeDerived());
        recomputeDerived();
    }

    public void start() {
        if (started) return;
        started = true;

        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (lastNanos == 0) { lastNanos = now; return; }
                long dtNanos = now - lastNanos;
                lastNanos = now;
                
                double dt = dtNanos / 1_000_000_000.0;
                if (dt > 0) {
                    realWorldElapsedSeconds.set(realWorldElapsedSeconds.get() + dt);
                }

                if (paused.get()) return;

                double scaled = dt * GAME_SECONDS_PER_REAL_SECOND
                        * Math.max(0.0, timeScale.get())
                        * Math.max(0.0, worldFlowScale.get());
                if (scaled <= 0) return;

                totalSeconds.set(totalSeconds.get() + scaled);
            }
        };
        timer.start();
    }

    public void stop() {
        if (!started) return;
        started = false;
        if (timer != null) timer.stop();
        timer = null;
        lastNanos = 0;
    }

    // ---- Derived state ----
    private void recomputeDerived() {
        double t = Math.max(0, totalSeconds.get());
        int day = (int) Math.floor(t / SECONDS_PER_DAY) + 1;
        double tod = t % SECONDS_PER_DAY;

        int seconds = (int) Math.floor(tod);
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;

        LocalTime lt = LocalTime.of(h, m);

        dayNumber.set(day);
        timeOfDay.set(lt);

        int secondsOfDay = (h * 3600) + (m * 60);
        int cycleSeconds = 24 * 3600;      // 24-hour anchor cycle (for 0..360)
        int anchorAtSix = 6 * 3600;        // 06:00 => 0 degree
        int elapsedFromSix = Math.floorMod(secondsOfDay - anchorAtSix, cycleSeconds);
        boolean isNight = elapsedFromSix >= (12 * 3600); // switch every 12 in-game hours from 06:00
        night.set(isNight);

        formatted.set(String.format("Day %d %02d:%02d", day, h, m));
    }

    // ---- Public properties (bind in UI) ----
    public DoubleProperty timeScaleProperty() { return timeScale; }
    public DoubleProperty worldFlowScaleProperty() { return worldFlowScale; }
    public BooleanProperty pausedProperty() { return paused; }
    public DoubleProperty totalSecondsProperty() { return totalSeconds; }

    public ReadOnlyIntegerProperty dayNumberProperty() { return dayNumber.getReadOnlyProperty(); }
    public ReadOnlyObjectProperty<LocalTime> timeOfDayProperty() { return timeOfDay.getReadOnlyProperty(); }
    public ReadOnlyBooleanProperty nightProperty() { return night.getReadOnlyProperty(); }
    public ReadOnlyStringProperty formattedProperty() { return formatted.getReadOnlyProperty(); }
    public ReadOnlyDoubleProperty realWorldElapsedSecondsProperty() { return realWorldElapsedSeconds.getReadOnlyProperty(); }

    public boolean isNight() { return night.get(); }

    // ---- Debug / manipulation API ----
    public void setPaused(boolean v) { paused.set(v); }
    public void togglePause() { paused.set(!paused.get()); }

    public void addMinutes(int minutes) {
        totalSeconds.set(Math.max(0, totalSeconds.get() + minutes * 60.0));
    }

    public void setTimeOfDay(int hour, int minute) {
        hour = Math.floorMod(hour, 24);
        minute = Math.max(0, Math.min(59, minute));

        double t = Math.max(0, totalSeconds.get());
        double dayStart = Math.floor(t / SECONDS_PER_DAY) * SECONDS_PER_DAY;
        totalSeconds.set(dayStart + (hour * 3600.0) + (minute * 60.0));
    }

    public void setDay(int day) {
        day = Math.max(1, day);
        LocalTime lt = timeOfDay.get();
        totalSeconds.set((day - 1) * SECONDS_PER_DAY + lt.getHour() * 3600.0 + lt.getMinute() * 60.0);
    }
}
