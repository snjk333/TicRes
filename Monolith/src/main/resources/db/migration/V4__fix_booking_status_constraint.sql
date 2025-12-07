-- Удаляем старый constraint если существует
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS bookings_status_check;

-- Создаем новый constraint с правильными значениями
ALTER TABLE bookings ADD CONSTRAINT bookings_status_check 
CHECK (status IN ('CREATED', 'PAID', 'CANCELLED', 'EXPIRED', 'WAITING_FOR_PAYMENT'));
