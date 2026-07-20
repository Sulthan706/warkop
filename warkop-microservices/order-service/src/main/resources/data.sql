INSERT INTO tab_order (customer_name, product_id, product_name, quantity, price, status, created_at) VALUES
('Budi',  1, 'Kopi Tubruk',             2, 12000, 'SELESAI',  NOW() - INTERVAL '2 day'),
('Budi',  2, 'Es Kopi Susu Gula Aren',  1, 18000, 'SELESAI',  NOW() - INTERVAL '2 day'),
('Sari',  3, 'Cappuccino',              3, 22000, 'SELESAI',  NOW() - INTERVAL '1 day'),
('Sari',  8, 'Roti Bakar Coklat',       2, 15000, 'SELESAI',  NOW() - INTERVAL '1 day'),
('Agus',  9, 'Indomie Telur',           1, 14000, 'DIPROSES', NOW() - INTERVAL '1 day'),
('Dewi',  4, 'Kopi V60',                2, 25000, 'SELESAI',  NOW()),
('Dewi',  6, 'Matcha Latte',            1, 24000, 'DIPROSES', NOW()),
('Rina',  5, 'Es Teh Manis',            4, 8000,  'SELESAI',  NOW()),
('Agus',  2, 'Es Kopi Susu Gula Aren',  2, 18000, 'BATAL',    NOW());
