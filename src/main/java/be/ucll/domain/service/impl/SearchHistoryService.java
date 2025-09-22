package be.ucll.domain.service.impl;

import be.ucll.application.dto.SearchCriteriaDto;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchHistoryService {

    private static final String SEARCH_HISTORY_SESSION_KEY = "searchHistory";
    private static final int MAX_HISTORY_SIZE = 5;


    public void addToHistory(SearchCriteriaDto criteria) {
        LinkedList<SearchCriteriaDto> history = (LinkedList<SearchCriteriaDto>)
                VaadinSession.getCurrent().getAttribute(SEARCH_HISTORY_SESSION_KEY);

        if (history == null) {
            history = new LinkedList<>();
        }

        Optional<SearchCriteriaDto> existingMatch = history.stream()
                .filter(criteria::equals)
                .findFirst();

        if (existingMatch.isPresent()) {
            SearchCriteriaDto existing = existingMatch.get();
            existing.setCreatedAfter(criteria.getCreatedAfter());

            history.remove(existing);
            history.addFirst(existing);
        } else {
            history.addFirst(criteria);

            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeLast();
            }
        }

        VaadinSession.getCurrent().setAttribute(SEARCH_HISTORY_SESSION_KEY, history);

        //TODO: fire history changed event
    }

    public LinkedList<SearchCriteriaDto> loadHistory() {
        LinkedList<SearchCriteriaDto> history =
                (LinkedList<SearchCriteriaDto>) VaadinSession.getCurrent().getAttribute(SEARCH_HISTORY_SESSION_KEY);
        if (history == null) {
            history = new LinkedList<>();
            VaadinSession.getCurrent().setAttribute(SEARCH_HISTORY_SESSION_KEY, history);
        }
        return history;
    }

    public String createHistoryLabel(SearchCriteriaDto criteria) {
        StringBuilder labelBuilder = new StringBuilder();
        if (criteria.getCreatedAfter() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            labelBuilder.append(formatter.format(criteria.getCreatedAfter())).append(": ");
        }

        //TODO : fix this to fit my current data model
        List<String> parts = new LinkedList<>();
        Optional.ofNullable(criteria.getMinAmount()).map(v -> "Min bedrag: " + v).ifPresent(parts::add);
        Optional.ofNullable(criteria.getMaxAmount()).map(v -> "Max bedrag: " + v).ifPresent(parts::add);
        Optional.ofNullable(criteria.getProductCount()).map(v -> "Aantal prod: " + v).ifPresent(parts::add);
        Optional.ofNullable(criteria.getProductName()).filter(s -> !s.isBlank()).map(v -> "Product: " + v).ifPresent(parts::add);
        Optional.ofNullable(criteria.getEmail()).filter(s -> !s.isBlank()).map(v -> "Email: " + v).ifPresent(parts::add);
        Optional.ofNullable(criteria.isDeliveredNullable()).map(v -> "Afgeleverd: " + (v ? "Ja" : "Nee")).ifPresent(parts::add);

        if (parts.isEmpty()) {
            return "Empty search history";
        }

        labelBuilder.append(String.join(", ", parts));

        return labelBuilder.toString();
    }
}
