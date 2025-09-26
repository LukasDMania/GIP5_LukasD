package be.ucll.application.events;

import be.ucll.application.dto.SearchCriteriaDto;
import org.springframework.context.ApplicationEvent;

import java.util.LinkedList;

public record SearchHistoryChangedEvent (LinkedList<SearchCriteriaDto> history){
}
