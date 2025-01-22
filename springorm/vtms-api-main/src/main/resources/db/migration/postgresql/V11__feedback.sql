create table feedbacks (
                           feedback_id varchar(36) not null,
                           "created_at" timestamp,
                           "created_by" varchar(255),
                           "status" varchar(255),
                           "updated_at" timestamp,
                           "updated_by" varchar(255),
                           visitor_comment jsonb,
                           visit_id varchar(36) not null,
                           primary key (feedback_id)
);

alter table if exists feedbacks
    add constraint visit_id
    foreign key (visit_id)
    references visits;

alter table if exists feedbacks
    add constraint UK_lhpw2ltggp1cm6yfqe9outiw3 unique (visit_id);