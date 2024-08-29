SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS inventory_summary_tbl;
CREATE TABLE inventory_summary_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  product_id BIGINT NULL,
  closing_stock DOUBLE NULL,
  valuation DOUBLE NULL,
  avg_valuation DOUBLE NULL,
  pur_price DOUBLE NULL,
  sales_price DOUBLE NULL,
  unique_batch_no VARCHAR(255) NULL,
  CONSTRAINT pk_inventory_summary_tbl PRIMARY KEY (id)
);

ALTER TABLE inventory_summary_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE inventory_summary_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE inventory_summary_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

DROP TABLE if EXISTS inventory_summary_transaction_details_tbl;
CREATE TABLE inventory_summary_transaction_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  opening_stock DOUBLE NULL,
  stock_in DOUBLE NULL,
  stock_out DOUBLE NULL,
  tranx_action VARCHAR(255) NULL,
  closing_stock DOUBLE NULL,
  tranx_date date NULL,
  tranx_id BIGINT NULL,
  h_unit_convesion DOUBLE NULL,
  m_unit_conversion DOUBLE NULL,
  l_unit_conversion DOUBLE NULL,
  financial_year VARCHAR(255) NULL,
  created_at datetime NULL,
  status BIT(1) NULL,
  valuation DOUBLE NULL,
  avg_valuation DOUBLE NULL,
  pur_price DOUBLE NULL,
  sales_price DOUBLE NULL,
  unique_batch_no VARCHAR(255) NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  product_id BIGINT NULL,
  transaction_type_id BIGINT NULL,
  CONSTRAINT pk_inventory_summary_transaction_details_tbl PRIMARY KEY (id)
);

ALTER TABLE inventory_summary_transaction_details_tbl ADD CONSTRAINT FK_INVENTORYSUMMARYTRANSACTIONDETAILSTBL_ON_TRANSACTIONTYPE FOREIGN KEY (transaction_type_id) REFERENCES transaction_type_master_tbl (id);

ALTER TABLE inventory_summary_transaction_details_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TRANSACTION_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE inventory_summary_transaction_details_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TRANSACTION_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE inventory_summary_transaction_details_tbl ADD CONSTRAINT FK_INVENTORY_SUMMARY_TRANSACTION_DETAILS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);
DROP TABLE if EXISTS inventory_serial_number_summary_tbl;
CREATE TABLE inventory_serial_number_summary_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  product_id BIGINT NULL,
  status BIT(1) NULL,
  serial_no VARCHAR(255) NULL,
  tranx_action VARCHAR(255) NULL,
  transaction_type_id BIGINT NULL,
  CONSTRAINT pk_inventory_serial_number_summary_tbl PRIMARY KEY (id)
);

ALTER TABLE inventory_serial_number_summary_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE inventory_serial_number_summary_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE inventory_serial_number_summary_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE inventory_serial_number_summary_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_TBL_ON_TRANSACTION_TYPE FOREIGN KEY (transaction_type_id) REFERENCES transaction_type_master_tbl (id);

DROP TABLE if EXISTS inventory_serial_number_summary_details_tbl;
CREATE TABLE inventory_serial_number_summary_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  product_id BIGINT NULL,
  transaction_type_id BIGINT NULL,
  serial_no VARCHAR(255) NULL,
  tranx_action VARCHAR(255) NULL,
  tranx_action_date date NULL,
  status BIT(1) NULL,
  CONSTRAINT pk_inventory_serial_number_summary_details_tbl PRIMARY KEY (id)
);

ALTER TABLE inventory_serial_number_summary_details_tbl ADD CONSTRAINT FK_INVENTORYSERIALNUMBERSUMMARYDETAILSTBL_ON_TRANSACTIONTYPE FOREIGN KEY (transaction_type_id) REFERENCES transaction_type_master_tbl (id);

ALTER TABLE inventory_serial_number_summary_details_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE inventory_serial_number_summary_details_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE inventory_serial_number_summary_details_tbl ADD CONSTRAINT FK_INVENTORY_SERIAL_NUMBER_SUMMARY_DETAILS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);
SET FOREIGN_KEY_CHECKS = 1;
