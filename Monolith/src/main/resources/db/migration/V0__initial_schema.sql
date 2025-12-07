-- V0: Initial schema (users, events, tickets, bookings)

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    event_date TIMESTAMP,
    image_url VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tickets (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    type VARCHAR(100),
    price DOUBLE PRECISION NOT NULL,
    place VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_tickets_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_tickets_event_id ON tickets(event_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);


CREATE TABLE IF NOT EXISTS bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    ticket_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT uk_bookings_ticket UNIQUE (ticket_id)
);

CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_ticket_id ON bookings(ticket_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);


COMMENT ON TABLE users IS 'Users';
COMMENT ON TABLE events IS 'Events';
COMMENT ON TABLE tickets IS 'Tickets';
COMMENT ON TABLE bookings IS 'Bookings';