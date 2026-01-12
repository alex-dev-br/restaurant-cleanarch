CREATE TABLE restaurant_employees (
                                      restaurant_id BIGINT NOT NULL,
                                      employee_id UUID NOT NULL,
                                      PRIMARY KEY (restaurant_id, employee_id),
                                      CONSTRAINT fk_restaurant_employees_restaurant
                                          FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
                                      CONSTRAINT fk_restaurant_employees_employee
                                          FOREIGN KEY (employee_id) REFERENCES users(id)
);