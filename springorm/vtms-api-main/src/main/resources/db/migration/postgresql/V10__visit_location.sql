

    	create table "visit_locations" (
       "visit_location_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        duration integer,
        location_tag varchar(64) not null,
        start_date_time timestamp(6) not null,
        end_date_time timestamp(6) not null,
        visit_service_id varchar(36) not null,
        location_id varchar(36) not null,
        primary key ("visit_location_id")
    );
    
    

    alter table if exists "visit_locations" 
       add constraint visit_service_id 
       foreign key (visit_service_id) 
       references visit_services;

    alter table if exists "visit_locations"
       add constraint location_id
       foreign key ("location_id")
       references "locations";
            
            
            