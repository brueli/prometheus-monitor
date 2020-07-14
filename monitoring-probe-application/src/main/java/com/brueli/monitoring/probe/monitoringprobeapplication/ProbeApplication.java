package com.brueli.monitoring.probe.monitoringprobeapplication;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import com.brueli.monitoring.probe.monitoringprobecore.IMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import io.prometheus.client.exporter.HTTPServer;

@SpringBootApplication
@ComponentScan(basePackages = {
	"com.brueli.monitoring.probe.monitoringprobesample"
})
@EnableConfigurationProperties(ServerConfiguration.class)
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
			System.out.println("Configured Beans");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println("- " + beanName);
			}
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
			String hostname = config.getHostname();
			Integer port = config.getPort();

			logger.info("Starting prometheus exporter HTTPServer");
			String serverAddress = "http://" + hostname + ":" + port.toString();
			if (hostname.equals("*")) {
				server = new HTTPServer(port, true);
			} else {
				server = new HTTPServer(hostname, port, true);
			}
			logger.info("Serving prometheus metrics under: " + serverAddress);
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
