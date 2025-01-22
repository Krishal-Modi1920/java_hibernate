
    create table tour_slot_personnel (
       "tour_slot_personnel_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        personnel_id varchar(36) not null,
        tour_slot_id varchar(36) not null,
        primary key ("tour_slot_personnel_id")
    );

    create table tour_slots (
       tour_slot_id varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
		stage varchar(255) not null,
        end_date_time timestamp(6) not null,
        max_guest_size integer not null,
        start_date_time timestamp(6) not null,
        "site_id" varchar(36),
        primary key (tour_slot_id)
    );

    alter table if exists tour_slot_personnel 
       add constraint personnel_id 
       foreign key (personnel_id) 
       references personnel;

    alter table if exists tour_slot_personnel 
       add constraint tour_slot_id 
       foreign key (tour_slot_id) 
       references tour_slots;

    alter table if exists tour_slots 
       add constraint "site_id" 
       foreign key ("site_id") 
       references sites;
