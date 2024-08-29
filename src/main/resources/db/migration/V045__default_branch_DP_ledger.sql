SET FOREIGN_KEY_CHECKS = 0;
DROP PROCEDURE if EXISTS default_dp_salary_ledger;
DELIMITER &&
CREATE PROCEDURE default_dp_salary_ledger(IN branch_id BIGINT,IN outlet_id BIGINT,IN created_by BIGINT)
BEGIN
    INSERT INTO `ledger_master_tbl` ( `ledger_name`, `ledger_code`, `unique_code`, `mailing_name`, `opening_bal_type`,
    `opening_bal`, `address`, `pincode`, `email`, `mobile`, `taxable`, `gstin`, `state_code`, `registration_type`,
    `date_of_registration`, `pancard`, `bank_name`, `account_number`, `ifsc`, `bank_branch`, `tax_type`, `slug_name`,
    `created_by`, `created_at`, `updated_by`, `updated_at`, `status`, `under_prefix`, `is_deleted`, `is_default_ledger`,
    `is_private`, `credit_days`, `applicable_from`, `food_license_no`, `tds`, `tds_applicable_date`, `tcs`,
    `tcs_applicable_date`, `district`, `principle_id`, `principle_groups_id`, `foundation_id`, `branch_id`,
    `outlet_id`, `country_id`, `state_id`, `balancing_method_id`, `associates_groups_id`)
    VALUES
    ('DP A/C', NULL, 'SUDR', 'DP A/C', 'Dr', 0, NULL, 0, 'NA', NULL, b'0', NULL, NULL, NULL, NULL, NULL, 'NA', 'NA', 'NA', 'NA', 'NA',
     'sundry_debtors', created_by, now(), NULL,  now(), b'1', 'PG#1', b'0', b'0', b'0', 0, 'NA', 'NA',
     NULL, NULL, NULL, NULL, NULL, 1, 1, 1, branch_id, outlet_id, NULL, NULL, NULL, NULL),
   ('Salary A/C', NULL, 'INEX', 'NA', 'NA', 0, 'NA', 0, 'NA', 0, b'0', 'NA', 'NA',   NULL, NULL, 'NA', 'NA', 'NA', 'NA', 'NA', 'NA',
   'others', created_by, now(), NULL, now(), b'1', 'P#12', b'0', b'1', NULL, NULL, NULL, NULL, NULL,
    NULL, NULL, NULL, NULL, 12, NULL, 4, branch_id, outlet_id, NULL, NULL, NULL, NULL);
END &&
DELIMITER ;



SET FOREIGN_KEY_CHECKS = 1;