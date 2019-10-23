CREATE OR REPLACE TABLE product_category_name_translation (
aindex int auto_increment not null,
product_category_id int(11) NOT NULL,
name varchar(100) not null,
language char(2) not null,
created datetime not null,
changed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (aindex)
);