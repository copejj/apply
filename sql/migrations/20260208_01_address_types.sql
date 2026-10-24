BEGIN;
ALTER TABLE address_types 
ADD CONSTRAINT unique_address_type_name UNIQUE (name);

insert into address_types (name) values ('Billing') on conflict do nothing;

update address_types set name = 'Mailing' where address_type_id = 1;
COMMIT;