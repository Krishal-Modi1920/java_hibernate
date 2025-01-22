
alter table if exists personnel_role
    add column site_id varchar(36);

alter table if exists personnel_role
    add constraint site_id
    foreign key (site_id)
    references sites;