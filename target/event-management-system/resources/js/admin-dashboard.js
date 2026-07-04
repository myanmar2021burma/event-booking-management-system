// Admin Dashboard JavaScript
const API_BASE_URL = '/event-management-system/api';
let currentAdmin = null;
let allEvents = [];
let allBookings = [];

// Check authentication on page load
document.addEventListener('DOMContentLoaded', async function() {
    await checkAuth();
    setupMenuNavigation();
    loadDashboardData();
    setupEventForm();
});

// Check if admin is authenticated
async function checkAuth() {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/session`);
        const data = await response.json();
        
        if (!data.loggedIn || data.userType !== 'admin') {
            // Not logged in or not an admin, redirect to login
            window.location.href = 'login.html';
            return;
        }
        
        currentAdmin = data;
        document.getElementById('admin-name').textContent = data.fullName || data.username;
    } catch (error) {
        console.error('Auth check failed:', error);
        window.location.href = 'login.html';
    }
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
            if (sectionId === 'manage-events') {
                loadAllEvents();
            } else if (sectionId === 'all-bookings') {
                loadAllBookings();
            } else if (sectionId === 'users') {
                loadAllUsers();
            }
        });
    });
}

// Load dashboard data
async function loadDashboardData() {
    await Promise.all([
        loadStats(),
        loadRecentBookings()
    ]);
}

// Format MMK without decimals
function formatMMK(amount) {
    // Remove decimals and format as whole number
    return Math.round(amount).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// Load statistics
async function loadStats() {
    try {
        // Load events
        const eventsResponse = await fetch(`${API_BASE_URL}/events/`);
        const events = await eventsResponse.json();
        document.getElementById('total-events').textContent = events.length;
        
        // Load bookings
        const bookingsResponse = await fetch(`${API_BASE_URL}/bookings/`);
        const bookings = await bookingsResponse.json();
        document.getElementById('total-bookings').textContent = bookings.length;
        
        // Load users for count (this will now only return customers, not admins)
        const usersResponse = await fetch(`${API_BASE_URL}/users/`);
        const users = await usersResponse.json();
        document.getElementById('total-users').textContent = users.length;
        
        // Calculate revenue
        const revenue = bookings
            .filter(b => b.status === 'CONFIRMED')
            .reduce((sum, b) => sum + b.totalAmount, 0);
        document.getElementById('total-revenue').textContent = `${formatMMK(revenue)} MMK`;
        
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Load recent bookings
async function loadRecentBookings() {
    const container = document.getElementById('recent-bookings-list');
    container.innerHTML = '<div class="loading">Loading...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/`);
        if (!response.ok) throw new Error('Failed to load bookings');
        
        const bookings = await response.json();
        const recent = bookings.slice(0, 5); // Show last 5
        
        displayBookings(recent, container);
    } catch (error) {
        console.error('Error loading recent bookings:', error);
        container.innerHTML = '<div class="empty-state"><h3>Failed to load bookings</h3></div>';
    }
}

// Setup event form
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
            loadStats(); // Refresh stats
        } catch (error) {
            console.error('Error creating event:', error);
            alert('Failed to create event: ' + error.message);
        }
    });
}

// Load all events
async function loadAllEvents() {
    const eventsGrid = document.getElementById('admin-events-grid');
    eventsGrid.innerHTML = '<div class="loading">Loading events...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/events/`);
        if (!response.ok) throw new Error('Failed to load events');
        
        allEvents = await response.json();
        displayAdminEvents(allEvents);
    } catch (error) {
        console.error('Error loading events:', error);
        eventsGrid.innerHTML = '<div class="empty-state"><h3>Failed to load events</h3></div>';
    }
}

// Display admin events
function displayAdminEvents(events) {
    const eventsGrid = document.getElementById('admin-events-grid');
    
    if (events.length === 0) {
        eventsGrid.innerHTML = '<div class="empty-state"><h3>No events yet</h3><p>Create your first event!</p></div>';
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
                <div class="event-actions">
                    <button class="btn btn-danger" onclick="deleteEvent(${event.id})">Delete</button>
                </div>
            </div>
        </div>
    `).join('');
}

// Delete event
async function deleteEvent(eventId) {
    if (!confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete event');
        
        alert('Event deleted successfully!');
        loadAllEvents();
        loadStats();
    } catch (error) {
        console.error('Error deleting event:', error);
        alert('Failed to delete event');
    }
}

// Load all bookings
async function loadAllBookings() {
    const bookingsList = document.getElementById('all-bookings-list');
    bookingsList.innerHTML = '<div class="loading">Loading bookings...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/`);
        if (!response.ok) throw new Error('Failed to load bookings');
        
        allBookings = await response.json();
        displayBookings(allBookings, bookingsList);
    } catch (error) {
        console.error('Error loading bookings:', error);
        bookingsList.innerHTML = '<div class="empty-state"><h3>Failed to load bookings</h3></div>';
    }
}

// Display bookings
function displayBookings(bookings, container) {
    if (bookings.length === 0) {
        container.innerHTML = '<div class="empty-state"><h3>No bookings found</h3></div>';
        return;
    }
    
    container.innerHTML = bookings.map(booking => `
        <div class="booking-card">
            <div class="booking-info">
                <div class="booking-id">Booking #${booking.id}</div>
                <div class="booking-event">${escapeHtml(booking.eventName || 'Event')}</div>
                <div class="booking-details">
                    <span>👤 ${escapeHtml(booking.customerName)}</span>
                    <span>📧 ${escapeHtml(booking.customerEmail)}</span>
                    <span>🎟️ ${booking.numberOfTickets} tickets</span>
                    <span>💰 ${formatMMK(booking.totalAmount)} MMK</span>
                    <span>📅 ${formatDate(booking.bookingDate)}</span>
                </div>
            </div>
            <div class="booking-actions">
                <span class="booking-status status-${booking.status.toLowerCase()}">${booking.status}</span>
                ${booking.status === 'CONFIRMED' ? `
                    <button class="btn btn-danger" onclick="cancelBookingAdmin(${booking.id})">Cancel</button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// Cancel booking (admin)
async function cancelBookingAdmin(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/${bookingId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to cancel booking');
        
        alert('Booking cancelled successfully!');
        loadAllBookings();
        loadStats();
    } catch (error) {
        console.error('Error cancelling booking:', error);
        alert('Failed to cancel booking');
    }
}

// Search bookings by email (admin only) - Button based
async function searchBookingsByEmail() {
    const email = document.getElementById('admin-email-search').value.trim();
    const bookingsList = document.getElementById('all-bookings-list');
    
    if (!email) {
        alert('Please enter an email address');
        return;
    }
    
    bookingsList.innerHTML = '<div class="loading">Searching bookings by email...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/?email=${encodeURIComponent(email)}`);
        if (!response.ok) throw new Error('Failed to search bookings');
        
        const bookings = await response.json();
        
        if (bookings.length === 0) {
            bookingsList.innerHTML = '<div class="empty-state"><h3>No bookings found for this email</h3></div>';
        } else {
            displayBookings(bookings, bookingsList);
        }
    } catch (error) {
        console.error('Error searching bookings:', error);
        bookingsList.innerHTML = '<div class="empty-state"><h3>Search failed</h3></div>';
    }
}

// Setup real-time email search (optional)
function setupEmailSearch() {
    const searchInput = document.getElementById('admin-email-search');
    if (!searchInput) return;
    
    let searchTimeout;
    searchInput.addEventListener('keyup', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            searchBookingsByEmail();
        }, 500); // Wait 500ms after user stops typing
    });
}

// Add this to DOMContentLoaded
document.addEventListener('DOMContentLoaded', async function() {
    await checkAuth();
    setupMenuNavigation();
    loadDashboardData();
    setupEventForm();
    setupEmailSearch(); // Add this line
});// Search bookings by email (admin only)
async function searchBookingsByEmail() {
    const email = document.getElementById('admin-email-search').value.trim();
    const bookingsList = document.getElementById('all-bookings-list');

    if (!email) {
        // If empty, show all bookings
        loadAllBookings();
        return;
    }

    bookingsList.innerHTML = '<div class="loading">Searching bookings by email...</div>';

    try {
        const response = await fetch(`${API_BASE_URL}/bookings/?email=${encodeURIComponent(email)}`);
        if (!response.ok)
            throw new Error('Failed to search bookings');

        const bookings = await response.json();

        if (bookings.length === 0) {
            bookingsList.innerHTML = '<div class="empty-state"><h3>No bookings found for this email</h3></div>';
        } else {
            displayBookings(bookings, bookingsList);
        }
    } catch (error) {
        console.error('Error searching bookings:', error);
        bookingsList.innerHTML = '<div class="empty-state"><h3>Search failed</h3></div>';
    }
}

// Setup real-time email search (optional)
function setupEmailSearch() {
    const searchInput = document.getElementById('admin-email-search');
    if (!searchInput)
        return;

    let searchTimeout;
    searchInput.addEventListener('keyup', function () {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            searchBookingsByEmail();
        }, 500); // Wait 500ms after user stops typing
    });
}

// Add this to DOMContentLoaded
document.addEventListener('DOMContentLoaded', async function () {
    await checkAuth();
    setupMenuNavigation();
    loadDashboardData();
    setupEventForm();
    setupEmailSearch(); // Add this line
});


// Load all users
async function loadAllUsers() {
    const usersList = document.getElementById('users-list');
    usersList.innerHTML = '<div class="loading">Loading users...</div>';
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/`);
        if (!response.ok) throw new Error('Failed to load users');
        
        const users = await response.json();
        displayUsers(users);
        
        // Also update the total users count in stats
        document.getElementById('total-users').textContent = users.length;
        
    } catch (error) {
        console.error('Error loading users:', error);
        usersList.innerHTML = '<div class="empty-state"><h3>Failed to load users</h3><p>Please try again later</p></div>';
    }
}

// Display users
function displayUsers(users) {
    const usersList = document.getElementById('users-list');
    
    if (users.length === 0) {
        usersList.innerHTML = '<div class="empty-state"><h3>No users found</h3><p>Users will appear here when they register.</p></div>';
        return;
    }
    
    usersList.innerHTML = users.map(user => `
        <div class="user-card">
            <div class="user-avatar">👤</div>
            <div class="user-name">${escapeHtml(user.fullName || user.username)}</div>
            <div class="user-email">${escapeHtml(user.email)}</div>
            <div class="user-role">${escapeHtml(user.role)}</div>
            <div class="user-joined">Joined: ${formatDate(user.createdAt)}</div>
            ${user.role !== 'ADMIN' ? `
                <button class="btn btn-danger btn-small" onclick="deleteUser(${user.id})">Delete</button>
            ` : ''}
        </div>
    `).join('');
}

// Delete user (admin only)
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/users/${userId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete user');
        
        alert('User deleted successfully!');
        loadAllUsers(); // Refresh users list
        loadStats(); // Refresh stats
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('Failed to delete user');
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