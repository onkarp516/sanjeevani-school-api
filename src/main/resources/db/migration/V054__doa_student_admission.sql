ALTER TABLE `student_admission_tbl`
ADD COLUMN  date_of_admission date NULL,
ADD COLUMN new_admitted_standard_id BIGINT NULL;
ALTER TABLE student_admission_tbl ADD CONSTRAINT FK_STUDENT_ADMISSION_TBL_ON_NEW_ADMITTED_STANDARD FOREIGN KEY (new_admitted_standard_id) REFERENCES standard_tbl (id);
