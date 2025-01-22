alter table if exists visits
    add column tour_slot_id varchar(36);

alter table if exists visits
    add constraint tour_slot_id
    foreign key (tour_slot_id)
    references tour_slots;