
alter table if exists visits
    add column interviewer_volunteer_name varchar(128);

alter table if exists visits
    add column interviewer_volunteer_phone_number varchar(12);


alter table if exists "visit_locations"
    rename column interview_setup to interview_package;
