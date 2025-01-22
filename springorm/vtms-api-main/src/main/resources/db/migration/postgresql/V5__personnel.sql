
create table "permissions" (
                               "permission_id" varchar(36) not null,
                               "created_at" timestamp,
                               "created_by" varchar(255),
                               "status" varchar(255),
                               "updated_at" timestamp,
                               "updated_by" varchar(255),
                               "name" varchar(255) not null,
                               primary key ("permission_id")
);

create table "personnel" (
                             "personnel_id" varchar(36) not null,
                             "created_at" timestamp,
                             "created_by" varchar(255),
                             "status" varchar(255),
                             "updated_at" timestamp,
                             "updated_by" varchar(255),
                             "age_group" varchar(10),
                             "center_id" varchar(16),
                             "email" varchar(255) not null,
                             "external_id" integer,
                             "first_name" varchar(32) not null,
                             "gender" varchar(6),
                             "last_name" varchar(64),
                             "mandal" varchar(16),
                             "middle_name" varchar(64),
                             "parazone_id" varchar(16),
                             "personnel_uuid" varchar(36),
                             "phone_country_code" varchar(3) not null,
                             "phone_number" varchar(12) not null,
                             "uucode" varchar(9),
                             primary key ("personnel_id")
);

create table "personnel_role" (
                                  "personnel_role_id" varchar(36) not null,
                                  "created_at" timestamp,
                                  "created_by" varchar(255),
                                  "status" varchar(255),
                                  "updated_at" timestamp,
                                  "updated_by" varchar(255),
                                  "personneld_id" varchar(36) not null,
                                  "role_id" varchar(36) not null,
                                  primary key ("personnel_role_id")
);

create table "roles" (
                         "role_id" varchar(36) not null,
                         "created_at" timestamp,
                         "created_by" varchar(255),
                         "status" varchar(255),
                         "updated_at" timestamp,
                         "updated_by" varchar(255),
                         lang_meta jsonb,
                         "name" varchar(255) not null,
                         is_check_availability boolean not null,
                         is_check_system_role boolean not null,
                         primary key ("role_id")
);
create table "role_permission" (
                                   "role_permission_id" varchar(36) not null,
                                   "created_at" timestamp,
                                   "created_by" varchar(255),
                                   "status" varchar(255),
                                   "updated_at" timestamp,
                                   "updated_by" varchar(255),
                                   "permission_id" varchar(36) not null,
                                   "role_id" varchar(36) not null,
                                   primary key ("role_permission_id")
);

create table "role_tags" (
                             "role_tag_id" varchar(36) not null,
                             "created_at" timestamp,
                             "created_by" varchar(255),
                             "status" varchar(255),
                             "updated_at" timestamp,
                             "updated_by" varchar(255),
                             "tag" varchar(64),
                             "role_id" varchar(36) not null,
                             primary key ("role_tag_id")
);

alter table if exists "role_permission"
    add constraint "permission_id"
        foreign key ("permission_id")
            references "permissions";

alter table if exists "role_permission"
    add constraint "role_id"
        foreign key ("role_id")
            references "roles";

alter table if exists "personnel_role"
    add constraint "personneld_id"
        foreign key ("personneld_id")
            references "personnel";

alter table if exists "personnel_role"
    add constraint "role_id"
        foreign key ("role_id")
            references "roles";

alter table if exists "role_tags"
    add constraint "role_id"
        foreign key ("role_id")
            references "roles";

alter table if exists "visit_personnel"
    add constraint "personnel_id"
        foreign key ("personnel_id")
            references "personnel";

alter table if exists "visit_personnel"
    add constraint "role_id"
        foreign key ("role_id")
            references "roles";

alter table if exists "visit_personnel"
    add constraint "visit_id"
        foreign key ("visit_id")
            references "visits";
