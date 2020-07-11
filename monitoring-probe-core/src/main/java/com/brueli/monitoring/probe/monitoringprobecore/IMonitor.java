package com.brueli.monitoring.probe.monitoringprobecore;

public interface IMonitor {
    void start();
    void stop();
    void register();
    void unregister();
    MonitorConfiguration getConfig();
}
