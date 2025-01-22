
    create table "locations" (
       "location_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "duration" integer not null,
        "name" varchar(64) not null,
        "sequence" integer not null,
        "type" varchar(16),
        "parent_location_id" varchar(36),
        "site_id" varchar(36),
        primary key ("location_id")
    );
    
    alter table if exists "locations" 
       add constraint "location_id" 
       foreign key ("parent_location_id") 
       references "locations";

    alter table if exists "locations" 
       add constraint "site_id" 
       foreign key ("site_id") 
       references "sites";
