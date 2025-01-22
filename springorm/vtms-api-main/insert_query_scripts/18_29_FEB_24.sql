--update visit's requested_services structure to make valid data in db
update public.visits set requested_services = '[
  "0096a255-72fb-45fa-9c7a-a26a8fe281b6"
]' where visit_id is not null;

--update visotor's phone_country_code and phone_number to make valid data in db
update public.visitors
set phone_country_code = '+91',
    phone_number = '9137174115'
where visitor_id is not null;