INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) 
VALUES ('9ebb22f4-04e4-4ec6-b9c6-4bbcf02c3802', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'DELETE_VISIT_INTERVIEW_SETUP');
INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) 
VALUES ('93b06540-8037-4f8c-9fd3-49cc9dd8b1c3', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'VIEW_PRE_BOOKED_VISIT_SELF_ASSIGN_LIST');


update public.roles set uucode = 'SUPER_ADMIN' where role_id = '5d66cf66-ba76-46c8-8243-5ff3f189f075';
update public.roles set uucode = 'RELATIONSHIP_MANAGER' where role_id = '81411948-d9a6-4f44-b5a3-22c3ab0a5f08';
update public.roles set uucode = 'GUEST_VISIT_COORDINATOR' where role_id = 'd74fc08e-e118-4b1a-8e95-51bc1f049e6e';
update public.roles set uucode = 'SERVICE_COORDINATOR' where role_id = '56d40657-042f-45e2-b453-66c8b969bbb9';
update public.roles set uucode = 'VISIT_ADMIN' where role_id = 'e36573cf-8df1-4df6-bec2-5f2979bcc899';
update public.roles set uucode = 'TOUR_GUIDE' where role_id = 'a5765790-d68f-44b1-9177-5a06688b2c61';
update public.roles set uucode = 'TOUR_COORDINATOR' where role_id = '564ace0f-ae3c-4efa-800d-3cecb4db791b';
update public.roles set uucode = 'MEETING_COORDINATOR' where role_id = '588f07e6-ff9a-4aba-aff3-22dd4f292868';
update public.roles set uucode = 'GUEST_USHER' where role_id = 'c1a5850f-8fbd-459b-8e97-d330866c3251';
update public.roles set uucode = 'INTERVIEW_SETUP_COORDINATOR' where role_id = '6c9f3aa6-efef-49b2-93a2-08bb3737a21b';
update public.roles set uucode = 'VOLUNTEER' where role_id = 'c1a5850f-8fbd-459b-8e97-d330866c3252';

INSERT INTO public.roles (role_id, created_at, created_by, status, updated_at, updated_by, lang_meta, uucode, name, is_check_availability, is_check_system_role) VALUES
    ('1760a863-2b49-4182-bd25-9013a3769304', '2023-10-03 09:57:32.500784', 'AUTO', 'ACTIVE', '2023-10-03 09:57:32.500784', 'AUTO', null, 'INTERVIEW_SETUP_VOLUNTEER', 'Interview Setup Volunteer', false, false);

INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('8e944cb1-8ae8-48f3-9d19-4801edb20123', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '9ebb22f4-04e4-4ec6-b9c6-4bbcf02c3802', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('c7cb2df9-b5f8-4054-b288-bf12aab376bd', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '93b06540-8037-4f8c-9fd3-49cc9dd8b1c3', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac1b63fa-f911-4158-9bd5-3699972bd504', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '93b06540-8037-4f8c-9fd3-49cc9dd8b1c3', 'c1a5850f-8fbd-459b-8e97-d330866c3252');


INSERT INTO public.role_tags 
VALUES ('c1b1d5a6-4168-411d-8aa9-874e21863262', '2023-09-28 10:25:41.758018', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.758018', 'AUTO', 'INTERVIEW_SETUP_VOLUNTEER', '1760a863-2b49-4182-bd25-9013a3769304');

SELECT pg_catalog.setval('public.tour_number', 1, true);

SELECT pg_catalog.setval('public.visit_number', 1, true);
