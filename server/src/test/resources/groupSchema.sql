CREATE SCHEMA IF NOT EXISTS "SVC_GROUP";
SET SCHEMA "SVC_GROUP";

CREATE TABLE IF NOT EXISTS "GROUPS" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "REALM" VARCHAR2(25) NOT NULL,
    "OWNER_ID" VARCHAR2(64) NOT NULL,
    "OWNER_TYPE" VARCHAR2(128),
    "OWNER_REALM" VARCHAR2(25)
) AS SELECT * FROM csvread('classpath:data/groups.csv');

CREATE TABLE IF NOT EXISTS "GROUP_NAMES" (
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "TEXT" VARCHAR2(2000) NOT NULL,
    "LANGUAGE" VARCHAR2(10) NOT NULL
) AS SELECT * FROM csvread('classpath:data/group_names.csv');

CREATE TABLE IF NOT EXISTS "GROUP_DESCRIPTIONS" (
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "TEXT" VARCHAR2(2000) NOT NULL,
    "LANGUAGE" VARCHAR2(10) NOT NULL
) AS SELECT * FROM csvread('classpath:data/group_descriptions.csv');

CREATE TABLE IF NOT EXISTS "GROUP_MEMBERSHIPS" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "MEMBER_ID" VARCHAR2(64) NOT NULL,
    "MEMBER_TYPE" VARCHAR2(128),
    "MEMBER_REALM" VARCHAR2(25),
 ) AS SELECT * FROM csvread('classpath:data/group_memberships.csv');
 
 CREATE TABLE IF NOT EXISTS "GROUP_ENTITLEMENTS" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "ENTITLEMENT" VARCHAR2(128) NOT NULL
) AS SELECT * FROM csvread('classpath:data/group_entitlements.csv');

CREATE TABLE IF NOT EXISTS "GROUPS_AUDIT" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "REALM" VARCHAR2(25) NOT NULL,
    "OWNER_ID" VARCHAR2(64) NOT NULL,
    "OWNER_TYPE" VARCHAR2(128),
    "OWNER_REALM" VARCHAR2(25)
);

CREATE TABLE IF NOT EXISTS "GROUP_MEMBERSHIPS_AUDIT" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "MEMBER_ID" VARCHAR2(64) NOT NULL,
    "MEMBER_TYPE" VARCHAR2(128),
    "MEMBER_REALM" VARCHAR2(25)
);

 CREATE TABLE IF NOT EXISTS "GROUP_ENTITLEMENTS_AUDIT" (
    "ID" VARCHAR2(64) NOT NULL,
    "CREATION_INSTANT" NUMBER(19) NOT NULL,
    "CREATOR" VARCHAR2(64) NOT NULL,
    "CREATOR_APPLICATION_ID" VARCHAR2(64) NOT NULL,
    "VERSION" NUMBER(19) NOT NULL,
    "GROUP_ID" VARCHAR2(64) NOT NULL,
    "ENTITLEMENT" VARCHAR2(128) NOT NULL
 );

COMMIT;