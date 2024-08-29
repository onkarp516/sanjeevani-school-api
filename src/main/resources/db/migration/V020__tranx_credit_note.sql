SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS tranx_credit_note_new_reference_tbl;
CREATE TABLE tranx_credit_note_new_reference_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   sundry_debtors_id BIGINT NULL,
   tranx_sales_invoice_id BIGINT NULL,
   tranx_sales_challan_id BIGINT NULL,
   tranx_sales_return_invoice_id BIGINT NULL,
   transaction_status_id BIGINT NULL,
   fiscal_year_id BIGINT NULL,
   srno BIGINT NULL,
   creditnote_new_reference_no VARCHAR(255) NULL,
   round_off DOUBLE NULL,
   total_base_amount DOUBLE NULL,
   total_amount DOUBLE NULL,
   taxable_amount DOUBLE NULL,
   totalgst DOUBLE NULL,
   sales_discount_amount DOUBLE NULL,
   sales_discount_per DOUBLE NULL,
   total_sales_discount_amt DOUBLE NULL,
   additional_charges_total DOUBLE NULL,
   financial_year VARCHAR(255) NULL,
   source VARCHAR(255) NULL,
   transcation_date date NULL,
   narrations VARCHAR(255) NULL,
   created_at datetime NULL,
   created_by BIGINT NULL,
   status BIT(1) NULL,
   adjustment_status VARCHAR(255) NULL,
   balance DOUBLE NULL,
   CONSTRAINT pk_tranx_credit_note_new_reference_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANXCREDITNOTENEWREFERENCETBL_ON_TRANXSALESRETURNINVOICE FOREIGN KEY (tranx_sales_return_invoice_id) REFERENCES tranx_sales_return_invoice_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_SUNDRY_DEBTORS FOREIGN KEY (sundry_debtors_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_TRANSACTION_STATUS FOREIGN KEY (transaction_status_id) REFERENCES transaction_status_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_TRANX_SALES_CHALLAN FOREIGN KEY (tranx_sales_challan_id) REFERENCES tranx_sales_challan_tbl (id);

ALTER TABLE tranx_credit_note_new_reference_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_NEW_REFERENCE_TBL_ON_TRANX_SALES_INVOICE FOREIGN KEY (tranx_sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);

DROP TABLE if EXISTS tranx_credit_note_details_tbl;
CREATE TABLE tranx_credit_note_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   sundry_debtors_id BIGINT NULL,
   transaction_status_id BIGINT NULL,
   ledger_id BIGINT NULL,
   tranx_creditnote_master_id BIGINT NULL,
   total_amount DOUBLE NULL,
   balance DOUBLE NULL,
   adjusted_id BIGINT NULL,
   adjusted_source VARCHAR(255) NULL,
   type VARCHAR(255) NULL,
   ledger_type VARCHAR(255) NULL,
   created_at datetime NULL,
   created_by BIGINT NULL,
   status BIT(1) NULL,
   adjustment_status VARCHAR(255) NULL,
   operations VARCHAR(255) NULL,
   paid_amt DOUBLE NULL,
   source VARCHAR(255) NULL,
   CONSTRAINT pk_tranx_credit_note_details_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_LEDGER FOREIGN KEY (ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_SUNDRY_DEBTORS FOREIGN KEY (sundry_debtors_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_TRANSACTION_STATUS FOREIGN KEY (transaction_status_id) REFERENCES transaction_status_tbl (id);

ALTER TABLE tranx_credit_note_details_tbl ADD CONSTRAINT FK_TRANX_CREDIT_NOTE_DETAILS_TBL_ON_TRANX_CREDITNOTE_MASTER FOREIGN KEY (tranx_creditnote_master_id) REFERENCES tranx_credit_note_new_reference_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;