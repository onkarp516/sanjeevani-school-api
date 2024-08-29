ALTER TABLE `student_admission_tbl`
ADD COLUMN reason VARCHAR(255) NULL,
ADD COLUMN paid_amount DOUBLE NULL,
ADD COLUMN outstanding DOUBLE NULL,
ADD COLUMN refund_amount DOUBLE NULL;
