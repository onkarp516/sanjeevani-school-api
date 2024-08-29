SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS student_admission_tbl;
CREATE TABLE student_admission_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   student_id BIGINT NULL,
   academic_year_id BIGINT NULL,
   standard_id BIGINT NULL,
   division_id BIGINT NULL,
   student_type INT NULL,
   student_group INT NULL,
   is_hostel BIT(1) NULL,
   is_bus_concession BIT(1) NULL,
   bus_concession_amount BIGINT NULL,
   is_scholarship BIT(1) NULL,
   type_of_student VARCHAR(255) NULL,
   nts VARCHAR(255) NULL,
   concession_amount BIGINT NULL,
   created_at datetime NULL,
   created_by BIGINT NULL,
   updated_at datetime NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   CONSTRAINT pk_student_admission_tbl PRIMARY KEY (id)
);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_DIVISION FOREIGN KEY (division_id) REFERENCES division_tbl (id);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_STANDARD FOREIGN KEY (standard_id) REFERENCES standard_tbl (id);

ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;