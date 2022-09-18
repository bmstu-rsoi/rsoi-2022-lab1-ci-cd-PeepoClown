--liquibase formatted sql

--changeset admin:1
CREATE TABLE IF NOT EXISTS public.t_person
(
    id serial PRIMARY KEY NOT NULL,
    name varchar(255) NOT NULL,
    age integer,
    address varchar(255),
    work varchar(255)
);
--rollback DROP TABLE IF EXISTS public.t_person;
