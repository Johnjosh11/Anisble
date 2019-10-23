CREATE OR REPLACE TABLE product_category (
aindex int auto_increment not null,
#[a-z][A-Z]וצהֵײִ[0-9]-._/
path_item_id int(11),
code_name varchar(100) not null,
meta_description text,
icon_name varchar(256),
weight int not null,
type set('sales','marketing','reporting') not null,
#content_type field can be removed when webshop supports pages which can have products and devices
content_type enum('products', 'devices', 'mixed') not null, 
validfrom datetime,
validto datetime,
created datetime not null,
changed timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
primary key (aindex),
key validto_idx (validto)
);
