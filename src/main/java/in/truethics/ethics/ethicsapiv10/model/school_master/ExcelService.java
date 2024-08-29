package in.truethics.ethics.ethicsapiv10.model.school_master;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.LedgerMaster;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.LedgerMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.AcademicYearRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.BranchRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.DivisionRepository;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.StandardRepository;
import in.truethics.ethics.ethicsapiv10.repository.student_tranx.FeesTransactionDetailRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptMasterRepositoty;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.apache.commons.math3.util.Precision;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    @Autowired
    private FeesTransactionDetailRepository feesTransactionDetailRepository;

    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private TranxJournalMasterRepository tranxJournalMasterRepository;
    @Autowired
    private TranxJournalDetailsRepository tranxJournalDetailsRepository;

    @Autowired
    private TranxReceiptPerticularsDetailsRepository tranxReceiptPerticularsDetailsRepository;

    @Autowired
    private TranxReceiptMasterRepositoty tranxReceiptMasterRepositoty;
    @Autowired
    private LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private TranxSalesInvoiceRepository tranxSalesInvoiceRepository;
    @Autowired
    private TranxReceiptPerticularsRepository tranxReceiptPerticularsRepository;

    public InputStream exportDailyCollectionSheet(Map<String, String> jsonRequest) throws IOException {
        String[] HEADERs = {"STUDENT ID", "STUDENT NAME", "RECEIPTNO", "TRANSACTION DATE", "STANDARD", "PAID AMOUNT"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(" DailyCollection List");
            Row headerRow = sheet.createRow(0);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);

            }
            int rowIdx = 1;

            String JsonToStr = jsonRequest.get("studentList");
            JsonArray studentArray = new JsonParser().parse(JsonToStr).getAsJsonArray();
            for (int i = 0; i < studentArray.size(); i++) {
                JsonObject studObj = studentArray.get(i).getAsJsonObject();
                Row row = sheet.createRow(rowIdx++);
                try {
                    row.createCell(0).setCellValue(!studObj.get("studentId").isJsonNull() ? studObj.get("studentId").getAsString() : "");
                    row.createCell(1).setCellValue(studObj.get("studentName").getAsString());
                    row.createCell(2).setCellValue(studObj.get("receiptNo").getAsString());
                    row.createCell(3).setCellValue(studObj.get("transactionDate").getAsString());
                    row.createCell(4).setCellValue(studObj.get("standard").getAsString());
                    row.createCell(5).setCellValue(studObj.get("paidAmount").getAsDouble());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception" + e.getMessage());
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException e ===>" + e.getMessage());
            throw new RuntimeException("failed to import Data to Excel File" + e.getMessage());
        }
    }

    public InputStream exportOutstandingListSheet(Map<String, String> jsonRequest) throws IOException {
        String[] Mainheader = {"BRANCH", "STANDARD", "DIVISION", "ACADEMIC YEAR", "STUDENT TYPE"};
//        String[] HEADERs = {"BRANCH", "ACADEMIC YEAR", "STUDENT ID", "STUDENT NAME", "STANDARD ", "DIVISION", "OUTSTANDING", " PAID AMOUNT ", "MOBILE NO"};
        String[] HEADERs = {"STUDENT ID", "STUDENT NAME", "MOBILE NO", "TOTAL FEES", " PAID AMOUNT ", "OUTSTANDING"};


        Long standardId = Long.valueOf(jsonRequest.get("standardId"));
        String standardName = "";
        Standard standard = standardRepository.findByIdAndStatus(standardId, true);
        if (standard != null) {
            standardName = standard.getStandardName();
        }

        Long divisionId = Long.valueOf(jsonRequest.get("divisionId"));
        String divisionName = "";
        Division division = divisionRepository.findByIdAndStatus(divisionId, true);
        if (division != null) {
            divisionName = division.getDivisionName();
        }
        Long branchId = Long.valueOf(jsonRequest.get("branchId"));
        String branchName = "";
        Branch branch = branchRepository.findByIdAndStatus(branchId, true);
        if (branch != null) {
            branchName = branch.getBranchName();
        }
        Long academicYearId = Long.valueOf(jsonRequest.get("academicYearId"));
        String academicYear = "";
        AcademicYear academicYear1 = academicYearRepository.findByIdAndStatus(academicYearId, true);
        if (academicYear1 != null) {
            academicYear = academicYear1.getYear();
        }
        String studentType = (jsonRequest.get("studentType"));


        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Outstanding List");
            Row headerRow = sheet.createRow(6);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("BRANCH");
            row1.createCell(1).setCellValue(branchName);

            Row row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("STANDARD");
            row2.createCell(1).setCellValue(standardName);

            Row row3 = sheet.createRow(2);
            row3.createCell(0).setCellValue("DIVISION");
            row3.createCell(1).setCellValue(divisionName);

            Row row4 = sheet.createRow(3);
            row4.createCell(0).setCellValue("ACADEMIC YEAR");
            row4.createCell(1).setCellValue(academicYear);

            Row row5 = sheet.createRow(4);
            row5.createCell(0).setCellValue("STUDENT TYPE");
            row5.createCell(1).setCellValue(studentType);


            rowIdx = 7;


            String JsonTostr = jsonRequest.get("studentList");
            JsonArray studentArray = new JsonParser().parse(JsonTostr).getAsJsonArray();
            for (int i = 0; i < studentArray.size(); i++) {
                JsonObject studObj = studentArray.get(i).getAsJsonObject();
                Row row = sheet.createRow(rowIdx++);
                try {

                    row.createCell(0).setCellValue(!studObj.get("studentId").isJsonNull() ? studObj.get("studentId").getAsString() : "");
                    row.createCell(1).setCellValue(studObj.get("studentName").getAsString());
                    row.createCell(2).setCellValue(!studObj.get("mobileNo").isJsonNull() ? studObj.get("mobileNo").getAsString() : "");
                    row.createCell(3).setCellValue(studObj.get("totalFees").getAsString());

                    row.createCell(4).setCellValue(studObj.get("paidAmount").getAsString());
                    row.createCell(5).setCellValue(studObj.get("outstanding").getAsString());


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception" + e.getMessage());
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException e ===>" + e.getMessage());
            throw new RuntimeException("failed to import Data to Excel File" + e.getMessage());
        }
    }

    public InputStream exportFeesPaymentSheetForTally(Map<String, String> jsonRequest, HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        String[] oldHEADERs = {"S.NO.", "VOUCHER DATE", "VOUCHER NUMBER", "DEBIT LEDGER(CASH/BANK)", "DEBIT AMOUNT", "CREDIT LEDGER", "CREDIT AMOUNT", "NARRATION"};
        String[] HEADERs = {"VOUCHER NUMBER", "VOUCHER DATE", "PARTICULAR", "AMOUNT", "DEBIT/CREDIT", "COST CATEGORY", "COST CENTRE", "NARRATION FOR EACH ENTRY",
                "NARRATION", "VOUCHER TYPE"};


        String standardId = jsonRequest.get("standardId");
//        String standardId = "2";
        String academicYearId = jsonRequest.get("academicYearId");
        String studentType = jsonRequest.get("studentType");
        String fromDate = jsonRequest.get("fromDate");
        String toDate = jsonRequest.get("toDate");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import");
            Sheet hostelsheet = workbook.createSheet("hostelsheet");

            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            /*For Hostel Sheet*/
            Row hostelheaderRow = hostelsheet.createRow(0);
            CellStyle hostelheaderCellStyle = workbook.createCellStyle();
            hostelheaderCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            hostelheaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = hostelheaderRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            int hrowIdx = 1;
            String voucherType = "Journal";
//            List<TranxJournalMaster> journalMasterList = tranxJournalMasterRepository.findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNoIsNotNull(
//                    users.getOutlet().getId(), LocalDate.parse(fromDate), LocalDate.parse(toDate), true);
//            List<TranxJournalMaster> journalMasterList = tranxJournalMasterRepository.findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNo(
//                    users.getOutlet().getId(), LocalDate.parse(fromDate), LocalDate.parse(toDate), true, "SCS2223-0615-062");
            List<TranxJournalMaster> journalMasterList = tranxJournalMasterRepository.getDataByStandardAndAcademicAndStudentTypeAndDateBetween(
                    users.getOutlet().getId(), users.getBranch().getId(), standardId, academicYearId, studentType, LocalDate.parse(fromDate), LocalDate.parse(toDate), true);
            System.out.println("journalMasterList size" + journalMasterList.size());

//            List<TranxReceiptMaster> tranxReceiptMasters=tranxReceiptMasterRepositoty.getDataFromReceiptMasterByStandardAndAcademicAndStudentTypeAndDateBetween(
//                    users.getOutlet().getId(),users.getBranch().getId(),standardId,academicYearId,studentType,LocalDate.parse(fromDate),LocalDate.parse(toDate),true);


            for (TranxJournalMaster tranxJournalMaster : journalMasterList) {
                System.out.println("tranxJournalMaster.getId() " + tranxJournalMaster.getId());

                /****** For Central Branch ********/
                List<TranxJournalDetails> tranxJournalDetailsList = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                boolean isFirstCr = true;
                int drCount = 0;
                int isFirstDrRow = 0;
                double lastDrAmt = 0;
                int messfoundcount = 0;
                for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList) {

                    try {
                        System.out.println("tranxJournalDetails.getBranch() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tranxJournalDetails.getBranch().getBranchName());
                        System.out.println("tranxJournalDetails.getType() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tranxJournalDetails.getType());
                        System.out.println("rowIdx:" + rowIdx);
                        System.out.println("isFirstDrRow:" + isFirstDrRow);
                        System.out.println("isFirstCr:" + isFirstCr);


                        if (tranxJournalDetails.getType().equalsIgnoreCase("dr")) {
                            Row row = sheet.createRow(rowIdx++);
                            drCount++;
                            row.createCell(0).setCellValue(tranxJournalMaster.getJournalNo());
                            row.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
//                            if(tranxJournalDetails.getLedgerMaster().getSlugName().toLowerCase().contains("dp"))

                            if (tranxJournalDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("dp")) {
                                row.createCell(2).setCellValue("Dnyaneshwar Pratisthan");
                            } else {
                                row.createCell(2).setCellValue(tranxJournalDetails.getLedgerMaster().getLedgerName());
                            }
                            row.createCell(3).setCellValue(tranxJournalDetails.getPaidAmount());
                            row.createCell(4).setCellValue("D");
                            row.createCell(5).setCellValue("Primary Cost Category");
                            row.createCell(6).setCellValue("");
                            row.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                            row.createCell(8).setCellValue(tranxJournalMaster.getNarrations());
                            row.createCell(9).setCellValue(voucherType);
                            if (drCount > 1) {
                                System.out.println("rowIdx >>>>>>>>>>>>>>>>>>>>>" + rowIdx);
                                int prevIndex = rowIdx - 2;
                                System.out.println("prevIndex >>>>>>>>>>>>>>>>>>>>>" + prevIndex);
                                lastDrAmt = lastDrAmt - tranxJournalDetails.getPaidAmount();
                                Row prevRow = sheet.getRow(prevIndex);
                                prevRow.createCell(3).setCellValue(lastDrAmt);
                            }
                            lastDrAmt = tranxJournalDetails.getPaidAmount();


                        } else if (tranxJournalDetails.getType().equalsIgnoreCase("cr")) {
                            List<Object[]> headsList = feesTransactionDetailRepository.getPaidFeeHeadsListByReceipt(tranxJournalMaster.getFeeReceiptNo(), 1);
                            for (int k = 0; k < headsList.size(); k++) {
                                Object[] feeObject = headsList.get(k);
                                Row crRow = sheet.createRow(rowIdx++);

                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + Arrays.stream(feeObject).toArray());
                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + feeObject[7] + ">>>>>>>>>>>" + feeObject[5]);
                                String headName = feeObject[5].toString().toUpperCase();
                                System.out.println("headName <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + headName);

                                if (Integer.parseInt(feeObject[7].toString()) != 0)
                                    headName = feeObject[6].toString().toUpperCase();

                                crRow.createCell(0).setCellValue(tranxJournalMaster.getJournalNo());
                                crRow.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
                                crRow.createCell(2).setCellValue(headName);
                                crRow.createCell(3).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));
                                crRow.createCell(4).setCellValue("C");
                                crRow.createCell(5).setCellValue("Primary Cost Category");
                                crRow.createCell(6).setCellValue("");
                                crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                crRow.createCell(8).setCellValue(tranxJournalMaster.getNarrations());
                                crRow.createCell(9).setCellValue(voucherType);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Exception" + e.getMessage());
                    }
                }


                /********* For Hostel Branch **********/
                Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);
                List<TranxJournalDetails> tranxJournalDetailsList1 = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), hostelBranch.getId(), true);

                if (tranxJournalDetailsList1.size() > 0) {
                    System.out.println("For Hostel Branch _________________________" + hrowIdx + " isFirstDrRow" + isFirstDrRow + " isFirstCr" + isFirstCr);
                    isFirstCr = true;
                    isFirstDrRow = 0;
                    for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList1) {
                        try {
                            System.out.println("tranxJournalDetails.getType() >>>>>>>>>>>> " + tranxJournalDetails.getId() + ">>>>>>>>>>>>>>>>>>>>>" + tranxJournalDetails.getType());

                            System.out.println("hrowIdx:" + hrowIdx);
                            System.out.println("isFirstDrRow:" + isFirstDrRow);
                            System.out.println("isFirstCr:" + isFirstCr);

                            if (tranxJournalDetails.getType().equalsIgnoreCase("dr")) {
                                Row row = hostelsheet.createRow(hrowIdx++);
                                row.createCell(0).setCellValue(tranxJournalMaster.getJournalNo());
                                row.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
//                                row.createCell(2).setCellValue(tranxJournalDetails.getLedgerMaster().getLedgerName());

                                if (tranxJournalDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("dp")) {
                                    row.createCell(2).setCellValue("Dnyaneshwar Pratisthan");
                                } else {
                                    row.createCell(2).setCellValue(tranxJournalDetails.getLedgerMaster().getLedgerName());
                                }
                                row.createCell(3).setCellValue(tranxJournalDetails.getPaidAmount());
                                row.createCell(4).setCellValue("D");
                                row.createCell(5).setCellValue("Primary Cost Category");
                                row.createCell(6).setCellValue("");
                                row.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                row.createCell(8).setCellValue(tranxJournalMaster.getNarrations());
                                row.createCell(9).setCellValue(voucherType);
                            } else if (tranxJournalDetails.getType().equalsIgnoreCase("cr")) {

                                List<Object[]> headsList = feesTransactionDetailRepository.getPaidFeeHeadsListByReceipt(tranxJournalMaster.getFeeReceiptNo(), 0);
                                for (int k = 0; k < headsList.size(); k++) {
                                    Object[] feeObject = headsList.get(k);
                                    Row crRow = hostelsheet.createRow(hrowIdx++);

                                    System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + Arrays.stream(feeObject).toArray());
                                    System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + feeObject[7] + ">>>>>>>>>>>" + feeObject[5]);
                                    String headName = feeObject[5].toString().toUpperCase();
                                    System.out.println("headName <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + headName);

                                    if (Integer.parseInt(feeObject[7].toString()) != 0)
                                        headName = feeObject[6].toString().toUpperCase();

                                    crRow.createCell(0).setCellValue(tranxJournalMaster.getJournalNo());
                                    crRow.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
                                    crRow.createCell(2).setCellValue(headName);
                                    crRow.createCell(3).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));
                                    crRow.createCell(4).setCellValue("C");
                                    crRow.createCell(5).setCellValue("Primary Cost Category");
                                    crRow.createCell(6).setCellValue("");
                                    crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                    crRow.createCell(8).setCellValue(tranxJournalMaster.getNarrations());
                                    crRow.createCell(9).setCellValue(voucherType);
                                }
                                /*Row crRow = hostelsheet.createRow(hrowIdx++);
                                crRow.createCell(0).setCellValue(tranxJournalMaster.getJournalNo());
                                crRow.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
                                crRow.createCell(2).setCellValue("HOSTEL FEE");
                                crRow.createCell(3).setCellValue(tranxJournalDetails.getPaidAmount());
                                crRow.createCell(4).setCellValue("C");
                                crRow.createCell(5).setCellValue("Primary Cost Category");
                                crRow.createCell(6).setCellValue("");
                                crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                crRow.createCell(8).setCellValue(tranxJournalMaster.getNarrations());
                                crRow.createCell(9).setCellValue(voucherType);*/
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception" + e.getMessage());
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (
                IOException e) {
            e.printStackTrace();
            System.out.println("IOException e ===>" + e.getMessage());
            throw new RuntimeException("failed to import Data to Excel File" + e.getMessage());
        }

    }

    public InputStream oldexportFeesPaymentSheetForTally(Map<String, String> jsonRequest, HttpServletRequest request) {
        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        String[] oldHEADERs = {"S.NO.", "VOUCHER DATE", "VOUCHER NUMBER", "DEBIT LEDGER(CASH/BANK)", "DEBIT AMOUNT", "CREDIT LEDGER", "CREDIT AMOUNT", "NARRATION"};
        String[] HEADERs = {"VOUCHER NUMBER", "VOUCHER DATE", "PARTICULAR", "AMOUNT", "DEBIT/CREDIT", "COST CATEGORY", "COST CENTRE", "NARRATION FOR EACH ENTRY", "NARRATION"};


        String standardId = jsonRequest.get("standardId");
        String academicYearId = jsonRequest.get("academicYearId");
        String studentType = jsonRequest.get("studentType");
        String fromDate = jsonRequest.get("fromDate");
        String toDate = jsonRequest.get("toDate");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Outstanding List");
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            int i = 1;
            List<TranxJournalMaster> journalMasterList = tranxJournalMasterRepository.findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNoIsNotNull(
                    users.getOutlet().getId(), LocalDate.parse(fromDate), LocalDate.parse(toDate), true);
//            List<TranxJournalMaster> journalMasterList = tranxJournalMasterRepository.findByOutletIdAndTranscationDateBetweenAndStatusAndFeeReceiptNo(
//                    users.getOutlet().getId(), LocalDate.parse(fromDate), LocalDate.parse(toDate), true, "SCS2223-0615-062");
            System.out.println("journalMasterList size" + journalMasterList.size());

            for (TranxJournalMaster tranxJournalMaster : journalMasterList) {
                System.out.println("tranxJournalMaster.getId() " + tranxJournalMaster.getId());

                /****** For Central Branch ********/
                List<TranxJournalDetails> tranxJournalDetailsList = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                boolean isFirstCr = true;
                int isFirstDrRow = 0;
                for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList) {
                    Row row = sheet.createRow(rowIdx++);
                    try {
                        System.out.println("tranxJournalDetails.getType() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tranxJournalDetails.getType());
                        System.out.println("rowIdx:" + rowIdx);
                        System.out.println("isFirstDrRow:" + isFirstDrRow);
                        System.out.println("isFirstCr:" + isFirstCr);

                        if (tranxJournalDetails.getType().equalsIgnoreCase("dr")) {
                            row.createCell(0).setCellValue(i);
                            row.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
                            if (isFirstDrRow == 0)
                                isFirstDrRow = row.getRowNum();
                            row.createCell(2).setCellValue(tranxJournalMaster.getJournalNo());
                            row.createCell(3).setCellValue(tranxJournalDetails.getLedgerMaster().getLedgerName());
                            row.createCell(4).setCellValue(tranxJournalDetails.getPaidAmount());
                            row.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                        } else if (tranxJournalDetails.getType().equalsIgnoreCase("cr")) {
                            List<Object[]> headsList = feesTransactionDetailRepository.getPaidFeeHeadsListByReceipt(tranxJournalMaster.getFeeReceiptNo(),1);
                            for (int k = 0; k < headsList.size(); k++) {
                                Object[] feeObject = headsList.get(k);

                                Row crRow = sheet.getRow(isFirstDrRow);
                                if (crRow == null)
                                    crRow = sheet.createRow(isFirstDrRow);

                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + Arrays.stream(feeObject).toArray());
                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + feeObject[7] + ">>>>>>>>>>>" + feeObject[5]);
                                String headName = feeObject[5].toString().toUpperCase();
                                System.out.println("headName <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + headName);

                                if (Integer.parseInt(feeObject[7].toString()) != 0)
                                    headName = feeObject[6].toString().toUpperCase();
                                if (isFirstCr == true) {
                                    crRow.createCell(5).setCellValue(headName);
                                    crRow.createCell(6).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));
                                    crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                    isFirstCr = false;
                                } else {
                                    crRow.createCell(5).setCellValue(headName);
                                    crRow.createCell(6).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));
                                    crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                }
                                isFirstDrRow++;
                            }
                            if (isFirstCr != true)
                                rowIdx = isFirstDrRow;
                            i--;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Exception" + e.getMessage());
                    }

                    i++;
                }


                /********* For Hostel Branch **********/
                Branch hostelBranch = branchRepository.getHostelBranchRecord(users.getOutlet().getId(), true);
                List<TranxJournalDetails> tranxJournalDetailsList1 = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(
                        tranxJournalMaster.getId(), users.getOutlet().getId(), hostelBranch.getId(), true);

                if (tranxJournalDetailsList1.size() > 0) {
                    i--;
                    System.out.println("For Hostel Branch _________________________" + rowIdx + " isFirstDrRow" + isFirstDrRow + " isFirstCr" + isFirstCr);
                    isFirstCr = true;
                    isFirstDrRow = 0;
                    for (TranxJournalDetails tranxJournalDetails : tranxJournalDetailsList1) {
                        Row row = sheet.createRow(rowIdx++);
                        try {
                            System.out.println("tranxJournalDetails.getType() >>>>>>>>>>>> " + tranxJournalDetails.getId() + ">>>>>>>>>>>>>>>>>>>>>" + tranxJournalDetails.getType());

                            System.out.println("rowIdx:" + rowIdx);
                            System.out.println("isFirstDrRow:" + isFirstDrRow);
                            System.out.println("isFirstCr:" + isFirstCr);

                            if (tranxJournalDetails.getType().equalsIgnoreCase("dr")) {
                                if (isFirstDrRow == 0)
                                    isFirstDrRow = row.getRowNum();
                                row.createCell(0).setCellValue(i);
                                row.createCell(1).setCellValue(tranxJournalMaster.getTranscationDate().toString());
                                row.createCell(2).setCellValue(tranxJournalMaster.getJournalNo());
                                row.createCell(3).setCellValue(tranxJournalDetails.getLedgerMaster().getLedgerName());
                                row.createCell(4).setCellValue(tranxJournalDetails.getPaidAmount());
                                row.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                            } else if (tranxJournalDetails.getType().equalsIgnoreCase("cr")) {
                                Row crRow = sheet.getRow(isFirstDrRow);
                                if (isFirstCr == true) {
                                    crRow.createCell(5).setCellValue("HOSTEL FEE");
                                    crRow.createCell(6).setCellValue(tranxJournalDetails.getPaidAmount());
                                    crRow.createCell(7).setCellValue(tranxJournalMaster.getNarrations());
                                    isFirstCr = false;
                                }
                                isFirstDrRow++;
                            }
                            if (isFirstCr != true)
                                rowIdx = isFirstDrRow;
                            i--;

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception" + e.getMessage());
                        }
                        i++;
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (
                IOException e) {
            e.printStackTrace();
            System.out.println("IOException e ===>" + e.getMessage());
            throw new RuntimeException("failed to import Data to Excel File" + e.getMessage());
        }
    }

    public InputStream exportFeesPaymentSheetForTallyByReceipt(Map<String, String> jsonRequest, HttpServletRequest request) {

        Users users = jwtTokenUtil.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        String[] oldHEADERs = {"S.NO.", "VOUCHER DATE", "VOUCHER NUMBER", "DEBIT LEDGER(CASH/BANK)", "DEBIT AMOUNT", "CREDIT LEDGER", "CREDIT AMOUNT", "NARRATION"};
//        String[] HEADERs = {"VOUCHER NUMBER", "VOUCHER DATE", "PARTICULAR", "AMOUNT", "DEBIT/CREDIT", "COST CATEGORY", "COST CENTRE", "NARRATION FOR EACH ENTRY",
//                "NARRATION", "VOUCHER TYPE"};
        String[] HEADERs = {"VOUCHER NUMBER", "VOUCHER DATE", "LEDGER NAME", "BILL NO", "CASH/BANK NAME", "CHEQUE NO", "AMOUNT", "NARRATION",
               };


        String standardId = jsonRequest.get("standardId");
//        String standardId = "2";
        String academicYearId = jsonRequest.get("academicYearId");
        String studentType = jsonRequest.get("studentType");
        String fromDate = jsonRequest.get("fromDate");
        String toDate = jsonRequest.get("toDate");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Import");
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            String voucherType = "receipt";
//
            List<TranxReceiptMaster> tranxReceiptMasterslist = tranxReceiptMasterRepositoty.getDataFromReceiptMasterByStandardAndAcademicAndStudentTypeAndDateBetween(
                    users.getOutlet().getId(), users.getBranch().getId(), standardId, academicYearId, studentType, LocalDate.parse(fromDate), LocalDate.parse(toDate), true);

            for (TranxReceiptMaster tranxReceiptMasterr : tranxReceiptMasterslist) {
                System.out.println("tranxReceiptMasterr.getId() " + tranxReceiptMasterr.getId());

                /****** For Central Branch ********/


                List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetailsList = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusOrderByTypeDesc(
                        tranxReceiptMasterr.getId(), users.getOutlet().getId(), users.getBranch().getId(), true);

                boolean isFirstCr = true;
                int drCount = 0;
                int isFirstDrRow = 0;
                double lastDrAmt = 0;
                for (TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails : tranxReceiptPerticularsDetailsList) {

                    try {
                        System.out.println("tranxReceiptPerticularsDetails.getBranch() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tranxReceiptPerticularsDetails.getBranch().getBranchName());
                        System.out.println("tranxReceiptPerticularsDetails.getType() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tranxReceiptPerticularsDetails.getType());
                        System.out.println("rowIdx:" + rowIdx);
                        System.out.println("isFirstDrRow:" + isFirstDrRow);
                        System.out.println("isFirstCr:" + isFirstCr);

                        TranxSalesInvoice tranxSalesInvoice=tranxSalesInvoiceRepository.findBySundryDebtorsIdAndStatus(tranxReceiptPerticularsDetails.getLedgerMaster().getId(),true);

                        if (tranxReceiptPerticularsDetails.getType().equalsIgnoreCase("dr")) {
                            Row row = sheet.createRow(rowIdx++);
                            drCount++;
                            row.createCell(0).setCellValue(tranxReceiptMasterr.getReceiptNo());
                            row.createCell(1).setCellValue(tranxReceiptMasterr.getTranscationDate().toString());
//                            if(tranxJournalDetails.getLedgerMaster().getSlugName().toLowerCase().contains("dp"))
                            if (tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("cosmos bank")) {
                                row.createCell(2).setCellValue("COSMOS BANK");
                            } else {
                                row.createCell(2).setCellValue(tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName());
                            }
                            row.createCell(3).setCellValue(tranxSalesInvoice!=null?tranxSalesInvoice.getSalesInvoiceNo():"");
                            if (tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("cosmos bank")) {
                                row.createCell(4).setCellValue("COSMOS BANK");
                            } else if (tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("cash")) {
                                row.createCell(4).setCellValue("CASH");
                            }else
                            {
                                row.createCell(4).setCellValue("");
                            }

//                            row.createCell(4).setCellValue("D");
                            row.createCell(5).setCellValue("Cheque No not Available");
//                            row.createCell(6).setCellValue("");
                            row.createCell(6).setCellValue(tranxReceiptPerticularsDetails.getPaidAmt());

                            row.createCell(7).setCellValue(tranxReceiptMasterr.getNarrations());

//                            row.createCell(9).setCellValue(voucherType);
                            if (drCount > 1) {
                                System.out.println("rowIdx >>>>>>>>>>>>>>>>>>>>>" + rowIdx);
                                int prevIndex = rowIdx - 2;
                                System.out.println("prevIndex >>>>>>>>>>>>>>>>>>>>>" + prevIndex);
                                lastDrAmt = lastDrAmt - tranxReceiptPerticularsDetails.getPaidAmt();
                                Row prevRow = sheet.getRow(prevIndex);
//                                prevRow.createCell(3).setCellValue(lastDrAmt);
                            }
                            lastDrAmt = tranxReceiptPerticularsDetails.getPaidAmt();
                        } else if (tranxReceiptPerticularsDetails.getType().equalsIgnoreCase("cr")) {
                            List<Object[]> headsList = feesTransactionDetailRepository.getPaidFeeHeadsListByReceipt(tranxReceiptMasterr.getReceiptNo(), 1);
                            for (int k = 0; k < headsList.size(); k++) {
                                Object[] feeObject = headsList.get(k);

                                Row crRow = sheet.createRow(rowIdx++);

                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + Arrays.stream(feeObject).toArray());
                                System.out.println("feeObject[6].toString() <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + feeObject[7] + ">>>>>>>>>>>" + feeObject[5]);
                                String headName = feeObject[5].toString().toUpperCase();
                                System.out.println("headName <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + headName);

                                if (Integer.parseInt(feeObject[7].toString()) != 0)
                                    headName = feeObject[6].toString().toUpperCase();

                                crRow.createCell(0).setCellValue(tranxReceiptMasterr.getReceiptNo());
                                crRow.createCell(1).setCellValue(tranxReceiptMasterr.getTranscationDate().toString());
                                crRow.createCell(2).setCellValue(headName);
                                crRow.createCell(3).setCellValue(tranxSalesInvoice!=null?tranxSalesInvoice.getSalesInvoiceNo():"");
//                                if (tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("cosmos bank")) {
//                                    crRow.createCell(4).setCellValue("COSMOS BANK");
//                                } else if (tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName().toLowerCase().contains("cash")) {
//                                    crRow.createCell(4).setCellValue("CASH");
//                                }else
//                                {
//                                    crRow.createCell(4).setCellValue("NA");
//                                }

//                                crRow.createCell(3).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));
//                                crRow.createCell(4).setCellValue("C");
                                crRow.createCell(5).setCellValue("Cheque No not Available");
//                                crRow.createCell(6).setCellValue("");
                                crRow.createCell(6).setCellValue(Precision.round(Double.parseDouble(feeObject[1].toString()), 0));

                                crRow.createCell(7).setCellValue(tranxReceiptMasterr.getNarrations());
//                                crRow.createCell(8).setCellValue(tranxReceiptMasterr.getNarrations());
//                                crRow.createCell(9).setCellValue(voucherType);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Exception" + e.getMessage());
                    }
                }


            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (
                IOException e) {
            e.printStackTrace();
            System.out.println("IOException e ===>" + e.getMessage());
            throw new RuntimeException("failed to import Data to Excel File" + e.getMessage());
        }

    }
}
