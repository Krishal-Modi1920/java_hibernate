ALTER TABLE public.personnel ALTER COLUMN status TYPE VARCHAR(50);

ALTER TABLE public.visits  DROP COLUMN interviewer_volunteer_name;
ALTER TABLE public.visits  DROP COLUMN interviewer_volunteer_phone_number;
