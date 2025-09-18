package be.ucll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.spring.VaadinServletContextInitializer;

@SpringBootApplication
public class InventoryApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	@Bean
	public VaadinServletContextInitializer s(ApplicationContext applicationContext) {
		return new VaadinServletContextInitializer(applicationContext);
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
}