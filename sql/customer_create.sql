CREATE TABLE cc_type
(
  cc_type_id tinyint(4) NOT NULL auto_increment,
  name varchar(25) NOT NULL default '',
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (cc_type_id)
);

INSERT INTO cc_type VALUES (1,'Visa',1,'2004-03-12 11:39:44',20040312113944);
INSERT INTO cc_type VALUES (2,'Mastercard',1,'2004-03-12 11:39:44',20040312113944);
INSERT INTO cc_type VALUES (3,'Discover',1,'2004-03-12 11:39:44',20040312113944);

CREATE TABLE customer (
  customer_id int(11) NOT NULL auto_increment,
  prefix varchar(5) default NULL,
  first_name varchar(50) NOT NULL default '',
  last_name varchar(75) NOT NULL default '',
  suffix varchar(10) default NULL,
  company_name varchar(100) default NULL,
  email varchar(255) default NULL,
  address1 varchar(50) default NULL,
  address2 varchar(50) default NULL,
  city varchar(50) default NULL,
  state_id tinyint(4) default '-1',
  postal_code varchar(10) default NULL,
  country_id tinyint(4) default '-1',
  cc_partial varchar(16) default NULL,
  cvv2_number varchar(4) default NULL,
  id_token varchar(24) default NULL,
  token_password varchar(24) default NULL,
  cc_type_id tinyint(4) default '-1',
  exp_date date default NULL,
  name_on_card varchar(100) default NULL,
  billing_address1 varchar(50) default NULL,
  billing_address2 varchar(50) default NULL,
  billing_city varchar(50) default NULL,
  billing_state_id tinyint(4) default '-1',
  billing_postal_code varchar(10) default NULL,
  billing_country_id tinyint(4) default '-1',
  primary_phone varchar(12) default NULL,
  secondary_phone varchar(12) default NULL,
  fax varchar(12) default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (customer_id)
);

CREATE TABLE customer_product (
  customer_product_id int(11) NOT NULL auto_increment,
  product_id int(11) NOT NULL default '0',
  customer_id int(11) NOT NULL default '0',
  amount_paid float(6,2) NOT NULL default '0.00',
  promotion_id int(11) default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (customer_product_id)
) TYPE=MyISAM;

CREATE TABLE customer_subscription (
  customer_subscription_id int(11) NOT NULL auto_increment,
  subscription_id int(11) NOT NULL default '0',
  customer_id int(11) NOT NULL default '0',
  start_date datetime NOT NULL default '0000-00-00 00:00:00',
  end_date datetime default NULL,
  amount_paid float(6,2) NOT NULL default '0.00',
  promotion_id int(11) default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (customer_subscription_id)
) TYPE=MyISAM;

product |CREATE TABLE `product` (
  `product_id` int(11) NOT NULL auto_increment,
  `reference_string` varchar(50) NOT NULL default '',
  `name` varchar(50) NOT NULL default '',
  `product_number` varchar(100) default NULL,
  `pricing_type_id` tinyint(4) NOT NULL default '1',
  `regular_price` float(12,2) default NULL,
  `sale_price` float(12,2) default NULL,
  `volume_pricing_config` varchar(255) default NULL,
  `is_recurring` tinyint(4) NOT NULL default '0',
  `search_keywords` text,
  `apply_category_discount` tinyint(4) NOT NULL default '1',
  `apply_state_tax` tinyint(4) NOT NULL default '1',
  `apply_country_tax` tinyint(4) NOT NULL default '1',
  `inventory_count` int(11) default NULL,
  `max_quantity` int(11) default NULL,
  `thumbnail_image_url` varchar(255) default NULL,
  `standard_image_url` varchar(255) default NULL,
  `enlarged_image_url` varchar(255) default NULL,
  `short_description` text NOT NULL,
  `long_description` text NOT NULL,
  `delivery_method_type_id` tinyint(4) NOT NULL default '3',
  `delivery_method_config` text NOT NULL,
  `allow_store_pickup` tinyint(4) NOT NULL default '0',
  `free_shipping` tinyint(4) NOT NULL default '0',
  `related_products` varchar(255) default NULL,
  `product_options` varchar(255) default NULL,
  `categories` varchar(255) default NULL,
  `status` tinyint(4) NOT NULL default '1',
  `creation_date` datetime NOT NULL default '0000-00-00 00:00:00',
  `last_updated` timestamp(14) NOT NULL,
  PRIMARY KEY  (`product_id`),
  UNIQUE KEY `universal_identifier` (`reference_string`);

CREATE TABLE product_type (
  product_type_id tinyint(4) NOT NULL auto_increment,
  name varchar(50) NOT NULL default '',
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (product_type_id)
) TYPE=MyISAM;

INSERT INTO product_type VALUES (1,'Simple',1,'2004-05-27 00:34:41',20040527003441);
INSERT INTO product_type VALUES (2,'Subscription',1,'2004-05-27 00:34:41',20040527003441);

CREATE TABLE promotion (
  promotion_id int(11) NOT NULL auto_increment,
  name varchar(50) NOT NULL default '',
  description varchar(255) NOT NULL default '',
  code varchar(10) NOT NULL default '',
  definition varchar(25) NOT NULL default '',
  start_date datetime NOT NULL default '0000-00-00 00:00:00',
  end_date datetime default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (promotion_id),
  UNIQUE KEY code (code)
) TYPE=MyISAM;

CREATE TABLE subscription (
  subscription_id int(11) NOT NULL auto_increment,
  subscription_type_id int(11) NOT NULL default '0',
  universal_identifier varchar(50) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  description varchar(255) NOT NULL default '',
  price float(6,2) default NULL,
  is_calculated tinyint(4) NOT NULL default '0',
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (subscription_id),
  UNIQUE KEY universal_identifier (universal_identifier)
) TYPE=MyISAM;

CREATE TABLE subscription_feature (
  subscription_feature_id int(11) NOT NULL auto_increment,
  universal_identifier varchar(50) NOT NULL default '',
  subscription_id int(11) NOT NULL default '0',
  feature_subscription_id int(11) default NULL,
  display_text varchar(50) NOT NULL default '',
  description varchar(255) NOT NULL default '',
  price float(7,3) default NULL,
  show_price tinyint(4) NOT NULL default '1',
  linked_subscription_feature_id int(11) default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (subscription_feature_id)
) TYPE=MyISAM;

CREATE TABLE subscription_feature_group (
  subscription_feature_group_id int(11) NOT NULL auto_increment,
  product_id int(11) default NULL,
  universal_identifier varchar(50) NOT NULL default '',
  name varchar(50) NOT NULL default '',
  exclusive tinyint(4) NOT NULL default '0',
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (subscription_feature_group_id)
) TYPE=MyISAM;

CREATE TABLE subscription_feature_option (
  subscription_feature_option_id int(11) NOT NULL auto_increment,
  universal_identifier varchar(50) NOT NULL default '',
  subscription_feature_id int(11) NOT NULL default '0',
  display_text varchar(50) NOT NULL default '',
  description varchar(255) NOT NULL default '',
  unit_price float(7,3) default NULL,
  num_units int(11) default NULL,
  order_id tinyint(4) default NULL,
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (subscription_feature_option_id)
) TYPE=MyISAM;

CREATE TABLE subscription_type (
  subscription_type_id int(11) NOT NULL auto_increment,
  name varchar(50) NOT NULL default '',
  description varchar(255) NOT NULL default '',
  status_id tinyint(4) NOT NULL default '1',
  creation_date datetime NOT NULL default '0000-00-00 00:00:00',
  last_updated timestamp(14) NOT NULL,
  PRIMARY KEY  (subscription_type_id)
) TYPE=MyISAM;

CREATE TABLE customer_subscription_detail
(
    customer_subscription_detail_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    customer_subscription_id INT NOT NULL,
    subscription_feature_id INT NOT NULL,
    subscription_feature_option_id INT,
    amount_paid FLOAT(6,2),
    promotion_id INT,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    status_id TINYINT NOT NULL DEFAULT 1,
    creation_date DATETIME NOT NULL,
    last_updated TIMESTAMP NOT NULL
) TYPE=ISAM;