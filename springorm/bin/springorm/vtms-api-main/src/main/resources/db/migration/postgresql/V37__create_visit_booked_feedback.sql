
    create table visit_public_feedback (
       visit_public_feedback_id varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255) not null,
        "updated_at" timestamp,
        "updated_by" varchar(255),
        comment varchar(512),
        rating jsonb,
        visit_id varchar(36) not null,
        primary key (visit_public_feedback_id)
    );

    alter table if exists visit_public_feedback 
       add constraint visit_id 
       foreign key (visit_id) 
       references visits;
