--liquibase formatted sql

--changeset starasov:1
CREATE TABLE IF NOT EXISTS socks
(
    id              bigint generated by default as identity primary key,
    color           varchar(20),
    cotton_part     int,
    stock           int
);