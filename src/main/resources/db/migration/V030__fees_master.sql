SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS fees_master_tbl;
CREATE TABLE fees_master_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   academic_year_id BIGINT NULL,
   standard_id BIGINT NULL,
   division_id BIGINT NULL,
   student_type INT NULL,
   student_group INT NULL,
   minimum_amount DOUBLE NULL,
   no_of_installment INT NULL,
   amount DOUBLE NULL,
   amount_for_boy DOUBLE NULL,
   amount_for_girl DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_fees_master_tbl PRIMARY KEY (id)
);

ALTER TABLE fees_master_tbl ADD CONSTRAINT FK_FEES_MASTER_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE fees_master_tbl ADD CONSTRAINT FK_FEES_MASTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fees_master_tbl ADD CONSTRAINT FK_FEES_MASTER_TBL_ON_DIVISION FOREIGN KEY (division_id) REFERENCES division_tbl (id);

ALTER TABLE fees_master_tbl ADD CONSTRAINT FK_FEES_MASTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fees_master_tbl ADD CONSTRAINT FK_FEES_MASTER_TBL_ON_STANDARD FOREIGN KEY (standard_id) REFERENCES standard_tbl (id);

DROP TABLE if EXISTS fees_details_tbl;
CREATE TABLE fees_details_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   fees_master_id BIGINT NULL,
   fee_head_id BIGINT NULL,
   sub_fee_head_id BIGINT NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   priority DOUBLE NULL,
   amount DOUBLE NULL,
   amount_for_boy DOUBLE NULL,
   amount_for_girl DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_fees_details_tbl PRIMARY KEY (id)
);

ALTER TABLE fees_details_tbl ADD CONSTRAINT FK_FEES_DETAILS_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fees_details_tbl ADD CONSTRAINT FK_FEES_DETAILS_TBL_ON_FEES_MASTER FOREIGN KEY (fees_master_id) REFERENCES fees_master_tbl (id);

ALTER TABLE fees_details_tbl ADD CONSTRAINT FK_FEES_DETAILS_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE fees_details_tbl ADD CONSTRAINT FK_FEES_DETAILS_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fees_details_tbl ADD CONSTRAINT FK_FEES_DETAILS_TBL_ON_SUB_FEE_HEAD FOREIGN KEY (sub_fee_head_id) REFERENCES sub_fee_head_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;