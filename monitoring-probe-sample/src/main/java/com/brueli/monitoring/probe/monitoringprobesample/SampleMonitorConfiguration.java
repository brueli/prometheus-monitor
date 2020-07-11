package com.brueli.monitoring.probe.monitoringprobesample;

import com.brueli.monitoring.probe.monitoringprobecore.SimpleMonitorConfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sample.monitor")
public class SampleMonitorConfiguration extends SimpleMonitorConfiguration {
    
}
