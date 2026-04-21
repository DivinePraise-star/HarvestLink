```sql
-- =====================
-- SEED: Farmers
-- =====================
INSERT INTO public.farmers (id, name, email, phone_number, location, rating, sales_completed, farm_name, is_online) VALUES
  ('a1b2c3d4-0001-0001-0001-000000000001', 'Joseph Mukasa', 'joseph.mukasa@gmail.com', '+256701234567', 'Wakiso, Uganda', 4.8, 120, 'Mukasa Fresh Farm', true),
  ('a1b2c3d4-0002-0002-0002-000000000002', 'Grace Nakato', 'grace.nakato@gmail.com', '+256712345678', 'Mpigi, Uganda', 4.5, 85, 'Nakato Organics', true),
  ('a1b2c3d4-0003-0003-0003-000000000003', 'Robert Ssemakula', 'robert.ssemakula@gmail.com', '+256723456789', 'Mubende, Uganda', 4.2, 60, 'Ssemakula Agro', false),
  ('a1b2c3d4-0004-0004-0004-000000000004', 'Fatuma Nakabuye', 'fatuma.nakabuye@gmail.com', '+256734567890', 'Luwero, Uganda', 4.7, 200, 'Nakabuye Green Gardens', true);

-- =====================
-- SEED: Buyers
-- =====================
INSERT INTO public.buyers (name, email, phone_number, delivery_address, preferred_payment_method, order_history_count, is_online) VALUES
  ('Samuel Mwangi', 'samuel.mwangi@gmail.com', '+256741234567', '14 Kampala Road, Kampala', 'Mobile Money', 8, true),
  ('Aisha Namukasa', 'aisha.namukasa@gmail.com', '+256752345678', '7 Entebbe Road, Entebbe', 'Cash on Delivery', 3, false),
  ('Daniel Okello', 'daniel.okello@gmail.com', '+256763456789', '22 Jinja Road, Jinja', 'Mobile Money', 15, true);

-- =====================
-- SEED: Produce
-- =====================
INSERT INTO public.produce (name, price, unit, available_quantity, rating, description, harvest_date, farmer_id, category) VALUES
  ('Tomatoes', 3500.0, 'kg', 200, 4.6, 'Fresh red tomatoes, locally grown without pesticides.', '2026-04-15', 'a1b2c3d4-0001-0001-0001-000000000001', 'Vegetables'),
  ('Maize', 1200.0, 'kg', 500, 4.3, 'Dry maize grain, good for posho or animal feed.', '2026-04-10', 'a1b2c3d4-0001-0001-0001-000000000001', 'Grains'),
  ('Matoke', 4000.0, 'bunch', 80, 4.7, 'Sweet highland matoke, harvested fresh from Mpigi.', '2026-04-18', 'a1b2c3d4-0002-0002-0002-000000000002', 'Fruits'),
  ('Sweet Potatoes', 2000.0, 'kg', 300, 4.4, 'Orange-flesh sweet potatoes, rich in vitamins.', '2026-04-12', 'a1b2c3d4-0002-0002-0002-000000000002', 'Tubers'),
  ('Cabbage', 2500.0, 'head', 150, 4.1, 'Large fresh cabbages from Mubende highlands.', '2026-04-16', 'a1b2c3d4-0003-0003-0003-000000000003', 'Vegetables'),
  ('Groundnuts', 6000.0, 'kg', 120, 4.8, 'Raw groundnuts, sun-dried and ready for sale.', '2026-04-08', 'a1b2c3d4-0003-0003-0003-000000000003', 'Legumes'),
  ('Pineapples', 3000.0, 'piece', 250, 4.9, 'Sweet Luwero pineapples, no artificial ripening.', '2026-04-17', 'a1b2c3d4-0004-0004-0004-000000000004', 'Fruits'),
  ('Beans', 4500.0, 'kg', 180, 4.5, 'Mixed red and white beans, freshly harvested.', '2026-04-11', 'a1b2c3d4-0004-0004-0004-000000000004', 'Legumes'),
  ('Onions', 2800.0, 'kg', 220, 4.2, 'Dry red onions, long shelf life.', '2026-04-14', 'a1b2c3d4-0001-0001-0001-000000000001', 'Vegetables'),
  ('Cassava', 1500.0, 'kg', 400, 4.0, 'Fresh cassava roots, good for flour or boiling.', '2026-04-13', 'a1b2c3d4-0003-0003-0003-000000000003', 'Tubers');

-- =====================
-- SEED: Orders
-- =====================
INSERT INTO public.orders (order_date, order_status, delivery_address, user_id, farmer_id) VALUES
  (1745100000000, 'PENDING', '14 Kampala Road, Kampala', 'buyer-001', 'a1b2c3d4-0001-0001-0001-000000000001'),
  (1745000000000, 'IN_TRANSIT', '7 Entebbe Road, Entebbe', 'buyer-002', 'a1b2c3d4-0002-0002-0002-000000000002'),
  (1744900000000, 'DELIVERED', '22 Jinja Road, Jinja', 'buyer-003', 'a1b2c3d4-0003-0003-0003-000000000003'),
  (1744800000000, 'CANCELLED', '14 Kampala Road, Kampala', 'buyer-001', 'a1b2c3d4-0004-0004-0004-000000000004');

-- =====================
-- SEED: Farmer Listings
-- =====================
INSERT INTO public.farmer_listings (produce_name, quantity_available, price_per_unit, status, quantity_sold) VALUES
  ('Tomatoes', 200, 3500.0, 'ACTIVE', 45),
  ('Maize', 500, 1200.0, 'ACTIVE', 120),
  ('Matoke', 80, 4000.0, 'ACTIVE', 30),
  ('Sweet Potatoes', 300, 2000.0, 'PENDING', 0),
  ('Groundnuts', 0, 6000.0, 'SOLD', 120),
  ('Pineapples', 250, 3000.0, 'ACTIVE', 60);

-- =====================
-- SEED: Farmer Order Requests
-- =====================
INSERT INTO public.farmer_order_requests (buyer_name, buyer_location, produce_name, quantity, offered_price_per_kg, buyer_note, request_date, is_responded) VALUES
  ('Samuel Mwangi', 'Kampala', 'Tomatoes', 50, 3200, 'Please deliver by Friday morning.', '2026-04-19', false),
  ('Aisha Namukasa', 'Entebbe', 'Matoke', 10, 3800, 'Need 10 fresh bunches for a function.', '2026-04-18', true),
  ('Daniel Okello', 'Jinja', 'Beans', 30, 4200, 'Bulk order, willing to pick up.', '2026-04-17', false),
  ('Samuel Mwangi', 'Kampala', 'Pineapples', 100, 2800, 'For a juice business, need weekly supply.', '2026-04-20', false);
```
