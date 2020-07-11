package com.brueli.monitoring.probe.monitoringprobecore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MonitorBase implements IMonitor {

    private final Logger logger = LoggerFactory.getLogger(MonitorBase.class);
    private ScheduledExecutorService scheduler;

    private boolean registered;
    private boolean enabled;

    @Override
    public final void start() {
        MonitorConfiguration config = this.getConfig();

        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(config.getNumThreads());
        }

        beforeStart();

        long initialDelay = convertTimespanString(config.getInitialDelay(), "initialDelay");
        long delay = convertTimespanString(config.getDelay(), "delay");

        Runnable measureTask = getMeasureTask();
        scheduler.scheduleWithFixedDelay(measureTask, initialDelay, delay, TimeUnit.MILLISECONDS);

        afterStart();
    }

    @Override
    public final void stop() {
        final String monitorTypeName = this.getClass().getCanonicalName();

        logger.error("stopping monitor " + monitorTypeName + "...");
        try {
            final long SHUTDOWN_TIMEOUT = 20;
            boolean shutdownComplete = false;

            beforeStop();
            logger.info("gracefully terminating scheduler...");
            try {
                scheduler.shutdown();
                scheduler.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
                shutdownComplete = true;
                logger.info("scheduler terminated gracefully");
            }
            catch (Throwable t) {
                logger.info("scheduler failed to terminate\ntermination exception was: " + t.toString());
            }
            if (!shutdownComplete) {
                logger.info("forcing shutdown...");
                scheduler.shutdownNow();
            }
            afterStop();
        }
        catch (Throwable t) {
            logger.error("failed stop monitor " + monitorTypeName + ".\nException was: " + t.toString());
        }
    }

    @Override
    public final void register() {
        final String monitorTypeName = this.getClass().getCanonicalName();
        logger.info("registering monitor " + monitorTypeName);
        try {
            onRegister();
            logger.info("monitor " + monitorTypeName + " successfully registered");
        }
        catch (Throwable t) {
            logger.error("failed to register monitor " + monitorTypeName + "\nexception was: " + t.toString());
        }
    }

    @Override
    public final void unregister() {
        final String monitorTypeName = this.getClass().getCanonicalName();
        logger.info("unregistering monitor " + monitorTypeName);
        try {
            onUnregister();
            logger.info("monitor " + monitorTypeName + " successfully removed");
        }
        catch (Throwable t) {
            logger.error("failed to unregister monitor " + monitorTypeName + "\nexception was: " + t.toString());
        }
    }

    @Override
    public abstract MonitorConfiguration getConfig();

    public final boolean isRegistered() { return registered; }
    public final boolean isEnabled() { return enabled; }

    protected abstract Runnable getMeasureTask();

    protected boolean beforeStart() { return true; }
    protected void afterStart() {}

    protected void beforeStop() {}
    protected void afterStop() {}

    protected void onRegister() {}
    protected void onUnregister() {}

    protected long convertTimespanString(String timespanString, String hint) throws RuntimeException {
        Pattern pattern = Pattern.compile("^(\\d+)(h|m|s|ms)?$", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(timespanString);
        if (!match.matches()) {
            throw new RuntimeException("invalid " + hint);
        }
        double multiplier = 1;
        if (match.groupCount() == 2 && match.group(2) != null) {
            String multiplierName = match.group(2);
            if (multiplierName.equalsIgnoreCase("h")) {
                multiplier = 60 * 60 * 1000;
            } else if (multiplierName.equalsIgnoreCase("m")) {
                multiplier = 60 * 1000;
            } else if (multiplierName.equalsIgnoreCase("s")) {
                multiplier = 1000;
            } else if (multiplierName.equalsIgnoreCase("ms")) {
                multiplier = 1;
            } else {
                multiplier = 1;
            }
        }
        Double timespanNumber = Double.parseDouble(match.group(1));
        Double timespanValue = timespanNumber * multiplier;
        return (long)Math.round(timespanValue);
    }

}