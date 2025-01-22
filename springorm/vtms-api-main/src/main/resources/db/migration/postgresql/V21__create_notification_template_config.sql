
    create table notification_templates_config (
       notification_templates_config_id varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        notification_template varchar(64) not null,
        temmpate_id varchar(36) not null,
        version integer not null,
        channel varchar(16) not null,
        primary key (notification_templates_config_id)
    );
