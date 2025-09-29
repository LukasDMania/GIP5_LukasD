package be.ucll;

import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.spring.VaadinServletContextInitializer;

@SpringBootApplication
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
public class InventoryApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	@Bean
	public VaadinServletContextInitializer s(ApplicationContext applicationContext) {
		return new VaadinServletContextInitializer(applicationContext);
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
}