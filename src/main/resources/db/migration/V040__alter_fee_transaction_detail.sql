ALTER TABLE `fees_transaction_detail_tbl` ADD `paid_amount` DOUBLE NOT NULL DEFAULT '0' AFTER `balance`;
ALTER TABLE `fees_transaction_detail_tbl` ADD `concession_amount` DOUBLE NOT NULL DEFAULT '0' AFTER `paid_amount`;

ALTER TABLE `fees_transaction_detail_tbl` ADD `special_concession_amount` DOUBLE NOT NULL DEFAULT '0' AFTER `concession_amount`,
    ADD `payment_mode` INT NULL DEFAULT NULL AFTER `special_concession_amount`,
    ADD `payment_no` VARCHAR(255) NULL DEFAULT NULL AFTER `payment_mode`;