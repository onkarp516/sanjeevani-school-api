SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS tranx_pur_return_invoice_tbl;
CREATE TABLE tranx_pur_return_invoice_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  sundry_creditors_id BIGINT NULL,
  purchase_account_ledger_id BIGINT NULL,
  purchase_discount_ledger_id BIGINT NULL,
  associates_groups_id BIGINT NULL,
  purchase_roundoff_id BIGINT NULL,
  tranx_pur_invoice_id BIGINT NULL,
  tranx_pur_challan_id BIGINT NULL,
  fiscal_year_id BIGINT NULL,
  pur_return_srno BIGINT NULL,
  pur_rtn_no VARCHAR(255) NULL,
  transaction_date date NULL,
  round_off DOUBLE NULL,
  total_base_amount DOUBLE NULL,
  total_amount DOUBLE NULL,
  totalcgst DOUBLE NULL,
  totalqty BIGINT NULL,
  totalsgst DOUBLE NULL,
  totaligst DOUBLE NULL,
  purchase_discount_amount DOUBLE NULL,
  purchase_discount_per DOUBLE NULL,
  total_purchase_discount_amt DOUBLE NULL,
  additional_charges_total DOUBLE NULL,
  taxable_amount DOUBLE NULL,
  tcs DOUBLE NULL,
  status BIT(1) NULL,
  financial_year VARCHAR(255) NULL,
  narration VARCHAR(255) NULL,
  operations VARCHAR(255) NULL,
  created_at datetime NULL,
  created_by BIGINT NULL,
  CONSTRAINT pk_tranx_pur_return_invoice_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_ASSOCIATES_GROUPS FOREIGN KEY (associates_groups_id) REFERENCES associates_groups_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_PURCHASE_ACCOUNT_LEDGER FOREIGN KEY (purchase_account_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_PURCHASE_DISCOUNT_LEDGER FOREIGN KEY (purchase_discount_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_PURCHASE_ROUNDOFF FOREIGN KEY (purchase_roundoff_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_SUNDRY_CREDITORS FOREIGN KEY (sundry_creditors_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_TRANX_PUR_CHALLAN FOREIGN KEY (tranx_pur_challan_id) REFERENCES tranx_purchase_challan_tbl (id);

ALTER TABLE tranx_pur_return_invoice_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_TBL_ON_TRANX_PUR_INVOICE FOREIGN KEY (tranx_pur_invoice_id) REFERENCES tranx_purchase_invoice_tbl (id);

DROP TABLE if EXISTS tranx_pur_return_invoice_additional_charges_tbl;
CREATE TABLE tranx_pur_return_invoice_additional_charges_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  pur_return_invoice_id BIGINT NULL,
  additional_charges_id BIGINT NULL,
  tranx_pur_invoice_id BIGINT NULL,
  amount DOUBLE NULL,
  created_at datetime NULL,
  created_by BIGINT NULL,
  status BIT(1) NULL,
  operation VARCHAR(255) NULL,
  CONSTRAINT pk_tranx_pur_return_invoice_additional_charges_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_pur_return_invoice_additional_charges_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEADDITIONALCHARGESTBL_ON_TRANXPURINVOICE FOREIGN KEY (tranx_pur_invoice_id) REFERENCES tranx_purchase_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_additional_charges_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEADDITIONALCHARGESTB_ON_PURRETURNINVOICE FOREIGN KEY (pur_return_invoice_id) REFERENCES tranx_pur_return_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_additional_charges_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEADDITIONALCHARGEST_ON_ADDITIONALCHARGES FOREIGN KEY (additional_charges_id) REFERENCES ledger_master_tbl (id);

DROP TABLE if EXISTS tranx_pur_return_invoice_details_tbl;
CREATE TABLE tranx_pur_return_invoice_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   pur_return_invoice_id BIGINT NULL,
   product_id BIGINT NULL,
   tranx_pur_invoice_id BIGINT NULL,
   packaging_id BIGINT NULL,
   supplier_bill_no VARCHAR(255) NULL,
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
   created_at datetime NULL,
   created_by BIGINT NULL,
   CONSTRAINT pk_tranx_pur_return_invoice_details_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_pur_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_DETAILS_TBL_ON_PACKAGING FOREIGN KEY (packaging_id) REFERENCES packing_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_DETAILS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_pur_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_DETAILS_TBL_ON_PUR_RETURN_INVOICE FOREIGN KEY (pur_return_invoice_id) REFERENCES tranx_pur_return_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_details_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_DETAILS_TBL_ON_TRANX_PUR_INVOICE FOREIGN KEY (tranx_pur_invoice_id) REFERENCES tranx_purchase_invoice_tbl (id);

DROP TABLE if EXISTS tranx_pur_return_invoice_duties_taxes_tbl;
CREATE TABLE tranx_pur_return_invoice_duties_taxes_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  pur_return_invoice_id BIGINT NOT NULL,
  sundry_creditors_id BIGINT NOT NULL,
  duties_taxes_ledger_id BIGINT NOT NULL,
  tranx_pur_invoice_id BIGINT NULL,
  intra BIT(1) NULL,
  amount DOUBLE NULL,
  status BIT(1) NULL,
  created_at datetime NULL,
  CONSTRAINT pk_tranx_pur_return_invoice_duties_taxes_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_pur_return_invoice_duties_taxes_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEDUTIESTAXESTBL_ON_DUTIESTAXESLEDGER FOREIGN KEY (duties_taxes_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_duties_taxes_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEDUTIESTAXESTBL_ON_PURRETURNINVOICE FOREIGN KEY (pur_return_invoice_id) REFERENCES tranx_pur_return_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_duties_taxes_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEDUTIESTAXESTBL_ON_SUNDRYCREDITORS FOREIGN KEY (sundry_creditors_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_duties_taxes_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEDUTIESTAXESTBL_ON_TRANXPURINVOICE FOREIGN KEY (tranx_pur_invoice_id) REFERENCES tranx_purchase_invoice_tbl (id);

DROP TABLE if EXISTS tranx_pur_return_invoice_product_sr_no_tbl;
CREATE TABLE tranx_pur_return_invoice_product_sr_no_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
  product_id BIGINT NOT NULL,
  pur_return_invoice_id BIGINT NULL,
  pur_return_invoice_details_id BIGINT NULL,
  transaction_type_master_id BIGINT NULL,
  tranx_pur_invoice_id BIGINT NULL,
  branch_id BIGINT NULL,
  outlet_id BIGINT NULL,
  serial_no VARCHAR(255) NULL,
  pur_return_created_at datetime NULL,
  transaction_status VARCHAR(255) NULL,
  operations VARCHAR(255) NULL,
  created_by BIGINT NULL,
  created_at datetime NULL,
  status BIT(1) NULL,
  CONSTRAINT pk_tranx_pur_return_invoice_product_sr_no_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEPRODUCTSRNOTBL_ON_PURRETURNINVOICE FOREIGN KEY (pur_return_invoice_id) REFERENCES tranx_pur_return_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEPRODUCTSRNOTBL_ON_TRANSACTIONTYPEMASTER FOREIGN KEY (transaction_type_master_id) REFERENCES transaction_type_master_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEPRODUCTSRNOTBL_ON_TRANXPURINVOICE FOREIGN KEY (tranx_pur_invoice_id) REFERENCES tranx_purchase_invoice_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANXPURRETURNINVOICEPRODUCTSRNOT_ON_PURRETURNINVOICEDETAILS FOREIGN KEY (pur_return_invoice_details_id) REFERENCES tranx_pur_return_invoice_details_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_PRODUCT_SR_NO_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_PRODUCT_SR_NO_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_pur_return_invoice_product_sr_no_tbl ADD CONSTRAINT FK_TRANX_PUR_RETURN_INVOICE_PRODUCT_SR_NO_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

DROP TABLE if EXISTS tranx_purchase_return_details_units_tbl;
CREATE TABLE tranx_purchase_return_details_units_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   purchase_return_invoice_id BIGINT NULL,
   purchase_return_details_id BIGINT NULL,
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
   CONSTRAINT pk_tranx_purchase_return_details_units_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_purchase_return_details_units_tbl ADD CONSTRAINT FK_TRANXPURCHASERETURNDETAILSUNITSTBL_ON_PURCHASERETURNDETAILS FOREIGN KEY (purchase_return_details_id) REFERENCES tranx_pur_return_invoice_details_tbl (id);

ALTER TABLE tranx_purchase_return_details_units_tbl ADD CONSTRAINT FK_TRANXPURCHASERETURNDETAILSUNITSTBL_ON_PURCHASERETURNINVOICE FOREIGN KEY (purchase_return_invoice_id) REFERENCES tranx_pur_return_invoice_tbl (id);

ALTER TABLE tranx_purchase_return_details_units_tbl ADD CONSTRAINT FK_TRANX_PURCHASE_RETURN_DETAILS_UNITS_TBL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product_tbl (id);

ALTER TABLE tranx_purchase_return_details_units_tbl ADD CONSTRAINT FK_TRANX_PURCHASE_RETURN_DETAILS_UNITS_TBL_ON_UNIT FOREIGN KEY (unit_id) REFERENCES units_tbl (id);


SET FOREIGN_KEY_CHECKS = 1;