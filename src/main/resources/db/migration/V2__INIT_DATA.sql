INSERT INTO roles (name) VALUES ('CREATE_USER_TYPE');
INSERT INTO roles (name) VALUES ('UPDATE_USER_TYPE');
INSERT INTO roles (name) VALUES ('DELETE_USER_TYPE');
INSERT INTO roles (name) VALUES ('VIEW_USER_TYPE');

INSERT INTO roles (name) VALUES ('VIEW_ROLE');

INSERT INTO roles (name) VALUES ('CREATE_RESTAURANT');
INSERT INTO roles (name) VALUES ('UPDATE_RESTAURANT');
INSERT INTO roles (name) VALUES ('DELETE_RESTAURANT');
INSERT INTO roles (name) VALUES ('VIEW_RESTAURANT');
INSERT INTO roles (name) VALUES ('VIEW_RESTAURANT_MANAGEMENT');

INSERT INTO roles (name) VALUES ('RESTAURANT_OWNER');

INSERT INTO roles (name) VALUES ('CREATE_USER');
INSERT INTO roles (name) VALUES ('UPDATE_USER');
INSERT INTO roles (name) VALUES ('DELETE_USER');
INSERT INTO roles (name) VALUES ('VIEW_USER');

INSERT INTO roles (name) VALUES ('CREATE_MENU_ITEM');
INSERT INTO roles (name) VALUES ('UPDATE_MENU_ITEM');
INSERT INTO roles (name) VALUES ('DELETE_MENU_ITEM');
INSERT INTO roles (name) VALUES ('VIEW_MENU_ITEM');

INSERT INTO user_types (name) VALUES ('RESTAURANT_OWNER');
INSERT INTO user_types (name) VALUES ('CUSTOMER');

-- associa todas as roles ao user type RESTAURANT_OWNER
INSERT INTO user_types_roles (user_type_id, role_id)
    SELECT ut.id, r.id FROM user_types ut JOIN roles r ON 1=1 WHERE ut.name = 'RESTAURANT_OWNER';

INSERT INTO user_types_roles (user_type_id, role_id)
SELECT ut.id, r.id FROM user_types ut JOIN roles r ON 1=1 WHERE ut.name = 'CUSTOMER' AND r.name in ('VIEW_RESTAURANT', 'VIEW_MENU_ITEM');