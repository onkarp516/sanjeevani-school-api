SET foreign_key_checks = 0;

DROP TABLE IF EXISTS student_bus_transaction_tbl;
CREATE TABLE student_bus_transaction_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   paid_from date NULL,
   paid_to date NULL,
   total_month INT NULL,
   month_name VARCHAR(255) NULL,
   bus_fee DOUBLE NULL,
   paid_month INT NULL,
   paid_amount DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   student_id BIGINT NULL,
   academic_year_id BIGINT NULL,
   student_transport_id BIGINT NULL,
   CONSTRAINT pk_student_bus_transaction_tbl PRIMARY KEY (id)
);

ALTER TABLE student_bus_transaction_tbl ADD CONSTRAINT FK_STUDENT_BUS_TRANSACTION_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE student_bus_transaction_tbl ADD CONSTRAINT FK_STUDENT_BUS_TRANSACTION_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE student_bus_transaction_tbl ADD CONSTRAINT FK_STUDENT_BUS_TRANSACTION_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE student_bus_transaction_tbl ADD CONSTRAINT FK_STUDENT_BUS_TRANSACTION_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);

ALTER TABLE student_bus_transaction_tbl ADD CONSTRAINT FK_STUDENT_BUS_TRANSACTION_TBL_ON_STUDENT_TRANSPORT FOREIGN KEY (student_transport_id) REFERENCES student_transport_tbl (id);

SET foreign_key_checks = 1;