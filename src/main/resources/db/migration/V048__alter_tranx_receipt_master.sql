ALTER TABLE `tranx_receipt_master_tbl`
ADD COLUMN under_branch_id BIGINT NULL,
ADD COLUMN student_ledger_id BIGINT NULL;

ALTER TABLE tranx_receipt_master_tbl ADD CONSTRAINT FK_TRANX_RECEIPT_MASTER_TBL_ON_STUDENT_LEDGER FOREIGN KEY (student_ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE tranx_receipt_master_tbl ADD CONSTRAINT FK_TRANX_RECEIPT_MASTER_TBL_ON_UNDER_BRANCH FOREIGN KEY (under_branch_id) REFERENCES branch_tbl (id);