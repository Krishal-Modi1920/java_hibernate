INSERT INTO public.permissions (permission_id, created_at, created_by, status, updated_at, updated_by, name) VALUES ('98b06536-8037-4f8c-9fd3-49cc9dd8b1c4', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', 'VIEW_DASHBOARD_PREBOOKED_VISIT_FEEDBACK_SUMMARY');

INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd403', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c4', '5d66cf66-ba76-46c8-8243-5ff3f189f075');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd503', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c4', 'c2a5850f-8fbd-459b-8e97-d330866c3252');
INSERT INTO public.role_permission (role_permission_id, created_at, created_by, status, updated_at, updated_by, permission_id, role_id) 
VALUES ('ac2b63fa-f911-4158-9bd5-3699972bd603', '2023-09-28 10:25:41.647194', 'AUTO', 'ACTIVE', '2023-09-28 10:25:41.647194', 'AUTO', '98b06536-8037-4f8c-9fd3-49cc9dd8b1c4', 'e36573cf-8df1-4df6-bec2-5f2979bcc899');


-- 1) tour type added in lookup:

INSERT INTO public.lookup (lookup_id, created_at, created_by, status, updated_at, updated_by, child_lookup, key,
                           lang_meta, seq_number, value, parent_lookup_id)
VALUES ('20262468-6fd4-4839-a07d-475a95a4f1fe', '2023-10-03 09:57:32.288598', 'AUTO', 'ACTIVE',
        '2023-10-03 09:57:32.288598', 'AUTO', '[
    {
      "key": "Full & Customize",
      "value": "Full & Customize",
      "sequenceNumber": "1"
    },
    {
      "key": "Hourly",
      "value": "Hourly",
      "sequenceNumber": "2"
    },
    {
      "key": "Meet & Greet",
      "value": "Meet & Greet",
      "sequenceNumber": "3"
    }
  ]', 'TOUR_TYPE', null, 1, 'Tour Type', null);

-- 2) daily visit list added in lookup:

INSERT INTO public.lookup (lookup_id, created_at, created_by, status, updated_at, updated_by, child_lookup, key,
                           lang_meta, seq_number, value, parent_lookup_id)
VALUES ('d1014466-2426-4f35-bb87-71edb7d798e9', '2023-10-03 09:57:32.288598', 'AUTO', 'ACTIVE',
        '2023-10-03 09:57:32.288598', 'AUTO', '[
    {
      "key": "vyom.suthar@baps.dev",
      "value": "vyom.suthar.baps.dev",
      "sequenceNumber": "1"
    },
    {
      "key": "sanket.raval@baps.dev",
      "value": "sanket.raval@baps.dev",
      "sequenceNumber": "2"
    },
    {
      "key": "bhavin.patel@na.baps.org",
      "value": "bhavin.patel@na.baps.org",
      "sequenceNumber": "3"
    }
  ]', 'DAILY_VISIT_EMAIL', null, 1, 'Daily Visit Email', null);

-- 3) type of public visit (not added by me) in lookup:
INSERT INTO public.lookup (lookup_id, created_at, created_by, status, updated_at, updated_by, child_lookup, key,
                           lang_meta, seq_number, value, parent_lookup_id)
VALUES ('559ea39d-5c02-46e0-8896-2c1fde1a5790', '2023-10-03 09:57:32.288598', 'AUTO', 'ACTIVE',
        '2023-10-03 09:57:32.288598', 'AUTO', '[
    {
      "key": "55 Plus Adult Community Group",
      "value": "55 Plus Adult Community Group",
      "sequenceNumber": "1"
    },
    {
      "key": "Adult Day Care Center -ADCC",
      "value": "Adult Day Care Center -ADCC",
      "sequenceNumber": "2"
    },
    {
      "key": "Corporation and Professional Organizations",
      "value": "Corporation and Professional Organizations",
      "sequenceNumber": "3"
    },
    {
      "key": "Children Groups - Clubs",
      "value": "Children Groups - Clubs",
      "sequenceNumber": "5"
    },
    {
      "key": "College",
      "value": "College",
      "sequenceNumber": "6"
    },
    {
      "key": "Corporate",
      "value": "Corporate",
      "sequenceNumber": "7"
    },
    {
      "key": "Guest From India",
      "value": "Guest From India",
      "sequenceNumber": "8"
    },
    {
      "key": "Local, State, or Federal Government Officials",
      "value": "Local, State, or Federal Government Officials",
      "sequenceNumber": "9"
    },
    {
      "key": "High School",
      "value": "High School",
      "sequenceNumber": "11"
    },
    {
      "key": "Hindu Cultural Center",
      "value": "Hindu Cultural Center",
      "sequenceNumber": "12"
    },
    {
      "key": "Hindu Religion Class",
      "value": "Hindu Religion Class",
      "sequenceNumber": "13"
    },
    {
      "key": "Middle School",
      "value": "Middle School",
      "sequenceNumber": "14"
    },
    {
      "key": "Travel Group Company -Indian",
      "value": "Travel Group Company -Indian",
      "sequenceNumber": "16"
    },
    {
      "key": "Indian Cultural Center/Organization",
      "value": "Indian Cultural Center/Organization",
      "sequenceNumber": "17"
    },
    {
      "key": "Hindu Religious Organization",
      "value": "Hindu Religious Organization",
      "sequenceNumber": "18"
    },
    {
      "key": "University Groups",
      "value": "University Groups",
      "sequenceNumber": "19"
    },
    {
      "key": "Elementary School",
      "value": "Elementary School",
      "sequenceNumber": "20"
    },
    {
      "key": "Social Group",
      "value": "Social Group",
      "sequenceNumber": "21"
    },
    {
      "key": "Preschool",
      "value": "Preschool",
      "sequenceNumber": "22"
    },
    {
      "key": "Travel Group Company - Non-Indian",
      "value": "Travel Group Company - Non-Indian",
      "sequenceNumber": "23"
    }
  ]', 'TYPE_OF_PUBLIC_VISIT', null, 1, 'type of visit', null);

-- 4) daily visit email added in notification_templates table :

INSERT
INTO public.notification_templates (notification_template_id, created_at, created_by, status, updated_at, updated_by, notification_template, temmpate_id, version, channel) VALUES ('4248c90f-1f15-4456-263e-346de1d9234d', '2024-10-08 16:36:15.000000', 'AUTO', 'ACTIVE', '2024-10-08 16:36:27.000000', 'AUTO', 'DAILY_VISIT_EMAIL', 't-bbeb3425a0', 1, 'EMAIL');


-- 5) update visitor in lookup table :
UPDATE public.lookup SET child_lookup = e'[
                       {
                           "key": "CA",
                           "value": "CA",
                           "sequenceNumber": "1"
                       },
                       {
                           "key": "PA",
                           "value": "PA",
                           "sequenceNumber": "2"
                       },
                       {
                           "key": "COMMS",
                           "value": "COMMS",
                           "sequenceNumber": "3"
                       },
                       {
                           "key": "ONLINE TOUR",
                           "value": "ONLINE TOUR",
                           "sequenceNumber": "4"
                       }
                   ]' WHERE lookup_id LIKE '4dca7532-b06d-4d37-aed5-e102bf830c60' ESCAPE '#'