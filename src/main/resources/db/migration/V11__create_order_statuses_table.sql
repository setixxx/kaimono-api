CREATE TABLE IF NOT EXISTS order_statuses (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT
    );

CREATE INDEX idx_order_statuses_code ON order_statuses(code);

INSERT INTO order_statuses (code, name, description) VALUES
    ('pending', 'Ожидает оплаты', 'Заказ создан, ожидает оплаты от покупателя'),
    ('paid', 'Оплачен', 'Заказ успешно оплачен'),
    ('processing', 'В обработке', 'Заказ обрабатывается и готовится к отправке'),
    ('shipped', 'Отправлен', 'Заказ отправлен курьером'),
    ('delivered', 'Доставлен', 'Заказ успешно доставлен покупателю'),
    ('cancelled', 'Отменен', 'Заказ был отменен')
    ON CONFLICT (code) DO NOTHING;