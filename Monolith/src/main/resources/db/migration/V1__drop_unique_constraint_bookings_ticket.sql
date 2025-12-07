-- Удаляем ограничение уникальности для ticket_id в таблице bookings
-- Это позволит создавать несколько бронирований для одного билета (например, после отмены)
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS uk_bookings_ticket;