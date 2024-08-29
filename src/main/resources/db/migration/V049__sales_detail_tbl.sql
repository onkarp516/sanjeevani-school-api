SET foreign_key_checks = 0;

DROP TABLE IF EXISTS tranx_sales_invoice_details_tbl;
CREATE TABLE tranx_sales_invoice_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   fiscal_year_id BIGINT NULL,
   sales_invoice_id BIGINT NULL,
   fee_head_id BIGINT NULL,
   amount DOUBLE NULL,
   status BIT(1) NULL,
   created_by BIGINT NULL,
   created_at datetime NULL,
   updated_by BIGINT NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_tranx_sales_invoice_details_tbl PRIMARY KEY (id)
);

ALTER TABLE tranx_sales_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE tranx_sales_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_DETAILS_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE tranx_sales_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_DETAILS_TBL_ON_FISCAL_YEAR FOREIGN KEY (fiscal_year_id) REFERENCES fiscal_year_tbl (id);

ALTER TABLE tranx_sales_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE tranx_sales_invoice_details_tbl ADD CONSTRAINT FK_TRANX_SALES_INVOICE_DETAILS_TBL_ON_SALES_INVOICE FOREIGN KEY (sales_invoice_id) REFERENCES tranx_sales_invoice_tbl (id);


SET foreign_key_checks = 1;