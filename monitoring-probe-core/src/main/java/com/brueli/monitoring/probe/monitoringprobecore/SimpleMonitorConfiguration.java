package com.brueli.monitoring.probe.monitoringprobecore;

public class SimpleMonitorConfiguration extends MonitorConfiguration {
    private String name;
    private String help;
  
    public String getName() { return name; }
    public void setName(String value) { name = value; }
  
    public String getHelp() { return help; }
    public void setHelp(String value) { help = value; }
}