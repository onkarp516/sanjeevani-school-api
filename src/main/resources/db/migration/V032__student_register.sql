SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS student_register_tbl;
CREATE TABLE student_register_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   branch_id BIGINT NULL,
   outlet_id BIGINT NULL,
   first_name VARCHAR(255) NULL,
   middle_name VARCHAR(255) NULL,
   last_name VARCHAR(255) NULL,
   gender VARCHAR(255) NULL,
   birth_date date NULL,
   age BIGINT NULL,
   birth_place VARCHAR(255) NULL,
   nationality VARCHAR(255) NULL,
   mother_tongue_id BIGINT NULL,
   religion_id BIGINT NULL,
   caste_id BIGINT NULL,
   sub_caste_id BIGINT NULL,
   caste_category_id BIGINT NULL,
   home_town VARCHAR(255) NULL,
   aadhar_no VARCHAR(255) NULL,
   saral_id VARCHAR(255) NULL,
   student_image VARCHAR(255) NULL,
   father_name VARCHAR(255) NULL,
   father_occupation VARCHAR(255) NULL,
   mother_name VARCHAR(255) NULL,
   mother_occupation VARCHAR(255) NULL,
   office_address VARCHAR(255) NULL,
   current_address VARCHAR(255) NULL,
   same_as_current_address BIT(1) NULL,
   permanent_address VARCHAR(255) NULL,
   phone_no_home BIGINT NULL,
   mobile_no BIGINT NULL,
   alt_mobile_no BIGINT NULL,
   email_id VARCHAR(255) NULL,
   father_image VARCHAR(255) NULL,
   mother_image VARCHAR(255) NULL,
   general_register_no VARCHAR(255) NULL,
   name_of_previous_school VARCHAR(255) NULL,
   std_in_previous_school VARCHAR(255) NULL,
   result VARCHAR(255) NULL,
   date_of_admission date NULL,
   academic_year_id BIGINT NULL,
   admitted_standard_id BIGINT NULL,
   division_id BIGINT NULL,
   current_standard_id BIGINT NULL,
   student_type INT NULL,
   student_group INT NULL,
   is_hostel BIT(1) NULL,
   is_bus_concession BIT(1) NULL,
   bus_concession_amount BIGINT NULL,
   is_scholarship BIT(1) NULL,
   type_of_student VARCHAR(255) NULL,
   nts VARCHAR(255) NULL,
   concession_amount BIGINT NULL,
   general_conduct VARCHAR(255) NULL,
   progress VARCHAR(255) NULL,
   dol date NULL,
   std_in_which_and_when VARCHAR(255) NULL,
   reason_of_leave_school VARCHAR(255) NULL,
   remarks VARCHAR(255) NULL,
   student_id VARCHAR(255) NULL,
   lc_no BIGINT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_student_register_tbl PRIMARY KEY (id)
);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_ADMITTED_STANDARD FOREIGN KEY (admitted_standard_id) REFERENCES standard_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_CASTE FOREIGN KEY (caste_id) REFERENCES caste_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_CASTE_CATEGORY FOREIGN KEY (caste_category_id) REFERENCES caste_category_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_CURRENT_STANDARD FOREIGN KEY (current_standard_id) REFERENCES standard_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_DIVISION FOREIGN KEY (division_id) REFERENCES division_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_MOTHER_TONGUE FOREIGN KEY (mother_tongue_id) REFERENCES mother_tongue_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_RELIGION FOREIGN KEY (religion_id) REFERENCES religion_tbl (id);

ALTER TABLE student_register_tbl ADD CONSTRAINT FK_STUDENT_REGISTER_TBL_ON_SUB_CASTE FOREIGN KEY (sub_caste_id) REFERENCES sub_caste_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;