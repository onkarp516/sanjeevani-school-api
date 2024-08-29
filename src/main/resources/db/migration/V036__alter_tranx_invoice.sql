ALTER TABLE tranx_sales_invoice_tbl
ADD COLUMN fees_account_ledger_id BIGINT NULL;
ALTER TABLE tranx_sales_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_TBL_ON_FEES_ACCOUNT_LEDGER FOREIGN KEY (fees_account_ledger_id) REFERENCES ledger_master_tbl (id);

