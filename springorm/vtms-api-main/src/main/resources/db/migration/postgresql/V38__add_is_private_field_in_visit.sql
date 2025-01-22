
    alter table if exists visits 
       add column is_private boolean not null DEFAULT 'false';
       
    update visits set is_private = 'false' where type = 'TOUR';
    
    update visits set is_private = 'true' where type = 'VISIT';