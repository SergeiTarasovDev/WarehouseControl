--liquibase formatted sql

--changeset starasov:1
CREATE TABLE IF NOT EXISTS socks
(
    id              bigint generated by default as identity primary key,
    color           varchar(20),
    cotton_part     int,
    stock           int
);

--changeset starasov:2
CREATE TABLE IF NOT EXISTS trading_actions
(
    id              bigint generated by default as identity primary key,
    operation_time  timestamp,
    operation_type  int,
    sock_id         bigint,
    quantity        int
);