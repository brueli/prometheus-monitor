package com.brueli.monitoring.probe.monitoringprobecore;

public class MonitorConfiguration {
    private boolean enabled = true;
    private int numThreads = 1;
    private String initialDelay = "0";
    private String delay = "1s";
    private boolean dontInterruptIfRunning = true;

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }

    public Integer getNumThreads() { return numThreads; }
    public void setNumThreads(Integer value) { numThreads = value; }
    
    public String getInitialDelay() { return initialDelay; }
    public void setInitialDelay(String value) { initialDelay = value; }

    public String getDelay() { return delay; }
    public void setDelay(String value) { delay = value; }

    public Boolean getDontInterruptIfRunning() { return dontInterruptIfRunning; }
    public void setDontInterruptIfRunning(boolean value) { dontInterruptIfRunning = value; }
}
