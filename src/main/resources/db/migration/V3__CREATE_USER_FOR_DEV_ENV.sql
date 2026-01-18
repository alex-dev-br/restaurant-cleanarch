INSERT INTO users (id, name, email, password_hash, user_type_id)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'dev',
           'dev.ownerId@mail.com',
           '$2a$10$i4yKBZSYKil7F5xZPtjVtepDhu4wW9d9q.ojsgAD0BVDTieTvHXMm', -- dev
           (SELECT id FROM user_types WHERE name = 'RESTAURANT_OWNER')
       );
