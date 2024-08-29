SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS fees_transaction_summary_tbl;
CREATE TABLE fees_transaction_summary_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   total_fees DOUBLE NULL,
   paid_amount DOUBLE NULL,
   balance DOUBLE NULL,
   student_type INT NULL,
   student_group INT NULL,
   concession_type INT NULL,
   is_manual BIT(1) NULL,
   concession_amount INT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   standard_id BIGINT NULL,
   academic_year_id BIGINT NULL,
   division_id BIGINT NULL,
   student_id BIGINT NULL,
   fees_master_id BIGINT NULL,
   CONSTRAINT pk_fees_transaction_summary_tbl PRIMARY KEY (id)
);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_DIVISION FOREIGN KEY (division_id) REFERENCES division_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_FEES_MASTER FOREIGN KEY (fees_master_id) REFERENCES fees_master_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_STANDARD FOREIGN KEY (standard_id) REFERENCES standard_tbl (id);

ALTER TABLE fees_transaction_summary_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_SUMMARY_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);


DROP TABLE if EXISTS fees_transaction_master_tbl;
CREATE TABLE fees_transaction_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   student_id BIGINT NULL,
   fees_transaction_summary_id BIGINT NULL,
   fee_head_id BIGINT NULL,
   sub_fee_head_id BIGINT NULL,
   fee_amount DOUBLE NULL,
   installment1 DOUBLE NULL,
   installment2 DOUBLE NULL,
   installment3 DOUBLE NULL,
   installment4 DOUBLE NULL,
   installment5 DOUBLE NULL,
   CONSTRAINT pk_fees_transaction_master_tbl PRIMARY KEY (id)
);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_FEES_TRANSACTION_SUMMARY FOREIGN KEY (fees_transaction_summary_id) REFERENCES fees_transaction_summary_tbl (id);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);

ALTER TABLE fees_transaction_master_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_MASTER_TBL_ON_SUB_FEE_HEAD FOREIGN KEY (sub_fee_head_id) REFERENCES sub_fee_head_tbl (id);


DROP TABLE if EXISTS fees_transaction_detail_tbl;
CREATE TABLE fees_transaction_detail_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   receipt_no VARCHAR(255) NULL,
   transaction_date date NULL,
   head_fee DOUBLE NULL,
   opening DOUBLE NULL,
   amount DOUBLE NULL,
   balance DOUBLE NULL,
   installment_no INT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   fees_transaction_summary_id BIGINT NULL,
   fee_head_id BIGINT NULL,
   sub_fee_head_id BIGINT NULL,
   CONSTRAINT pk_fees_transaction_detail_tbl PRIMARY KEY (id)
);

ALTER TABLE fees_transaction_detail_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_DETAIL_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fees_transaction_detail_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_DETAIL_TBL_ON_FEES_TRANSACTION_SUMMARY FOREIGN KEY (fees_transaction_summary_id) REFERENCES fees_transaction_summary_tbl (id);

ALTER TABLE fees_transaction_detail_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_DETAIL_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE fees_transaction_detail_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_DETAIL_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fees_transaction_detail_tbl ADD CONSTRAINT FK_FEES_TRANSACTION_DETAIL_TBL_ON_SUB_FEE_HEAD FOREIGN KEY (sub_fee_head_id) REFERENCES sub_fee_head_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;