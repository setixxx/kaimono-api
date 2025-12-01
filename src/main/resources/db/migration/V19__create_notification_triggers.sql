CREATE OR REPLACE FUNCTION notify_order_status_change()
RETURNS TRIGGER AS $$
DECLARE
v_status_name VARCHAR(100);
    v_order_public_id UUID;
BEGIN
    IF NEW.status_id != OLD.status_id THEN
SELECT os.name, o.public_id
INTO v_status_name, v_order_public_id
FROM order_statuses os
         JOIN orders o ON o.id = NEW.id
WHERE os.id = NEW.status_id;

INSERT INTO notifications (user_id, order_id, type, title, message)
VALUES (
           NEW.user_id,
           NEW.id,
           'order_status_changed',
           'Статус заказа изменен',
           'Ваш заказ #' || v_order_public_id || ' изменил статус на "' || v_status_name || '"'
       );
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notify_order_status_change
    AFTER UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION notify_order_status_change();

CREATE OR REPLACE FUNCTION notify_delivery_status_change()
RETURNS TRIGGER AS $$
DECLARE
v_user_id BIGINT;
    v_order_public_id UUID;
    v_status_message TEXT;
BEGIN
    IF NEW.status != OLD.status THEN
SELECT o.user_id, o.public_id
INTO v_user_id, v_order_public_id
FROM orders o
WHERE o.id = NEW.order_id;

v_status_message := CASE NEW.status
            WHEN 'pending' THEN 'Ваш заказ #' || v_order_public_id || ' готовится к отправке'
            WHEN 'in_transit' THEN 'Ваш заказ #' || v_order_public_id || ' отправлен и находится в пути'
            WHEN 'delivered' THEN 'Ваш заказ #' || v_order_public_id || ' успешно доставлен'
            WHEN 'failed' THEN 'Возникли проблемы с доставкой заказа #' || v_order_public_id
            ELSE 'Статус доставки заказа #' || v_order_public_id || ' изменен'
END;

INSERT INTO notifications (user_id, order_id, type, title, message)
VALUES (
           v_user_id,
           NEW.order_id,
           'delivery_update',
           'Обновление доставки',
           v_status_message
       );
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notify_delivery_status_change
    AFTER UPDATE ON deliveries
    FOR EACH ROW
    EXECUTE FUNCTION notify_delivery_status_change();

CREATE OR REPLACE FUNCTION notify_delivery_created()
RETURNS TRIGGER AS $$
DECLARE
v_user_id BIGINT;
    v_order_public_id UUID;
BEGIN
SELECT o.user_id, o.public_id
INTO v_user_id, v_order_public_id
FROM orders o
WHERE o.id = NEW.order_id;

INSERT INTO notifications (user_id, order_id, type, title, message)
VALUES (
           v_user_id,
           NEW.order_id,
           'delivery_update',
           'Доставка создана',
           'Для вашего заказа #' || v_order_public_id || ' создана доставка'
       );

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notify_delivery_created
    AFTER INSERT ON deliveries
    FOR EACH ROW
    EXECUTE FUNCTION notify_delivery_created();