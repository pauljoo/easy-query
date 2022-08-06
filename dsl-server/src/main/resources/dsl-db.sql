CREATE TABLE ORDERS
(
    O_ORDERKEY      INT         NOT NULL PRIMARY KEY,
    O_CUSTKEY       INT         NOT NULL,
    O_ORDERSTATUS   TEXT        NOT NULL,
    O_TOTALPRICE    DECIMAL(15,2) NOT NULL,
    O_ORDERDATE     timestamp NOT NULL,
    O_ORDERPRIORITY TEXT        NOT NULL,
    O_CLERK         TEXT        NOT NULL,
    O_SHIPPRIORITY  INT         NOT NULL,
    O_COMMENT       TEXT        NOT NULL
);

CREATE TABLE CUSTOMER
(
    C_CUSTKEY    INT    NOT NULL PRIMARY KEY,
    C_NAME       TEXT   NOT NULL,
    C_ADDRESS    TEXT   NOT NULL,
    C_NATIONKEY  INT    NOT NULL,
    C_PHONE      TEXT   NOT NULL,
    C_ACCTBAL    DECIMAL(15,2) NOT NULL,
    C_MKTSEGMENT TEXT   NOT NULL,
    C_COMMENT    TEXT   NOT NULL
);

CREATE TABLE NATION(
       N_NATIONKEY INT NOT NULL PRIMARY KEY,
       N_NAME text NOT NULL,
       N_REGIONKEY INT NOT NULL,
       N_COMMENT text NOT NULL
);

load data infile 'orders.txt'
into table ORDERS
fields terminated by '|'
optionally enclosed by '"'
escaped by ','
lines terminated by '\r\n'
ignore 1 lines;

db.NATION.insert({"N_NATIONKEY": 0, "N_NAME": "ALGERIA", "N_REGIONKEY": 0, "N_COMMENT": "haggle. carefully final deposits detect slyly agai"})