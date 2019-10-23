CREATE OR REPLACE TABLE product_category_path_item (
aindex int(11) auto_increment not null,
#Name should be [a-z][A-Z]וצהֵײִ[0-9]-._/
name varchar(50) not null,
product_category_id int(11) not null,
created datetime not null,
closed datetime,
changed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (aindex),
KEY categoryid_idx (product_category_id)
);