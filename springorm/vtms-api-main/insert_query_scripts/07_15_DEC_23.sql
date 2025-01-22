update public.notification_templates
set temmpate_id='t-39ec29726d'
where notification_template='VISIT_ASSIGNED_EMAIL';

insert into public.notification_templates (notification_template_id, created_at, created_by, status, updated_at, updated_by, notification_template, temmpate_id, version, channel) values ('af1b07c0-cea3-441f-9072-45c6ecedb3e5', '2023-12-12 12:17:33.000000', 'AUTO', 'ACTIVE', '2023-12-12 12:17:48.000000', 'AUTO', 'MEETING_WITH_GUEST_EMAIL', 't-2d1c69d508', 1, 'EMAIL');
insert into public.notification_templates (notification_template_id, created_at, created_by, status, updated_at, updated_by, notification_template, temmpate_id, version, channel) values ('681a4262-9703-4942-b542-3b816d27262a', '2023-12-12 12:17:33.000000', 'AUTO', 'ACTIVE', '2023-12-12 12:17:48.000000', 'AUTO', 'VISIT_FEEDBACK_EMAIL', 't-cab4dbacfd', 1, 'EMAIL');

update public.notification_templates
set version = 2
where notification_template_id = '5248c90f-1f15-4456-463e-346de1d9234e';