SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS tranx_counter_sales_tbl;
CREATE TABLE tranx_counter_sales_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   fiscal_year_id BIGINT NULL,
   counter_sale_no BIGINT NULL,
   transaction_date date NULL,
   narration VARCHAR(255) NULL,
   customer_name VARCHAR(255) NULL,
   mobile_number BIGINT NULL,
   total_bill DOUBLE NULL,
   total_base_amt DOUBLE NULL,
   taxable_amt DOUBLE NULL,
   roundoff DOUBLE NULL,
   status BIT(1) NULL,
   is_bill_converted BIT(1) NULL,
   created_by BIGINT NULL,
   financial_year VARCHAR(255) NULL,
   created_at datetime NULL,
   CONSTRAINT pk_tranx_counter_sales_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_counter_sales_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_counter_sales_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE tranx_counter_sales_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS tranx_cs_prod_details_tbl;
CREATE TABLE tranx_cs_prod_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   product_id BIGINT NULL,
   tranx_counter_sale_id BIGINT NULL,
   packing_master_id BIGINT NULL,
   qty_high DOUBLE NULL,
   rate_high DOUBLE NULL,
   qty_medium DOUBLE NULL,
   rate_medium DOUBLE NULL,
   qty_low DOUBLE NULL,
   rate_low DOUBLE NULL,
   base_amt_high DOUBLE NULL,
   base_amt_low DOUBLE NULL,
   base_amt_medium DOUBLE NULL,
   total_price DOUBLE NULL,
   dis_amt DOUBLE NULL,
   dis_per DOUBLE NULL,
   dis_amt_cal DOUBLE NULL,
   dis_per_cal DOUBLE NULL,
   status BIT(1) NULL,
   bill_date date NULL,
   CONSTRAINT pk_tranx_cs_prod_details_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_cs_prod_details_tbl ADD CONSTRAINT FK_TRANX_CS_PROD_DETAILS_TBL_ON_PACKING_MASTER FOREIGN KEY (packing_master_id) REFERENCES packing_master_tbl (id);

ALTER TABLE tranx_cs_prod_details_tbl ADD CONSTRAINT FK_TRANX_CS_PROD_DETAILS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_cs_prod_details_tbl ADD CONSTRAINT FK_TRANX_CS_PROD_DETAILS_TBL_ON_TRANX_COUNTER_SALE FOREIGN KEY (tranx_counter_sale_id) REFERENCES tranx_counter_sales_tbl (id);

DROP TABLE if EXISTS tranx_cs_pr_sr_no;
CREATE TABLE tranx_cs_pr_sr_no (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NOT NULL,
   product_id BIGINT NOT NULL,
   counter_sales_details_id BIGINT NULL,
   counter_sale_id BIGINT NULL,
   serial_no VARCHAR(255) NULL,
   transaction_status VARCHAR(255) NULL,
   operations VARCHAR(255) NULL,
   created_by BIGINT NULL,
   created_at datetime NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_tranx_cs_pr_sr_no PRIMARY KEY (id)
);

ALTER TABLE tranx_cs_pr_sr_no ADD CONSTRAINT FK_TRANX_CS_PR_SR_NO_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_cs_pr_sr_no ADD CONSTRAINT FK_TRANX_CS_PR_SR_NO_ON_COUNTER_SALE FOREIGN KEY (counter_sale_id) REFERENCES tranx_counter_sales_tbl (id);

ALTER TABLE tranx_cs_pr_sr_no ADD CONSTRAINT FK_TRANX_CS_PR_SR_NO_ON_COUNTER_SALES_DETAILS FOREIGN KEY (counter_sales_details_id) REFERENCES tranx_cs_prod_details_tbl (id);

ALTER TABLE tranx_cs_pr_sr_no ADD CONSTRAINT FK_TRANX_CS_PR_SR_NO_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_cs_pr_sr_no ADD CONSTRAINT FK_TRANX_CS_PR_SR_NO_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

DROP TABLE if EXISTS tranx_counter_sales_details_units_tbl;
CREATE TABLE tranx_counter_sales_details_units_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   counter_sales_id BIGINT NULL,
   counter_sales_details_id BIGINT NULL,
   product_id BIGINT NULL,
   unit_id BIGINT NULL,
   unit_conversions DOUBLE NULL,
   qty DOUBLE NULL,
   rate DOUBLE NULL,
   base_amt DOUBLE NULL,
   created_at datetime NULL,
   created_by BIGINT NULL,
   updated_at datetime NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_tranx_counter_sales_details_units_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_counter_sales_details_units_tbl ADD CONSTRAINT FK_TRANXCOUNTERSALESDETAILSUNITSTBL_ON_COUNTERSALESDETAILS FOREIGN KEY (counter_sales_details_id) REFERENCES tranx_cs_prod_details_tbl (id);

ALTER TABLE tranx_counter_sales_details_units_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_DETAILS_UNITS_TBL_ON_COUNTER_SALES FOREIGN KEY (counter_sales_id) REFERENCES tranx_counter_sales_tbl (id);

ALTER TABLE tranx_counter_sales_details_units_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_DETAILS_UNITS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_counter_sales_details_units_tbl ADD CONSTRAINT FK_TRANX_COUNTER_SALES_DETAILS_UNITS_TBL_ON_UNIT FOREIGN KEY (unit_id) REFERENCES units_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;