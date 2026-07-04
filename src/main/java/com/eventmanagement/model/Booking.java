package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int eventId;
     private String eventName; 
    private String customerName;
    private String customerEmail;
    private int numberOfTickets;
    private LocalDateTime bookingDate;
    private double totalAmount;
    private String status; // CONFIRMED, CANCELLED

    public Booking() {
    }

    public Booking(int id, int eventId, String customerName, String customerEmail, 
                  int numberOfTickets, LocalDateTime bookingDate, double totalAmount, String status) {
        this.id = id;
        this.eventId = eventId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.numberOfTickets = numberOfTickets;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {        // Added getter
        return userId;
    }

    public void setUserId(int userId) {   // Added setter
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

        public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", numberOfTickets=" + numberOfTickets +
                ", bookingDate=" + bookingDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
