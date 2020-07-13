package com.brueli.monitoring.probe.monitoringprobesample;

import java.util.Random;

import com.brueli.monitoring.probe.monitoringprobecore.MonitorBase;
import com.brueli.monitoring.probe.monitoringprobecore.MonitorConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import io.prometheus.client.Gauge;

@Service
@EnableConfigurationProperties(SampleMonitorConfiguration.class)
public class SampleMonitor extends MonitorBase {

	private Gauge queueLength;

	@Autowired
	private SampleMonitorConfiguration config;

	private final Random random = new Random();

	@Override 
	protected void onRegister() {
		Gauge.Builder builder = Gauge.build(config.getName(), config.getHelp());
		if (config.getSubsystem() != null) {
			builder = builder.subsystem(config.getSubsystem());
		}
		queueLength = builder.register();
	}
	
	protected Runnable getMeasureTask() {
		return new MeasureTask(this);
	}

	public MonitorConfiguration getConfig() {
		return config; 
	}

	private static final class MeasureTask implements Runnable {
		private final SampleMonitor monitor;
		private final Logger logger = LoggerFactory.getLogger(MeasureTask.class);

		public MeasureTask(SampleMonitor monitor) {
			this.monitor = monitor;
		}

		@Override public void run() {
			logger.info("SampleMonitor.measure()");
			int randomQueueLength = monitor.random.nextInt(200);
        	monitor.queueLength.set(randomQueueLength);
		}
	  }
}