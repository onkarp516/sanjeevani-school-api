ALTER TABLE `tranx_sales_invoice_tbl`
ADD COLUMN  fees_asso_account_ledger_id BIGINT NULL;
ALTER TABLE tranx_sales_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_TBL_ON_FEES_ASSO_ACCOUNT_LEDGER FOREIGN KEY
(fees_asso_account_ledger_id) REFERENCES associates_groups_tbl (id);


ALTER TABLE `tranx_sales_return_invoice_tbl`
ADD COLUMN  fees_asso_account_ledger_id BIGINT NULL;
ALTER TABLE tranx_sales_return_invoice_tbl ADD CONSTRAINT FK_TRANX_SALES_RETURN_INVOICE_TBL_ON_FEES_ASSO_ACCOUNT_LEDGER FOREIGN KEY
(fees_asso_account_ledger_id) REFERENCES associates_groups_tbl (id);
