INSERT INTO user_types (name) VALUES ('RESTAURANT_OWNER');

-- associa todas as roles ao user type RESTAURANT_OWNER
INSERT INTO user_types_roles (user_type_id, role_id)
SELECT ut.id, r.id
FROM user_types ut
         JOIN roles r ON 1=1
WHERE ut.name = 'RESTAURANT_OWNER';
