SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS academic_year_tbl;
CREATE TABLE academic_year_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   year VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_academic_year_tbl PRIMARY KEY (id)
);

ALTER TABLE academic_year_tbl ADD CONSTRAINT FK_ACADEMIC_YEAR_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE academic_year_tbl ADD CONSTRAINT FK_ACADEMIC_YEAR_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS standard_tbl;
CREATE TABLE standard_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   standard_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   CONSTRAINT pk_standard_tbl PRIMARY KEY (id)
);

ALTER TABLE standard_tbl ADD CONSTRAINT FK_STANDARD_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE standard_tbl ADD CONSTRAINT FK_STANDARD_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

DROP TABLE if EXISTS division_tbl;
CREATE TABLE division_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   division_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   outlet_id BIGINT NULL,
   branch_id BIGINT NULL,
   standard_id BIGINT NULL,
   CONSTRAINT pk_division_tbl PRIMARY KEY (id)
);

ALTER TABLE division_tbl ADD CONSTRAINT FK_DIVISION_TBL_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branch_tbl (id);

ALTER TABLE division_tbl ADD CONSTRAINT FK_DIVISION_TBL_ON_OUTLET FOREIGN KEY (outlet_id) REFERENCES outlet_tbl (id);

ALTER TABLE division_tbl ADD CONSTRAINT FK_DIVISION_TBL_ON_STANDARD FOREIGN KEY (standard_id) REFERENCES standard_tbl (id);

SET FOREIGN_KEY_CHECKS = 1;