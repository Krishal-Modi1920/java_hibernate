
    alter table if exists public.visit_public_feedback
    	drop column rating;

	alter table if exists visit_public_feedback 
       add column booking_process_rating integer;

    alter table if exists visit_public_feedback 
       add column is_booking_feedback boolean not null DEFAULT 'false';
       
       
    alter table if exists visit_public_feedback 
       add column overall_rating integer;