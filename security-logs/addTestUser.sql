INSERT INTO csm_user (LOGIN_NAME, MIGRATED_FLAG, FIRST_NAME, LAST_NAME, PASSWORD, UPDATE_DATE)
       VALUES ('testuser', 0 , 'testuser', 'testuser', 'orDBlaojDQE=', sysdate());

insert into principal (id, version, discriminator, login, csm_user_id, recv_bulk_emails,
            full_name, email, need_pwd_change,activity_status_id)
       select coalesce(MAX(id), 0)+1, 0, 'User', login_name, user_id, 1, concat(first_name, ' ', last_name), email_id, 0, 1
       from csm_user,principal
       where login_name = 'testuser'
       group by principal.id;

insert into csm_user_group_role_pg (user_id, role_id, protection_group_id, update_date)
       select user_id, 8, 1, sysdate()
       from csm_user
       where login_name = 'testuser';

-- add testuser to group Super Administrators
insert into group_user(user_id, group_id)
       select u.id, g.id
       from principal u, principal g
       where u.login='testuser'
       and g.name='Global Administrators';

