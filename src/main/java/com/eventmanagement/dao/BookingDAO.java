package com.eventmanagement.dao;

import com.eventmanagement.model.Booking;
import java.util.List;

public interface BookingDAO {
    List<Booking> getAllBookings();
    List<Booking> getBookingsByUserId(int userId);
    Booking getBookingById(int id);
    List<Booking> getBookingsByEventId(int eventId);
    List<Booking> getBookingsByCustomerEmail(String email);
    int createBooking(Booking booking);
    boolean updateBooking(Booking booking);
    boolean cancelBooking(int bookingId);
}
