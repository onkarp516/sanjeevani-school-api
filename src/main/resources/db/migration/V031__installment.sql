SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS installment_master_tbl;
CREATE TABLE installment_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   fees_master_id BIGINT NULL,
   student_type INT NULL,
   concession_type INT NULL,
   student_group INT NULL,
   installment_no INT NULL,
   fees_amount DOUBLE NULL,
   boys_amount DOUBLE NULL,
   girls_amount DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_installment_master_tbl PRIMARY KEY (id)
);

ALTER TABLE installment_master_tbl ADD CONSTRAINT FK_INSTALLMENT_MASTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE installment_master_tbl ADD CONSTRAINT FK_INSTALLMENT_MASTER_TBL_ON_FEES_MASTER FOREIGN KEY (fees_master_id) REFERENCES fees_master_tbl (id);

ALTER TABLE installment_master_tbl ADD CONSTRAINT FK_INSTALLMENT_MASTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);


DROP TABLE if EXISTS installment_details_tbl;
CREATE TABLE installment_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   installment_master_id BIGINT NULL,
   fee_head_id BIGINT NULL,
   sub_fee_head_id BIGINT NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   priority DOUBLE NULL,
   amount DOUBLE NULL,
   boys_amount DOUBLE NULL,
   girls_amount DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_installment_details_tbl PRIMARY KEY (id)
);

ALTER TABLE installment_details_tbl ADD CONSTRAINT FK_INSTALLMENT_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE installment_details_tbl ADD CONSTRAINT FK_INSTALLMENT_DETAILS_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE installment_details_tbl ADD CONSTRAINT FK_INSTALLMENT_DETAILS_TBL_ON_INSTALLMENT_MASTER FOREIGN KEY (installment_master_id) REFERENCES installment_master_tbl (id);

ALTER TABLE installment_details_tbl ADD CONSTRAINT FK_INSTALLMENT_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE installment_details_tbl ADD CONSTRAINT FK_INSTALLMENT_DETAILS_TBL_ON_SUB_FEE_HEAD FOREIGN KEY (sub_fee_head_id) REFERENCES sub_fee_head_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;