SET FOREIGN_KEY_CHECKS = 0;
DROP FUNCTION if EXISTS CAL_CR;
DELIMITER $$
CREATE FUNCTION CAL_CR(OPENING_BAL DOUBLE,CREDIT DOUBLE,DEBIT DOUBLE)
RETURNS DOUBLE
DETERMINISTIC
BEGIN
    RETURN OPENING_BAL-DEBIT+CREDIT;
END $$
DELIMITER ;

DROP FUNCTION if EXISTS CAL_DR;
DELIMITER $$
CREATE FUNCTION CAL_DR(OPENING_BAL DOUBLE,CREDIT DOUBLE,DEBIT DOUBLE)
RETURNS DOUBLE
DETERMINISTIC
BEGIN
    RETURN OPENING_BAL+DEBIT-CREDIT;
END $$
DELIMITER ;

DROP PROCEDURE if EXISTS LEDGER_TRANSACTION_DETAILS_POSTINGS_INSERT_CADMIN;
DELIMITER &&
CREATE PROCEDURE LEDGER_TRANSACTION_DETAILS_POSTINGS_INSERT_CADMIN(IN foundation_fk BIGINT,IN principle_fk BIGINT, IN principle_groups_fk BIGINT,IN associate_group_fk BIGINT,IN tranx_type_master_fk BIGINT, IN balancing_method_fk BIGINT,IN branch_fk BIGINT,IN outlet_fk BIGINT, IN payment_status VARCHAR(255),IN debit DOUBLE,IN credit DOUBLE,IN transaction_date DATE,IN payment_date DATE,IN tranx_id BIGINT,IN tranx_type VARCHAR(255),IN underprefix VARCHAR(255),IN financial_year VARCHAR(255),IN created_by BIGINT,IN ledger_master_fk BIGINT,IN voucher_no VARCHAR(255))
BEGIN
    DECLARE DBOPENING_BAL DOUBLE DEFAULT 0.0;
    DECLARE CHECKEXIST INT;
    DECLARE CHECKBALEXIST INT;
    DECLARE OPENING_BAL DOUBLE DEFAULT 0.0;
    DECLARE LEDGER_MST_ID BIGINT;
    DECLARE INSTITUTE_ID BIGINT;
    DECLARE BRANCH_ID BIGINT;
    DECLARE OUTLET_ID BIGINT;
    DECLARE DETAIL_ID BIGINT;
    DECLARE CREDIT_AMT DOUBLE DEFAULT 0.0;
    DECLARE DEBIT_AMT DOUBLE DEFAULT 0.0;
    DECLARE CLOSING_BAL DOUBLE DEFAULT 0.0;

    SET LEDGER_MST_ID = ledger_master_fk;

SELECT EXISTS (SELECT * FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id =ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id=outlet_fk ORDER BY ledger_transaction_details_tbl.id DESC LIMIT 1) INTO CHECKEXIST;

    IF CHECKEXIST = 0 THEN
        SELECT ledger_master_tbl.opening_bal INTO DBOPENING_BAL FROM ledger_master_tbl WHERE ledger_master_tbl.id = ledger_master_fk;
    ELSE
        SELECT ledger_transaction_details_tbl.id, ledger_transaction_details_tbl.opening_bal,ledger_transaction_details_tbl.closing_bal,ledger_transaction_details_tbl.ledger_master_id,ledger_transaction_details_tbl.branch_id,ledger_transaction_details_tbl.outlet_id INTO DETAIL_ID,OPENING_BAL,DBOPENING_BAL,LEDGER_MST_ID,BRANCH_ID,OUTLET_ID FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id = ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id = outlet_fk ORDER BY ledger_transaction_details_tbl.id DESC LIMIT 1;
    END IF;

    IF credit <> 0 THEN
        SET CLOSING_BAL = CAL_CR(DBOPENING_BAL,credit,0.0);

    ELSEIF debit <> 0 THEN
        SET CLOSING_BAL = CAL_DR(DBOPENING_BAL,0.0,debit);

    ELSE
        SET CLOSING_BAL = CAL_CR(DBOPENING_BAL,0.0,0.0);
    END IF;

    INSERT INTO ledger_transaction_details_tbl(foundation_id, principle_id, principle_groups_id, associate_groups_id, transaction_type_id, balancing_method_id, branch_id, outlet_id, payment_status, debit, credit, opening_bal, closing_bal, transaction_date, payment_date, transaction_id, tranx_type, under_prefix, financial_year, ledger_master_id,status) VALUES (foundation_fk, principle_fk, principle_groups_fk, associate_group_fk, tranx_type_master_fk, balancing_method_fk,branch_fk, outlet_fk, payment_status, debit, credit, DBOPENING_BAL, CLOSING_BAL, transaction_date,payment_date, tranx_id,tranx_type, underprefix, financial_year, LEDGER_MST_ID,1);

    IF CLOSING_BAL > 0 THEN
        SET CREDIT_AMT = CLOSING_BAL;
    ELSE
        SET DEBIT_AMT = CLOSING_BAL;
    END IF;

SELECT EXISTS (SELECT * FROM ledger_balance_summary_tbl WHERE ledger_balance_summary_tbl.ledger_master_id=LEDGER_MST_ID) INTO CHECKBALEXIST;
    IF CHECKBALEXIST <> 0 THEN
  UPDATE ledger_balance_summary_tbl SET ledger_balance_summary_tbl.balance=CLOSING_BAL,ledger_balance_summary_tbl.closing_bal=CLOSING_BAL,ledger_balance_summary_tbl.credit=CREDIT_AMT,ledger_balance_summary_tbl.debit=DEBIT_AMT WHERE ledger_balance_summary_tbl.ledger_master_id=LEDGER_MST_ID;
  ELSE
  INSERT INTO `ledger_balance_summary_tbl`(`foundation_id`, `principle_id`, `principle_groups_id`, `ledger_master_id`, `debit`, `credit`, `opening_bal`, `closing_bal`, `balance`, `under_prefix`, `created_at`, `updated_at`, `status`, `branch_id`, `outlet_id`, `associate_groups_id`) VALUES (foundation_fk, principle_fk, principle_groups_fk,LEDGER_MST_ID,DEBIT_AMT, CREDIT_AMT,DBOPENING_BAL,CLOSING_BAL,CLOSING_BAL,underprefix,NOW(),NOW(),1,branch_fk,outlet_fk,associate_group_fk);
  END IF;

END &&
DELIMITER ;
SET FOREIGN_KEY_CHECKS = 1;

