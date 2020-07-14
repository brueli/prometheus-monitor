package com.brueli.monitoring.probe.monitoringprobeapplication;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import com.brueli.monitoring.probe.monitoringprobecore.IMonitor;
import com.brueli.monitoring.probe.monitoringprobesample.SampleMonitorConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import io.prometheus.client.exporter.HTTPServer;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.brueli.monitoring.probe.monitoringprobesample"
})
public class ProbeApplication {

	private final Logger logger = LoggerFactory.getLogger(ProbeApplication.class);
	private HTTPServer server;

	@Autowired
	private IMonitor[] monitors;

	/**
	 * Run the ProbeApplication with spring boot. 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProbeApplication.class, args);
	}

	@Bean
	@Order(2)
	public ApplicationRunner dumpBeans(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println("- " + beanName);
			}
		};
	}

	@Bean
	@Order(3)
	public ApplicationRunner dumpConfig(SampleMonitorConfiguration config) {
		return args -> {
			System.out.println("configuration properties from application.properties");
			if (config == null) {
				System.out.println("config is null");
				return;
			}
			System.out.println("subsystem=" + config.getSubsystem());
			System.out.println("name=" + config.getName());
			System.out.println("help=" + config.getHelp());
			System.out.println("numThreads=" + config.getNumThreads());
			System.out.println("delay=" + config.getDelay());
			System.out.println("initialDelay=" + config.getInitialDelay());
			System.out.println("dontInterruptIfRunning=" + config.getDontInterruptIfRunning());
		};
	}

	/**
	 * Start the HTTP server when the application starts.
	 * @param ctx
	 * @return An ApplicationRunner which starts the prometheus HTTPServer
	 */
	@Bean
	@Order(0) 
	public ApplicationRunner runHttpServer(ServerConfiguration config) {
		return args -> {
			logger.info("Starting prometheus exporter HTTPServer");
			Integer port = config.getPort();
			String hostname = config.getHostname();
			if (hostname != null) {
				server = new HTTPServer(hostname, port, true);
			} else {
				server = new HTTPServer(port, true);
			}
		};
	}

	@Bean
	@Order(1)
	public ApplicationRunner runMonitor() {
		return args -> {
			logger.info("Starting monitors");
			for (IMonitor monitor : monitors) {
				String typeName = monitor.getClass().getCanonicalName();
				logger.info("Monitor " + typeName);
				monitor.register();
				monitor.start();
			}
		};
	}

	/**
	 * Stop the HTTP server before the application is destroyed.
	 */
	@PreDestroy
	public void preDestroy() {
		logger.info("Stopping prometheus exporter HTTPServer");
		server.stop();

		logger.info("Stopping monitors");
		for (IMonitor monitor : monitors) {
			monitor.stop();
			monitor.unregister();
		}
	}

}
