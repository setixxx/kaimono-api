-- =====================================================
-- Demo Data Migration for E-commerce API (FIXED ALL SEQUENCES AND VALUE LISTS)
-- =====================================================

-- 1. USERS (5 demo users) - ДОБАВЛЕН ЯВНЫЙ ID
-- Password for all users: "password123" (hashed with SHA-256)
INSERT INTO users (id, public_id, name, surname, phone, email, birthday, gender, password_hash) VALUES
(1, uuid_generate_v4(), 'Алексей', 'Иванов', '79001234567', 'alexey.ivanov@example.com', '1990-05-15', 'Male', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
(2, uuid_generate_v4(), 'Мария', 'Петрова', '79001234568', 'maria.petrova@example.com', '1992-08-22', 'Female', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
(3, uuid_generate_v4(), 'Дмитрий', 'Сидоров', '79001234569', 'dmitry.sidorov@example.com', '1988-03-10', 'Male', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
(4, uuid_generate_v4(), 'Елена', 'Козлова', '79001234570', 'elena.kozlova@example.com', '1995-11-30', 'Female', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f'),
(5, uuid_generate_v4(), 'Иван', 'Морозов', '79001234571', 'ivan.morozov@example.com', '1993-07-18', 'Male', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

-- Обновление последовательности для PostgreSQL
SELECT setval('users_id_seq', 5, true);
---------------------------------------------------------------------

-- 2. ADDRESSES (2-3 addresses per user) - ДОБАВЛЕН ЯВНЫЙ ID И ИСПРАВЛЕНО КОЛИЧЕСТВО СТОЛБЦОВ
INSERT INTO addresses (id, user_id, city, street, house, apartment, zip_code, additional_info, is_default) VALUES
(1, 1, 'Москва', 'Тверская улица', '12', '45', '125009', 'Домофон 45', true),
(2, 1, 'Москва', 'Арбат', '8', '12', '119019', NULL, false),
(3, 2, 'Санкт-Петербург', 'Невский проспект', '28', '101', '191186', 'Подъезд 2', true),
(4, 2, 'Санкт-Петербург', 'Литейный проспект', '15', '33', '191014', NULL, false),
(5, 3, 'Казань', 'Баумана улица', '64', NULL, '420111', 'Частный дом', true),
(6, 3, 'Казань', 'Кремлёвская улица', '18', '7', '420008', NULL, false), -- ИСПРАВЛЕНО: добавлено 'Казань'
(7, 4, 'Екатеринбург', 'Ленина проспект', '50', '88', '620075', 'Код домофона 88', true),
(8, 5, 'Новосибирск', 'Красный проспект', '35', '120', '630099', NULL, true),
(9, 5, 'Новосибирск', 'Вокзальная магистраль', '16', '45', '630004', 'Офисное здание', false); -- ИСПРАВЛЕНО: добавлено 'Новосибирск'

-- Обновление последовательности для PostgreSQL
SELECT setval('addresses_id_seq', 9, true);
---------------------------------------------------------------------

-- 3. PAYMENT METHODS (1-2 per user) - ДОБАВЛЕН ЯВНЫЙ ID
INSERT INTO payment_methods (id, user_id, card_number_last4, card_holder_name, expiry_month, expiry_year, is_default) VALUES
(1, 1, '4242', 'ALEXEY IVANOV', 12, 2026, true),
(2, 1, '5555', 'ALEXEY IVANOV', 8, 2025, false),
(3, 2, '4111', 'MARIA PETROVA', 3, 2027, true),
(4, 3, '3782', 'DMITRY SIDOROV', 6, 2026, true),
(5, 3, '6011', 'DMITRY SIDOROV', 9, 2025, false),
(6, 4, '5105', 'ELENA KOZLOVA', 11, 2027, true),
(7, 5, '4012', 'IVAN MOROZOV', 4, 2026, true);

-- Обновление последовательности для PostgreSQL
SELECT setval('payment_methods_id_seq', 7, true);
---------------------------------------------------------------------

-- 4. CATEGORIES (hierarchical structure) - ID уже явный
INSERT INTO categories (id, name, description, parent_id) VALUES
(1, 'Одежда', 'Мужская и женская одежда', NULL),
(2, 'Обувь', 'Обувь на любой сезон', NULL),
(3, 'Аксессуары', 'Модные аксессуары', NULL),
(4, 'Мужская одежда', 'Одежда для мужчин', 1),
(5, 'Женская одежда', 'Одежда для женщин', 1),
(6, 'Футболки', 'Повседневные футболки', 4),
(7, 'Рубашки', 'Классические рубашки', 4),
(8, 'Джинсы', 'Джинсовая одежда', 4),
(9, 'Платья', 'Женские платья', 5),
(10, 'Блузки', 'Элегантные блузки', 5),
(11, 'Кроссовки', 'Спортивная обувь', 2),
(12, 'Ботинки', 'Классическая обувь', 2),
(13, 'Сумки', 'Модные сумки', 3),
(14, 'Часы', 'Наручные часы', 3);

-- Обновление последовательности для PostgreSQL
SELECT setval('categories_id_seq', 14, true);
---------------------------------------------------------------------

-- 5. PRODUCTS (15 products with various categories) - ДОБАВЛЕН ЯВНЫЙ ID
INSERT INTO products (id, public_id, name, description, base_price, is_available) VALUES
(1, uuid_generate_v4(), 'Классическая белая футболка', 'Базовая футболка из 100% хлопка. Идеально подходит для повседневной носки.', 1299.00, true),
(2, uuid_generate_v4(), 'Чёрная футболка Oversize', 'Стильная футболка свободного кроя. Материал: хлопок премиум качества.', 1599.00, true),
(3, uuid_generate_v4(), 'Синяя рубашка Slim Fit', 'Приталенная рубашка из качественного хлопка. Подходит для офиса и встреч.', 2990.00, true),
(4, uuid_generate_v4(), 'Классические синие джинсы', 'Прямой крой, средняя посадка. Материал: деним с эластаном.', 3499.00, true),
(5, uuid_generate_v4(), 'Чёрные зауженные джинсы', 'Современный зауженный крой. Удобная посадка, стрейч-деним.', 3799.00, true),
(6, uuid_generate_v4(), 'Летнее цветочное платье', 'Лёгкое платье с цветочным принтом. Идеально для лета.', 4299.00, true),
(7, uuid_generate_v4(), 'Чёрное вечернее платье', 'Элегантное платье для особых случаев. Длина миди.', 6999.00, true),
(8, uuid_generate_v4(), 'Белая шёлковая блузка', 'Роскошная блузка из натурального шёлка. Классический крой.', 5499.00, true),
(9, uuid_generate_v4(), 'Кроссовки Nike Air Max', 'Спортивные кроссовки с технологией Air Max. Максимальный комфорт.', 8999.00, true),
(10, uuid_generate_v4(), 'Кроссовки Adidas Ultraboost', 'Беговые кроссовки с технологией Boost. Легкие и удобные.', 12999.00, true),
(11, uuid_generate_v4(), 'Классические кожаные ботинки', 'Мужские ботинки из натуральной кожи. Подходят для офиса.', 7999.00, true),
(12, uuid_generate_v4(), 'Женские ботильоны на каблуке', 'Стильные ботильоны из замши. Высота каблука 7 см.', 5999.00, true),
(13, uuid_generate_v4(), 'Кожаная сумка-тоут', 'Просторная сумка из натуральной кожи. Множество отделений.', 9999.00, true),
(14, uuid_generate_v4(), 'Рюкзак городской', 'Вместительный рюкзак для города. Водоотталкивающий материал.', 4499.00, true),
(15, uuid_generate_v4(), 'Наручные часы Casio', 'Классические кварцевые часы. Водонепроницаемые.', 6499.00, true);

-- Обновление последовательности для PostgreSQL
SELECT setval('products_id_seq', 15, true);
---------------------------------------------------------------------

-- 6. PRODUCT CATEGORIES (linking products to categories)
INSERT INTO product_categories (product_id, category_id) VALUES
(1, 6), (1, 4),
(2, 6), (2, 4),
(3, 7), (3, 4),
(4, 8), (4, 4),
(5, 8), (5, 4),
(6, 9), (6, 5),
(7, 9), (7, 5),
(8, 10), (8, 5),
(9, 11), (9, 2),
(10, 11), (10, 2),
(11, 12), (11, 2),
(12, 12), (12, 2),
(13, 13), (13, 3),
(14, 13), (14, 3),
(15, 14), (15, 3);

-- 7. PRODUCT SIZES (different sizes for different products) - ДОБАВЛЕН ЯВНЫЙ ID
INSERT INTO product_sizes (id, product_id, size, stock_quantity, price_modifier) VALUES
-- T-shirts (1-2)
(1, 1, 'XS', 15, 0), (2, 1, 'S', 25, 0), (3, 1, 'M', 30, 0), (4, 1, 'L', 25, 0), (5, 1, 'XL', 20, 0), (6, 1, 'XXL', 10, 100),
(7, 2, 'S', 12, 0), (8, 2, 'M', 20, 0), (9, 2, 'L', 18, 0), (10, 2, 'XL', 15, 0), (11, 2, 'XXL', 8, 100),

-- Shirt (3)
(12, 3, 'S', 10, 0), (13, 3, 'M', 18, 0), (14, 3, 'L', 15, 0), (15, 3, 'XL', 12, 0), (16, 3, 'XXL', 5, 200),

-- Jeans (4-5)
(17, 4, '28', 8, 0), (18, 4, '30', 12, 0), (19, 4, '32', 15, 0), (20, 4, '34', 12, 0), (21, 4, '36', 8, 0),
(22, 5, '28', 5, 0), (23, 5, '30', 10, 0), (24, 5, '32', 12, 0), (25, 5, '34', 10, 0), (26, 5, '36', 6, 0),

-- Dresses (6-7)
(27, 6, 'XS', 8, 0), (28, 6, 'S', 15, 0), (29, 6, 'M', 20, 0), (30, 6, 'L', 12, 0), (31, 6, 'XL', 8, 0),
(32, 7, 'XS', 5, 0), (33, 7, 'S', 12, 0), (34, 7, 'M', 15, 0), (35, 7, 'L', 10, 0), (36, 7, 'XL', 5, 0),

-- Blouse (8)
(37, 8, 'XS', 6, 0), (38, 8, 'S', 10, 0), (39, 8, 'M', 12, 0), (40, 8, 'L', 8, 0), (41, 8, 'XL', 4, 0),

-- Sneakers (9-10)
(42, 9, '38', 10, 0), (43, 9, '39', 15, 0), (44, 9, '40', 18, 0), (45, 9, '41', 20, 0), (46, 9, '42', 18, 0), (47, 9, '43', 15, 0), (48, 9, '44', 10, 0), (49, 9, '45', 5, 0),
(50, 10, '38', 8, 0), (51, 10, '39', 12, 0), (52, 10, '40', 15, 0), (53, 10, '41', 18, 0), (54, 10, '42', 15, 0), (55, 10, '43', 12, 0), (56, 10, '44', 8, 0), (57, 10, '45', 4, 0),

-- Boots (11-12)
(58, 11, '39', 6, 0), (59, 11, '40', 10, 0), (60, 11, '41', 12, 0), (61, 11, '42', 12, 0), (62, 11, '43', 10, 0), (63, 11, '44', 6, 0),
(64, 12, '36', 8, 0), (65, 12, '37', 12, 0), (66, 12, '38', 15, 0), (67, 12, '39', 12, 0), (68, 12, '40', 8, 0),

-- Bags and accessories (13-15) - usually one size
(69, 13, 'ONE SIZE', 20, 0),
(70, 14, 'ONE SIZE', 25, 0),
(71, 15, 'ONE SIZE', 15, 0);

-- Обновление последовательности для PostgreSQL
SELECT setval('product_sizes_id_seq', 71, true);
---------------------------------------------------------------------

-- 8. PRODUCT IMAGES
INSERT INTO product_images (product_id, image_url, display_order, is_primary) VALUES
(1, 'https://example.com/images/white-tshirt-1.jpg', 0, true),
(1, 'https://example.com/images/white-tshirt-2.jpg', 1, false),
(2, 'https://example.com/images/black-oversize-1.jpg', 0, true),
(2, 'https://example.com/images/black-oversize-2.jpg', 1, false),
(3, 'https://example.com/images/blue-shirt-1.jpg', 0, true),
(3, 'https://example.com/images/blue-shirt-2.jpg', 1, false),
(4, 'https://example.com/images/blue-jeans-1.jpg', 0, true),
(5, 'https://example.com/images/black-jeans-1.jpg', 0, true),
(6, 'https://example.com/images/floral-dress-1.jpg', 0, true),
(6, 'https://example.com/images/floral-dress-2.jpg', 1, false),
(7, 'https://example.com/images/black-dress-1.jpg', 0, true),
(8, 'https://example.com/images/silk-blouse-1.jpg', 0, true),
(9, 'https://example.com/images/nike-airmax-1.jpg', 0, true),
(9, 'https://example.com/images/nike-airmax-2.jpg', 1, false),
(10, 'https://example.com/images/adidas-ultra-1.jpg', 0, true),
(11, 'https://example.com/images/leather-boots-1.jpg', 0, true),
(12, 'https://example.com/images/ankle-boots-1.jpg', 0, true),
(13, 'https://example.com/images/leather-bag-1.jpg', 0, true),
(13, 'https://example.com/images/leather-bag-2.jpg', 1, false),
(14, 'https://example.com/images/backpack-1.jpg', 0, true),
(15, 'https://example.com/images/casio-watch-1.jpg', 0, true);

-- 9. CARTS (create carts for some users) - ДОБАВЛЕН ЯВНЫЙ ID
INSERT INTO cart (id, user_id, created_at, updated_at) VALUES
(1, 1, NOW(), NOW()),
(2, 2, NOW(), NOW()),
(3, 4, NOW(), NOW());

-- Обновление последовательности для PostgreSQL
SELECT setval('cart_id_seq', 3, true);
---------------------------------------------------------------------

-- 10. CART ITEMS (add items to carts) - ИСПРАВЛЕН product_size_id
INSERT INTO cart_items (cart_id, product_id, product_size_id, quantity, added_at) VALUES
(1, 1, 3, 2, NOW()),  -- User 1: 2x White T-shirt M (ID 3)
(1, 9, 44, 1, NOW()), -- User 1: 1x Nike Air Max 40 (ID 44)
(2, 6, 29, 1, NOW()), -- User 2: 1x Floral Dress M (ID 29)
(2, 13, 69, 1, NOW()), -- User 2: 1x Leather Bag (ID 69)
(3, 4, 19, 1, NOW()), -- User 4: 1x Blue Jeans 32 (ID 19)
(3, 10, 53, 1, NOW()); -- User 4: 1x Adidas Ultraboost 41 (ID 53)

-- 11. ORDERS (create some completed orders) - ДОБАВЛЕН ЯВНЫЙ ID
INSERT INTO orders (id, public_id, user_id, address_id, status_id, total_amount, created_at, updated_at) VALUES
(1, uuid_generate_v4(), 1, 1, 5, 26997.00, NOW() - INTERVAL '15 days', NOW() - INTERVAL '5 days'), -- address_id 1
(2, uuid_generate_v4(), 2, 3, 5, 11298.00, NOW() - INTERVAL '20 days', NOW() - INTERVAL '10 days'), -- address_id 3
(3, uuid_generate_v4(), 3, 5, 4, 16498.00, NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days'), -- address_id 5
(4, uuid_generate_v4(), 4, 7, 3, 8798.00, NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'), -- address_id 7
(5, uuid_generate_v4(), 5, 8, 2, 21498.00, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'); -- address_id 8

-- Обновление последовательности для PostgreSQL
SELECT setval('orders_id_seq', 5, true);
---------------------------------------------------------------------

-- 12. ORDER ITEMS - ИСПРАВЛЕН product_size_id
INSERT INTO order_items (order_id, product_id, product_size_id, quantity, price_at_purchase) VALUES
-- Order 1 (User 1)
(1, 1, 3, 2, 1299.00),  -- 2x White T-shirt M (ID 3)
(1, 3, 13, 1, 2990.00), -- 1x Blue Shirt M (ID 13)
(1, 9, 44, 1, 8999.00), -- 1x Nike Air Max 40 (ID 44)
(1, 13, 69, 1, 9999.00),-- 1x Leather Bag (ID 69)

-- Order 2 (User 2)
(2, 6, 29, 1, 4299.00), -- 1x Floral Dress M (ID 29)
(2, 8, 38, 1, 5499.00), -- 1x Silk Blouse S (ID 38)
(2, 12, 66, 1, 5999.00),-- 1x Ankle Boots 38 (ID 66)

-- Order 3 (User 3)
(3, 4, 19, 2, 3499.00), -- 2x Blue Jeans 32 (ID 19)
(3, 10, 53, 1, 12999.00), -- 1x Adidas Ultraboost 41 (ID 53)

-- Order 4 (User 4)
(4, 2, 8, 1, 1599.00), -- 1x Black Oversize M (ID 8)
(4, 5, 24, 1, 3799.00), -- 1x Black Jeans 32 (ID 24)
(4, 14, 70, 1, 4499.00),-- 1x Backpack (ID 70)

-- Order 5 (User 5)
(5, 7, 34, 1, 6999.00), -- 1x Black Evening Dress M (ID 34)
(5, 11, 60, 1, 7999.00), -- 1x Leather Boots 41 (ID 60)
(5, 15, 71, 1, 6499.00);-- 1x Casio Watch (ID 71)

-- 13. PAYMENTS - order_id и payment_method_id references fixed IDs
INSERT INTO payments (order_id, payment_method_id, amount, status, transaction_id, paid_at, created_at) VALUES
(1, 1, 26997.00, 'completed', 'TXN_' || substr(md5(random()::text), 1, 20), NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
(2, 3, 11298.00, 'completed', 'TXN_' || substr(md5(random()::text), 1, 20), NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
(3, 4, 16498.00, 'completed', 'TXN_' || substr(md5(random()::text), 1, 20), NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(4, 6, 8798.00, 'completed', 'TXN_' || substr(md5(random()::text), 1, 20), NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(5, 7, 21498.00, 'completed', 'TXN_' || substr(md5(random()::text), 1, 20), NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');

-- 14. DELIVERIES
INSERT INTO deliveries (order_id, address_id, tracking_number, estimated_delivery_date, actual_delivery_date, status, created_at, updated_at) VALUES
(1, 1, 'TRACK123456789', NOW() - INTERVAL '8 days', NOW() - INTERVAL '5 days', 'delivered', NOW() - INTERVAL '15 days', NOW() - INTERVAL '5 days'),
(2, 3, 'TRACK987654321', NOW() - INTERVAL '13 days', NOW() - INTERVAL '10 days', 'delivered', NOW() - INTERVAL '20 days', NOW() - INTERVAL '10 days'),
(3, 5, 'TRACK456789123', NOW() + INTERVAL '2 days', NULL, 'in_transit', NOW() - INTERVAL '5 days', NOW() - INTERVAL '2 days'),
(4, 7, NULL, NOW() + INTERVAL '4 days', NULL, 'pending', NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'),
(5, 8, NULL, NOW() + INTERVAL '6 days', NULL, 'pending', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day');

-- 15. REVIEWS (only for delivered orders)
INSERT INTO reviews (user_id, product_id, order_id, rating, comment, created_at, updated_at) VALUES
(1, 1, 1, 5, 'Отличная футболка! Качество на высоте, ткань мягкая и приятная к телу.', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(1, 9, 1, 4, 'Кроссовки удобные, но размер маломерит. Советую брать на размер больше.', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(2, 6, 2, 5, 'Платье просто великолепное! Цвета яркие, сидит идеально. Очень довольна покупкой!', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
(2, 8, 2, 5, 'Шёлк натуральный, блузка смотрится дорого. Рекомендую!', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days');

-- 16. WISHLIST (add items to wishlist for users) - ИСПРАВЛЕН product_size_id
INSERT INTO wishlist (user_id, product_id, product_size_id, added_at) VALUES
(1, 7, 34, NOW() - INTERVAL '10 days'), -- User 1 wants Black Evening Dress (Size M - ID 34)
(1, 11, 60, NOW() - INTERVAL '5 days'), -- User 1 wants Leather Boots (Size 41 - ID 60)
(2, 10, 53, NOW() - INTERVAL '7 days'), -- User 2 wants Adidas Ultraboost (Size 41 - ID 53)
(3, 13, 69, NOW() - INTERVAL '12 days'), -- User 3 wants Leather Bag (ONE SIZE - ID 69)
(3, 15, 71, NOW() - INTERVAL '8 days'), -- User 3 wants Casio Watch (ONE SIZE - ID 71)
(4, 1, 3, NOW() - INTERVAL '6 days'), -- User 4 wants White T-shirt (Size M - ID 3)
(5, 4, 19, NOW() - INTERVAL '3 days'); -- User 5 wants Blue Jeans (Size 32 - ID 19)

-- =====================================================
-- Verification Queries (commented out, use for testing)
-- =====================================================