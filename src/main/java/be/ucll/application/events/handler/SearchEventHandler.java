package be.ucll.application.events.handler;

import be.ucll.application.dto.product.ProductResponseDto;
import be.ucll.application.events.SearchCompletedEvent;
import be.ucll.application.events.SearchRequestedEvent;
import be.ucll.domain.service.ProductService;
import be.ucll.domain.service.impl.SearchHistoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SearchEventHandler.class);

    private final ProductService productService;
    private final SearchHistoryService searchHistoryService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public SearchEventHandler(ProductService productService, SearchHistoryService searchHistoryService, ApplicationEventPublisher applicationEventPublisher) {
        this.productService = productService;
        this.searchHistoryService = searchHistoryService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    @Transactional // Ensures the service method call is within a transaction
    public void handleSearchRequestedEvent(SearchRequestedEvent event) {
        LOG.info("SearchEventHandler handling SearchRequestedEvent");

        // This is a crucial part. The security context is often not available
        // in event listeners. A custom context propagation solution is needed.
        // For Vaadin, a manual approach is sometimes the most reliable.
        try {
            // Retrieve and set the security context. This is a common workaround.
            //VaadinSecurityContextHolder.setContext();

            // Call the secured service method
            List<ProductResponseDto> productResponseDtos = productService.searchProductsByCriteria(event.searchCriteriaDto());

            // Save history (business logic)
            searchHistoryService.addToHistory(event.searchCriteriaDto());

            // Publish a new event to notify the UI that the search is complete
            applicationEventPublisher.publishEvent(new SearchCompletedEvent(productResponseDtos, event.searchCriteriaDto()));

        } finally {
            //VaadinSecurityContextHolder.clearContext();
        }
    }
}
