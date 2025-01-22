alter table if exists public.personnel 
   add column email_source VARCHAR(30) default 'NONE';

alter table if exists public.personnel 
   add column personal_email varchar(255);
   
alter table public.personnel alter column email drop not null;      
  