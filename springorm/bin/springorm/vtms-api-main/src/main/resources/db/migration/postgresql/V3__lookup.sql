    create table "lookup" (
       "lookup_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        child_lookup jsonb,
        "key" varchar(64) not null,
        lang_meta jsonb,
        "seq_number" integer not null,
        "value" varchar(255) not null,
        "parent_lookup_id" varchar(36),
        primary key ("lookup_id")
    );

    alter table if exists "lookup" 
       add constraint "lookup_id" 
       foreign key ("parent_lookup_id") 
       references "lookup";
