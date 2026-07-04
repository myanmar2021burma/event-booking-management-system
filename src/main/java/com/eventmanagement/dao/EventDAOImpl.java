package com.eventmanagement.dao;

import com.eventmanagement.model.Event;
import com.eventmanagement.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAOImpl implements EventDAO {
    
    // Fixed queries to match your actual table columns
    private static final String GET_ALL_EVENTS = "SELECT * FROM events ORDER BY date_time ASC";
    private static final String GET_EVENT_BY_ID = "SELECT * FROM events WHERE id = ?";
    private static final String INSERT_EVENT = "INSERT INTO events (name, description, date_time, venue, total_seats, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_EVENT = "UPDATE events SET name = ?, description = ?, date_time = ?, venue = ?, total_seats = ?, available_seats = ?, price = ? WHERE id = ?";
    private static final String DELETE_EVENT = "DELETE FROM events WHERE id = ?";
    private static final String UPDATE_AVAILABLE_SEATS = "UPDATE events SET available_seats = available_seats - ? WHERE id = ? AND available_seats >= ?";

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_EVENTS)) {
            
            while (rs.next()) {
                events.add(extractEventFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event getEventById(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_EVENT_BY_ID)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEventFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

@Override
public boolean addEvent(Event event) {
    String sql = "INSERT INTO events (name, description, date_time, venue, total_seats, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        stmt.setString(1, event.getName());
        stmt.setString(2, event.getDescription());
        stmt.setTimestamp(3, Timestamp.valueOf(event.getEventDate()));
        stmt.setString(4, event.getLocation());
        stmt.setInt(5, event.getAvailableTickets()); // total_seats
        stmt.setInt(6, event.getAvailableTickets()); // available_seats
        stmt.setDouble(7, event.getTicketPrice());
        
        System.out.println("Executing update...");
        int affectedRows = stmt.executeUpdate();
        System.out.println("Affected rows: " + affectedRows);
        
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                    System.out.println("Generated ID: " + event.getId());
                    return true;
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("SQL ERROR in addEvent:");
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Message: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    @Override
    public boolean updateEvent(Event event) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EVENT)) {
            
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getEventDate()));
            stmt.setString(4, event.getLocation());
            stmt.setInt(5, event.getTotalSeats());
            stmt.setInt(6, event.getAvailableTickets());
            stmt.setDouble(7, event.getTicketPrice());
            stmt.setInt(8, event.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEvent(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_EVENT)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAvailableTickets(int eventId, int ticketsBooked) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_AVAILABLE_SEATS)) {
            
            stmt.setInt(1, ticketsBooked);
            stmt.setInt(2, eventId);
            stmt.setInt(3, ticketsBooked);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getTimestamp("date_time").toLocalDateTime());
        event.setLocation(rs.getString("venue"));
        event.setAvailableTickets(rs.getInt("available_seats"));
        event.setTicketPrice(rs.getDouble("price"));
        // You'll need to add totalSeats field
        return event;
    }
}