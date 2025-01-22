
   ALTER TABLE personnel_role
		RENAME COLUMN personneld_id to personnel_id;

    alter table if exists personnel_role 
       add constraint personnel_id 
       foreign key (personnel_id) 
       references personnel;