package com.eventmanagement.controller;

import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.EventDAOImpl;
import com.eventmanagement.model.Event;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/events/*")
public class EventServlet extends BaseServlet {
    
    private final EventDAO eventDAO;
    
    public EventServlet() {
        this.eventDAO = new EventDAOImpl();
    }
    
    public EventServlet(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all events
                List<Event> events = eventDAO.getAllEvents();
                sendJsonResponse(response, events);
            } else {
                // Get event by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int eventId = Integer.parseInt(pathParts[1]);
                    Event event = eventDAO.getEventById(eventId);
                    if (event != null) {
                        sendJsonResponse(response, event);
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    try {
        Event newEvent = parseJsonRequest(request, Event.class);
        
        // Debug: Print received data
        System.out.println("=== CREATE EVENT ATTEMPT ===");
        System.out.println("Name: " + newEvent.getName());
        System.out.println("Description: " + newEvent.getDescription());
        System.out.println("Event Date: " + newEvent.getEventDate());
        System.out.println("Location: " + newEvent.getLocation());
        System.out.println("Available Tickets: " + newEvent.getAvailableTickets());
        System.out.println("Ticket Price: " + newEvent.getTicketPrice());
        
        // Validation
        if (newEvent.getName() == null || newEvent.getName().trim().isEmpty()) {
            System.out.println("Validation failed: Name is empty");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event name is required");
            return;
        }
        if (newEvent.getEventDate() == null) {
            System.out.println("Validation failed: Event date is null");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event date is required");
            return;
        }
        if (newEvent.getLocation() == null || newEvent.getLocation().trim().isEmpty()) {
            System.out.println("Validation failed: Location is empty");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Location is required");
            return;
        }
        if (newEvent.getAvailableTickets() <= 0) {
            System.out.println("Validation failed: Invalid ticket count: " + newEvent.getAvailableTickets());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Available tickets must be greater than 0");
            return;
        }
        
        System.out.println("Calling eventDAO.addEvent...");
        if (eventDAO.addEvent(newEvent)) {
            System.out.println("Event created successfully with ID: " + newEvent.getId());
            response.setStatus(HttpServletResponse.SC_CREATED);
            sendJsonResponse(response, newEvent);
        } else {
            System.out.println("eventDAO.addEvent returned false");
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create event");
        }
    } catch (Exception e) {
        System.err.println("EXCEPTION in doPost: " + e.getMessage());
        e.printStackTrace();
        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request: " + e.getMessage());
    }
}
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            
            int eventId = Integer.parseInt(pathParts[1]);
            Event existingEvent = eventDAO.getEventById(eventId);
            if (existingEvent == null) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
                return;
            }
            
            Event updatedEvent = parseJsonRequest(request, Event.class);
            updatedEvent.setId(eventId);
            
            if (eventDAO.updateEvent(updatedEvent)) {
                sendJsonResponse(response, updatedEvent);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update event");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            
            int eventId = Integer.parseInt(pathParts[1]);
            
            if (eventDAO.deleteEvent(eventId)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Event not found or could not be deleted");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}
