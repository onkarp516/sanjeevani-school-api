SET FOREIGN_KEY_CHECKS = 0;
DROP PROCEDURE if EXISTS rightoff_ledger;
DELIMITER &&
CREATE PROCEDURE rightoff_ledger(IN branch_id BIGINT,IN outlet_id BIGINT,IN created_by BIGINT)
BEGIN
    INSERT INTO `ledger_master_tbl` (`ledger_name`, `foundation_id`, `principle_groups_id`, `principle_id`, `branch_id`, `outlet_id`, `account_number`, `address`, `bank_branch`, `bank_name`, `created_by`, `created_at`, `date_of_registration`, `email`, `gstin`, `ifsc`, `mailing_name`, `mobile`, `opening_bal`,  `opening_bal_type`, `pancard`, `pincode`, `registration_type`, `state_code`, `status`, `tax_type`, `taxable`,  `updated_by`, `updated_at`, `country_id`, `state_id`, `slug_name`, `unique_code`,`under_prefix`, `is_deleted`,`is_default_ledger`) VALUES
    ('Right Off',4,NULL,12,branch_id,outlet_id,'NA','NA','NA','NA',created_by,now(),NULL,'NA','NA','NA','NA',0,0,'NA','NA',0,NULL,'NA',b'1','NA',b'0',0,now(),NULL,NULL,'others','INEX','P#12',b'0',b'1');


END &&
DELIMITER ;