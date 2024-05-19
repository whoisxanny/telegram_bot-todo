-- liquibase formatted sql

--changeset atimurovich:1
--CREATE TABLE task (id BIGINT NOT NULL IDENTITY, chat_id BIGINT NOT NULL, notification_text TEXT NOT NULL, notificationDatetime DATETIME NOT NULL, PRIMARY KEY (id));