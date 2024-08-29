SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS student_transport_tbl;
CREATE TABLE student_transport_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   student_type INT NULL,
   bus_id BIGINT NULL,
   academic_year_id BIGINT NULL,
   standard_id BIGINT NULL,
   student_id BIGINT NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_student_transport_tbl PRIMARY KEY (id)
);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_ACADEMIC_YEAR FOREIGN KEY (academic_year_id) REFERENCES academic_year_tbl (id);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_BUS FOREIGN KEY (bus_id) REFERENCES bus_tbl (id);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_STANDARD FOREIGN KEY (standard_id) REFERENCES standard_tbl (id);

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student_register_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;