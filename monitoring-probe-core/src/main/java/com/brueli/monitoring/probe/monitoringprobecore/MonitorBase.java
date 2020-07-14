package com.brueli.monitoring.probe.monitoringprobecore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class MonitorBase implements IMonitor {

    private final Logger logger = LoggerFactory.getLogger(MonitorBase.class);
    
    private ScheduledExecutorService scheduler;

    private boolean registered;
    private boolean started;
    private ScheduledFuture<?> scheduledTask;

    @Override
    public final void start() {
        MonitorConfiguration config = this.getConfig();
        final String monitorType = this.getMonitorTypeName();

        if (!isEnabled()) {
            logger.info("monitor is disabled: " + monitorType);
            return;
        }

        if (started) {
            logger.info("monitor has already started: " + monitorType);
            return;
        }

        logger.info("starting monitor: " + monitorType);

        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(config.getNumThreads());
        }

        if (!beforeStart()) {
            logger.info("monitor rejected to start: " + monitorType);
        }

        long initialDelay = getConverter().convertTimespanString(config.getInitialDelay(), "initialDelay");
        long delay = getConverter().convertTimespanString(config.getDelay(), "delay");

        Runnable measureTask = getMeasureTask();
        scheduledTask = scheduler.scheduleWithFixedDelay(measureTask, initialDelay, delay, TimeUnit.MILLISECONDS);

        started = true;

        afterStart();

        logger.info("monitor started: " + monitorType);
    }

    @Override
    public final void stop() {
        final String monitorType = this.getMonitorTypeName();

        if (!started) {
            logger.info("monitor not started: " + monitorType);
            return;
        }

        logger.info("stopping monitor: " + monitorType);
        try {
            final long SHUTDOWN_TIMEOUT = 20;
            boolean shutdownComplete = false;

            beforeStop();

            // cancel pending task
            scheduledTask.cancel(!getConfig().getDontInterruptIfRunning());

            logger.info("gracefully terminating scheduler: " + monitorType);
            try {
                scheduler.shutdown();
                scheduler.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
                shutdownComplete = true;
                logger.info("scheduler terminated gracefully: " + monitorType);
            }
            catch (Throwable t) {
                logger.info("scheduler failed to terminate: " + monitorType + "\ntermination exception was: " + t.toString());
            }
            if (!shutdownComplete) {
                logger.info("forcing shutdown: " + monitorType);
                scheduler.shutdownNow();
            }
            afterStop();
            logger.info("monitor has stopped" + monitorType);
        }
        catch (Throwable t) {
            logger.error("failed stop monitor " + monitorType + ".\nException was: " + t.toString());
        }
    }

    @Override
    public final void register() {
        final String monitorType = this.getMonitorTypeName();
        logger.info("registering monitor: " + monitorType);
        try {
            onRegister();
            logger.info("monitor successfully registered: " + monitorType);
        }
        catch (Throwable t) {
            logger.error("failed to register monitor: " + monitorType + "\nexception was: " + t.toString());
        }
    }

    @Override
    public final void unregister() {
        final String monitorType = this.getMonitorTypeName();
        logger.info("unregistering monitor: " + monitorType);
        try {
            onUnregister();
            logger.info("monitor successfully removed: " + monitorType);
        }
        catch (Throwable t) {
            logger.error("failed to unregister monitor " + monitorType + "\nexception was: " + t.toString());
        }
    }

    @Override
    public abstract MonitorConfiguration getConfig();

    protected PropertyConverter getConverter() {
        return new PropertyConverter();
    }

    protected final String getMonitorTypeName() { return this.getClass().getCanonicalName(); }

    public final Boolean isRegistered() { return registered; }
    public final Boolean isEnabled() { return getConfig().getEnabled(); }
    public final Boolean isStarted() { return started; }

    protected abstract Runnable getMeasureTask();

    protected boolean beforeStart() { return true; }
    protected void afterStart() {}

    protected void beforeStop() {}
    protected void afterStop() {}

    protected void onRegister() {}
    protected void onUnregister() {}

}