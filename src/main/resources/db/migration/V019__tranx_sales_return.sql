SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS tranx_sales_return_invoice_tbl;
CREATE TABLE tranx_sales_return_invoice_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   sundry_debtors_id BIGINT NULL,
   sales_account_ledger_id BIGINT NULL,
   sales_discount_ledger_id BIGINT NULL,
   sales_roundoff_id BIGINT NULL,
   associates_groups_id BIGINT NULL,
   sales_invoice_id BIGINT NULL,
   tranx_sales_challan_id BIGINT NULL,
   fiscal_year_id BIGINT NULL,
   sales_rtn_sr_no BIGINT NULL,
   sales_return_no VARCHAR(255) NULL,
   transaction_date date NULL,
   round_off DOUBLE NULL,
   total_base_amount DOUBLE NULL,
   total_amount DOUBLE NULL,
   totalcgst DOUBLE NULL,
   totalqty BIGINT NULL,
   totalsgst DOUBLE NULL,
   totaligst DOUBLE NULL,
   sales_discount_amount DOUBLE NULL,
   sales_discount_per DOUBLE NULL,
   total_sales_discount_amt DOUBLE NULL,
   additional_charges_total DOUBLE NULL,
   taxable_amount DOUBLE NULL,
   tcs DOUBLE NULL,
   status BIT(1) NULL,
   financial_year VARCHAR(255) NULL,
   narration VARCHAR(255) NULL,
   operations VARCHAR(255) NULL,
   created_by BIGINT NULL,
   created_date datetime NULL,
   CONSTRAINT pk_tranx_sales_return_invoice_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_ASSOCIATES_GROUPS FOREIGN KEY (associates_groups_id) REFERENCES associates_groups_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_SALES_ACCOUNT_LEDGER FOREIGN KEY (sales_account_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_SALES_DISCOUNT_LEDGER FOREIGN KEY (sales_discount_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_SALES_ROUNDOFF FOREIGN KEY (sales_roundoff_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_SUNDRY_DEBTORS FOREIGN KEY (sundry_debtors_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_TRANX_SALES_CHALLAN FOREIGN KEY (tranx_sales_challan_id) REFERENCES tranx_sales_challan_tbl (id);

DROP TABLE if EXISTS tranx_sales_return_invoice_details_tbl;
CREATE TABLE tranx_sales_return_invoice_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   sales_return_invoice_id BIGINT NULL,
   sales_invoice_id BIGINT NULL,
   product_id BIGINT NULL,
   packaging_id BIGINT NULL,
   sales_invoice_no VARCHAR(255) NULL,
   base_amt DOUBLE NULL,
   total_amount DOUBLE NULL,
   discount_amount DOUBLE NULL,
   discount_per DOUBLE NULL,
   discount_amount_cal DOUBLE NULL,
   discount_per_cal DOUBLE NULL,
   igst DOUBLE NULL,
   sgst DOUBLE NULL,
   cgst DOUBLE NULL,
   total_igst DOUBLE NULL,
   total_sgst DOUBLE NULL,
   total_cgst DOUBLE NULL,
   final_amount DOUBLE NULL,
   qty_high DOUBLE NULL,
   rate_high DOUBLE NULL,
   qty_medium DOUBLE NULL,
   rate_medium DOUBLE NULL,
   qty_low DOUBLE NULL,
   rate_low DOUBLE NULL,
   base_amt_high DOUBLE NULL,
   base_amt_low DOUBLE NULL,
   base_amt_medium DOUBLE NULL,
   status BIT(1) NULL,
   operations VARCHAR(255) NULL,
   created_date datetime NULL,
   created_by BIGINT NULL,
   CONSTRAINT pk_tranx_sales_return_invoice_details_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_invoice_details_tbl ADD CONSTRAINT FK_TRANXSALESRETURNINVOICEDETAILSTBL_ON_SALESRETURNINVOICE FOREIGN KEY (sales_return_invoice_id) REFERENCES tranx_sales_return_invoice_tbl (id);

ALTER TABLE tranx_sales_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_DETAILS_TBL_ON_PACKAGING FOREIGN KEY (packaging_id) REFERENCES packing_master_tbl (id);

ALTER TABLE tranx_sales_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_DETAILS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_sales_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_DETAILS_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

DROP TABLE if EXISTS tranx_sales_return_addi_charges_tbl;
CREATE TABLE tranx_sales_return_addi_charges_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   sales_return_invoice_id BIGINT NULL,
   sales_invoice_id BIGINT NULL,
   additional_charges_id BIGINT NULL,
   amount DOUBLE NULL,
   created_date datetime NULL,
   created_by BIGINT NULL,
   status BIT(1) NULL,
   operation VARCHAR(255) NULL,
   CONSTRAINT pk_tranx_sales_return_addi_charges_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_addi_charges_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_ADDI_CHARGES_TBL_ON_ADDITIONAL_CHARGES FOREIGN KEY (additional_charges_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_addi_charges_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_ADDI_CHARGES_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

ALTER TABLE tranx_sales_return_addi_charges_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_ADDI_CHARGES_TBL_ON_SALES_RETURN_INVOICE FOREIGN KEY (sales_return_invoice_id) REFERENCES tranx_sales_return_invoice_tbl (id);

DROP TABLE if EXISTS tranx_sales_return_duties_taxes_tbl;
CREATE TABLE tranx_sales_return_duties_taxes_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   sales_return_invoice_id BIGINT NOT NULL,
   sales_invoice_id BIGINT NOT NULL,
   sundry_debtors_id BIGINT NOT NULL,
   duties_taxes_ledger_id BIGINT NOT NULL,
   intra BIT(1) NULL,
   amount DOUBLE NULL,
   status BIT(1) NULL,
   created_date datetime NULL,
   created_by BIGINT NULL,
   CONSTRAINT pk_tranx_sales_return_duties_taxes_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_duties_taxes_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DUTIES_TAXES_TBL_ON_DUTIES_TAXES_LEDGER FOREIGN KEY (duties_taxes_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_sales_return_duties_taxes_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DUTIES_TAXES_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

ALTER TABLE tranx_sales_return_duties_taxes_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DUTIES_TAXES_TBL_ON_SALES_RETURN_INVOICE FOREIGN KEY (sales_return_invoice_id) REFERENCES tranx_sales_return_invoice_tbl (id);

ALTER TABLE tranx_sales_return_duties_taxes_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DUTIES_TAXES_TBL_ON_SUNDRY_DEBTORS FOREIGN KEY (sundry_debtors_id) REFERENCES ledger_master_tbl (id);

DROP TABLE if EXISTS tranx_sales_return_prod_sr_no_tbl;
CREATE TABLE tranx_sales_return_prod_sr_no_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NOT NULL,
   product_id BIGINT NOT NULL,
   sales_return_invoice_id BIGINT NULL,
   sales_invoice_id BIGINT NULL,
   sales_return_invoice_details_id BIGINT NULL,
   transaction_type_master_id BIGINT NULL,
   serial_no VARCHAR(255) NULL,
   sale_return_created_at datetime NULL,
   transaction_status VARCHAR(255) NULL,
   operations VARCHAR(255) NULL,
   created_by BIGINT NULL,
   created_date datetime NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_tranx_sales_return_prod_sr_no_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANXSALESRETURNPRODSRNOTBL_ON_SALESRETURNINVOICEDETAILS FOREIGN KEY (sales_return_invoice_details_id) REFERENCES tranx_sales_return_invoice_details_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_SALES_RETURN_INVOICE FOREIGN KEY (sales_return_invoice_id) REFERENCES tranx_sales_return_invoice_tbl (id);

ALTER TABLE tranx_sales_return_prod_sr_no_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_PROD_SR_NO_TBL_ON_TRANSACTION_TYPE_MASTER FOREIGN KEY (transaction_type_master_id) REFERENCES transaction_type_master_tbl (id);

DROP TABLE if EXISTS tranx_sales_return_details_units_tbl;
CREATE TABLE tranx_sales_return_details_units_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   sales_return_id BIGINT NULL,
   sales_return_details_id BIGINT NULL,
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
   CONSTRAINT pk_tranx_sales_return_details_units_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_return_details_units_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DETAILS_UNITS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_sales_return_details_units_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DETAILS_UNITS_TBL_ON_SALES_RETURN FOREIGN KEY (sales_return_id) REFERENCES tranx_sales_return_invoice_tbl (id);

ALTER TABLE tranx_sales_return_details_units_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DETAILS_UNITS_TBL_ON_SALES_RETURN_DETAILS FOREIGN KEY (sales_return_details_id) REFERENCES tranx_sales_return_invoice_details_tbl (id);

ALTER TABLE tranx_sales_return_details_units_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_DETAILS_UNITS_TBL_ON_UNIT FOREIGN KEY (unit_id) REFERENCES units_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;