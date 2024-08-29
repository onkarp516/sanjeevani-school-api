SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE if EXISTS mother_tongue_tbl;
CREATE TABLE mother_tongue_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(255) NULL,
   status BIT(1) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_mother_tongue_tbl PRIMARY KEY (id)
);

DROP TABLE if EXISTS religion_tbl;
CREATE TABLE religion_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   religion_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_religion_tbl PRIMARY KEY (id)
);

DROP TABLE if EXISTS caste_tbl;
CREATE TABLE caste_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   caste_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   religion_id BIGINT NULL,
   CONSTRAINT pk_caste_tbl PRIMARY KEY (id)
);

ALTER TABLE caste_tbl ADD CONSTRAINT FK_CASTE_TBL_ON_RELIGION FOREIGN KEY (religion_id) REFERENCES religion_tbl (id);

DROP TABLE if EXISTS sub_caste_tbl;
CREATE TABLE sub_caste_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   religion_id BIGINT NULL,
   caste_id BIGINT NULL,
   sub_caste_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_sub_caste_tbl PRIMARY KEY (id)
);

ALTER TABLE sub_caste_tbl ADD CONSTRAINT FK_SUB_CASTE_TBL_ON_CASTE FOREIGN KEY (caste_id) REFERENCES caste_tbl (id);

ALTER TABLE sub_caste_tbl ADD CONSTRAINT FK_SUB_CASTE_TBL_ON_RELIGION FOREIGN KEY (religion_id) REFERENCES religion_tbl (id);

DROP TABLE if EXISTS caste_category_tbl;
CREATE TABLE caste_category_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   religion_id BIGINT NULL,
   caste_id BIGINT NULL,
   sub_caste_id BIGINT NULL,
   category_name VARCHAR(255) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_caste_category_tbl PRIMARY KEY (id)
);

ALTER TABLE caste_category_tbl ADD CONSTRAINT FK_CASTE_CATEGORY_TBL_ON_CASTE FOREIGN KEY (caste_id) REFERENCES caste_tbl (id);

ALTER TABLE caste_category_tbl ADD CONSTRAINT FK_CASTE_CATEGORY_TBL_ON_RELIGION FOREIGN KEY (religion_id) REFERENCES religion_tbl (id);

ALTER TABLE caste_category_tbl ADD CONSTRAINT FK_CASTE_CATEGORY_TBL_ON_SUB_CASTE FOREIGN KEY (sub_caste_id) REFERENCES sub_caste_tbl (id);


DROP TABLE if EXISTS document_tbl;
CREATE TABLE document_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(255) NULL,
   is_required BIT(1) NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_document_tbl PRIMARY KEY (id)
);

SET FOREIGN_KEY_CHECKS = 1;