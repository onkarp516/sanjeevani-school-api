SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE if EXISTS bus_tbl;
CREATE TABLE bus_tbl (
  id BIGINT AUTO_INCREMENT NOT NULL,
   bus_stop_name VARCHAR(255) NULL,
   bus_fee DOUBLE NULL,
   created_by BIGINT NULL,
   updated_by BIGINT NULL,
   status BIT(1) NULL,
   created_at datetime NULL,
   updated_at datetime NULL,
   CONSTRAINT pk_bus_tbl PRIMARY KEY (id)
);

SET FOREIGN_KEY_CHECKS = 1;