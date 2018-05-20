CREATE TABLE isa_component(
  id SERIAL PRIMARY KEY,
  name VARCHAR(100)
);

CREATE TABLE isa_item(
  id SERIAL PRIMARY KEY,
  component_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  price INTEGER NULL ,
  FOREIGN KEY (component_id) REFERENCES isa_component
);

CREATE TABLE isa_user(
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE isa_role(
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL
);

CREATE TABLE isa_user_role(
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES isa_user,
  FOREIGN KEY (role_id) REFERENCES isa_role
);

CREATE TABLE isa_payment(
  id SERIAL PRIMARY KEY,
  from_id INTEGER NOT NULL,
  to_id INTEGER NOT NULL,
  amount INTEGER NOT NULL,
  status VARCHAR(32),
  FOREIGN KEY (from_id) REFERENCES isa_user,
  FOREIGN KEY (to_id) REFERENCES isa_user
);

CREATE TABLE isa_order(
  id SERIAL PRIMARY KEY,
  status VARCHAR(32),
  from_id INTEGER NOT NULL,
  to_id INTEGER NULL,
  payment_id INTEGER NULL,
  FOREIGN KEY (from_id) REFERENCES isa_user,
  FOREIGN KEY (to_id) REFERENCES isa_user,
  FOREIGN KEY (payment_id) REFERENCES isa_payment
);

CREATE TABLE isa_order_item(
  id SERIAL PRIMARY KEY,
  order_id INTEGER,
  item_id INTEGER,
  FOREIGN KEY (order_id) REFERENCES isa_order,
  FOREIGN KEY (item_id) REFERENCES isa_item
);

CREATE TABLE isa_storage(
  id SERIAL PRIMARY KEY
);

CREATE TABLE isa_provider(
  id SERIAL PRIMARY KEY,
  name VARCHAR(100)
);

CREATE TABLE isa_storage_item(
  id SERIAL PRIMARY KEY,
  storage_id INTEGER NOT NULL ,
  item_id INTEGER NOT NULL ,
  FOREIGN KEY (storage_id) REFERENCES isa_storage,
  FOREIGN KEY (item_id) REFERENCES isa_item
);

CREATE TABLE isa_provider_item(
  id SERIAL PRIMARY KEY,
  provider_id INTEGER NOT NULL,
  item_id INTEGER NOT NULL
);
