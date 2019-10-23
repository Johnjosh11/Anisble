CREATE OR REPLACE TABLE product_category_handset_map (
aindex int(11) auto_increment not null,
product_category_id int(11) not null,
handset_model_id int(11) not null,
validfrom datetime,
validto datetime,
created datetime not null,
changed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (aindex),
key validto_idx (validto)
);