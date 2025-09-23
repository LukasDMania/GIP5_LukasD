package be.ucll.application.events;

import be.ucll.application.dto.SearchCriteriaDto;

public record SearchRequestedEvent(SearchCriteriaDto searchCriteriaDto) {
}
