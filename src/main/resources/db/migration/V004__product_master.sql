SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS group_tbl;
CREATE TABLE group_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  group_name VARCHAR(255) NULL,
  status BIT(1) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  CONSTRAINT pk_group_tbl PRIMARY KEY (id)
);

ALTER TABLE group_tbl ADD CONSTRAINT FK_GROUP_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE group_tbl ADD CONSTRAINT FK_GROUP_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);
DROP TABLE if EXISTS subgroup_tbl;
CREATE TABLE subgroup_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  subgroup_name VARCHAR(255) NULL,
  status BIT(1) NULL,
  group_id BIGINT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  CONSTRAINT pk_subgroup_tbl PRIMARY KEY (id)
);

ALTER TABLE subgroup_tbl ADD CONSTRAINT FK_SUBGROUP_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE subgroup_tbl ADD CONSTRAINT FK_SUBGROUP_TBL_ON_GROUP FOREIGN KEY (group_id) REFERENCES group_tbl (id);

ALTER TABLE subgroup_tbl ADD CONSTRAINT FK_SUBGROUP_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS category_tbl;
CREATE TABLE category_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  category_name VARCHAR(255) NULL,
  image VARCHAR(255) NULL,
  status BIT(1) NULL,
  subgroup_id BIGINT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  CONSTRAINT pk_category_tbl PRIMARY KEY (id)
);

ALTER TABLE category_tbl ADD CONSTRAINT FK_CATEGORY_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE category_tbl ADD CONSTRAINT FK_CATEGORY_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE category_tbl ADD CONSTRAINT FK_CATEGORY_TBL_ON_SUBGROUP FOREIGN KEY (subgroup_id) REFERENCES subgroup_tbl (id);

DROP TABLE if EXISTS subcategory_tbl;
CREATE TABLE subcategory_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  subcategory_name VARCHAR(255) NULL,
  status BIT(1) NULL,
  category_id BIGINT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  CONSTRAINT pk_subcategory_tbl PRIMARY KEY (id)
);

ALTER TABLE subcategory_tbl ADD CONSTRAINT FK_SUBCATEGORY_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE subcategory_tbl ADD CONSTRAINT FK_SUBCATEGORY_TBL_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category_tbl (id);

ALTER TABLE subcategory_tbl ADD CONSTRAINT FK_SUBCATEGORY_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS product_hsn_tbl;
CREATE TABLE product_hsn_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  hsn_number VARCHAR(255) NULL,
  description VARCHAR(255) NULL,
  igst DOUBLE NULL,
  cgst DOUBLE NULL,
  sgst DOUBLE NULL,
  type VARCHAR(255) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NOT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  status BIT(1) NULL,
  CONSTRAINT pk_producthsn_tbl PRIMARY KEY (id)
);

ALTER TABLE product_hsn_tbl ADD CONSTRAINT FK_PRODUCTHSN_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE product_hsn_tbl ADD CONSTRAINT FK_PRODUCTHSN_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS units_tbl;
CREATE TABLE units_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  unit_name VARCHAR(255) NULL,
  unit_code VARCHAR(255) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NOT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  updated_at datetime NULL,
  updated_by BIGINT NULL,
  status BIT(1) NULL,
  CONSTRAINT pk_units_tbl PRIMARY KEY (id)
);

ALTER TABLE units_tbl ADD CONSTRAINT FK_UNITS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE units_tbl ADD CONSTRAINT FK_UNITS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS tax_master_tbl;
CREATE TABLE tax_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  gst_per VARCHAR(255) NULL,
  cgst DOUBLE NULL,
  sgst DOUBLE NULL,
  igst DOUBLE NULL,
  sratio DOUBLE NULL,
  applicable_date date NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  status BIT(1) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  CONSTRAINT pk_tax_master_tbl PRIMARY KEY (id)
);

ALTER TABLE tax_master_tbl ADD CONSTRAINT FK_TAX_MASTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tax_master_tbl ADD CONSTRAINT FK_TAX_MASTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS packing_master_tbl;
CREATE TABLE packing_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  pack_name VARCHAR(255) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  status BIT(1) NULL,
  CONSTRAINT pk_packing_master_tbl PRIMARY KEY (id)
);

ALTER TABLE packing_master_tbl ADD CONSTRAINT FK_PACKING_MASTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE packing_master_tbl ADD CONSTRAINT FK_PACKING_MASTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS product_tbl;
CREATE TABLE product_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   product_name VARCHAR(255) NULL,
   product_code VARCHAR(255) NULL,
   price DOUBLE NULL,
   status BIT(1) NULL,
   description VARCHAR(255) NULL,
   alias VARCHAR(255) NULL,
   is_warranty_applicable BIT(1) NULL,
   warranty_days INT NULL,
   is_serial_number BIT(1) NULL,
   is_negative_stocks BIT(1) NULL,
   is_draft BIT(1) NULL,
   is_inventory BIT(1) NULL,
   created_by BIGINT NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   updated_by BIGINT NULL,
   applicable_date date NULL,
   image VARCHAR(255) NULL,
   unit_count INT NULL,
   is_multi_units BIT(1) NULL,
   is_packings BIT(1) NULL,
   packing_count INT NULL,
   group_id BIGINT NULL,
   subgroup_id BIGINT NULL,
   category_id BIGINT NULL,
   subcategory_id BIGINT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   hsn_id BIGINT NULL,
   taxmaster_id BIGINT NULL,
   CONSTRAINT pk_product_tbl PRIMARY KEY (id)
);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_GROUP FOREIGN KEY (group_id) REFERENCES group_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_HSN FOREIGN KEY (hsn_id) REFERENCES product_hsn_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_SUBCATEGORY FOREIGN KEY (subcategory_id) REFERENCES subcategory_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_SUBGROUP FOREIGN KEY (subgroup_id) REFERENCES subgroup_tbl (id);

ALTER TABLE product_tbl ADD CONSTRAINT FK_PRODUCT_TBL_ON_TAXMASTER FOREIGN KEY (taxmaster_id) REFERENCES tax_master_tbl (id);

DROP TABLE if EXISTS product_unit_packing_tbl;
CREATE TABLE product_unit_packing_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   unit_type VARCHAR(255) NULL,
   unit_conversion DOUBLE NULL,
   unit_conv_margn DOUBLE NULL,
   discount_in_per DOUBLE NULL,
   discount_in_amt DOUBLE NULL,
   purchase_rate DOUBLE NULL,
   sales_rate DOUBLE NULL,
   mrp DOUBLE NULL,
   min_qty DOUBLE NULL,
   max_qty DOUBLE NULL,
   min_sales_rate DOUBLE NULL,
   opening_qty DOUBLE NULL,
   opening_valution DOUBLE NULL,
   product_id BIGINT NOT NULL,
   units_id BIGINT NULL,
   packing_master_id BIGINT NULL,
   created_by BIGINT NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_product_unit_packing_tbl PRIMARY KEY (id)
);

ALTER TABLE product_unit_packing_tbl ADD CONSTRAINT FK_PRODUCT_UNIT_PACKING_TBL_ON_PACKING_MASTER FOREIGN KEY (packing_master_id) REFERENCES packing_master_tbl (id);

ALTER TABLE product_unit_packing_tbl ADD CONSTRAINT FK_PRODUCT_UNIT_PACKING_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE product_unit_packing_tbl ADD CONSTRAINT FK_PRODUCT_UNIT_PACKING_TBL_ON_UNITS FOREIGN KEY (units_id) REFERENCES units_tbl (id);
DROP TABLE if EXISTS product_opening_stocks_tbl;
CREATE TABLE product_opening_stocks_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   opening_qty DOUBLE NULL,
   product_id BIGINT NOT NULL,
   fiscal_year_id BIGINT NULL,
   created_by BIGINT NULL,
   created_at datetime NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_product_opening_stocks_tbl PRIMARY KEY (id)
);

ALTER TABLE product_opening_stocks_tbl ADD CONSTRAINT FK_PRODUCT_OPENING_STOCKS_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE product_opening_stocks_tbl ADD CONSTRAINT FK_PRODUCT_OPENING_STOCKS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

DROP TABLE if EXISTS product_image_master_tbl;
CREATE TABLE product_image_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   product_id BIGINT NULL,
   image_path VARCHAR(255) NULL,
   CONSTRAINT pk_product_image_master_tbl PRIMARY KEY (id)
);

ALTER TABLE product_image_master_tbl ADD CONSTRAINT FK_PRODUCT_IMAGE_MASTER_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);
SET FOREIGN_KEY_CHECKS = 1;

