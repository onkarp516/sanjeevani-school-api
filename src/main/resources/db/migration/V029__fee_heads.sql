SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS fee_head_tbl;
CREATE TABLE fee_head_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   fee_head_name VARCHAR(255) NULL,
   student_type INT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   under_branch_id BIGINT NULL,
   ledger_id BIGINT NULL,
   CONSTRAINT pk_fee_head_tbl PRIMARY KEY (id)
);

ALTER TABLE fee_head_tbl ADD CONSTRAINT FK_FEE_HEAD_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE fee_head_tbl ADD CONSTRAINT FK_FEE_HEAD_TBL_ON_LEDGER FOREIGN KEY (ledger_id) REFERENCES ledger_master_tbl (id);

ALTER TABLE fee_head_tbl ADD CONSTRAINT FK_FEE_HEAD_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE fee_head_tbl ADD CONSTRAINT FK_FEE_HEAD_TBL_ON_UNDER_BRANCH FOREIGN KEY (under_branch_id) REFERENCES branch_tbl (id);

DROP TABLE if EXISTS sub_fee_head_tbl;
CREATE TABLE sub_fee_head_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   student_type INT NULL,
   sub_fee_head_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   fee_head_id BIGINT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   CONSTRAINT pk_sub_fee_head_tbl PRIMARY KEY (id)
);

ALTER TABLE sub_fee_head_tbl ADD CONSTRAINT FK_SUB_FEE_HEAD_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE sub_fee_head_tbl ADD CONSTRAINT FK_SUB_FEE_HEAD_TBL_ON_FEE_HEAD FOREIGN KEY (fee_head_id) REFERENCES fee_head_tbl (id);

ALTER TABLE sub_fee_head_tbl ADD CONSTRAINT FK_SUB_FEE_HEAD_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;