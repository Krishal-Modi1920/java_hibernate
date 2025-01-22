      create table "sites" (
       "site_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "date_format" varchar(12),
        "description" varchar(100),
        "name" varchar(64) not null,
        "short_name" varchar(64),
        "uucode" varchar(8) not null,
        "time_zone" varchar(3) not null,
         start_time time not null,
         end_time time not null,
        primary key ("site_id")
    );

    create table "visit_visitor" (
       "visit_visitor_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "contact_type" varchar(64) not null,
        "visit_id" varchar(36) not null,
        "visitor_id" varchar(36) not null,
        primary key ("visit_visitor_id")
    );

    create table "visitors" (
       "visitor_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        address_line_1 varchar(100),
        address_line_2 varchar(100),
        city varchar(36),
        country varchar(36),
        designation varchar(56),
        email varchar(255),
        facebook_id varchar(255),
        first_name varchar(32) not null,
        gender varchar(6) not null,
        instagram_id varchar(255),
        last_name varchar(64) not null,
        linkedin_id varchar(255),
        middle_name varchar(32),
        organization_address varchar(255),
        organization_name varchar(255),
        organization_website varchar(255),
        phone_country_code varchar(5) not null,
        phone_number varchar(12) not null,
        postal_code varchar(8),
        preferred_comm_mode varchar(20),
        "salutation" varchar(10) not null,
        state varchar(36),
        telegram_id varchar(255),
        twitter_id varchar(255),
        primary key ("visitor_id")
    );

    create table "visits" (
       "visit_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "adult_female_count" integer,
        "adult_male_count" integer,
        "child_female_count" integer,
        "child_male_count" integer,
        "end_date_time" timestamp(6),
        "request_number" varchar(16),
        "requested_services" jsonb,
        "requester_notes" varchar(512),
        "senior_female_count" integer,
        "senior_male_count" integer,
        "stage_history" jsonb,
        "start_date_time" timestamp(6) not null,
        "total_visitors" integer,
        "type_of_visit" varchar(128),
        "stage" varchar(12),
        "type" varchar(64),
        "visitor_comments" varchar(512),
        "documents" jsonb,
		site_id varchar(36),
        primary key ("visit_id")
    );

alter table if exists visits 
       add constraint site_id 
       foreign key (site_id) 
       references sites;
       
alter table if exists "visit_visitor" 
       add constraint "visit_id" 
       foreign key ("visit_id") 
       references "visits";

alter table if exists "visit_visitor" 
       add constraint "visitor_id" 
       foreign key ("visitor_id") 
       references "visitors";