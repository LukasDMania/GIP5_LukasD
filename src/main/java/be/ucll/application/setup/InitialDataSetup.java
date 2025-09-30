package be.ucll.application.setup;

import be.ucll.domain.model.Product;
import be.ucll.domain.model.Role;
import be.ucll.domain.model.StockAdjustment;
import be.ucll.domain.model.User;
import be.ucll.util.RoleConstants;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class InitialDataSetup {

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PersistenceContext
	private EntityManager entityManager;

	private final Faker faker = new Faker();
	private final Random random = new Random();

	@PostConstruct
	public void setup() {
		TransactionTemplate tx = new TransactionTemplate(platformTransactionManager);
		tx.execute(status -> {

			Role userRole = new Role(RoleConstants.ROLE_USER);
			Role managerRole = new Role(RoleConstants.ROLE_MANAGER);
			Role adminRole = new Role(RoleConstants.ROLE_ADMIN);
			entityManager.persist(userRole);
			entityManager.persist(managerRole);
			entityManager.persist(adminRole);

			User user = new User();
			user.setUsername("admin");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setEmail(faker.internet().emailAddress());
			user.setRoles(new HashSet<>(Arrays.asList(managerRole, adminRole)));

			User user1 = new User();
			user1.setUsername("user");
			user1.setPassword(passwordEncoder.encode("user"));
			user1.setEmail(faker.internet().emailAddress());
			user1.setRoles(new HashSet<>(List.of(userRole)));

			entityManager.persist(user);
			entityManager.persist(user1);

			List<User> users = generateUsers(40, userRole, managerRole, adminRole);
			users.forEach(entityManager::persist);

			List<Product> products = generateProducts(5000);
			products.forEach(entityManager::persist);

			//List<StockAdjustment> adjustments = generateStockAdjustments(10000, products, users);
			//adjustments.forEach(entityManager::persist);

			List<StockAdjustment> adjustments = generateStockAdjustmentsForEachProduct(50, products, users);
			adjustments.forEach(entityManager::persist);
			return null;
		});
	}

	private List<User> generateUsers(int count, Role userRole, Role managerRole, Role adminRole) {
		List<User> users = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			User u = new User();
			u.setUsername("user" + i);
			u.setPassword(passwordEncoder.encode("password"));
			u.setEmail(faker.internet().emailAddress());

			Set<Role> roles = new HashSet<>();
			roles.add(userRole);
			if (random.nextDouble() < 0.2) roles.add(managerRole);
			if (random.nextDouble() < 0.1) roles.add(adminRole);
			u.setRoles(roles);

			users.add(u);
		}
		return users;
	}

	private List<Product> generateProducts(int count) {
		List<Product> products = new ArrayList<>();
		Set<String> usedNames = new HashSet<>();

		while (products.size() < count) {
			String name = faker.commerce().productName();
			if (usedNames.add(name)) {
				Product p = new Product();
				p.setName(name);
				p.setDescription(faker.lorem().sentence());
				p.setStock(random.nextInt(5000));
				p.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
				products.add(p);
			}
		}
		return products;
	}

	private List<StockAdjustment> generateStockAdjustments(int count, List<Product> products, List<User> users) {
		List<StockAdjustment> adjustments = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			StockAdjustment adj = new StockAdjustment();
			Product product = products.get(random.nextInt(products.size()));
			adj.setProduct(product);

			User user = users.get(random.nextInt(users.size()));
			adj.setAdjustedBy(user);

			int delta = random.nextInt(20) + 1;
			if (random.nextBoolean()) delta = -delta;
			adj.setDelta(delta);

			adj.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(30)));

			adjustments.add(adj);
		}
		return adjustments;
	}

	private List<StockAdjustment> generateStockAdjustmentsForEachProduct(int randomBound, List<Product> products, List<User> users) {
		List<StockAdjustment> adjustments = new ArrayList<>();
		for (Product product : products) {
			int count = random.nextInt(randomBound + 1);
			for (int j = 0; j < count; j++) {
				StockAdjustment adj = new StockAdjustment();
				adj.setProduct(product);
				adj.setAdjustedBy(users.get(random.nextInt(users.size())));
				adj.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(30)));

				int delta = random.nextInt(20) + 1;
				if (random.nextBoolean()) delta = -delta;
				adj.setDelta(delta);

				adj.setStockAfter(product.getStock() + delta);

				adjustments.add(adj);
			}
		}
		return adjustments;
	}
}
