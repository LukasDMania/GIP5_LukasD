package be.ucll.application.events;

import be.ucll.application.dto.SearchCriteriaDto;
import be.ucll.application.dto.product.ProductResponseDto;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public record SearchCompletedEvent(List<ProductResponseDto> results, SearchCriteriaDto criteria) {}
