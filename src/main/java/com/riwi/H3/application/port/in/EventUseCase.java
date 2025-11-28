package com.riwi.H3.application.port.in;

import com.riwi.H3.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EventUseCase {

    Event create(Event event);
    Event update(Long id, Event event);
    Event findById(Long id);
    List<Event> findAll();
    void delete(Long id);
    Page<Event> findAll(Pageable pageable);
}
