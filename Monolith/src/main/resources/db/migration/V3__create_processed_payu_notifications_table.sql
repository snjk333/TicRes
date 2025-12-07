-- Создание таблицы для отслеживания обработанных PayU уведомлений
-- Защита от повторной обработки webhook (idempotency)

CREATE TABLE IF NOT EXISTS processed_payu_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payu_order_id VARCHAR(100) NOT NULL UNIQUE,
    booking_id UUID NOT NULL,
    payment_status VARCHAR(50),
    amount BIGINT,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_processed_payu_notifications_booking 
        FOREIGN KEY (booking_id) 
        REFERENCES bookings(id) 
        ON DELETE CASCADE
);

-- Индекс для быстрого поиска по payu_order_id
CREATE INDEX IF NOT EXISTS idx_payu_order_id 
    ON processed_payu_notifications(payu_order_id);

-- Индекс для поиска по booking_id
CREATE INDEX IF NOT EXISTS idx_processed_notifications_booking 
    ON processed_payu_notifications(booking_id);

-- Индекс для очистки старых записей
CREATE INDEX IF NOT EXISTS idx_processed_notifications_date 
    ON processed_payu_notifications(processed_at);

COMMENT ON TABLE processed_payu_notifications IS 'Хранит информацию об обработанных PayU webhook уведомлениях для предотвращения дубликатов';
COMMENT ON COLUMN processed_payu_notifications.payu_order_id IS 'Уникальный ID заказа в системе PayU';
COMMENT ON COLUMN processed_payu_notifications.booking_id IS 'ID бронирования в нашей системе';
COMMENT ON COLUMN processed_payu_notifications.payment_status IS 'Статус платежа (COMPLETED, CANCELED)';
COMMENT ON COLUMN processed_payu_notifications.amount IS 'Сумма платежа в грошах (1 PLN = 100 groszy)';
