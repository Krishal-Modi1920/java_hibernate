
    create table service_location (
       service_location_id varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255) not null,
        "updated_at" timestamp,
        "updated_by" varchar(255),
        location_id varchar(36) not null,
        service_id varchar(36) not null,
        primary key (service_location_id)
    );

    alter table if exists service_location 
       add constraint location_id 
       foreign key (location_id) 
       references "locations";

    alter table if exists service_location 
       add constraint service_id 
       foreign key (service_id) 
       references services;
