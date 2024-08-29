CREATE TABLE ledger_transaction_postings_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   ledger_master_id BIGINT NULL,
   transaction_type_id BIGINT NULL,
   associate_groups_id BIGINT NULL,
   fiscal_year_id BIGINT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   amount DOUBLE NULL,
   transaction_date date NULL,
   transaction_id BIGINT NULL,
   invoice_no VARCHAR(255) NULL,
   ledger_type VARCHAR(255) NULL,
   tranx_type VARCHAR(255) NULL,
   operations VARCHAR(255) NULL,
   created_at datetime NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   updated_at datetime NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_ledger_transaction_postings_tbl PRIMARY KEY (id)
);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_ASSOCIATE_GROUPS FOREIGN KEY (associate_groups_id) REFERENCES associates_groups_tbl (id);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_LEDGER_MASTER FOREIGN KEY (ledger_master_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE ledger_transaction_postings_tbl ADD CONSTRAINT FK_LEDGER_TRANSACTION_POSTINGS_TBL_ON_TRANSACTION_TYPE FOREIGN KEY (transaction_type_id) REFERENCES transaction_type_master_tbl (id);