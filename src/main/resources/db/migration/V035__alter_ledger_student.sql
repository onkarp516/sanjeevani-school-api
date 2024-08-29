ALTER TABLE `ledger_master_tbl` ADD `student_id` BIGINT NULL DEFAULT NULL;

ALTER TABLE ledger_master_tbl ADD CONSTRAINT FK_LEDGER_MASTER_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);