--query for dashboard api

INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) 
VALUES ('97b06533-8037-4f8c-9fd3-49cc9dd8b1c3', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'VIEW_DASHBOARD_VISIT_COUNT');
INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) 
VALUES ('97b16534-8037-4f8c-9fd3-49cc9dd8b1c3', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'VIEW_DASHBOARD_VISIT_SUMMARY');
INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) 
VALUES ('98b06536-8037-4f8c-9fd3-49cc9dd8b1c3', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'VIEW_DASHBOARD_PREBOOKED_VISIT_SUMMARY');


INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd401', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '97b06533-8037-4f8c-9fd3-49cc9dd8b1c3', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd402', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '97b16534-8037-4f8c-9fd3-49cc9dd8b1c3', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd403', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c3', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd404', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '97b06533-8037-4f8c-9fd3-49cc9dd8b1c3', 'c1a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd405', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '97b16534-8037-4f8c-9fd3-49cc9dd8b1c3', 'c1a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd406', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c3', 'c1a5850f-8fbd-459b-8e97-d330866c3252');

