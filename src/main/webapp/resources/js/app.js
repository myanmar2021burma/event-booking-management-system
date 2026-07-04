// API Base URL - Update this based on your deployment
const API_BASE_URL = '/event-management-system/api';

// Global state
let currentEvent = null;
let allEvents = [];

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeNavigation();
    loadEvents();
    setupEventForm();
    setupBookingForm();
});

// Navigation
function initializeNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remove active class from all links and sections
            navLinks.forEach(l => l.classList.remove('active'));
            document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
            
            // Add active class to clicked link
            this.classList.add('active');
            
            // Show corresponding section
            const targetId = this.getAttribute('href').substring(1);
            const targetSection = document.getElementById(targetId);
            if (targetSection) {
                targetSection.classList.add('active');
                
                // Load data based on section
                if (targetId === 'admin') {
                    loadAdminEvents();
                    loadAllBookings();
                }
            }
        });
    });
}

// Load all events
async function loadEvents() {
    const eventsGrid = document.getElementById('events-grid');
    eventsGrid.innerHTML = '<div class="loading">Loading events...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/events/`);
        if (!response.ok) throw new Error('Failed to load events');
        
        allEvents = await response.json();
        displayEvents(allEvents);
    } catch (error) {
        console.error('Error loading events:', error);
        eventsGrid.innerHTML = `
            <div class="empty-state">
                <h3>Failed to load events</h3>
                <p>Please try again later</p>
            </div>
        `;
    }
}

// Display events in grid
function displayEvents(events) {
    const eventsGrid = document.getElementById('events-grid');
    
    if (events.length === 0) {
        eventsGrid.innerHTML = `
            <div class="empty-state">
                <h3>No events available</h3>
                <p>Check back later for upcoming events</p>
            </div>
        `;
        return;
    }
    
    eventsGrid.innerHTML = events.map(event => `
        <div class="event-card">
            <div class="event-header">
                <h3>${escapeHtml(event.name)}</h3>
                <div class="event-date">📅 ${formatDateTime(event.eventDate)}</div>
            </div>
            <div class="event-body">
                <p class="event-description">${escapeHtml(event.description || 'No description available')}</p>
                <div class="event-info">
                    <div class="event-info-item">
                        <span>📍 ${escapeHtml(event.location)}</span>
                    </div>
                    <div class="event-info-item">
                        <span>🎫 ${event.availableTickets} tickets available</span>
                    </div>
                </div>
                <div class="event-footer">
                    <div class="event-price">$${event.ticketPrice.toFixed(2)}</div>
                    <button class="btn btn-primary" onclick="openBookingModal(${event.id})" 
                            ${event.availableTickets === 0 ? 'disabled' : ''}>
                        ${event.availableTickets === 0 ? 'Sold Out' : 'Book Now'}
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Open booking modal
function openBookingModal(eventId) {
    currentEvent = allEvents.find(e => e.id === eventId);
    if (!currentEvent) return;
    
    const modal = document.getElementById('booking-modal');
    const eventDetails = document.getElementById('modal-event-details');
    
    eventDetails.innerHTML = `
        <h3>${escapeHtml(currentEvent.name)}</h3>
        <p><strong>Date:</strong> ${formatDateTime(currentEvent.eventDate)}</p>
        <p><strong>Location:</strong> ${escapeHtml(currentEvent.location)}</p>
        <p><strong>Price per ticket:</strong> $${currentEvent.ticketPrice.toFixed(2)}</p>
        <p><strong>Available tickets:</strong> ${currentEvent.availableTickets}</p>
    `;
    
    document.getElementById('booking-event-id').value = eventId;
    document.getElementById('num-tickets').max = currentEvent.availableTickets;
    
    modal.classList.add('active');
}

// Close booking modal
function closeBookingModal() {
    const modal = document.getElementById('booking-modal');
    modal.classList.remove('active');
    document.getElementById('booking-form').reset();
    currentEvent = null;
}

// Setup booking form
function setupBookingForm() {
    const form = document.getElementById('booking-form');
    const numTicketsInput = document.getElementById('num-tickets');
    const totalAmountSpan = document.getElementById('total-amount');
    
    // Update total amount when number of tickets changes
    numTicketsInput.addEventListener('input', function() {
        if (currentEvent) {
            const total = this.value * currentEvent.ticketPrice;
            totalAmountSpan.textContent = total.toFixed(2);
        }
    });
    
    // Handle form submission
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const booking = {
            eventId: parseInt(document.getElementById('booking-event-id').value),
            customerName: document.getElementById('customer-name').value,
            customerEmail: document.getElementById('customer-email').value,
            numberOfTickets: parseInt(document.getElementById('num-tickets').value)
        };
        
        try {
            const response = await fetch(`${API_BASE_URL}/bookings/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(booking)
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Failed to create booking');
            }
            
            const result = await response.json();
            alert(`Booking successful! Your booking ID is: ${result.id}`);
            closeBookingModal();
            loadEvents(); // Reload events to update available tickets
        } catch (error) {
            console.error('Error creating booking:', error);
            alert('Failed to create booking: ' + error.message);
        }
    });
}

// Search bookings by email
async function searchBookings() {
    const email = document.getElementById('search-email').value.trim();
    const bookingsList = document.getElementById('bookings-list');
    
    if (!email) {
        alert('Please enter an email address');
        return;
    }
    
    bookingsList.innerHTML = '<div class="loading">Loading bookings...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/?email=${encodeURIComponent(email)}`);
        if (!response.ok) throw new Error('Failed to load bookings');
        
        const bookings = await response.json();
        displayBookings(bookings, bookingsList);
    } catch (error) {
        console.error('Error loading bookings:', error);
        bookingsList.innerHTML = `
            <div class="empty-state">
                <h3>Failed to load bookings</h3>
                <p>Please try again later</p>
            </div>
        `;
    }
}

// Display bookings
function displayBookings(bookings, container) {
    if (bookings.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <h3>No bookings found</h3>
                <p>You haven't made any bookings yet</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = bookings.map(booking => `
        <div class="booking-card">
            <div class="booking-header">
                <span class="booking-id">Booking #${booking.id}</span>
                <span class="booking-status ${booking.status.toLowerCase()}">${booking.status}</span>
            </div>
            <div class="booking-details">
                <div class="booking-detail">
                    <strong>Customer:</strong> ${escapeHtml(booking.customerName)}
                </div>
                <div class="booking-detail">
                    <strong>Email:</strong> ${escapeHtml(booking.customerEmail)}
                </div>
                <div class="booking-detail">
                    <strong>Tickets:</strong> ${booking.numberOfTickets}
                </div>
                <div class="booking-detail">
                    <strong>Total:</strong> $${booking.totalAmount.toFixed(2)}
                </div>
                <div class="booking-detail">
                    <strong>Booking Date:</strong> ${formatDateTime(booking.bookingDate)}
                </div>
            </div>
            ${booking.status === 'CONFIRMED' ? `
                <div class="booking-actions">
                    <button class="btn btn-danger" onclick="cancelBooking(${booking.id})">Cancel Booking</button>
                </div>
            ` : ''}
        </div>
    `).join('');
}

// Cancel booking
async function cancelBooking(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/${bookingId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Failed to cancel booking');
        }
        
        alert('Booking cancelled successfully');
        searchBookings(); // Reload bookings
        loadEvents(); // Reload events to update available tickets
    } catch (error) {
        console.error('Error cancelling booking:', error);
        alert('Failed to cancel booking: ' + error.message);
    }
}

// Admin: Show tab
function showAdminTab(tabName) {
    // Remove active class from all tabs
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.admin-tab').forEach(tab => tab.classList.remove('active'));
    
    // Add active class to selected tab
    event.target.classList.add('active');
    document.getElementById(tabName).classList.add('active');
}

// Admin: Setup event form
function setupEventForm() {
    const form = document.getElementById('event-form');
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const eventData = {
            name: document.getElementById('event-name').value,
            description: document.getElementById('event-description').value,
            eventDate: document.getElementById('event-date').value,
            location: document.getElementById('event-location').value,
            availableTickets: parseInt(document.getElementById('event-tickets').value),
            ticketPrice: parseFloat(document.getElementById('event-price').value)
        };
        
        try {
            const response = await fetch(`${API_BASE_URL}/events/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(eventData)
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Failed to create event');
            }
            
            alert('Event created successfully!');
            form.reset();
            loadEvents();
            loadAdminEvents();
        } catch (error) {
            console.error('Error creating event:', error);
            alert('Failed to create event: ' + error.message);
        }
    });
}

// Admin: Load events for management
async function loadAdminEvents() {
    const adminEventsList = document.getElementById('admin-events-list');
    adminEventsList.innerHTML = '<div class="loading">Loading events...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/events/`);
        if (!response.ok) throw new Error('Failed to load events');
        
        const events = await response.json();
        displayAdminEvents(events);
    } catch (error) {
        console.error('Error loading admin events:', error);
        adminEventsList.innerHTML = `
            <div class="empty-state">
                <h3>Failed to load events</h3>
                <p>Please try again later</p>
            </div>
        `;
    }
}

// Admin: Display events
function displayAdminEvents(events) {
    const adminEventsList = document.getElementById('admin-events-list');
    
    if (events.length === 0) {
        adminEventsList.innerHTML = `
            <div class="empty-state">
                <h3>No events found</h3>
                <p>Create your first event</p>
            </div>
        `;
        return;
    }
    
    adminEventsList.innerHTML = events.map(event => `
        <div class="admin-event-card">
            <div class="admin-event-info">
                <h4>${escapeHtml(event.name)}</h4>
                <div class="admin-event-meta">
                    <span>📅 ${formatDateTime(event.eventDate)}</span>
                    <span>📍 ${escapeHtml(event.location)}</span>
                    <span>🎫 ${event.availableTickets} tickets</span>
                    <span>💰 $${event.ticketPrice.toFixed(2)}</span>
                </div>
            </div>
            <div class="admin-event-actions">
                <button class="btn btn-danger" onclick="deleteEvent(${event.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

// Admin: Delete event
async function deleteEvent(eventId) {
    if (!confirm('Are you sure you want to delete this event? This will also delete all associated bookings.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Failed to delete event');
        }
        
        alert('Event deleted successfully');
        loadEvents();
        loadAdminEvents();
    } catch (error) {
        console.error('Error deleting event:', error);
        alert('Failed to delete event: ' + error.message);
    }
}

// Admin: Load all bookings
async function loadAllBookings() {
    const adminBookingsList = document.getElementById('admin-bookings-list');
    adminBookingsList.innerHTML = '<div class="loading">Loading bookings...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/`);
        if (!response.ok) throw new Error('Failed to load bookings');
        
        const bookings = await response.json();
        displayBookings(bookings, adminBookingsList);
    } catch (error) {
        console.error('Error loading all bookings:', error);
        adminBookingsList.innerHTML = `
            <div class="empty-state">
                <h3>Failed to load bookings</h3>
                <p>Please try again later</p>
            </div>
        `;
    }
}

// Utility: Format date and time
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Utility: Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('booking-modal');
    if (event.target === modal) {
        closeBookingModal();
    }
}
