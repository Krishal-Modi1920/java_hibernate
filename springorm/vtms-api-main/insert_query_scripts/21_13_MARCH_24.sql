--update existing personnel rating
update public.visit_personnel
set visitor_rating = '[
        {
            "key": "knowlegeable",
            "rating": 1
        },
        {
            "key": "friendly",
            "rating": 1
        },
        {
            "key": "attentive",
            "rating": 1
        },
        {
            "key": "able_to_answer_your_questions",
            "rating": 1
        }
    ]'
where visitor_rating is not null;


--update general feedback
update public.feedbacks
set comment                         = 'test',
    visitor_general_feedback_rating = '[
      {
        "key": "overall_feedback",
        "rating": 1
      },
      {
        "key": "booking_process",
        "rating": 1
      },
      {
        "key": "welcome_center",
        "rating": 1
      },
      {
        "key": "theater",
        "rating": 1
      },
      {
        "key": "mandir",
        "rating": 1
      },
      {
        "key": "exhibition",
        "rating": 1
      },
      {
        "key": "food-service",
        "rating": 1
      },
      {
        "key": "parking-service",
        "rating": 1
      },
      {
        "key": "visit comments",
        "rating": 1
      },
      {
        "key": "visitor comment",
        "rating": 1
      },
      {
        "key": "personnel comment",
        "rating": 1
      },
      {
        "key": "expense comment",
        "rating": 1
      },
      {
        "key": "visit-service comment",
        "rating": 1
      },
      {
        "key": "visit-tour comment",
        "rating": 1
      },
      {
        "key": "visit-meeting comment",
        "rating": 1
      }
    ]'
where visitor_general_feedback_rating is not null;

--insert service with location mapping(relation)

INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('85079c43-0820-4918-ac62-dba7e2a0f55c', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '85079c43-0820-4918-ac62-dba7e2a0f55c',
        '419023c0-87fb-4036-96ce-dba3cbf984eb');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('adbfa882-7994-4857-acda-bfe0b1e21486', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'adbfa882-7994-4857-acda-bfe0b1e21486',
        '419023c0-87fb-4036-96ce-dba3cbf984eb');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('8cae610e-c826-49ea-9ebe-90ceceaea9dc', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '8cae610e-c826-49ea-9ebe-90ceceaea9dc',
        '419023c0-87fb-4036-96ce-dba3cbf984eb');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('e0fdc21e-e515-4c4f-8242-2f3ac5343bfb', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'e0fdc21e-e515-4c4f-8242-2f3ac5343bfb',
        '419023c0-87fb-4036-96ce-dba3cbf984eb');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('9d5f6ace-f7c5-4318-aad3-5b4e2b7592ed', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '9d5f6ace-f7c5-4318-aad3-5b4e2b7592ed',
        '419023c0-87fb-4036-96ce-dba3cbf984eb');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('847c9f51-855f-4a40-ba94-dac5980bf709', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '847c9f51-855f-4a40-ba94-dac5980bf709',
        '15424218-7eaa-44c5-bdfe-ce2e5355bcd8');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('3a13ae8e-cabb-4d7a-b219-3f4059c5b4a4', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '3a13ae8e-cabb-4d7a-b219-3f4059c5b4a4',
        '9e51bff3-b70a-4442-8423-c46ac23db32b');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('96c678fc-204c-4908-a46a-5573e03260ff', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '96c678fc-204c-4908-a46a-5573e03260ff',
        'c52c566e-feda-46bc-97d1-c1eebea13bd2');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('3c10475f-7126-419a-b9b4-8b0fa99230b6', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '3c10475f-7126-419a-b9b4-8b0fa99230b6',
        '0096a255-72fb-45fa-9c7a-a26a8fe281b6');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('db5bbbdd-a6c3-47ab-9f2f-45b4fedcf2ed', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'db5bbbdd-a6c3-47ab-9f2f-45b4fedcf2ed',
        '5ceb4448-a321-4df1-ac6c-b4537da28ec6');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('60ae3856-8a68-4b81-8ee6-f34355c91103', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '60ae3856-8a68-4b81-8ee6-f34355c91103',
        '4d6eb2af-bf6a-4002-8b2c-c34de3c84356');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('5501e9fb-fe7d-497a-b3ad-dca98ec713c0', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '5501e9fb-fe7d-497a-b3ad-dca98ec713c0',
        '17ddd9ce-c21d-4124-99a9-9321195198e0');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('db3defb5-e5cb-461b-8d2e-b1d598ed7c33', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'db3defb5-e5cb-461b-8d2e-b1d598ed7c33',
        '9a78d4db-2439-49cb-a0c4-288b5c652399');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('d433f9f1-cd27-4a24-bb63-bf11836b7cb5', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'd433f9f1-cd27-4a24-bb63-bf11836b7cb5',
        '77741180-a9ce-4933-a0d8-23e2adb247df');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('0c4cac1d-3d48-41cd-be19-141b5a9e8e00', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '0c4cac1d-3d48-41cd-be19-141b5a9e8e00',
        '77741180-a9ce-4933-a0d8-23e2adb247df');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('23c20ae5-ebb4-45d5-a246-10299a490dc9', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', '23c20ae5-ebb4-45d5-a246-10299a490dc9',
        '77741180-a9ce-4933-a0d8-23e2adb247df');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('aedfd50f-6693-4269-af14-f520ce12788a', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'aedfd50f-6693-4269-af14-f520ce12788a',
        '77741180-a9ce-4933-a0d8-23e2adb247df');
INSERT INTO public.service_location (service_location_id, created_at, created_by, status, updated_at, updated_by,
                                     location_id, service_id)
VALUES ('fd89ca5d-9ec0-426f-8ea5-adc9cd49fdb5', '2023-09-28 10:25:41.891321', 'AUTO', 'ACTIVE',
        '2023-09-28 10:25:41.891321', 'AUTO', 'fd89ca5d-9ec0-426f-8ea5-adc9cd49fdb5',
        '77741180-a9ce-4933-a0d8-23e2adb247df');