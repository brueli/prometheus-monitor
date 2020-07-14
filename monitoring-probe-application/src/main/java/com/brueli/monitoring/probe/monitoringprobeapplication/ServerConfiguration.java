package com.brueli.monitoring.probe.monitoringprobeapplication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exporter.http.server")
public class ServerConfiguration {
    @Value("${port:9100}")
    private int port;

    @Value("${hostname:*}")
    private String hostname;
    
    public Integer getPort() { return port; }
    public void setPort(int value) { port = value; }

    public String getHostname() { return hostname; }
    public void setHostname(String value) { hostname = value; }
}