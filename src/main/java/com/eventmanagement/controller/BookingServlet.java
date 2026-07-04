package com.eventmanagement.controller;

import com.eventmanagement.dao.BookingDAO;
import com.eventmanagement.dao.BookingDAOImpl;
import com.eventmanagement.dao.EventDAO;
import com.eventmanagement.dao.EventDAOImpl;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Event;

import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/bookings/*")
public class BookingServlet extends BaseServlet {
    
    private final BookingDAO bookingDAO;
    private final EventDAO eventDAO;
    
    public BookingServlet() {
        this.bookingDAO = new BookingDAOImpl();
        this.eventDAO = new EventDAOImpl();
    }
    
    public BookingServlet(BookingDAO bookingDAO, EventDAO eventDAO) {
        this.bookingDAO = bookingDAO;
        this.eventDAO = eventDAO;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        String email = request.getParameter("email");
        String eventId = request.getParameter("eventId");
        String userId = request.getParameter("userId"); 
        
        try {
            if (userId != null && !userId.trim().isEmpty()) {
        // Get bookings by user ID
        int id = Integer.parseInt(userId);
        List<Booking> bookings = bookingDAO.getBookingsByUserId(id);
        sendJsonResponse(response, bookings);
    } else if (email != null && !email.trim().isEmpty()) {
                // Get bookings by customer email
                List<Booking> bookings = bookingDAO.getBookingsByCustomerEmail(email);
                sendJsonResponse(response, bookings);
            } else if (eventId != null && !eventId.trim().isEmpty()) {
                // Get bookings by event ID
                int id = Integer.parseInt(eventId);
                List<Booking> bookings = bookingDAO.getBookingsByEventId(id);
                sendJsonResponse(response, bookings);
            } else if (pathInfo == null || pathInfo.equals("/")) {
                // Get all bookings
                List<Booking> bookings = bookingDAO.getAllBookings();
                sendJsonResponse(response, bookings);
            } else {
                // Get booking by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int bookingId = Integer.parseInt(pathParts[1]);
                    Booking booking = bookingDAO.getBookingById(bookingId);
                    if (booking != null) {
                        sendJsonResponse(response, booking);
                    } else {
                        sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    try {
        // Get the logged-in user's ID from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            System.err.println("No user session found for booking attempt");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to book tickets");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        System.out.println("=== BOOKING ATTEMPT ===");
        System.out.println("User ID from session: " + userId);
        
        // Parse the booking from request
        Booking newBooking = parseJsonRequest(request, Booking.class);
        
        // Set the user ID from session
        newBooking.setUserId(userId);
        
        System.out.println("Booking data received:");
        System.out.println("  Event ID: " + newBooking.getEventId());
        System.out.println("  Customer Name: " + newBooking.getCustomerName());
        System.out.println("  Customer Email: " + newBooking.getCustomerEmail());
        System.out.println("  Number of Tickets: " + newBooking.getNumberOfTickets());
        System.out.println("  User ID (from session): " + newBooking.getUserId());
        
        // Validate required fields
        if (newBooking.getEventId() <= 0) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID");
            return;
        }
        
        if (newBooking.getCustomerName() == null || newBooking.getCustomerName().trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Customer name is required");
            return;
        }
        
        if (newBooking.getCustomerEmail() == null || newBooking.getCustomerEmail().trim().isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Customer email is required");
            return;
        }
        
        if (newBooking.getNumberOfTickets() <= 0) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Number of tickets must be greater than 0");
            return;
        }
        
        // Check if event exists and has enough available tickets
        Event event = eventDAO.getEventById(newBooking.getEventId());
        if (event == null) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }
        
        System.out.println("Event found: " + event.getName());
        System.out.println("Available tickets: " + event.getAvailableTickets());
        System.out.println("Ticket price: " + event.getTicketPrice());
        
        if (event.getAvailableTickets() < newBooking.getNumberOfTickets()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Not enough tickets available");
            return;
        }
        
        // Calculate total amount and set booking details
        double totalAmount = event.getTicketPrice() * newBooking.getNumberOfTickets();
        newBooking.setTotalAmount(totalAmount);
        newBooking.setBookingDate(LocalDateTime.now());
        newBooking.setStatus("CONFIRMED");
        
        System.out.println("Total amount: " + totalAmount);
        System.out.println("Creating booking in database...");
        
        // Start transaction
        int bookingId = bookingDAO.createBooking(newBooking);
        System.out.println("createBooking returned ID: " + bookingId);
        
        if (bookingId > 0) {
            // Update available tickets
            System.out.println("Updating available tickets...");
            if (eventDAO.updateAvailableTickets(event.getId(), newBooking.getNumberOfTickets())) {
                newBooking.setId(bookingId);
                response.setStatus(HttpServletResponse.SC_CREATED);
                System.out.println("Booking successful! ID: " + bookingId);
                sendJsonResponse(response, newBooking);
            } else {
                System.err.println("Failed to update available tickets - rolling back");
                // Rollback - mark booking as failed
                bookingDAO.cancelBooking(bookingId);
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to process booking");
            }
        } else {
            System.err.println("Failed to create booking in database");
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create booking");
        }
        
    } catch (Exception e) {
        System.err.println("EXCEPTION in doPost:");
        e.printStackTrace();
        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing booking: " + e.getMessage());
    }
}
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            
            int bookingId = Integer.parseInt(pathParts[1]);
            Booking existingBooking = bookingDAO.getBookingById(bookingId);
            if (existingBooking == null) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }
            
            // Only allow updating certain fields
            Booking updatedBooking = parseJsonRequest(request, Booking.class);
            existingBooking.setCustomerName(updatedBooking.getCustomerName());
            existingBooking.setCustomerEmail(updatedBooking.getCustomerEmail());
            
            if (bookingDAO.updateBooking(existingBooking)) {
                sendJsonResponse(response, existingBooking);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update booking");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID format");
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }
            
            int bookingId = Integer.parseInt(pathParts[1]);
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            if (booking == null) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Booking not found");
                return;
            }
            
            // Check if booking is already cancelled
            if ("CANCELLED".equals(booking.getStatus())) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Booking is already cancelled");
                return;
            }
            
            // Update booking status to CANCELLED
            booking.setStatus("CANCELLED");
            if (bookingDAO.updateBooking(booking)) {
                // Return tickets to available count
                eventDAO.updateAvailableTickets(booking.getEventId(), -booking.getNumberOfTickets());
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to cancel booking");
            }
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid booking ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}
