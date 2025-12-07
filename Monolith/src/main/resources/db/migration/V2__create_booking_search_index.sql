-- Создаем индекс для быстрого поиска активных бронирований
-- Это улучшит производительность проверки конфликтов
CREATE INDEX IF NOT EXISTS idx_bookings_user_ticket_status 
ON bookings (user_id, ticket_id, status);