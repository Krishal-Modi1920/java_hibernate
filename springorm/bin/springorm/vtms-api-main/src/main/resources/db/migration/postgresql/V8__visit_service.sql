
    create table visit_services (
       visit_service_id varchar(36) not null,
        created_at timestamp,
        created_by varchar(255),
        status varchar(255),
        updated_at timestamp,
        updated_by varchar(255),
        actual_end_date_time timestamp(6),
        actual_start_date_time timestamp(6),
        comments jsonb,
        end_date_time timestamp(6) not null,
        metadata jsonb,
        seq_number integer not null,
        start_date_time timestamp(6) not null,
        service_id varchar(36) not null,
        visit_id varchar(36) not null,
        meeting_personnel_id varchar(36),
        primary key (visit_service_id)
    );

    alter table if exists visit_services 
       add constraint service_id 
       foreign key (service_id) 
       references services;

    alter table if exists visit_services 
       add constraint visit_id 
       foreign key (visit_id) 
       references visits;
       
    alter table if exists visit_personnel 
       add constraint visit_service_id 
       foreign key (visit_service_id) 
       references visit_services;

    alter table if exists visit_services 
       add constraint personnel_id 
       foreign key (meeting_personnel_id) 
       references personnel;
