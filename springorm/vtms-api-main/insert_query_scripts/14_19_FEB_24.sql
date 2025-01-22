-- Created new role Tour Admin
INSERT INTO public.roles (role_id, created_at, created_by, status, updated_at, updated_by, lang_meta, name,
                          is_check_availability, is_check_system_role, uucode)
VALUES ('c2a5850f-8fbd-459b-8e97-d330866c3252', '2023-10-03 09:57:32.500784', 'AUTO', 'ACTIVE',
        '2023-10-03 09:57:32.500784', 'AUTO', null, 'Tour Admin', false, true, 'TOUR_ADMIN');

--Remove all role for personnel
delete
from public.personnel_role
where personnel_id = '913ffca8-07f9-4e79-8205-5311a69d898a';

-- Assign new role to personnel
INSERT INTO public.personnel_role (personnel_role_id, created_at, created_by, status, updated_at, updated_by,
                                   personnel_id, role_id, site_id)
VALUES ('4e79a1d0-757f-45bc-9862-1289e41b48bf', '2023-10-03 09:57:32.500784', 'AUTO', 'ACTIVE',
        '2023-10-03 09:57:32.500784', 'AUTO', '913ffca8-07f9-4e79-8205-5311a69d898a',
        'c2a5850f-8fbd-459b-8e97-d330866c3252', '42a11ac7-deba-4a5d-b6da-d63912069177');
        
-- Assign permission to role(tour admin)
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b3c87e8f-38bc-494e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', 'ead1ffc9-2dcf-4af6-9fa0-6e4762796317',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b7c87e8f-38bc-494e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', 'ead1ffc9-2dcf-4af6-9fa0-6e4762796318',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b4c87e8f-38bc-494e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b5c87e8f-38bc-494e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '97b06533-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-494e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '8bf510f0-5434-4662-8ed9-44bdd6a01067',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a1-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '97b16534-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');       
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a2-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', 'ead1ffc9-2dcf-4af6-9fa0-6e4762796316',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
 INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a3-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '94e3c324-2db2-4dcd-8a15-adf451f8cef6',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a4-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '97b06540-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a5-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '93b06540-8037-4f8c-9fd3-49cc9dd8b1c2',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a6-8b8217c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '93b06540-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a6-8b8218c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '97b16540-8037-4f8c-9fd3-49cc9dd8b1c3',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by,
                                    permission_id, role_id)
VALUES ('b6c87e8f-38bc-493e-95a6-8b8228c5f956', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.647194', 'AUTO', '99518de5-ed7c-4c2d-917f-c40f09790eee',
        'c2a5850f-8fbd-459b-8e97-d330866c3252');
        
        
-- remove VIEW_VISIT_ALL_LIST from Volunteer
delete
from public.role_permission rp
where rp.role_id = 'c1a5850f-8fbd-459b-8e97-d330866c3252'
  and rp.permission_id = '98b06540-8037-4f8c-9fd3-49cc9dd8b1c3'
  
-- updated Check-in to Checked-in
update public.lookup set child_lookup = '[
  {
    "key": "PENDING",
    "value": "Pending",
    "sequenceNumber": "1"
  },
  {
    "key": "ACCEPTED",
    "value": "Accepted",
    "sequenceNumber": "2"
  },
  {
    "key": "DECLINED",
    "value": "Declined",
    "sequenceNumber": "3"
  },
  {
    "key": "CANCELLED",
    "value": "Cancelled",
    "sequenceNumber": "4"
  },
  {
    "key": "CLOSED",
    "value": "Closed",
    "sequenceNumber": "5"
  },
  {
    "key": "COMPLETED",
    "value": "Completed",
    "sequenceNumber": "6"
  },
  {
    "key": "EXPIRED",
    "value": "Expired",
    "sequenceNumber": "7"
  },
  {
    "key": "CHECK_IN",
    "value": "Checked-in",
    "sequenceNumber": "8"
  },
  {
    "key": "NOSHOW",
    "value": "No-Show",
    "sequenceNumber": "9"
  }
]' where lookup_id = 'd1014466-2426-4f34-bb87-71edb7d798e6'