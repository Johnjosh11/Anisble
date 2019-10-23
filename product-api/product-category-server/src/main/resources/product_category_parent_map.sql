CREATE OR REPLACE TABLE product_category_parent_map (
aindex int auto_increment not null,
product_category_id int,
parent_id int,
validfrom datetime,
validto datetime,
created datetime not null,
changed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (aindex),
key prodcat_idx (product_category_id, validto),
key validto_idx (validto)
);
