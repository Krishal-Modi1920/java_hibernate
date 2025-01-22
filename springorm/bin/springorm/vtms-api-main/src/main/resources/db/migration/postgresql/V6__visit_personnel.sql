

    create table "visit_personnel" (
       "visit_personnel_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "personnel_id" varchar(36) not null,
        "role_id" varchar(36) not null,
        "visit_id" varchar(36) not null,
        "visit_service_id" varchar(36),
        tag varchar(64),
       visitor_rating jsonb,
       personnel_feedback jsonb,
        primary key ("visit_personnel_id")
    );
    

    alter table if exists visit_personnel 
       add constraint personnel_id 
       foreign key (personnel_id) 
       references personnel;

    alter table if exists visit_personnel 
       add constraint role_id 
       foreign key (role_id) 
       references roles;

    alter table if exists visit_personnel 
       add constraint visit_id 
       foreign key (visit_id) 
       references visits;