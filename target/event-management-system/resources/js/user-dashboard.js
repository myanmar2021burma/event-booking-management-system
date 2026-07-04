// User Dashboard JavaScript
const API_BASE_URL = '/event-management-system/api';
let currentUser = null;
let allEvents = [];
let currentEvent = null;

// Check authentication on page load
document.addEventListener('DOMContentLoaded', async function() {
    await checkAuth();
    setupMenuNavigation();
    loadEvents();
});

// Check if user is authenticated
async function checkAuth() {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/session`);
        const data = await response.json();
        
        if (!data.loggedIn || data.userType !== 'user') {
            // Not logged in or not a user, redirect to login
            window.location.href = 'login.html';
            return;
        }
        
        currentUser = data;
        console.log('Current user data:', currentUser); // Add this to see what's in currentUser
        document.getElementById('user-name').textContent = data.fullName || data.username;
        loadUserProfile();
    } catch (error) {
        console.error('Auth check failed:', error);
        window.location.href = 'login.html';
    }
}

// Format MMK without decimals
function formatMMK(amount) {
    // Remove decimals and format as whole number with thousand separators
    return Math.round(amount).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// Load user profile
function loadUserProfile() {
    document.getElementById('profile-name').textContent = currentUser.fullName || 'N/A';
    document.getElementById('profile-email').textContent = `Email: ${currentUser.email || 'N/A'}`;
    document.getElementById('profile-username').textContent = `Username: @${currentUser.username}`;
    document.getElementById('profile-phone').textContent = `Phone: ${currentUser.phone || 'Not provided'}`;
    
    // Also update the navbar greeting if needed
    document.getElementById('user-name').textContent = currentUser.fullName || currentUser.username;
}

// Setup menu navigation
function setupMenuNavigation() {
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(item => {
        item.addEventListener('click', function() {
            // Remove active class from all items
            menuItems.forEach(mi => mi.classList.remove('active'));
            // Add active to clicked item
            this.classList.add('active');
            
            // Hide all sections
            document.querySelectorAll('.content-section').forEach(section => {
                section.classList.remove('active');
            });
            
            // Show selected section
            const sectionId = this.getAttribute('data-section');
            document.getElementById(sectionId).classList.add('active');
            
            // Load data for specific sections
            if (sectionId === 'my-bookings') {
                loadMyBookings();
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
        eventsGrid.innerHTML = '<div class="empty-state"><h3>Failed to load events</h3><p>Please try again later</p></div>';
    }
}

// Display events
function displayEvents(events) {
    const eventsGrid = document.getElementById('events-grid');
    
    if (events.length === 0) {
        eventsGrid.innerHTML = '<div class="empty-state"><h3>No events available</h3><p>Check back later for new events!</p></div>';
        return;
    }
    
    eventsGrid.innerHTML = events.map(event => `
        <div class="event-card">
            <div class="event-header">
                <div>
                    <h3 class="event-title">${escapeHtml(event.name)}</h3>
                    <p class="event-date">📅 ${formatDate(event.eventDate)}</p>
                </div>
            </div>
            <p class="event-description">${escapeHtml(event.description)}</p>
            <div class="event-details">
                <div class="event-detail">
                    <span class="event-detail-icon">📍</span>
                    <span>${escapeHtml(event.location)}</span>
                </div>
                <div class="event-detail">
                    <span class="event-detail-icon">🎟️</span>
                    <span>${event.availableTickets} tickets available</span>
                </div>
            </div>
            <div class="event-footer">
                <span class="event-price">${formatMMK(event.ticketPrice)} MMK</span>
                <button class="btn btn-primary" onclick="openBookingModal(${event.id})" ${event.availableTickets === 0 ? 'disabled' : ''}>
                    ${event.availableTickets === 0 ? 'Sold Out' : 'Book Now'}
                </button>
            </div>
        </div>
    `).join('');
}

// Filter events
function filterEvents() {
    const searchTerm = document.getElementById('event-search').value.toLowerCase();
    const filtered = allEvents.filter(event => 
        event.name.toLowerCase().includes(searchTerm) ||
        event.description.toLowerCase().includes(searchTerm) ||
        event.location.toLowerCase().includes(searchTerm)
    );
    displayEvents(filtered);
}

// Open booking modal
function openBookingModal(eventId) {
    currentEvent = allEvents.find(e => e.id === eventId);
    if (!currentEvent) return;
    
    const modal = document.getElementById('booking-modal');
    const modalDetails = document.getElementById('modal-event-details');
    
    modalDetails.innerHTML = `
        <div style="margin-bottom: 20px;">
            <h3>${escapeHtml(currentEvent.name)}</h3>
            <p style="color: var(--grey-light); margin-top: 5px;">${formatDate(currentEvent.eventDate)}</p>
            <p style="color: var(--grey-light);">📍 ${escapeHtml(currentEvent.location)}</p>
            <p style="color: var(--gold-primary); font-size: 1.2rem; margin-top: 10px;">${formatMMK(currentEvent.ticketPrice)} MMK per ticket</p>
        </div>
    `;
    
    document.getElementById('num-tickets').value = 1;
    document.getElementById('num-tickets').max = currentEvent.availableTickets;
    updateTotalAmount();
    
    // Setup ticket change listener
    document.getElementById('num-tickets').addEventListener('input', updateTotalAmount);
    
    modal.classList.add('active');
    
    // Setup form submission
    const form = document.getElementById('booking-form');
    form.onsubmit = handleBooking;
}

// Close booking modal
function closeBookingModal() {
    document.getElementById('booking-modal').classList.remove('active');
}

// Update total amount
function updateTotalAmount() {
    const numTickets = parseInt(document.getElementById('num-tickets').value) || 0;
    const total = numTickets * currentEvent.ticketPrice;
    document.getElementById('total-amount').value = `${formatMMK(total)} MMK`;
}

// Handle booking
async function handleBooking(e) {
    e.preventDefault();
    
    const numTickets = parseInt(document.getElementById('num-tickets').value);
    
    if (numTickets > currentEvent.availableTickets) {
        alert('Not enough tickets available!');
        return;
    }
    
    const bookingData = {
        eventId: currentEvent.id,
        customerName: currentUser.fullName,
        customerEmail: currentUser.email || currentUser.username + '@example.com',
        numberOfTickets: numTickets
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookingData)
        });
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Booking failed');
        }
        
        const booking = await response.json();
        alert(`Booking successful! Your booking ID is: ${booking.id}`);
        closeBookingModal();
        loadEvents(); // Refresh events
        loadMyBookings(); // Refresh bookings
    } catch (error) {
        console.error('Booking error:', error);
        alert('Booking failed: ' + error.message);
    }
}

// Load my bookings
async function loadMyBookings() {
    const bookingsList = document.getElementById('my-bookings-list');
    bookingsList.innerHTML = '<div class="loading">Loading your bookings...</div>';
    
    try {
        // Get the user ID from currentUser (which comes from session)
        if (!currentUser.userId) {
            console.error('User ID not found in currentUser:', currentUser);
            bookingsList.innerHTML = '<div class="empty-state"><h3>Error: User ID not found</h3></div>';
            return;
        }
        
        const response = await fetch(`${API_BASE_URL}/bookings/?userId=${currentUser.userId}`);
        if (!response.ok) throw new Error('Failed to load bookings');
        
        const bookings = await response.json();
        displayBookings(bookings, bookingsList);
    } catch (error) {
        console.error('Error loading bookings:', error);
        bookingsList.innerHTML = '<div class="empty-state"><h3>Failed to load bookings</h3></div>';
    }
}

// Search my bookings (filter within user's own bookings)
function searchMyBookings() {
    const searchTerm = document.getElementById('booking-search').value.toLowerCase();
    
    // If search is empty, just reload normal bookings
    if (!searchTerm) {
        loadMyBookings();
        return;
    }
    
    const bookingsList = document.getElementById('my-bookings-list');
    bookingsList.innerHTML = '<div class="loading">Searching your bookings...</div>';
    
    // Get current user's bookings
    fetch(`${API_BASE_URL}/bookings/?userId=${currentUser.userId}`)
        .then(response => response.json())
        .then(bookings => {
            // Filter bookings by event name or date
            const filtered = bookings.filter(booking => 
                (booking.eventName || '').toLowerCase().includes(searchTerm) ||
                formatDate(booking.bookingDate).toLowerCase().includes(searchTerm) ||
                booking.id.toString().includes(searchTerm)
            );
            
            if (filtered.length === 0) {
                bookingsList.innerHTML = '<div class="empty-state"><h3>No matching bookings found</h3></div>';
            } else {
                displayBookings(filtered, bookingsList);
            }
        })
        .catch(error => {
            console.error('Error searching bookings:', error);
            bookingsList.innerHTML = '<div class="empty-state"><h3>Search failed</h3></div>';
        });
}

// Display bookings
function displayBookings(bookings, container) {
    if (bookings.length === 0) {
        container.innerHTML = '<div class="empty-state"><h3>No bookings found</h3><p>Book your first event now!</p></div>';
        return;
    }
    
    container.innerHTML = bookings.map(booking => `
        <div class="booking-card">
            <div class="booking-info">
                <div class="booking-id">Booking #${booking.id}</div>
                <div class="booking-event">${escapeHtml(booking.eventName || 'Event')}</div>
                <div class="booking-details">
                    <span>🎟️ ${booking.numberOfTickets} tickets</span>
                    <span>💰 ${formatMMK(booking.totalAmount)} MMK</span>
                    <span>📅 ${formatDate(booking.bookingDate)}</span>
                </div>
            </div>
            <div class="booking-actions">
                <span class="booking-status status-${booking.status.toLowerCase()}">${booking.status}</span>
                ${booking.status === 'CONFIRMED' ? `
                    <button class="btn btn-danger" onclick="cancelBooking(${booking.id})">Cancel</button>
                ` : ''}
            </div>
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
        
        if (!response.ok) throw new Error('Failed to cancel booking');
        
        alert('Booking cancelled successfully!');
        loadMyBookings();
        loadEvents(); // Refresh events to show updated ticket count
    } catch (error) {
        console.error('Error cancelling booking:', error);
        alert('Failed to cancel booking');
    }
}

// Logout
async function logout() {
    try {
        await fetch(`${API_BASE_URL}/auth/logout`, { method: 'POST' });
    } catch (error) {
        console.error('Logout error:', error);
    }
    window.location.href = 'home.html';
}

// Utility functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}