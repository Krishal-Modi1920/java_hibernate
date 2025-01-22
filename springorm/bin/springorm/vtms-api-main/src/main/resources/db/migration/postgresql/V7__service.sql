create table "services"
(
    "service_id" varchar(36) not null,
    "created_at" timestamp,
    "created_by" varchar(255),
    "status" varchar(255),
    "updated_at" timestamp,
    "updated_by" varchar(255),
    "description" varchar(100),
    "fields" jsonb,
    "lang_meta" jsonb,
    "name" varchar(64),
    "sub_type" varchar(16),
    "type" varchar(16),
    "site_id" varchar(36),
    is_check_visit_time boolean not null,
    primary key ("service_id")
);

alter table if exists "services"
    add constraint "site_id"
    foreign key ("site_id")
    references "sites";
