package com.eventmanagement.dao;

import com.eventmanagement.model.Booking;
import com.eventmanagement.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {
    
    // Fixed queries joining with users table
    private static final String GET_ALL_BOOKINGS = 
       "SELECT b.*, u.username, u.email, u.full_name, e.name as event_name, e.date_time as event_date " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN events e ON b.event_id = e.id " +
        "ORDER BY b.booking_date DESC";
    
    private static final String GET_BOOKING_BY_ID = 
        "SELECT b.*, u.username, u.email, u.full_name, e.name as event_name, e.date_time as event_date " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN events e ON b.event_id = e.id " +
        "WHERE b.id = ?";
    
    private static final String GET_BOOKINGS_BY_EVENT_ID = 
        "SELECT b.*, u.username, u.email, u.full_name, e.name as event_name, e.date_time as event_date " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN events e ON b.event_id = e.id " +
        "WHERE b.event_id = ? ORDER BY b.booking_date DESC";
    
    private static final String GET_BOOKINGS_BY_EMAIL = 
       "SELECT b.*, u.username, u.email, u.full_name, e.name as event_name, e.date_time as event_date " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN events e ON b.event_id = e.id " +
        "WHERE u.email = ? ORDER BY b.booking_date DESC";
    
    private static final String INSERT_BOOKING = 
        "INSERT INTO bookings (user_id, event_id, number_of_tickets, total_amount, status) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_BOOKING = 
        "UPDATE bookings SET user_id = ?, event_id = ?, number_of_tickets = ?, total_amount = ?, status = ? WHERE id = ?";
    
    private static final String CANCEL_BOOKING = 
        "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?";
    
    private static final String GET_BOOKINGS_BY_USER_ID = 
    "SELECT b.*, u.username, u.email, u.full_name, e.name as event_name, e.date_time as event_date " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.id " +
        "JOIN events e ON b.event_id = e.id " +
        "WHERE b.user_id = ? ORDER BY b.booking_date DESC";
    
    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_BOOKINGS)) {
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public Booking getBookingById(int id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOKING_BY_ID)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractBookingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> getBookingsByEventId(int eventId) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOKINGS_BY_EVENT_ID)) {
            
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBookingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingsByCustomerEmail(String email) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BOOKINGS_BY_EMAIL)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBookingFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public int createBooking(Booking booking) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getEventId());
            stmt.setInt(3, booking.getNumberOfTickets());
            stmt.setDouble(4, booking.getTotalAmount());
            stmt.setString(5, booking.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateBooking(Booking booking) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOKING)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getEventId());
            stmt.setInt(3, booking.getNumberOfTickets());
            stmt.setDouble(4, booking.getTotalAmount());
            stmt.setString(5, booking.getStatus());
            stmt.setInt(6, booking.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CANCEL_BOOKING)) {
            
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
    booking.setId(rs.getInt("id"));
    booking.setUserId(rs.getInt("user_id"));
    booking.setEventId(rs.getInt("event_id"));
    booking.setNumberOfTickets(rs.getInt("number_of_tickets"));
    booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
    booking.setTotalAmount(rs.getDouble("total_amount"));
    booking.setStatus(rs.getString("status"));
    
    // Set customer info from users table
    booking.setCustomerName(rs.getString("full_name"));
    booking.setCustomerEmail(rs.getString("email"));
    
    // Set event info from events table
    booking.setEventName(rs.getString("event_name"));  // You need to add this field to Booking.java
    
    return booking;
    }
    
    @Override
public List<Booking> getBookingsByUserId(int userId) {
    List<Booking> bookings = new ArrayList<>();
    try (Connection conn = DatabaseUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(GET_BOOKINGS_BY_USER_ID)) {
        
        stmt.setInt(1, userId);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return bookings;
}
}