package com.brueli.monitoring.probe.monitoringprobeapplication;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exporter.http.server")
public class ServerConfiguration {
    private int port;
    private String hostname;
    
    public Integer getPort() { return port; }
    public void setPort(int value) { port = value; }

    public String getHostname() { return hostname; }
    public void setHostname(String value) { hostname = value; }
}