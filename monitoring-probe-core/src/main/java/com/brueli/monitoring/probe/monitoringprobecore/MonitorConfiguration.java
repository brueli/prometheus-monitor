package com.brueli.monitoring.probe.monitoringprobecore;

public class MonitorConfiguration {
    private int numThreads;
    private String initialDelay;
    private String delay;
    private String dontInterruptIfRunning;

    public Integer getNumThreads() { return numThreads; }
    public void setNumThreads(Integer value) { numThreads = value; }
    
    public String getInitialDelay() { return initialDelay; }
    public void setInitialDelay(String value) { initialDelay = value; }

    public String getDelay() { return delay; }
    public void setDelay(String value) { delay = value; }

    public String getDontInterruptIfRunning() { return dontInterruptIfRunning; }
    public void setDontInterruptIfRunning(String value) { dontInterruptIfRunning = value; }
}
