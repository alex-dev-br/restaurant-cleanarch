INSERT INTO users (id, name, email, password_hash, user_type_id)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'Dev Owner',
           'dev.owner@local',
           'dev',
           (SELECT id FROM user_types WHERE name = 'RESTAURANT_OWNER')
       );
