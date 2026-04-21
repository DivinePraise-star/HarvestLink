 -- Farmers
  create table public.farmers (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    email text not null,
    phone_number text not null,
    location text not null,
    rating double precision default 0,
    sales_completed int default 0,
    farm_name text,
    is_online boolean default false
  );

  -- Buyers
  create table public.buyers (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    email text not null,
    phone_number text not null,
    delivery_address text,
    preferred_payment_method text,
    order_history_count int default 0,
    is_online boolean default false
  );

  -- Produce
  create table public.produce (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    price double precision not null,
    unit text not null,
    available_quantity int not null,
    rating double precision default 0,
    description text,
    harvest_date text,
    farmer_id uuid references public.farmers(id),
    category text not null
  );

  -- Orders
  create table public.orders (
    id serial primary key,
    order_date bigint not null,
    order_status text not null,
    delivery_address text not null,
    user_id text not null,
    farmer_id text not null
  );

  -- Farmer Listings
  create table public.farmer_listings (
    id uuid primary key default gen_random_uuid(),
    produce_name text not null,
    quantity_available int not null,
    price_per_unit double precision not null,
    status text not null,
    quantity_sold int default 0
  );

  -- Farmer Order Requests
  create table public.farmer_order_requests (
    id uuid primary key default gen_random_uuid(),
    buyer_name text not null,
    buyer_location text not null,
    produce_name text not null,
    quantity int not null,
    offered_price_per_kg int not null,
    buyer_note text,
    request_date text not null,
    is_responded boolean default false
  );

  -- Public read policies
  alter table public.farmers enable row level security;
  alter table public.buyers enable row level security;
  alter table public.produce enable row level security;
  alter table public.orders enable row level security;
  alter table public.farmer_listings enable row level security;
  alter table public.farmer_order_requests enable row level security;

  create policy "Public read" on public.farmers for select using (true);
  create policy "Public read" on public.buyers for select using (true);
  create policy "Public read" on public.produce for select using (true);
  create policy "Public read" on public.orders for select using (true);
  create policy "Public read" on public.farmer_listings for select using (true);
  create policy "Public read" on public.farmer_order_requests for select using (true);