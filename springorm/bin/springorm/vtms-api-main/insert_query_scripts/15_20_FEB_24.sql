-- update roles 
update public.roles
set is_check_system_role = true
where role_id in ('5d66cf66-ba76-46c8-8243-5ff3f189f075', 'e36573cf-8df1-4df6-bec2-5f2979bcc899',
                  'd74fc08e-e118-4b1a-8e95-51bc1f049e6e');
                  
update public.roles
set is_check_system_role = false
where role_id not in ('5d66cf66-ba76-46c8-8243-5ff3f189f075', 'e36573cf-8df1-4df6-bec2-5f2979bcc899',
                  'd74fc08e-e118-4b1a-8e95-51bc1f049e6e');