ALTER TABLE `student_transport_tbl`
ADD COLUMN  start_date date NULL,
ADD COLUMN end_date date NULL,
ADD COLUMN total_month INT NULL,
ADD COLUMN outstanding DOUBLE NULL,
ADD COLUMN bus_fee DOUBLE NULL,
ADD COLUMN paid_month INT NULL,
ADD COLUMN paid_amount DOUBLE NULL,
ADD COLUMN is_bus_fee_pending BIT(1) NULL,
ADD COLUMN division_id BIGINT NULL;

ALTER TABLE student_transport_tbl ADD CONSTRAINT FK_STUDENT_TRANSPORT_TBL_ON_DIVISION FOREIGN KEY (division_id) REFERENCES division_tbl (id);