-- delete unnecessary data from lookup
delete
from public.lookup
where key in ('SERVICE_TYPE', 'SERVICE_SUB_TYPE_TOUR', 'SERVICE_SUB_TYPE_MEETING', 'SERVICE_SUB_TYPE_SERVICE',
              'VISIT_TASKS_PRIORITY')