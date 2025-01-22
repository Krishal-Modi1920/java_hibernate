 create table "countries" (
       "country_id" varchar(36) not null,
        "created_at" timestamp,
        "created_by" varchar(255),
        "status" varchar(255),
        "updated_at" timestamp,
        "updated_by" varchar(255),
        "country_code" varchar(3) not null,
        "divion_id" integer not null,
        "isd_code" varchar(5),
        "name" varchar(255) not null,
        primary key ("country_id")
    );
    
create table "states" (
   "state_id" varchar(36) not null,
    "created_at" timestamp,
    "created_by" varchar(255),
    "status" varchar(255),
    "updated_at" timestamp,
    "updated_by" varchar(255),
    "abbrevation" varchar(10) not null,
    "name" varchar(255) not null,
    "country_id" varchar(36) not null,
    primary key ("state_id")
    );
    
alter table if exists "states" 
   add constraint "country_id" 
   foreign key ("country_id") 
   references "countries";
       
            