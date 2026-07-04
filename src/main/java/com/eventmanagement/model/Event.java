package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Event {
    private int id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private int availableTickets;
    private double ticketPrice;
    private int totalSeats;

    public Event() {
    }

    public Event(int id, String name, String description, LocalDateTime eventDate, 
                String location, int availableTickets, double ticketPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.availableTickets = availableTickets;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
    
    public int getTotalSeats() {
    return totalSeats;
    }
    
    public void setTotalSeats(int totalSeats) {
    this.totalSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", availableTickets=" + availableTickets +
                ", ticketPrice=" + ticketPrice +
                '}';
    }
}
