ALTER TABLE IF EXISTS public.visit_services ALTER COLUMN start_date_time DROP NOT NULL;
ALTER TABLE IF EXISTS public.visit_services ALTER COLUMN end_date_time DROP NOT NULL;