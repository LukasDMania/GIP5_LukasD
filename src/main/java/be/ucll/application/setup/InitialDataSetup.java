package be.ucll.application.setup;

import be.ucll.domain.model.Role;
import be.ucll.domain.model.User;
import be.ucll.util.RoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitialDataSetup {

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void setup() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.execute(status -> {

			Role userRole = new Role(RoleConstants.ROLE_USER);
			Role managerRole = new Role(RoleConstants.ROLE_MANAGER);
			Role adminRole = new Role(RoleConstants.ROLE_ADMIN);

			entityManager.persist(userRole);
			entityManager.persist(managerRole);
			entityManager.persist(adminRole);

			User normalUser = new User();
			normalUser.setUsername("john");
			normalUser.setPassword(passwordEncoder.encode("password123"));
			normalUser.setEmail("john@example.com");
			normalUser.setRoles(Set.of(userRole));
			entityManager.persist(normalUser);

			User manager = new User();
			manager.setUsername("alice");
			manager.setPassword(passwordEncoder.encode("admin123"));
			manager.setEmail("alice@example.com");
			manager.setRoles(new HashSet<>(Set.of(userRole, managerRole)));
			entityManager.persist(manager);

			return null;
		});
	}
}
