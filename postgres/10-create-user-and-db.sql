-- file: 10-create-user-and-db.sql
CREATE DATABASE persons;
CREATE ROLE admin WITH PASSWORD 'admin';
GRANT ALL PRIVILEGES ON DATABASE persons TO admin;
ALTER ROLE admin WITH LOGIN;
