package com.eventmanagement.dao;

import com.eventmanagement.model.Event;
import java.util.List;

public interface EventDAO {
    List<Event> getAllEvents();
    Event getEventById(int id);
    boolean addEvent(Event event);
    boolean updateEvent(Event event);
    boolean deleteEvent(int id);
    boolean updateAvailableTickets(int eventId, int ticketsBooked);
}
