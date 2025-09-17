package be.ucll.application.setup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Component
public class InitialDataSetup {

	@Autowired
	private PlatformTransactionManager platformTransactionManager;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void setup() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
		transactionTemplate.execute(e -> {

			IntStream.range(0, 5).forEach(value -> {
				TestEntity testEntity = new TestEntity();
				testEntity.setValue("This is value nr " + value + " created at " + new SimpleDateFormat().format(new Date()));
				entityManager.persist(testEntity);
			});

			/**
			 * Hier kan je meer data setup in plaatsen van het moment je datamodel klaar is
			 */

			return null;
		});
	}
}