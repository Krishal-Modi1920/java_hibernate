
alter table if exists "visit_locations"
    add column comments varchar(128);

alter table if exists "visit_locations"
    add column interview_setup varchar(64);