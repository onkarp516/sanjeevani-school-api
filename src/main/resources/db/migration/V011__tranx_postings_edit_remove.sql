SET FOREIGN_KEY_CHECKS = 0;
DROP PROCEDURE if EXISTS SP_SUCESSIVE_ROW_CAL_TRANX;
DELIMITER &&
CREATE PROCEDURE SP_SUCESSIVE_ROW_CAL_TRANX(IN ledger_master_id BIGINT,IN INOPENING_BAL DOUBLE,IN IN_DETAIL_ID BIGINT)
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
    DECLARE CUR_CAL CURSOR FOR SELECT id,ledger_master_id,credit,debit,opening_bal,closing_bal FROM ethics_new_db_v_1_0.ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.ledger_master_id=ledger_master_id AND ledger_transaction_details_tbl.ID>IN_DETAIL_ID;
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

        UPDATE ledger_transaction_details_tbl SET ledger_transaction_details_tbl.opening_bal=IN_OPENING_BAL,ledger_transaction_details_tbl.closing_bal=CLOSING_BAL WHERE ledger_transaction_details_tbl.id=DBID;
        SET IN_OPENING_BAL = CLOSING_BAL;

    END LOOP READ_LOOP;


    CLOSE CUR_CAL;

    IF IN_OPENING_BAL > 0 THEN
        SET CREDIT_AMT = IN_OPENING_BAL;
    ELSE
        SET DEBIT_AMT = IN_OPENING_BAL;
    END IF;

    UPDATE ledger_balance_summary_tbl SET ledger_balance_summary_tbl.balance=IN_OPENING_BAL,ledger_balance_summary_tbl.closing_bal=IN_OPENING_BAL,ledger_balance_summary_tbl.credit=CREDIT_AMT,ledger_balance_summary_tbl.debit=DEBIT_AMT WHERE ledger_balance_summary_tbl.ledger_master_id=IN_LEDGER_ID;


END &&

DELIMITER ;
DROP PROCEDURE if EXISTS LEDGER_POSTING_TRANX_REMOVE;

DELIMITER &&
CREATE PROCEDURE LEDGER_POSTING_TRANX_REMOVE(IN ledger_master_id BIGINT,IN transaction_id BIGINT,IN transaction_type_id BIGINT)
BEGIN
    SET @DBCREDIT=0.0;
    SET @DBDEBIT=0.0;
    SET @DBOPENING_BAL=0.0;
    SET @DBCLOSING_BAL=0.0;
    SET @DETAIL_ID=0.0;
    SET @LEDGER_ID=ledger_master_id;
    SET @OUTSTANDING_AMT= 0.0;
    SET @DEBIT_AMT = 0.0;
    SET @CREDIT_AMT =0.0;

    SELECT ledger_transaction_details_tbl.id,ledger_transaction_details_tbl.credit, ledger_transaction_details_tbl.debit,ledger_transaction_details_tbl.opening_bal,ledger_transaction_details_tbl.closing_bal INTO @DETAIL_ID , @DBCREDIT , @DBDEBIT,@DBOPENING_BAL,@DBCLOSING_BAL FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.transaction_id= transaction_id AND ledger_transaction_details_tbl.ledger_master_id=@LEDGER_ID AND ledger_transaction_details_tbl.transaction_type_id=transaction_type_id ORDER BY ledger_transaction_details_tbl.id DESC LIMIT 1 ;

    UPDATE ledger_transaction_details_tbl SET ledger_transaction_details_tbl.status=0 WHERE ledger_transaction_details_tbl.id = @DETAIL_ID;

    CALL SP_SUCESSIVE_ROW_CAL_TRANX(@LEDGER_ID,@DBOPENING_BAL,@DETAIL_ID);

END &&

DELIMITER ;
DROP PROCEDURE if EXISTS LEDGER_POSTING_EDIT_TRANX;

DELIMITER &&

CREATE PROCEDURE LEDGER_POSTING_EDIT_TRANX(IN ledger_master_id BIGINT,IN transaction_id BIGINT,IN transaction_type_id BIGINT,IN TRANX_TYPE VARCHAR(20),IN AMT DOUBLE)
BEGIN
    SET @DBCREDIT=0.0;
    SET @DBDEBIT=0.0;
    SET @DBOPENING_BAL=0.0;
    SET @DBCLOSING_BAL=0.0;
    SET @DETAIL_ID=0.0;
    SET @LEDGER_ID=ledger_master_id;
    SET @OUTSTANDING_AMT= 0.0;
    SET @DEBIT_AMT = 0.0;
    SET @CREDIT_AMT =0.0;
    SET @CLOSING_BAL =0.0;

    SELECT id,credit, debit,opening_bal,closing_bal INTO @DETAIL_ID , @DBCREDIT , @DBDEBIT,@DBOPENING_BAL,@DBCLOSING_BAL FROM ledger_transaction_details_tbl WHERE ledger_transaction_details_tbl.transaction_id= transaction_id AND ledger_transaction_details_tbl.ledger_master_id=@LEDGER_ID AND ledger_transaction_details_tbl.transaction_type_id=transaction_type_id LIMIT 1;

    IF TRANX_TYPE = "CR" THEN
        SET @CLOSING_BAL = CAL_CR(@DBOPENING_BAL,AMT,0.0);
        UPDATE ledger_transaction_details_tbl SET ledger_transaction_details_tbl.credit=AMT, ledger_transaction_details_tbl.closing_bal = @CLOSING_BAL WHERE ledger_transaction_details_tbl.id = @DETAIL_ID;

    ELSE
        SET @CLOSING_BAL = CAL_DR(@DBOPENING_BAL,0.0,AMT);
        UPDATE ledger_transaction_details_tbl SET ledger_transaction_details_tbl.debit=AMT, ledger_transaction_details_tbl.closing_bal = @CLOSING_BAL WHERE ledger_transaction_details_tbl.id = @DETAIL_ID;

    END IF;

    CALL SP_SUCESSIVE_ROW_CAL_TRANX(@LEDGER_ID,@CLOSING_BAL,@DETAIL_ID);

END &&

DELIMITER ;
SET FOREIGN_KEY_CHECKS = 1;