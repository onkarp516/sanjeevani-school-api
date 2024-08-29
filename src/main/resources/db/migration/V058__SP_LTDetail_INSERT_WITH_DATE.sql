SET FOREIGN_KEY_CHECKS = 0;

DROP PROCEDURE if EXISTS SP_SUCESSIVE_ROW_CAL_TRANX_DATE;
DELIMITER &&
CREATE PROCEDURE SP_SUCESSIVE_ROW_CAL_TRANX_DATE(IN ledger_master_id BIGINT,IN INOPENING_BAL DOUBLE,IN IN_DETAIL_ID BIGINT,IN LED_TRANX_DATE DATE, IN DB_OPENING_BAL DOUBLE, IN tranx_type VARCHAR(255))
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE DBCREDIT DOUBLE DEFAULT 0.0;
    DECLARE DBDEBIT DOUBLE DEFAULT 0.0;
    DECLARE DBOPENING_BAL DOUBLE DEFAULT 0.0;
    DECLARE DBCLOSING_BAL DOUBLE DEFAULT 0.0;
    DECLARE DBID BIGINT DEFAULT 0;
    DECLARE DB_LEDGER_ID BIGINT DEFAULT 0;
    DECLARE CLOSING_BAL DOUBLE DEFAULT 0.0;
    DECLARE IN_OPENING_BAL DOUBLE DEFAULT 0.0;
    DECLARE IN_LEDGER_ID BIGINT;
    DECLARE CREDIT_AMT DOUBLE DEFAULT 0.0;
    DECLARE DEBIT_AMT DOUBLE DEFAULT 0.0;
    DECLARE CUR_CAL CURSOR FOR SELECT id,ledger_master_id,credit,debit,opening_bal,closing_bal FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id=ledger_master_id AND ledger_transaction_details_tbl.status=1 AND ledger_transaction_details_tbl.transaction_date>LED_TRANX_DATE ORDER BY ledger_transaction_details_tbl.transaction_date ASC, ledger_transaction_details_tbl.id ASC;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET IN_OPENING_BAL = INOPENING_BAL;
    SET IN_LEDGER_ID = ledger_master_id;

    OPEN CUR_CAL;

    READ_LOOP: LOOP
        FETCH CUR_CAL INTO DBID,DB_LEDGER_ID,DBCREDIT,DBDEBIT,DBOPENING_BAL,DBCLOSING_BAL;
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF DBCREDIT <> 0 THEN
            SET CLOSING_BAL = CAL_CR(IN_OPENING_BAL,DBCREDIT,0.0);

        ELSEIF DBDEBIT <> 0 THEN
            SET CLOSING_BAL = CAL_DR(IN_OPENING_BAL,0.0,DBDEBIT);

        ELSE
            SET CLOSING_BAL = CAL_CR(IN_OPENING_BAL,0.0,0.0);
        END IF;

        UPDATE ledger_transaction_details_tbl SET ledger_transaction_details_tbl.opening_bal=IN_OPENING_BAL,ledger_transaction_details_tbl.closing_bal=CLOSING_BAL, ledger_transaction_details_tbl.updated_at=NOW() WHERE ledger_transaction_details_tbl.id=DBID;
        SET IN_OPENING_BAL = CLOSING_BAL;

    END LOOP READ_LOOP;


    CLOSE CUR_CAL;

    IF IN_OPENING_BAL > 0 THEN
        SET CREDIT_AMT = IN_OPENING_BAL;
    ELSE
        SET DEBIT_AMT = IN_OPENING_BAL;
    END IF;

    UPDATE ledger_balance_summary_tbl SET ledger_balance_summary_tbl.balance=IN_OPENING_BAL,ledger_balance_summary_tbl.closing_bal=IN_OPENING_BAL,ledger_balance_summary_tbl.credit=CREDIT_AMT,ledger_balance_summary_tbl.debit=DEBIT_AMT, ledger_balance_summary_tbl.updated_at=NOW() WHERE ledger_balance_summary_tbl.ledger_master_id=IN_LEDGER_ID;


END &&

DELIMITER ;


DROP PROCEDURE if EXISTS LEDGER_TRANSACTION_DETAILS_POSTING_TRANX_DATE;
DELIMITER &&
CREATE PROCEDURE LEDGER_TRANSACTION_DETAILS_POSTING_TRANX_DATE(IN foundation_fk BIGINT,IN principle_fk BIGINT, IN principle_groups_fk BIGINT,IN associate_group_fk BIGINT,IN tranx_type_master_fk BIGINT, IN balancing_method_fk BIGINT,IN branch_fk BIGINT,IN outlet_fk BIGINT, IN payment_status VARCHAR(255),IN debit DOUBLE,IN credit DOUBLE,IN tranx_date DATE,IN payment_date DATE,IN tranx_id BIGINT,IN tranx_type VARCHAR(255),IN underprefix VARCHAR(255),IN financial_year VARCHAR(255),IN created_by BIGINT,IN ledger_master_fk BIGINT,IN voucher_no VARCHAR(255),IN status bit(1))
BEGIN
    DECLARE DBOPENING_BAL DOUBLE DEFAULT 0.0;
    DECLARE CHECKDATEEXIST INT;
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

    SELECT EXISTS (SELECT * FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id =ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id=outlet_fk and ledger_transaction_details_tbl.status=1 and ledger_transaction_details_tbl.transaction_date=tranx_date ORDER BY ledger_transaction_details_tbl.id DESC LIMIT 1) INTO CHECKDATEEXIST;

    IF CHECKDATEEXIST = 0 THEN
        SELECT EXISTS (SELECT * FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id =ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id=outlet_fk and ledger_transaction_details_tbl.status=1 AND ledger_transaction_details_tbl.transaction_date<tranx_date ORDER BY ledger_transaction_details_tbl.transaction_date DESC, ledger_transaction_details_tbl.id DESC limit 1) INTO CHECKEXIST;
        IF CHECKEXIST = 0 THEN
            SELECT ledger_master_tbl.opening_bal INTO DBOPENING_BAL FROM ledger_master_tbl WHERE ledger_master_tbl.id = ledger_master_fk;
        ELSE
            SELECT ledger_transaction_details_tbl.id, ledger_transaction_details_tbl.opening_bal,ledger_transaction_details_tbl.closing_bal,ledger_transaction_details_tbl.ledger_master_id,ledger_transaction_details_tbl.branch_id,ledger_transaction_details_tbl.outlet_id INTO DETAIL_ID,OPENING_BAL,DBOPENING_BAL,LEDGER_MST_ID,BRANCH_ID,OUTLET_ID FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id = ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id = outlet_fk and ledger_transaction_details_tbl.status=1 AND ledger_transaction_details_tbl.transaction_date<tranx_date ORDER BY ledger_transaction_details_tbl.transaction_date DESC, ledger_transaction_details_tbl.id DESC limit 1;
        END IF;
    ELSE
        SELECT ledger_transaction_details_tbl.id, ledger_transaction_details_tbl.opening_bal,ledger_transaction_details_tbl.closing_bal,ledger_transaction_details_tbl.ledger_master_id,ledger_transaction_details_tbl.branch_id,ledger_transaction_details_tbl.outlet_id INTO DETAIL_ID,OPENING_BAL,DBOPENING_BAL,LEDGER_MST_ID,BRANCH_ID,OUTLET_ID FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id = ledger_master_fk and (ledger_transaction_details_tbl.branch_id = branch_fk or ledger_transaction_details_tbl.branch_id IS NULL) and ledger_transaction_details_tbl.outlet_id = outlet_fk and ledger_transaction_details_tbl.status=1 and ledger_transaction_details_tbl.transaction_date=tranx_date ORDER BY ledger_transaction_details_tbl.id DESC LIMIT 1;
    END IF;

    IF credit <> 0 THEN
        SET CLOSING_BAL = CAL_CR(DBOPENING_BAL,credit,0.0);

    ELSEIF debit <> 0 THEN
        SET CLOSING_BAL = CAL_DR(DBOPENING_BAL,0.0,debit);

    ELSE
        SET CLOSING_BAL = CAL_CR(DBOPENING_BAL,0.0,0.0);
    END IF;

    INSERT INTO ledger_transaction_details_tbl(foundation_id, principle_id, principle_groups_id, associate_groups_id, transaction_type_id, balancing_method_id, branch_id, outlet_id, payment_status, debit, credit, opening_bal, closing_bal, transaction_date, payment_date, transaction_id, tranx_type, under_prefix, financial_year, ledger_master_id,status, created_at) VALUES (foundation_fk, principle_fk, principle_groups_fk, associate_group_fk, tranx_type_master_fk, balancing_method_fk,branch_fk, outlet_fk, payment_status, debit, credit, DBOPENING_BAL, CLOSING_BAL, tranx_date,payment_date, tranx_id,tranx_type, underprefix, financial_year, LEDGER_MST_ID,status, NOW());

    IF CLOSING_BAL > 0 THEN
        SET CREDIT_AMT = CLOSING_BAL;
    ELSE
        SET DEBIT_AMT = CLOSING_BAL;
    END IF;

    CALL SP_SUCESSIVE_ROW_CAL_TRANX_DATE(ledger_master_fk,CLOSING_BAL,DETAIL_ID, tranx_date, DBOPENING_BAL, tranx_type);

END &&
DELIMITER ;
SET FOREIGN_KEY_CHECKS = 1;
