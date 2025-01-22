 alter table if exists "visit_locations" 
       add column visit_personnel_id varchar(36);

    alter table if exists "visit_locations" 
       add constraint visit_personnel_id 
       foreign key (visit_personnel_id) 
       references visit_personnel;
