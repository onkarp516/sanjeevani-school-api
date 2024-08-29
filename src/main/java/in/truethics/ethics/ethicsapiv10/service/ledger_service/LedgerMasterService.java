package in.truethics.ethics.ethicsapiv10.service.ledger_service;

import com.github.underscore.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.common.GenerateFiscalYear;
import in.truethics.ethics.ethicsapiv10.common.GenerateSlugs;
import in.truethics.ethics.ethicsapiv10.common.LedgerCommonPostings;
import in.truethics.ethics.ethicsapiv10.dto.ClientDetails;
import in.truethics.ethics.ethicsapiv10.dto.ClientsListDTO;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerBalanceSummary;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionDetails;
import in.truethics.ethics.ethicsapiv10.model.ledgers_details.LedgerTransactionPostings;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoice;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesReturnInvoice;
import in.truethics.ethics.ethicsapiv10.model.school_master.StudentAdmission;
import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.contra.TranxContraMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.journal.TranxJournalMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.payment.TranxPaymentPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoice;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurReturnInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptMaster;
import in.truethics.ethics.ethicsapiv10.model.tranx.receipt.TranxReceiptPerticularsDetails;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.ledgerdetails_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository.TranxContraDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.contra_repository.TranxContraMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.journal_repository.TranxJournalMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository.TranxPaymentMasterRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.payment_repository.TranxPaymentPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurReturnDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.pur_repository.TranxPurReturnsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptMasterRepositoty;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.receipt_repository.TranxReceiptPerticularsDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceDetailsRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesInvoiceRepository;
import in.truethics.ethics.ethicsapiv10.repository.tranxs_repository.sales_repository.TranxSalesReturnRepository;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LedgerMasterService {

    private static final Logger ledgerLogger = LoggerFactory.getLogger(LedgerMasterService.class);
    @Autowired
    JwtTokenUtil jwtRequestFilter;
    @Autowired
    PrincipleRepository principleRepository;
    @Autowired
    PrincipleGroupsRepository principleGroupsRepository;
    @Autowired
    TranxSalesInvoiceRepository tranxSalesInvoiceRepository;

    @Autowired
    StudentAdmissionRepository studentAdmissionRepository;
    @Autowired
    LedgerTransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    TranxJournalDetailsRepository tranxJournalDetailsRepository;
    @Autowired
    BalancingMethodRepository balancingMethodRepository;
    @Autowired
    TranxPurInvoiceRepository tranxPurInvoiceRepository;
    @Autowired
    TranxPurReturnsRepository tranxPurReturnsRepository;
    @Autowired
    TranxSalesReturnRepository tranxSalesReturnRepository;

    @Autowired
    LedgerMasterRepository ledgerMasterRepository;
    @Autowired
    private TranxSalesInvoiceDetailsRepository getSalesInvoiceDetailsRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private LedgerMasterRepository repository;
    @Autowired
    private TranxJournalMasterRepository tranxJournalMasterRepository;
    @Autowired
    private TranxContraMasterRepository tranxContraMasterRepository;
    @Autowired
    private TranxPaymentMasterRepository tranxPaymentMasterRepository;
    @Autowired
    private TranxReceiptMasterRepositoty tranxReceiptMasterRepositoty;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private LedgerMasterRepository ledgerRepository;
    @Autowired
    private LedgerBalanceSummaryRepository balanceSummaryRepository;
    @Autowired
    private LedgerTransactionDetailsRepository transactionDetailsRepositorys;

    @Autowired
    private TranxContraDetailsRepository tranxContraDetailsRepository;
    @Autowired
    private TranxPaymentPerticularsDetailsRepository tranxPaymentPerticularsDetailsRepository;
    @Autowired
    private TranxReceiptPerticularsDetailsRepository tranxReceiptPerticularsDetailsRepository;

    @Autowired
    private LedgerGstDetailsRepository ledgerGstDetailsRepository;
    @Autowired
    private LedgerShippingDetailsRepository ledgerShippingDetailsRepository;
    @Autowired
    private LedgerDeptDetailsRepository ledgerDeptDetailsRepository;
    @Autowired
    private LedgerBillingDetailsRepository ledgerBillingDetailsRepository;
    @Autowired
    private LedgerTransactionPostingsRepository ledgerTransactionPostingsRepository;
    /*
     @Autowired
     private LedgerTransactionDetailsRepository transactionDetailsRepository;*/
    @Autowired
    private GenerateSlugs generateSlugs;
    /* @Autowired
     private PaymentTransactionDetailsRepository paymentTransactionDetailsRepo;
     @Autowired
     private TransactionTypeMasterDetailsRepository tranxDetailsRepository;*/
    @Autowired
    private GenerateFiscalYear generateFiscalYear;
    @Autowired
    private AssociateGroupsRepository associateGroupsRepository;
    @Autowired
    private GstTypeMasterRepository gstMasterRepository;
    @Autowired
    private TranxPurReturnDetailsRepository tranxPurReturnDetailsRepository;
    @Autowired
    private TranxSalesInvoiceDetailsRepository salesInvoiceDetailsRepository;
    @Autowired
    private LedgerCommonPostings ledgerCommonPostings;


    public Object createLedgerMaster(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        LedgerMaster lMaster = null;
        ResponseMessage responseMessage = new ResponseMessage();
        if (users.getBranch() == null)
            lMaster = ledgerRepository.findByOutletIdAndLedgerNameIgnoreCaseAndStatus(
                    users.getOutlet().getId(), request.getParameter("ledger_name"), true);
        if (users.getBranch() != null)
            lMaster = ledgerRepository.findByOutletIdAndBranchIdAndLedgerNameIgnoreCaseAndStatus(
                    users.getOutlet().getId(), users.getBranch().getId(), request.getParameter("ledger_name"), true);
        if (lMaster == null) {
            LedgerMaster ledgerMaster = new LedgerMaster();
            LedgerMaster mLedger = ledgerCreateUpdate(request, "create", ledgerMaster);
            if (mLedger != null) {
//                insertIntoLedgerBalanceSummary(mLedger, "create");
                responseMessage.setMessage("Ledger created successfully");
                responseMessage.setResponseStatus(HttpStatus.OK.value());

            } else {
                responseMessage.setMessage("Error in ledger creation");
                responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
            }
        } else {
            System.out.println("Already Ledger created");
            responseMessage.setMessage("Already Ledger created..");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        }
        return responseMessage;
    }

    public LedgerMaster ledgerCreateUpdate(HttpServletRequest request, String key,
                                           LedgerMaster ledgerMaster) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        Map<String, String[]> paramMap = request.getParameterMap();
        PrincipleGroups groups = null;
        Principles principles = null;
        Foundations foundations = null;
        AssociateGroups associateGroups = null;
        State mState = null;
        Country mCountry = null;
        LedgerMaster mLedger = null;

        if (paramMap.containsKey("branchId")) {
            Branch mBranch = branchRepository.findByIdAndStatus(Long.parseLong(request.getParameter("branchId")), true);
            ledgerMaster.setBranch(mBranch);
        }
        if (paramMap.containsKey("principle_id")) {
            principles = principleRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("principle_id")), true);
            foundations = principles.getFoundations();
        }
        if (paramMap.containsKey("principle_group_id")) {
            groups = principleGroupsRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("principle_group_id")), true);
        }
        if (paramMap.containsKey("associates_id")) {
            associateGroups = associateGroupsRepository.findByIdAndStatus(Long.parseLong(
                    request.getParameter("associates_id")), true);
        }
        ledgerMaster.setAssociateGroups(associateGroups);
        if (groups != null) {
            ledgerMaster.setPrincipleGroups(groups);
            ledgerMaster.setPrinciples(principles);
            ledgerMaster.setUniqueCode(groups.getUniqueCode());
        } else {
            ledgerMaster.setPrinciples(principles);
            ledgerMaster.setUniqueCode(principles.getUniqueCode());
        }
        if (foundations != null)
            ledgerMaster.setFoundations(foundations);
        if (paramMap.containsKey("is_private"))
            ledgerMaster.setIsPrivate(Boolean.parseBoolean(request.getParameter("is_private")));
        if (key.equalsIgnoreCase("create")) {
            ledgerMaster.setIsDeleted(true); //isDelete : true means , we can delete this ledger
            // if it is not involved into any tranxs
            ledgerMaster.setStatus(true);
            ledgerMaster.setIsDefaultLedger(false);
        }
        if (users.getBranch() != null)
            ledgerMaster.setBranch(users.getBranch());
        ledgerMaster.setOutlet(users.getOutlet());
        ledgerMaster.setCreatedBy(users.getId());
        ledgerMaster.setLedgerName(request.getParameter("ledger_name"));
        if (paramMap.containsKey("slug"))
            ledgerMaster.setSlugName(request.getParameter("slug"));
        else
            ledgerMaster.setSlugName("NA");
        if (paramMap.containsKey("under_prefix"))
            ledgerMaster.setUnderPrefix(request.getParameter("under_prefix"));
        else
            ledgerMaster.setUnderPrefix("NA");
        if (request.getParameter("slug").equalsIgnoreCase("sundry_creditors") ||
                request.getParameter("slug").equalsIgnoreCase("sundry_debtors")) {
            ledgerMaster.setTaxType("NA");
            if (paramMap.containsKey("supplier_code")) {
                ledgerMaster.setLedgerCode(request.getParameter("supplier_code"));
            }

            if (paramMap.containsKey("mailing_name")) {
                ledgerMaster.setMailingName(request.getParameter("mailing_name"));
            } else {
                ledgerMaster.setMailingName("NA");
            }
            if (paramMap.containsKey("opening_bal_type")) {
                ledgerMaster.setOpeningBalType(request.getParameter("opening_bal_type"));
            } else {
                ledgerMaster.setOpeningBalType("NA");
            }
            if (paramMap.containsKey("balancing_method")) {
                BalancingMethod balancingMethod = balancingMethodRepository.findByIdAndStatus(
                        Long.parseLong(request.getParameter("balancing_method")),
                        true);
                ledgerMaster.setBalancingMethod(balancingMethod);
            }
            if (paramMap.containsKey("address")) {
                ledgerMaster.setAddress(request.getParameter("address"));
            } else {
                ledgerMaster.setAddress("NA");
            }
            if (paramMap.containsKey("state")) {
                Optional<State> state = stateRepository.findById(Long.parseLong(
                        request.getParameter("state")));
                mState = state.get();
                ledgerMaster.setState(mState);
            }
            if (paramMap.containsKey("country")) {
                Optional<Country> country = countryRepository.findById(Long.parseLong(
                        request.getParameter("country")));
                mCountry = country.get();
                ledgerMaster.setCountry(mCountry);
            }
            if (paramMap.containsKey("pincode")) {
                ledgerMaster.setPincode(Long.parseLong(request.getParameter("pincode")));
            } else {
                ledgerMaster.setPincode(0L);
            }
            if (paramMap.containsKey("email")) {
                ledgerMaster.setEmail(request.getParameter("email"));
            } else {
                ledgerMaster.setEmail("NA");
            }
            if (paramMap.containsKey("mobile_no")) {
                ledgerMaster.setMobile(Long.parseLong(request.getParameter("mobile_no")));
            } else {
                ledgerMaster.setMobile(0L);
            }
            ledgerMaster.setTaxable(Boolean.parseBoolean(request.getParameter("taxable")));
            if (Boolean.parseBoolean(request.getParameter("taxable"))) {
                GstTypeMaster gstTypeMaster = gstMasterRepository.findById(
                        Long.parseLong(request.getParameter("registration_type"))).get();
                ledgerMaster.setRegistrationType(gstTypeMaster.getId());
            } else {
                GstTypeMaster gstTypeMaster = gstMasterRepository.findById(3L).get();
                ledgerMaster.setRegistrationType(gstTypeMaster.getId());
                if (mState != null)
                    ledgerMaster.setStateCode(mState.getStateCode());
                if (paramMap.containsKey("pan_no")) {
                    ledgerMaster.setPancard(request.getParameter("pan_no"));
                } else {
                    ledgerMaster.setPancard("NA");
                }
            }
            if (request.getParameter("slug").equalsIgnoreCase("sundry_creditors")) {
                if (paramMap.containsKey("bank_name"))
                    ledgerMaster.setBankName(request.getParameter("bank_name"));
                else
                    ledgerMaster.setBankName("NA");
                if (paramMap.containsKey("account_no"))
                    ledgerMaster.setAccountNumber(request.getParameter("account_no"));
                else
                    ledgerMaster.setAccountNumber("NA");
                if (paramMap.containsKey("ifsc_code"))
                    ledgerMaster.setIfsc(request.getParameter("ifsc_code"));
                else
                    ledgerMaster.setIfsc("NA");
                if (paramMap.containsKey("bank_branch"))
                    ledgerMaster.setBankBranch(request.getParameter("bank_branch"));
                else
                    ledgerMaster.setBankBranch("NA");
                if (paramMap.containsKey("opening_bal")) {
                    if (request.getParameter("opening_bal_type").equalsIgnoreCase("Cr")) {
                        ledgerMaster.setOpeningBal(Double.parseDouble(request.getParameter("opening_bal")));
                    } else {
                        Double openingBal = Double.parseDouble(request.getParameter("opening_bal"));
                        openingBal *= -1;
                        ledgerMaster.setOpeningBal(openingBal);
                    }
                }
            } else {
                ledgerMaster.setBankName("NA");
                ledgerMaster.setAccountNumber("NA");
                ledgerMaster.setIfsc("NA");
                ledgerMaster.setBankBranch("NA");
                if (paramMap.containsKey("opening_bal")) {
                    if (request.getParameter("opening_bal_type").equalsIgnoreCase("Dr")) {
                        Double openingBal = Double.parseDouble(request.getParameter("opening_bal"));
                        openingBal *= -1;
                        ledgerMaster.setOpeningBal(openingBal);
                    } else {
                        ledgerMaster.setOpeningBal(Double.parseDouble(request.getParameter("opening_bal")));
                    }
                }
            }
            /* pune demo visit changes */
            if (paramMap.containsKey("credit_days")) {
                ledgerMaster.setCreditDays(Integer.parseInt(request.getParameter("credit_days")));
                ledgerMaster.setApplicableFrom(request.getParameter("applicable_from"));
            } else {
                ledgerMaster.setCreditDays(0);
                ledgerMaster.setApplicableFrom("NA");
            }
            if (paramMap.containsKey("fssai"))
                ledgerMaster.setFoodLicenseNo(request.getParameter("fssai"));
            else
                ledgerMaster.setFoodLicenseNo("NA");

            if (paramMap.containsKey("tds"))
                ledgerMaster.setTds(Boolean.parseBoolean(request.getParameter("tds")));
            if (paramMap.containsKey("tds_applicable_date"))
                ledgerMaster.setTdsApplicableDate(LocalDate.parse(request.getParameter("tds_applicable_date")));
            if (paramMap.containsKey("tcs"))
                ledgerMaster.setTcs(Boolean.parseBoolean(request.getParameter("tcs")));
            if (paramMap.containsKey("tcs_applicable_date"))
                ledgerMaster.setTcsApplicableDate(LocalDate.parse(request.getParameter("tcs_applicable_date")));
        } else if (request.getParameter("slug").equalsIgnoreCase("bank_account")) {
            if (paramMap.containsKey("opening_bal_type")) {
                ledgerMaster.setOpeningBalType(request.getParameter("opening_bal_type"));
            }
            if (paramMap.containsKey("opening_bal")) {
                if (request.getParameter("opening_bal_type").equalsIgnoreCase("Dr")) {
                    Double openingBal = Double.parseDouble(request.getParameter("opening_bal"));
                    openingBal *= -1;
                    ledgerMaster.setOpeningBal(openingBal);
                } else {
                    ledgerMaster.setOpeningBal(Double.parseDouble(request.getParameter("opening_bal")));
                }
            }
            if (paramMap.containsKey("state")) {
                Optional<State> state = stateRepository.findById(Long.parseLong(
                        request.getParameter("state")));
                mState = state.get();
                ledgerMaster.setState(mState);
            }
            if (paramMap.containsKey("country")) {
                Optional<Country> country = countryRepository.findById(Long.parseLong(
                        request.getParameter("country")));
                mCountry = country.get();
                ledgerMaster.setCountry(mCountry);
            }
            if (paramMap.containsKey("pincode")) {
                ledgerMaster.setPincode(Long.parseLong(request.getParameter("pincode")));
            } else {
                ledgerMaster.setPincode(0L);
            }
            if (paramMap.containsKey("email")) {
                ledgerMaster.setEmail(request.getParameter("email"));
            } else {
                ledgerMaster.setEmail("NA");
            }
            if (paramMap.containsKey("address")) {
                ledgerMaster.setAddress(request.getParameter("address"));
            } else {
                ledgerMaster.setAddress("NA");
            }
            if (paramMap.containsKey("mobile_no")) {
                ledgerMaster.setMobile(Long.parseLong(request.getParameter("mobile_no")));
            } else {
                ledgerMaster.setMobile(0L);
            }
            ledgerMaster.setTaxable(Boolean.parseBoolean(request.getParameter("taxable")));
            if (Boolean.parseBoolean(request.getParameter("taxable"))) {
                ledgerMaster.setGstin(request.getParameter("gstin"));
                if (paramMap.containsKey("gstType")) {
                    GstTypeMaster gstTypeMaster = gstMasterRepository.findById(
                            Long.parseLong(request.getParameter("gstType"))).get();
                    ledgerMaster.setRegistrationType(gstTypeMaster.getId());
                }
            } else {
                GstTypeMaster gstTypeMaster = gstMasterRepository.findById(3L).get();
                ledgerMaster.setRegistrationType(gstTypeMaster.getId());
            }
            if (paramMap.containsKey("pan_no")) {
                ledgerMaster.setPancard(request.getParameter("pan_no"));
            } else {
                ledgerMaster.setPancard("NA");
            }
            if (paramMap.containsKey("bank_name"))
                ledgerMaster.setBankName(request.getParameter("bank_name"));
            else
                ledgerMaster.setBankName("NA");
            if (paramMap.containsKey("account_no"))
                ledgerMaster.setAccountNumber(request.getParameter("account_no"));
            else
                ledgerMaster.setAccountNumber("NA");
            if (paramMap.containsKey("ifsc_code"))
                ledgerMaster.setIfsc(request.getParameter("ifsc_code"));
            else
                ledgerMaster.setIfsc("NA");
            if (paramMap.containsKey("bank_branch"))
                ledgerMaster.setBankBranch(request.getParameter("bank_branch"));
            else
                ledgerMaster.setBankBranch("NA");
            ledgerMaster.setMailingName("NA");
            ledgerMaster.setStateCode("NA");
        } else if (request.getParameter("slug").equalsIgnoreCase("duties_taxes")) {
            ledgerMaster.setTaxType(request.getParameter("tax_type"));
            ledgerMaster.setMailingName("NA");
            ledgerMaster.setOpeningBalType("NA");
            ledgerMaster.setOpeningBal(0.0);
            ledgerMaster.setAddress("NA");
            ledgerMaster.setPincode(0L);
            ledgerMaster.setEmail("NA");
            ledgerMaster.setMobile(0L);
            ledgerMaster.setTaxable(false);
            ledgerMaster.setGstin("NA");
            ledgerMaster.setRegistrationType(0L);
            ledgerMaster.setPancard("NA");
            ledgerMaster.setBankName("NA");
            ledgerMaster.setAccountNumber("NA");
            ledgerMaster.setIfsc("NA");
            ledgerMaster.setBankBranch("NA");
            ledgerMaster.setStateCode("NA");
        } else if (request.getParameter("slug").

                equalsIgnoreCase("others")) {
            if (paramMap.containsKey("pincode")) {
                ledgerMaster.setPincode(Long.parseLong(request.getParameter("pincode")));
            } else {
                ledgerMaster.setPincode(0L);
            }
            if (paramMap.containsKey("address")) {
                ledgerMaster.setAddress(request.getParameter("address"));
            } else {
                ledgerMaster.setAddress("NA");
            }
            if (paramMap.containsKey("mobile_no")) {
                ledgerMaster.setMobile(Long.parseLong(request.getParameter("mobile_no")));
            } else {
                ledgerMaster.setMobile(0L);
            }
            ledgerMaster.setTaxType("NA");
            ledgerMaster.setMailingName("NA");
            ledgerMaster.setOpeningBalType("NA");
            ledgerMaster.setOpeningBal(0.0);
            ledgerMaster.setEmail("NA");
            ledgerMaster.setTaxable(false);
            ledgerMaster.setGstin("NA");
            ledgerMaster.setRegistrationType(0L);
            ledgerMaster.setPancard("NA");
            ledgerMaster.setBankName("NA");
            ledgerMaster.setAccountNumber("NA");
            ledgerMaster.setIfsc("NA");
            ledgerMaster.setBankBranch("NA");
            ledgerMaster.setStateCode("NA");
        } else if (request.getParameter("slug").equalsIgnoreCase("assets")) {
            if (paramMap.containsKey("opening_bal_type")) {
                ledgerMaster.setOpeningBalType(request.getParameter("opening_bal_type"));
            } else {
                ledgerMaster.setOpeningBalType("NA");
            }
            if (paramMap.containsKey("opening_bal")) {
                if (request.getParameter("opening_bal_type").equalsIgnoreCase("Dr")) {
                    ledgerMaster.setOpeningBal(Double.parseDouble(request.getParameter("opening_bal")));
                } else {
                    Double openingBal = Double.parseDouble(request.getParameter("opening_bal"));
                    openingBal *= -1;
                    ledgerMaster.setOpeningBal(openingBal);
                }
            }
            ledgerMaster.setTaxType("NA");
            ledgerMaster.setMailingName("NA");
            ledgerMaster.setEmail("NA");
            ledgerMaster.setTaxable(false);
            ledgerMaster.setGstin("NA");
            ledgerMaster.setRegistrationType(0L);
            ledgerMaster.setPancard("NA");
            ledgerMaster.setBankName("NA");
            ledgerMaster.setAccountNumber("NA");
            ledgerMaster.setIfsc("NA");
            ledgerMaster.setBankBranch("NA");
            ledgerMaster.setStateCode("NA");
            ledgerMaster.setAddress("NA");
            ledgerMaster.setPincode(0L);
            ledgerMaster.setMobile(0L);
        }
        try {
            mLedger = repository.save(ledgerMaster);
        } catch (Exception e) {
            System.out.println("exceptions" + e.getMessage());
            e.printStackTrace();
        }
        if (paramMap.containsKey("gstdetails")) {
            if (key.equalsIgnoreCase("create"))
                insertIntoGstDetails(mLedger, request);
            else
                updateGstDetails(mLedger, request);
        }
        if (paramMap.containsKey("shippingDetails")) {
            if (key.equalsIgnoreCase("create"))
                insertIntoShippingDetails(mLedger, request);
            else
                updateShippingDetails(mLedger, request);
        }
        if (paramMap.containsKey("deptDetails")) {
            if (key.equalsIgnoreCase("create"))
                insertIntoDeptDetails(mLedger, request);
            else
                updateDeptDetails(mLedger, request);
        }
        if (paramMap.containsKey("billingDetails")) {
            if (key.equalsIgnoreCase("create"))
                insertIntoBillingDetails(mLedger, request);
            else
                updateBillingDetails(mLedger, request);
        }
        return mLedger;
    }

    private void updateBillingDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("billingDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray billDetailsJson = gstElements.getAsJsonArray();
        LedgerBillingDetails billDetails = null;
        if (billDetailsJson.size() > 0) {
            for (JsonElement mList : billDetailsJson) {
                JsonObject object = mList.getAsJsonObject();
                if (object.get("id").getAsLong() != 0) {
                    billDetails = ledgerBillingDetailsRepository.findByIdAndStatus(object.get("id").getAsLong(), true);
                } else {
                    billDetails = new LedgerBillingDetails();
                    billDetails.setStatus(true);
                }
                billDetails.setDistrict(object.get("district").getAsString());
                billDetails.setBillingAddress(object.get("billing_address").getAsString());
                billDetails.setCreatedBy(mLedger.getCreatedBy());
                billDetails.setLedgerMaster(mLedger);
                ledgerBillingDetailsRepository.save(billDetails);
            }
        }
        /* Remove from existing and set status false */
        String removeBillingDetails = request.getParameter("removeBillingList");
        JsonElement removeBillingElements = parser.parse(removeBillingDetails);
        JsonArray removeBillingJson = removeBillingElements.getAsJsonArray();
        LedgerBillingDetails mDeptDetails = null;
        if (removeBillingJson.size() > 0) {
            for (JsonElement mList : removeBillingJson) {
                Long object = mList.getAsLong();
                if (object != 0) {
                    mDeptDetails = ledgerBillingDetailsRepository.findByIdAndStatus(object, true);
                    if (mDeptDetails != null)
                        mDeptDetails.setStatus(false);
                    try {
                        ledgerBillingDetailsRepository.save(mDeptDetails);
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateDeptDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("deptDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray deptDetailsJson = gstElements.getAsJsonArray();
        LedgerDeptDetails deptDetails = null;
        if (deptDetailsJson.size() > 0) {
            for (JsonElement mList : deptDetailsJson) {
                JsonObject object = mList.getAsJsonObject();
                if (object.get("id").getAsLong() != 0) {
                    deptDetails = ledgerDeptDetailsRepository.findByIdAndStatus(object.get("id").getAsLong(), true);
                } else {
                    deptDetails = new LedgerDeptDetails();
                    deptDetails.setStatus(true);
                }
                deptDetails.setDept(object.get("dept").getAsString());
                deptDetails.setContactPerson(object.get("contact_person").getAsString());
                if (object.has("email"))
                    deptDetails.setEmail(object.get("email").getAsString());
                else
                    deptDetails.setEmail("NA");
                if (object.has("contact_no"))
                    deptDetails.setContactNo(object.get("contact_no").getAsLong());
                deptDetails.setCreatedBy(mLedger.getCreatedBy());
                deptDetails.setLedgerMaster(mLedger);
                ledgerDeptDetailsRepository.save(deptDetails);
            }
        }
        /* Remove from existing and set status false */
        String removeDeptDetails = request.getParameter("removeDeptList");
        JsonElement removeDeptElements = parser.parse(removeDeptDetails);
        JsonArray removeDeptJson = removeDeptElements.getAsJsonArray();
        LedgerDeptDetails mDeptDetails = null;
        if (removeDeptJson.size() > 0) {
            for (JsonElement mList : removeDeptJson) {
                Long object = mList.getAsLong();
                if (object != 0) {
                    mDeptDetails = ledgerDeptDetailsRepository.findByIdAndStatus(object, true);
                    if (mDeptDetails != null)
                        mDeptDetails.setStatus(false);
                    try {
                        ledgerDeptDetailsRepository.save(mDeptDetails);
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateShippingDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("shippingDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray shippingDetailsJson = gstElements.getAsJsonArray();
        LedgerShippingAddress spDetails = null;
        if (shippingDetailsJson.size() > 0) {
            for (JsonElement mList : shippingDetailsJson) {
                JsonObject object = mList.getAsJsonObject();
                if (object.get("id").getAsLong() != 0) {
                    spDetails = ledgerShippingDetailsRepository.findByIdAndStatus(object.get("id").getAsLong(), true);
                } else {
                    spDetails = new LedgerShippingAddress();
                    spDetails.setStatus(true);
                }
                spDetails.setDistrict(object.get("district").getAsString());
                spDetails.setShippingAddress(object.get("shipping_address").getAsString());
                spDetails.setCreatedBy(mLedger.getCreatedBy());
                spDetails.setLedgerMaster(mLedger);
                ledgerShippingDetailsRepository.save(spDetails);
            }
        }
        /* Remove from existing and set status false */
        String removeShippingDetails = request.getParameter("removeShippingList");
        JsonElement removeShippingElements = parser.parse(removeShippingDetails);
        JsonArray removeShippingJson = removeShippingElements.getAsJsonArray();
        LedgerShippingAddress mShippingDetails = null;
        if (removeShippingJson.size() > 0) {
            for (JsonElement mList : removeShippingJson) {
                Long object = mList.getAsLong();
                if (object != 0) {
                    mShippingDetails = ledgerShippingDetailsRepository.findByIdAndStatus(object, true);
                    if (mShippingDetails != null)
                        mShippingDetails.setStatus(false);
                    try {
                        ledgerShippingDetailsRepository.save(mShippingDetails);
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateGstDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("gstdetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray gstDetailsJson = gstElements.getAsJsonArray();
        LedgerGstDetails gstDetails = null;
        if (gstDetailsJson.size() > 0) {
            for (JsonElement mList : gstDetailsJson) {
                JsonObject object = mList.getAsJsonObject();
                if (object.get("id").getAsLong() != 0) {
                    gstDetails = ledgerGstDetailsRepository.findByIdAndStatus(object.get("id").getAsLong(), true);
                } else {
                    gstDetails = new LedgerGstDetails();
                    gstDetails.setStatus(true);
                }
                gstDetails.setGstin(object.get("gstin").getAsString());
                if (object.has("dateofregistartion"))
                    gstDetails.setDateOfRegistration(LocalDate.parse(
                            object.get("dateofregistartion").getAsString()));
                if (object.has("pancard"))
                    gstDetails.setPanCard(object.get("pancard").getAsString());
                else {
                    gstDetails.setPanCard("NA");
                }

                String stateCode = object.get("gstin").getAsString().substring(0, 2);
                gstDetails.setStateCode(stateCode);
                gstDetails.setCreatedBy(mLedger.getCreatedBy());
                gstDetails.setLedgerMaster(mLedger);
                try {
                    ledgerGstDetailsRepository.save(gstDetails);
                } catch (Exception e) {
                    System.out.println("Exception:" + e.getMessage());
                    e.getMessage();
                    e.printStackTrace();
                }
            }
        }
        /* Remove from existing and set status false */
        String removeGstDetails = request.getParameter("removeGstList");
        JsonElement removeGstElements = parser.parse(removeGstDetails);
        JsonArray removeGstJson = removeGstElements.getAsJsonArray();
        LedgerGstDetails mGstDetails = null;
        if (removeGstJson.size() > 0) {
            for (JsonElement mList : removeGstJson) {
                Long object = mList.
                        getAsLong();
                if (object != 0) {
                    mGstDetails = ledgerGstDetailsRepository.findByIdAndStatus(object, true);
                    if (mGstDetails != null)
                        mGstDetails.setStatus(false);
                    try {
                        ledgerGstDetailsRepository.save(mGstDetails);
                    } catch (Exception e) {
                        System.out.println("Exception:" + e.getMessage());
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void insertIntoBillingDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("billingDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray billDetailsJson = gstElements.getAsJsonArray();
        if (billDetailsJson.size() > 0) {
            for (JsonElement mList : billDetailsJson) {
                LedgerBillingDetails billDetails = new LedgerBillingDetails();
                JsonObject object = mList.getAsJsonObject();
                billDetails.setDistrict(object.get("district").getAsString());
                billDetails.setBillingAddress(object.get("billing_address").getAsString());
                billDetails.setCreatedBy(mLedger.getCreatedBy());
                billDetails.setStatus(true);
                billDetails.setLedgerMaster(mLedger);
                ledgerBillingDetailsRepository.save(billDetails);
            }
        }
    }

    private void insertIntoDeptDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("deptDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray deptDetailsJson = gstElements.getAsJsonArray();
        if (deptDetailsJson.size() > 0) {
            for (JsonElement mList : deptDetailsJson) {
                LedgerDeptDetails deptDetails = new LedgerDeptDetails();
                JsonObject object = mList.getAsJsonObject();
                deptDetails.setDept(object.get("dept").getAsString());
                deptDetails.setContactPerson(object.get("contact_person").getAsString());
                if (object.has("email"))
                    deptDetails.setEmail(object.get("email").getAsString());
                else
                    deptDetails.setEmail("NA");
                if (object.has("contact_no"))
                    deptDetails.setContactNo(object.get("contact_no").getAsLong());
                deptDetails.setCreatedBy(mLedger.getCreatedBy());
                deptDetails.setStatus(true);
                deptDetails.setLedgerMaster(mLedger);
                ledgerDeptDetailsRepository.save(deptDetails);
            }
        }
    }

    private void insertIntoShippingDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("shippingDetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray shippingDetailsJson = gstElements.getAsJsonArray();
        if (shippingDetailsJson.size() > 0) {
            for (JsonElement mList : shippingDetailsJson) {
                LedgerShippingAddress spDetails = new LedgerShippingAddress();
                JsonObject object = mList.getAsJsonObject();
                spDetails.setDistrict(object.get("district").getAsString());
                spDetails.setShippingAddress(object.get("shipping_address").getAsString());
                spDetails.setCreatedBy(mLedger.getCreatedBy());
                spDetails.setStatus(true);
                spDetails.setLedgerMaster(mLedger);
                ledgerShippingDetailsRepository.save(spDetails);
            }
        }
    }

    private void insertIntoGstDetails(LedgerMaster mLedger, HttpServletRequest request) {
        String strJson = request.getParameter("gstdetails");
        JsonParser parser = new JsonParser();
        JsonElement gstElements = parser.parse(strJson);
        JsonArray gstDetailsJson = gstElements.getAsJsonArray();
        if (gstDetailsJson.size() > 0) {
            LedgerGstDetails gstDetails = null;
            for (JsonElement mList : gstDetailsJson) {
                JsonObject object = mList.getAsJsonObject();
                gstDetails = new LedgerGstDetails();
                gstDetails.setGstin(object.get("gstin").getAsString());
                if (object.has("dateofregistartion"))
                    gstDetails.setDateOfRegistration(LocalDate.parse(
                            object.get("dateofregistartion").getAsString()));
                if (object.has("pancard"))
                    gstDetails.setPanCard(object.get("pancard").getAsString());
                else {
                    gstDetails.setPanCard("NA");
                }
                String stateCode = object.get("gstin").getAsString().substring(0, 2);
                gstDetails.setStateCode(stateCode);
                gstDetails.setCreatedBy(mLedger.getCreatedBy());
                gstDetails.setStatus(true);
                gstDetails.setLedgerMaster(mLedger);
                try {
                    ledgerGstDetailsRepository.save(gstDetails);
                } catch (Exception e) {
                    System.out.println("Exception:" + e.getMessage());
                    e.getMessage();
                    e.printStackTrace();
                }
            }
        }
    }

    public void insertIntoLedgerBalanceSummary(LedgerMaster mLedger, String key) {
        LedgerBalanceSummary ledgerBalanceSummary = null;
        if (key.equalsIgnoreCase("create")) {
            ledgerBalanceSummary = new LedgerBalanceSummary();
        } /*else {
            ledgerBalanceSummary = balanceSummaryRepository.findByLedgerMasterId(mLedger.getId());
        }*/
        ledgerBalanceSummary.setLedgerMaster(mLedger);
        ledgerBalanceSummary.setFoundations(mLedger.getFoundations());
        ledgerBalanceSummary.setPrinciples(mLedger.getPrinciples());
        ledgerBalanceSummary.setPrincipleGroups(mLedger.getPrincipleGroups());
        ledgerBalanceSummary.setDebit(0.0);
        ledgerBalanceSummary.setCredit(0.0);
        ledgerBalanceSummary.setOpeningBal(mLedger.getOpeningBal());
        ledgerBalanceSummary.setClosingBal(0.0);
        ledgerBalanceSummary.setBalance(mLedger.getOpeningBal());
        ledgerBalanceSummary.setStatus(true);
        ledgerBalanceSummary.setUnderPrefix(mLedger.getUnderPrefix());
        try {
            balanceSummaryRepository.save(ledgerBalanceSummary);
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
            e1.printStackTrace();
        }
    }

    /* get Sundry Creditors Ledgers by outlet id */
    public JsonObject getSundryCreditors(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<Object[]> sundryCreditors = ledgerRepository.findSundryCreditorsByOutletId(
                users.getOutlet().getId());
        JsonArray result = new JsonArray();
        result = getResult(sundryCreditors);
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    public JsonArray getResult(List<Object[]> list) {
        JsonArray result = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject response = new JsonObject();
            Object[] obj = list.get(i);
            response.addProperty("id", obj[0].toString());
            response.addProperty("name", obj[1].toString());
            if (obj[2] != null)
                response.addProperty("ledger_code", obj[2].toString());
            if (obj[3] != null)
                response.addProperty("state", obj[3].toString());
            response.add("gstDetails", getGSTDetails(Long.parseLong(obj[0].toString())));
            Long sundryCreditorId = Long.parseLong(obj[0].toString());
            Double balance = balanceSummaryRepository.findBalance(sundryCreditorId);
            if (balance != null) {
                if (balance > 0) {
                    response.addProperty("ledger_balance", balance);
                    response.addProperty("ledger_balance_type", "CR");
                } else {
                    response.addProperty("ledger_balance", Math.abs(balance));
                    response.addProperty("ledger_balance_type", "DR");
                }
            }
            result.add(response);
        }
        return result;
    }

    /* Get  */
    public JsonArray getGSTDetails(Long ledger_id) {

        JsonArray gstArray = new JsonArray();
        List<LedgerGstDetails> ledgerGstDetails = ledgerGstDetailsRepository.findByLedgerMasterIdAndStatus(ledger_id, true);
        for (LedgerGstDetails mDetails : ledgerGstDetails) {
            JsonObject mObject = new JsonObject();
            mObject.addProperty("gstin", mDetails.getGstin());
            mObject.addProperty("state", mDetails.getStateCode());
            gstArray.add(mObject);
        }
        //    gstDetails.add("gstDetails",gstArray);
        return gstArray;
    }

    public JsonObject getSundryDebtors(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<Object[]> sundryDebtors = ledgerRepository.findSundryDebtorsByOutletId(
                users.getOutlet().getId());
        JsonArray result = new JsonArray();
        result = getResult(sundryDebtors);
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    /* get Purchase Account by outletId and principleId */
    public JsonObject getPurchaseAccount(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        result = getLedgers("Purchase Accounts", users.getOutlet().getId());
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    private JsonArray getLedgers(String key, Long outletId) {
        Principles principles = principleRepository.findByPrincipleNameIgnoreCaseAndStatus(key, true);
        List<LedgerMaster> indirect_incomes = ledgerRepository.findByOutletIdAndPrinciplesIdAndStatus(
                outletId, principles.getId(), true);
        JsonArray result = new JsonArray();
        for (LedgerMaster mAccount : indirect_incomes) {
            JsonObject response = new JsonObject();
            response.addProperty("id", mAccount.getId());
            response.addProperty("name", mAccount.getLedgerName());
            response.addProperty("unique_code", principles.getUniqueCode());
            result.add(response);
        }
        return result;
    }

    /* get Sales Account by outletId and principleId */
    public JsonObject getSalesAccount(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        result = getLedgers("Sales Accounts", users.getOutlet().getId());
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    /* get All Indirect incomes by principleId(here principle id: 9 is for indirect incomes) */
    public JsonObject getIndirectIncomes(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        result = getLedgers("Indirect Income", users.getOutlet().getId());
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    /* get All Indirect expenses by principleId(here principle id: 9 is for indirect incomes) */
    public JsonObject getIndirectExpenses(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        result = getLedgers("Indirect Expenses", users.getOutlet().getId());
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

  /*  public JsonObject getAllLedgers(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));

        List<LedgerBalanceSummary> balanceSummaries = new ArrayList<>();

        if (users.getBranch() != null)
            balanceSummaries = balanceSummaryRepository.findByOutletIdAndBranchIdOrderByIdDesc(
                    users.getOutlet().getId(), users.getBranch().getId());
        else
            balanceSummaries = balanceSummaryRepository.findByOutletIdAndBranchNullOrderByIdDesc(
                    users.getOutlet().getId());
        for (LedgerBalanceSummary balanceSummary : balanceSummaries) {
            if (balanceSummary.getLedgerMaster() != null) {
                LedgerMaster ledgerMaster = ledgerRepository.findByIdAndStatus(balanceSummary.getLedgerMaster().getId(), true);
                if (ledgerMaster != null) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", balanceSummary.getId());
                    jsonObject.addProperty("ledgerId", balanceSummary.getLedgerMaster().getId());
                    if (balanceSummary.getPrinciples() != null) {
                        jsonObject.addProperty("principle_name", balanceSummary.getPrinciples().getPrincipleName());
                    }
                    if (balanceSummary.getPrincipleGroups() != null) {
                        jsonObject.addProperty("subprinciple_name", balanceSummary.getPrincipleGroups().getGroupName());
                    } else {
                        jsonObject.addProperty("subprinciple_name", "");
                    }
                    jsonObject.addProperty("default_ledger", balanceSummary.getLedgerMaster().getIsDefaultLedger());
                    jsonObject.addProperty("ledger_form_parameter_slug", ledgerMaster.getSlugName());
                    jsonObject.addProperty("cr", Math.abs(balanceSummary.getCredit()));
                    jsonObject.addProperty("dr", Math.abs(balanceSummary.getDebit()));
                    jsonObject.addProperty("balance", balanceSummary.getBalance());
                    jsonObject.addProperty("ledger_name", ledgerMaster.getLedgerName());
                    jsonObject.addProperty("group_name", ledgerMaster.getAssociateGroups() != null ?
                            ledgerMaster.getAssociateGroups().getAssociatesName() : "");
                    result.add(jsonObject);
                }
            }
        }
        JsonObject json = new JsonObject();
        json.addProperty("message", "success");
        json.addProperty("responseStatus", HttpStatus.OK.value());
        json.add("responseList", result);
        return json;
    }*/
  public JsonObject getAllLedgers(HttpServletRequest request) {
      JsonArray result = new JsonArray();
      Double closingBalance = 0.0;
      Double sumCR = 0.0;
      Double sumDR = 0.0;
      DecimalFormat df = new DecimalFormat("0.00");
      Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
      List<LedgerMaster> balanceSummaries = new ArrayList<>();
      if (users.getBranch() != null) {
          /**** Default ledgers for Branch Users *****/
          balanceSummaries = ledgerRepository.findByOutletIdAndBranchIdAndStatusOrderByIdDesc(
                  users.getOutlet().getId(), users.getBranch().getId(), true);
      } else {
          balanceSummaries = ledgerRepository.findByOutletIdAndStatusAndBranchIsNullOrderByIdDesc(users.getOutlet().getId(), true);
      }
      for (LedgerMaster balanceSummary : balanceSummaries) {
          Long ledgerId = balanceSummary.getId();
          JsonObject jsonObject = new JsonObject();

          jsonObject.addProperty("id", balanceSummary.getId());
          jsonObject.addProperty("foundations_name", balanceSummary.getFoundations().getFoundationName());

          if (balanceSummary.getPrinciples() != null) {
              jsonObject.addProperty("principle_name", balanceSummary.getPrinciples().getPrincipleName());
          }
          if (balanceSummary.getPrincipleGroups() != null) {
              jsonObject.addProperty("subprinciple_name", balanceSummary.getPrincipleGroups().getGroupName());
          } else {
              jsonObject.addProperty("subprinciple_name", "");
          }
          try {
              Double openingBalance = ledgerRepository.findOpeningBalance(balanceSummary.getId());//00
              sumCR = ledgerTransactionPostingsRepository.findsumCR(balanceSummary.getId());//10000
              sumDR = ledgerTransactionPostingsRepository.findsumDR(balanceSummary.getId());//15000
              closingBalance = openingBalance - sumDR + sumCR;//0-(-0.40)-0.20// 0-15000+10000  //-5000
          } catch (Exception e) {
              ledgerLogger.error("Exception:" + e.getMessage());
              e.printStackTrace();
          }
          jsonObject.addProperty("default_ledger", balanceSummary.getIsDefaultLedger());
          jsonObject.addProperty("ledger_form_parameter_slug", balanceSummary.getSlugName());
//            LedgerTransactionPostings mLedger = ledgerTransactionPostingsRepository.findByLedgerMasterIdAndStatus(balanceSummary.getId(), true);
          if (balanceSummary.getFoundations().getId() == 1) {
              if (closingBalance > 0) {
                  jsonObject.addProperty("cr", df.format(Math.abs(closingBalance)));
                  jsonObject.addProperty("dr", df.format(0));
              } else {
                  jsonObject.addProperty("cr", df.format(0));
                  jsonObject.addProperty("dr", df.format(Math.abs(closingBalance)));
              }

          } else if (balanceSummary.getFoundations().getId() == 2) {
              if (closingBalance > 0) {
                  jsonObject.addProperty("cr", df.format(Math.abs(closingBalance)));
                  jsonObject.addProperty("dr", df.format(0));

              } else {
                  jsonObject.addProperty("cr", df.format(0));
                  jsonObject.addProperty("dr", df.format(Math.abs(closingBalance)));
              }

          } else if (balanceSummary.getFoundations().getId() == 3) {
              if (closingBalance > 0) {
                  jsonObject.addProperty("cr", df.format(Math.abs(closingBalance)));
                  jsonObject.addProperty("dr", df.format(0));
              } else {
                  jsonObject.addProperty("cr", df.format(0));
                  jsonObject.addProperty("dr", df.format(Math.abs(closingBalance)));
              }

          } else if (balanceSummary.getFoundations().getId() == 4) {
              if (closingBalance < 0) {
                  jsonObject.addProperty("cr", df.format(0));
                  jsonObject.addProperty("dr", df.format(Math.abs(closingBalance)));
              } else {
                  jsonObject.addProperty("cr", df.format(Math.abs(closingBalance)));
                  jsonObject.addProperty("dr", df.format(0));
              }
          }

          jsonObject.addProperty("ledger_name", balanceSummary.getLedgerName());
          result.add(jsonObject);
      }
      JsonObject json = new JsonObject();
      json.addProperty("message", "success");
      json.addProperty("responseStatus", HttpStatus.OK.value());
      json.add("responseList", result);
      return json;
  }

    /* Sundry creditors overdue for bil by bill */
    public JsonObject getTotalAmountBillbyBill(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));

        List<LedgerTransactionDetails> list = new ArrayList<>();

        list = transactionDetailsRepository.
                findByLedgerMasterIdAndOutletIdAndTransactionTypeId(Long.parseLong(
                        request.getParameter("id")), users.getOutlet().getId(), 1L);
        JsonArray result = new JsonArray();

        for (LedgerTransactionDetails mList : list) {
            JsonObject jsonObject = new JsonObject();
            if (!mList.getPaymentStatus().equalsIgnoreCase("completed")) {
                jsonObject.addProperty("id", mList.getId());
                jsonObject.addProperty("ledger_id", mList.getLedgerMaster().getId());
                jsonObject.addProperty("transaction_id", mList.getTransactionId());
                if (mList.getPaymentStatus().equalsIgnoreCase("pending")) {
                    jsonObject.addProperty("amount", mList.getCredit());
                } else {
                    /*PaymentTransactionDetails paymentDetails = paymentTransactionDetailsRepo.
                            findTopByTransactionDetailsIdAndPaymentStatusOrderByIdDesc(mList.getId(), "partially_paid");
                    jsonObject.addProperty("amount", paymentDetails.getRemainingAmt());*/
                }
                result.add(jsonObject);
            }
        }
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("list", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            response.addProperty("message", "empty list");
            response.add("list", result);
        }
        return response;
    }

    public Object editLedgerMaster(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        Long id = Long.parseLong(request.getParameter("id"));
        LedgerMaster ledgerMaster = repository.findByIdAndStatus(id, true);
        LedgerMaster mLedger = ledgerCreateUpdate(request, "edit", ledgerMaster);
        if (mLedger != null) {
            // insertIntoLedgerBalanceSummary(mLedger, "edit");
            responseMessage.setMessage("Ledger updated successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } else {
            responseMessage.setMessage("error");
            responseMessage.setResponseStatus(HttpStatus.FORBIDDEN.value());
        }
        return responseMessage;
    }

    /* get total balance of each sundry creditors for Payment Vouchers  */
    public JsonObject getTotalAmount(HttpServletRequest request, String key) {
        List<Object[]> list = new ArrayList<>();
        if (key.equalsIgnoreCase("sc")) {
            list = balanceSummaryRepository.calculate_total_amount(5L);
        } else if (key.equalsIgnoreCase("sd")) {
            list = balanceSummaryRepository.calculate_total_amount(1L);
        }
        JsonArray result = new JsonArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            Object[] obj = list.get(i);
            jsonObject.addProperty("id", obj[0].toString());
            jsonObject.addProperty("amount", Math.abs(Double.parseDouble(obj[1].toString())));
            jsonObject.addProperty("name", obj[2].toString());
            LedgerMaster creditors = ledgerRepository.findByIdAndStatus(
                    Long.parseLong(obj[0].toString()), true);
            jsonObject.addProperty("balancing_method",
                    generateSlugs.getSlug(creditors.getBalancingMethod().getBalancingMethod()));
            jsonObject.addProperty("slug", creditors.getSlugName());
            if (key.equalsIgnoreCase("sc")) {
                if (Double.parseDouble(obj[1].toString()) > 0)
                    jsonObject.addProperty("type", "DR");
                else
                    jsonObject.addProperty("type", "CR");
            } else if (key.equalsIgnoreCase("sd")) {
                if (Double.parseDouble(obj[1].toString()) > 0)
                    jsonObject.addProperty("type", "CR");
                else
                    jsonObject.addProperty("type", "DR");
            }
            result.add(jsonObject);
        }
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("list", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            response.addProperty("message", "empty list");
            response.add("list", result);
        }
        return response;
    }

    public JsonObject getLedgersById(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        JsonObject jsonObject = new JsonObject();
       /* Long id = Long.parseLong(request.getParameter("ledger_form_parameter_id"));
        String slug_name = request.getParameter("ledger_form_parameter_slug");*/
        LedgerMaster mLedger = repository.findByIdAndStatus(Long.parseLong(request.getParameter("id")), true);
        if (mLedger != null) {
            jsonObject.addProperty("id", mLedger.getId());
            jsonObject.addProperty("ledger_name", mLedger.getLedgerName());
            jsonObject.addProperty("is_private", mLedger.getIsPrivate());
            jsonObject.addProperty("branchId", String.valueOf(mLedger.getBranch() != null ? mLedger.getBranch().getId() : ""));
            jsonObject.addProperty("default_ledger", mLedger.getIsDefaultLedger());
            jsonObject.addProperty("supplier_code", mLedger.getLedgerCode() != null ? mLedger.getLedgerCode() : null);
            if (mLedger.getMailingName() != null)
                jsonObject.addProperty("mailing_name", mLedger.getMailingName());
            if (mLedger.getOpeningBalType() != null)
                jsonObject.addProperty("opening_bal_type", mLedger.getOpeningBalType());
            if (mLedger.getOpeningBal() != null)
                jsonObject.addProperty("opening_bal", Math.abs(mLedger.getOpeningBal()));
            if (mLedger.getBalancingMethod() != null)
                jsonObject.addProperty("balancing_method", mLedger.getBalancingMethod().getId());
            if (mLedger.getAddress() != null)
                jsonObject.addProperty("address", mLedger.getAddress());
            jsonObject.addProperty("state", mLedger.getState() != null ? mLedger.getState().getId() : null);
            jsonObject.addProperty("country", mLedger.getCountry() != null ? mLedger.getCountry().getId() : null);
            if (mLedger.getPincode() != null)
                jsonObject.addProperty("pincode", mLedger.getPincode());
            if (mLedger.getEmail() != null)
                jsonObject.addProperty("email", mLedger.getEmail());
            if (mLedger.getMobile() != null)
                jsonObject.addProperty("mobile_no", mLedger.getMobile());
            if (mLedger.getTaxable() != null)
                jsonObject.addProperty("taxable", mLedger.getTaxable());
            if (mLedger.getTaxType() != null)
                jsonObject.addProperty("tax_type", mLedger.getTaxType());
            jsonObject.addProperty("under_prefix", mLedger.getUnderPrefix());
            jsonObject.addProperty("under_prefix_separator", mLedger.getUnderPrefix().split("#")[0]);
            jsonObject.addProperty("under_id", mLedger.getUnderPrefix().split("#")[1]);
            /* pune visit changes */
            jsonObject.addProperty("credit_days", mLedger.getCreditDays());
            jsonObject.addProperty("applicable_from", mLedger.getApplicableFrom());
            jsonObject.addProperty("fssai", mLedger.getFoodLicenseNo());
            jsonObject.addProperty("tds", mLedger.getTds());
            jsonObject.addProperty("tcs", mLedger.getTcs());
            jsonObject.addProperty("tds_applicable_date", mLedger.getTdsApplicableDate() != null ? mLedger.getTdsApplicableDate().toString() : "NA");
            jsonObject.addProperty("tcs_applicable_date", mLedger.getTcsApplicableDate() != null ? mLedger.getTcsApplicableDate().toString() : "NA");

            /* gst Details of Ledger */
            if (mLedger.getTaxable() != null) {
                if (mLedger.getTaxable()) {

                    JsonArray jsongstArray = new JsonArray();
                    List<LedgerGstDetails> gstList = new ArrayList<>();
                    gstList = ledgerGstDetailsRepository.findByLedgerMasterIdAndStatus(mLedger.getId(), true);
                    if (gstList != null && gstList.size() > 0) {
                        for (LedgerGstDetails mList : gstList) {
                            JsonObject mObject = new JsonObject();
                            mObject.addProperty("id", mList.getId());
                            mObject.addProperty("gstin", mList.getGstin());
                            mObject.addProperty("dateOfRegistration", mList.getDateOfRegistration() != null ? mList.getDateOfRegistration().toString() : "NA");
                            mObject.addProperty("pancard", mList.getPanCard());
                            jsongstArray.add(mObject);
                        }
                    }
                    jsonObject.add("gstdetails", jsongstArray);
                }
            }
            /* end of GST Details */

            /* Shipping Address Details */
            JsonArray jsonshippingArray = new JsonArray();
            List<LedgerShippingAddress> shippingList = new ArrayList<>();
            shippingList = ledgerShippingDetailsRepository.findByLedgerMasterIdAndStatus(mLedger.getId(), true);
            if (shippingList != null && shippingList.size() > 0) {
                for (LedgerShippingAddress mList : shippingList) {
                    JsonObject mObject = new JsonObject();
                    mObject.addProperty("id", mList.getId());
                    mObject.addProperty("district", mList.getDistrict());
                    mObject.addProperty("shipping_address", mList.getShippingAddress());
                    jsonshippingArray.add(mObject);
                }
            }
            jsonObject.add("shippingDetails", jsonshippingArray);
            /* End of Shipping Address Details */

            /* Billing Address Details */
            JsonArray jsonbillingArray = new JsonArray();
            List<LedgerBillingDetails> billingDetails = new ArrayList<>();
            billingDetails = ledgerBillingDetailsRepository.findByLedgerMasterIdAndStatus(mLedger.getId(), true);
            if (billingDetails != null && billingDetails.size() > 0) {
                for (LedgerBillingDetails mList : billingDetails) {
                    JsonObject mObject = new JsonObject();
                    mObject.addProperty("id", mList.getId());
                    mObject.addProperty("district", mList.getDistrict());
                    mObject.addProperty("billing_address", mList.getBillingAddress());
                    jsonbillingArray.add(mObject);
                }
            }
            jsonObject.add("billingDetails", jsonbillingArray);
            /* End of Billing Address Details */

            /* Deptartment Details */
            JsonArray jsondeptArray = new JsonArray();
            List<LedgerDeptDetails> deptDetails = new ArrayList<>();
            deptDetails = ledgerDeptDetailsRepository.findByLedgerMasterIdAndStatus(mLedger.getId(), true);
            if (deptDetails != null && deptDetails.size() > 0) {
                for (LedgerDeptDetails mList : deptDetails) {
                    JsonObject mObject = new JsonObject();
                    mObject.addProperty("id", mList.getId());
                    mObject.addProperty("dept", mList.getDept());
                    mObject.addProperty("contact_person", mList.getContactPerson());
                    mObject.addProperty("contact_no", mList.getContactNo());
                    mObject.addProperty("email", mList.getEmail());
                    jsondeptArray.add(mObject);
                }
            }
            jsonObject.add("deptDetails", jsondeptArray);
            /* End of Department Details */

            if (mLedger.getRegistrationType() != null)
                jsonObject.addProperty("registration_type", mLedger.getRegistrationType());
            if (mLedger.getPancard() != null)
                jsonObject.addProperty("pancard_no", mLedger.getPancard());
            if (mLedger.getBankName() != null)
                jsonObject.addProperty("bank_name", mLedger.getBankName());
            if (mLedger.getAccountNumber() != null)
                jsonObject.addProperty("account_no", mLedger.getAccountNumber());
            if (mLedger.getIfsc() != null)
                jsonObject.addProperty("ifsc_code", mLedger.getIfsc());
            if (mLedger.getBankBranch() != null)
                jsonObject.addProperty("bank_branch", mLedger.getBankBranch());
            if (mLedger.getPrincipleGroups() != null) {
                jsonObject.addProperty("principle_id", mLedger.getPrinciples().getId());
                jsonObject.addProperty("principle_name", mLedger.getPrinciples().getPrincipleName());
                jsonObject.addProperty("ledger_form_parameter_id",
                        mLedger.getPrincipleGroups().getLedgerFormParameter().getId());
                jsonObject.addProperty("ledger_form_parameter_slug",
                        mLedger.getPrincipleGroups().getLedgerFormParameter().getSlugName());
                jsonObject.addProperty("sub_principle_id", mLedger.getPrincipleGroups().getId());
                jsonObject.addProperty("subprinciple_name",
                        mLedger.getPrincipleGroups().getGroupName());
            } else {
                jsonObject.addProperty("principle_id", mLedger.getPrinciples().getId());
                jsonObject.addProperty("principle_name",
                        mLedger.getPrinciples().getPrincipleName());
                jsonObject.addProperty("ledger_form_parameter_id",
                        mLedger.getPrinciples().getLedgerFormParameter().getId());
                jsonObject.addProperty("ledger_form_parameter_slug",
                        mLedger.getPrinciples().getLedgerFormParameter().getSlugName());
                jsonObject.addProperty("sub_principle_id", "");
                jsonObject.addProperty("subprinciple_name", "");
            }
            result.addProperty("message", "success");
            result.addProperty("responseStatus", HttpStatus.OK.value());
            result.add("response", jsonObject);
        } else {
            result.addProperty("message", "Not Found");
            result.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
        }
        return result;
    }


    /* public Object DTGetallledgers(Map<String, String> request, HttpServletRequest req) {
     *//*Users users = jwtRequestFilter.getUserDataFromToken(req.getHeader("Authorization").substring(7));
        Long outletId = users.getOutlet().getId();
        Integer from = Integer.parseInt(request.get("from"));
        Integer to = Integer.parseInt(request.get("to"));
        String searchText = request.get("searchText");

        GenericDatatable genericDatatable = new GenericDatatable();
        List<LedgerBalanceSummaryDtView> ledgerBalanceSummaryDtViewList = new ArrayList<>();
        try {
            String query = "SELECT * FROM `ledger_balance_summary_dt_view` WHERE ledger_balance_summary_dt_view.outlet_id='" + outletId
                    + "' AND ledger_balance_summary_dt_view.status=1";

            if (!searchText.equalsIgnoreCase("")) {
                query = query + " AND (id LIKE '%" + searchText + "%' OR  ledger_name LIKE '%" + searchText + "%' OR group_name LIKE '%" +
                        searchText + "%' OR  principle_name LIKE '%" + searchText + "%' OR credit LIKE '%" +
                        searchText + "%' OR debit LIKE '%" +
                        searchText + "%' )";
            }

            String jsonToStr = request.get("sort");
            System.out.println(" sort " + jsonToStr);
            JsonObject jsonObject = new Gson().fromJson(jsonToStr, JsonObject.class);
            if (!jsonObject.get("colId").toString().equalsIgnoreCase("null") &&
                    jsonObject.get("colId").getAsString() != null) {
                System.out.println(" ORDER BY " + jsonObject.get("colId").getAsString());
                String sortBy = jsonObject.get("colId").getAsString();
                query = query + " ORDER BY " + sortBy;
                if (jsonObject.get("isAsc").getAsBoolean() == true) {
                    query = query + " ASC";
                } else {
                    query = query + " DESC";
                }
            } else {
                query = query + " ORDER BY ledger_name ASC";
            }
            String query1 = query;
            Integer endLimit = to - from;
            query = query + " LIMIT " + from + ", " + endLimit;
            System.out.println("query " + query);*//*

        //  Query q = entityManager.createNativeQuery(query, LedgerBalanceSummaryDtView.class);
        //  Query q1 = entityManager.createNativeQuery(query1, LedgerBalanceSummaryDtView.class);

        // ledgerBalanceSummaryDtViewList = q.getResultList();
        //   System.out.println("Limit total rows " + ledgerBalanceSummaryDtViewList.size());

        //   List<LedgerBalanceSummaryDtView> ledgerBalanceSummaryDtViewArrayList = new ArrayList<>();
       *//*     ledgerBalanceSummaryDtViewArrayList = q1.getResultList();
            System.out.println("total rows " + ledgerBalanceSummaryDtViewArrayList.size());

            genericDatatable.setRows(ledgerBalanceSummaryDtViewList);
            genericDatatable.setTotalRows(ledgerBalanceSummaryDtViewArrayList.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception " + e.getMessage());

            genericDatatable.setRows(ledgerBalanceSummaryDtViewList);
            genericDatatable.setTotalRows(0);
        }
        return genericDatatable;*//*
    }*/

    /* get sundry creditors, sundry debtors,cash account and  bank accounts*/
    public Object getClientList(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        ClientsListDTO clientsListDTO = new ClientsListDTO();
        /* sundry Creditors List */
        List<Object[]> sundryCreditors = ledgerRepository.findSundryCreditorsByOutletId(
                users.getOutlet().getId());
        List<ClientDetails> clientDetails = new ArrayList<>();
        for (int i = 0; i < sundryCreditors.size(); i++) {
            ClientDetails mDetails = new ClientDetails();
            Object[] obj = sundryCreditors.get(i);
            mDetails.setId(Long.parseLong(obj[0].toString()));
            mDetails.setLedger_name((String) obj[1]);
            mDetails.setLedger_code((String) obj[2]);
            mDetails.setStateCode((String) obj[3]);
            clientDetails.add(mDetails);
        }
        /* end of Sundry creditors List */

        /* sundry Debtors List */
        List<Object[]> sundryDebtors = ledgerRepository.findSundryDebtorsByOutletId(
                users.getOutlet().getId());
        for (int i = 0; i < sundryDebtors.size(); i++) {
            ClientDetails mDetails = new ClientDetails();
            Object[] obj = sundryDebtors.get(i);
            mDetails.setId(Long.parseLong(obj[0].toString()));
            mDetails.setLedger_name((String) obj[1]);
            mDetails.setLedger_code((String) obj[2]);
            mDetails.setStateCode((String) obj[3]);
            clientDetails.add(mDetails);
        }
        /* end of Sundry debtors List */

        /* Cash-in Hand List */
        List<Object[]> cashInHands = ledgerRepository.findCashInHandByOutletId(
                users.getOutlet().getId());
        for (int i = 0; i < cashInHands.size(); i++) {
            ClientDetails mDetails = new ClientDetails();
            Object[] obj = cashInHands.get(i);
            mDetails.setId(Long.parseLong(obj[0].toString()));
            mDetails.setLedger_name((String) obj[1]);
            mDetails.setLedger_code((String) obj[2]);
            mDetails.setStateCode((String) obj[3]);
            clientDetails.add(mDetails);
        }
        /* end of Cash in Hand List */

        /* Bank Accounts List */
        List<Object[]> bankAccounts = ledgerRepository.findBankAccountsByOutletId(
                users.getOutlet().getId());
        for (int i = 0; i < bankAccounts.size(); i++) {
            ClientDetails mDetails = new ClientDetails();
            Object[] obj = bankAccounts.get(i);
            mDetails.setId(Long.parseLong(obj[0].toString()));
            mDetails.setLedger_name((String) obj[1]);
            mDetails.setLedger_code((String) obj[2]);
            mDetails.setStateCode((String) obj[3]);
            clientDetails.add(mDetails);
        }
        /* end of Bank accounts List */
        if (clientDetails.size() > 0) {
            clientsListDTO.setMessage("success");
            clientsListDTO.setResponseStatus(HttpStatus.OK.value());
            clientsListDTO.setList(clientDetails);
        } else {
            clientsListDTO.setMessage("empty list");
            clientsListDTO.setResponseStatus(HttpStatus.OK.value());
            clientsListDTO.setList(clientDetails);
        }
        return clientsListDTO;
    }

    /* Get Cash-In-Hand and Bank Account Ledger from ledger balancer summary   */
    public JsonObject getCashAcBankAccount(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonArray result = new JsonArray();
        LedgerMaster ledgerMaster = ledgerRepository.findLedgerIdAndName(users.getOutlet().getId());
        LedgerBalanceSummary cashList = balanceSummaryRepository.findByLedgerMasterId(ledgerMaster.getId());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", cashList.getId());
        jsonObject.addProperty("name", cashList.getLedgerMaster().getLedgerName());
        jsonObject.addProperty("slug", generateSlugs.getSlug(
                cashList.getPrincipleGroups().getGroupName()));
        if (cashList.getDebit() != 0.0)
            jsonObject.addProperty("amount", cashList.getDebit());
        else
            jsonObject.addProperty("amount", cashList.getCredit());
        result.add(jsonObject);
        List<LedgerBalanceSummary> bankList = balanceSummaryRepository.findByPrincipleGroupsId(2L);
        for (LedgerBalanceSummary mList : bankList) {
            JsonObject jsonObject_ = new JsonObject();
            jsonObject_.addProperty("id", mList.getId());
            jsonObject_.addProperty("name", mList.getLedgerMaster().getLedgerName());
            jsonObject_.addProperty("slug", generateSlugs.getSlug(
                    mList.getPrincipleGroups().getGroupName()));
            if (cashList.getDebit() != 0.0)
                jsonObject.addProperty("amount", mList.getDebit());
            else
                jsonObject.addProperty("amount", mList.getCredit());
            result.add(jsonObject_);
        }
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("list", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.FORBIDDEN.value());
            response.addProperty("message", "empty list");
            response.add("list", result);
        }
        return response;
    }

    public JsonObject getSundryDebtorsById(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        LedgerMaster sundryDebtors = ledgerRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("sundry_debtors_id")), true);
        if (sundryDebtors != null) {
            result.addProperty("id", sundryDebtors.getId());
            result.addProperty("sundry_debtors_name", sundryDebtors.getLedgerName());
            result.addProperty("mobile", sundryDebtors.getMobile());
            result.addProperty("address", sundryDebtors.getAddress());
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("data", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "empty data");
            response.add("data", result);
        }

        return response;
    }

    public JsonObject getSundryCreditorsById(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        JsonObject response = new JsonObject();
        JsonObject result = new JsonObject();
        System.out.println("Sundry Creditor Id:" + request.getParameter("sundry_creditors_id"));
        LedgerMaster sundryCreditor = ledgerRepository.findByIdAndStatus(
                Long.parseLong(request.getParameter("sundry_creditors_id")), true);
        if (sundryCreditor != null) {
            result.addProperty("id", sundryCreditor.getId());
            result.addProperty("sundry_creditor_name", sundryCreditor.getLedgerName());
            result.addProperty("mobile", sundryCreditor.getMobile());
            result.addProperty("address", sundryCreditor.getAddress());
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("data", result);
        } else {
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.addProperty("message", "success");
            response.add("data", result);
        }
        return response;
    }

    public JsonObject getGstDetails(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray result = new JsonArray();
        List<LedgerGstDetails> gstDetails = new ArrayList<>();
        Long ledgerId = Long.valueOf(request.getParameter("ledger_id"));
        gstDetails = ledgerGstDetailsRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
        if (gstDetails != null && gstDetails.size() > 0) {
            for (LedgerGstDetails mDetails : gstDetails) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("id", mDetails.getId());
                mObject.addProperty("gstNo", mDetails.getGstin());
                mObject.addProperty("dateOfRegistration", mDetails.getDateOfRegistration() != null ? mDetails.getDateOfRegistration().toString() : "NA");
                mObject.addProperty("pancard", mDetails.getPanCard());
                result.add(mObject);
            }
        }
        response.addProperty("message", "success");
        response.addProperty("responseStatus", HttpStatus.OK.value());
        response.add("list", result);
        return response;
    }

    public JsonObject getShippingDetails(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray result = new JsonArray();
        List<LedgerShippingAddress> shippingDetails = new ArrayList<>();
        Long ledgerId = Long.valueOf(request.getParameter("ledger_id"));
        shippingDetails = ledgerShippingDetailsRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
        if (shippingDetails != null && shippingDetails.size() > 0) {
            for (LedgerShippingAddress mDetails : shippingDetails) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("id", mDetails.getId());
                mObject.addProperty("district", mDetails.getDistrict());
                mObject.addProperty("shipping_address", mDetails.getShippingAddress());
                result.add(mObject);
            }
        }
        response.addProperty("message", "success");
        response.addProperty("responseStatus", HttpStatus.OK.value());
        response.add("list", result);
        return response;
    }

    public JsonObject getDeptDetails(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray result = new JsonArray();
        List<LedgerDeptDetails> deptDetails = new ArrayList<>();
        Long ledgerId = Long.valueOf(request.getParameter("ledger_id"));
        deptDetails = ledgerDeptDetailsRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
        if (deptDetails != null && deptDetails.size() > 0) {
            for (LedgerDeptDetails mDetails : deptDetails) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("id", mDetails.getId());
                mObject.addProperty("department", mDetails.getDept());
                mObject.addProperty("contact_no", mDetails.getContactNo());
                mObject.addProperty("contact_person", mDetails.getContactPerson());
                mObject.addProperty("email", mDetails.getEmail());
                result.add(mObject);
            }
        }
        response.addProperty("message", "success");
        response.addProperty("responseStatus", HttpStatus.OK.value());
        response.add("list", result);
        return response;
    }

    public JsonObject getBillingDetails(HttpServletRequest request) {
        JsonObject response = new JsonObject();
        JsonArray result = new JsonArray();
        List<LedgerBillingDetails> billDetails = new ArrayList<>();
        Long ledgerId = Long.valueOf(request.getParameter("ledger_id"));
        billDetails = ledgerBillingDetailsRepository.findByLedgerMasterIdAndStatus(ledgerId, true);
        if (billDetails != null && billDetails.size() > 0) {
            for (LedgerBillingDetails mDetails : billDetails) {
                JsonObject mObject = new JsonObject();
                mObject.addProperty("id", mDetails.getId());
                mObject.addProperty("billing_address", mDetails.getBillingAddress());
                mObject.addProperty("district", mDetails.getDistrict());
                result.add(mObject);
            }
        }
        response.addProperty("message", "success");
        response.addProperty("responseStatus", HttpStatus.OK.value());
        response.add("list", result);
        return response;
    }

    public JsonObject getLedgersForList(HttpServletRequest request) {
        JsonObject result = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<LedgerMaster> mList = ledgerRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        if (mList.size() > 0) {
            for (LedgerMaster mLedger : mList) {
                JsonObject response = new JsonObject();
                response.addProperty("id", mLedger.getId());
                response.addProperty("ledger_name", mLedger.getLedgerName());
                jsonArray.add(response);
            }
        }

        result.add("responseObject", jsonArray);
        result.addProperty("responseStatus", HttpStatus.OK.value());

        return result;
    }

    public JsonObject getLedgersByBranch(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        JsonObject result = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        Long branchId = Long.valueOf(request.getParameter("branchId"));
        List<Object[]> getledgerlist = ledgerRepository.getLedgerList(users.getOutlet().getId(), branchId);

        for (int i = 0; i < getledgerlist.size(); i++) {
            Object[] ledgerObj = getledgerlist.get(i);

            JsonObject response = new JsonObject();
            response.addProperty("id", ledgerObj[0].toString());
            response.addProperty("ledger_name", ledgerObj[1].toString());
            jsonArray.add(response);

        }
        result.add("responseObject", jsonArray);
        result.addProperty("responseStatus", HttpStatus.OK.value());

        return result;
    }

    public JsonObject getSundryDebtorsStudents(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<Object[]> sundryDebtors = ledgerRepository.findSundryDebtorsByOutletIdAndBranchId(
                users.getOutlet().getId(), Long.valueOf(request.getParameter("branchId")));
        JsonArray result = new JsonArray();
        result = getResult(sundryDebtors);
        JsonObject response = new JsonObject();
        if (result.size() > 0) {
            response.addProperty("message", "success");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        } else {
            response.addProperty("message", "empty");
            response.addProperty("responseStatus", HttpStatus.OK.value());
            response.add("list", result);
        }
        return response;
    }

    /*Get Ledger Details By Id */
    public JsonObject getLedgerDetails(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        String sundarydebtorsid = request.getParameter("id");
        List<LedgerTransactionPostings> ledgertranxdetails = new ArrayList<>();
        Long sdid = Long.parseLong(sundarydebtorsid);
        LocalDate now = LocalDate.now();
        Double opening = 0.0;
        if (users.getBranch() != null) {
        //  ledgertranxdetails = transactionDetailsRepositorys.findByLedgerMasterIdAndOutletIdAndBranchIdAndTransactionDateBetween(sdid, users.getOutlet().getId(), users.getBranch().getId(), now.withDayOfMonth(1), now);
            ledgertranxdetails = ledgerTransactionPostingsRepository.findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(sdid, users.getOutlet().getId(), users.getBranch().getId(), true);
        } else {
            ledgertranxdetails = ledgerTransactionPostingsRepository.findByLedgerMasterIdAndOutletIdAndStatus(sdid, users.getOutlet().getId(), true);
        }
        for (int i = 0; i < ledgertranxdetails.size(); i++) {
            JsonObject response = new JsonObject();
            if (i == 0) {
                opening = ledgertranxdetails.get(i).getLedgerMaster().getOpeningBal();
            }
            response.addProperty("id", ledgertranxdetails.get(i).getId());
            response.addProperty("invoice_date", ledgertranxdetails.get(i).getTransactionDate().toString());
//            long sid=ledgertranxdetails.get(i).getLedgerMaster().getStudentRegister().getId();
//          StudentAdmission studentAdmission= studentAdmissionRepository.findByStudentRegisterId(sid);
//            if(studentAdmission!=null)
//            {
//
//            }



//            if(mDetails.getType().equalsIgnoreCase("DR"))
            response.addProperty("particulars", ledgertranxdetails.get(i).getLedgerMaster().getLedgerName());
            response.addProperty("voucher_type", ledgertranxdetails.get(i).getTransactionType().getId() == 17 ? "Sales Invoice" : ledgertranxdetails.get(i).getTransactionType().getTransactionName());
            /***** Sales Invoice ****/
            if (ledgertranxdetails.get(i).getTransactionType().getId() == 17) {
                /*** No 1 : Sales Invoice **/
                TranxSalesInvoice mInvoice = tranxSalesInvoiceRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
//                response.addProperty("particulars", mInvoice.getFeesAct().getAssociatesName());
                response.addProperty("voucher_no", mInvoice.getSalesInvoiceNo());
                response.addProperty("voucher_id", mInvoice.getId());
                TranxSalesInvoiceDetails tranxSalesInvoiceDetails = getSalesInvoiceDetailsRepository.findTop1ByTranxSalesInvoiceIdAndStatusOrderByIdAsc(mInvoice.getId(), true);
                response.addProperty("particulars", tranxSalesInvoiceDetails != null ? tranxSalesInvoiceDetails.getFeeHead().getFeeHeadName() : "NA");
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarration());
            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 10) {
                /*** No 2: Journal Master **/
                TranxJournalMaster mInvoice = tranxJournalMasterRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                List<TranxJournalDetails> mInvoiceDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                            mInvoice.getId(), users.getOutlet().getId(), users.getBranch().getId(), true, "DR");
                response.addProperty("particulars", mInvoiceDetails.get(0).getLedgerMaster().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getJournalNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarrations());
                response.addProperty("feeReceiptNo", mInvoice.getFeeReceiptNo());
            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 9) {
                /*** No 3 : Contra Master ***/
                TranxContraMaster mInvoice = tranxContraMasterRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                TranxContraDetails mInvoiceDetails = tranxContraDetailsRepository.findByTranxContraMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(mInvoice.getId(), users.getOutlet().getId(), users.getBranch().getId(), true, "DR");
                response.addProperty("particulars", mInvoiceDetails.getLedgerMaster().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getContraNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarrations());

            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 6) {
                /*** No 4 :Payment Master ***/
                TranxPaymentMaster mInvoice = tranxPaymentMasterRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                TranxPaymentPerticularsDetails mInvoiceDetails = tranxPaymentPerticularsDetailsRepository.findBytranxPaymentMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(mInvoice.getId(), users.getOutlet().getId(), users.getBranch().getId(), true, "DR");
                response.addProperty("particulars", mInvoiceDetails.getLedgerMaster().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getPaymentNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarrations());

            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 5) {
                /*** No 5: Receipt Master  ***/
                TranxReceiptMaster mInvoice = tranxReceiptMasterRepositoty.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                if(mInvoice!=null) {
                    List<TranxReceiptPerticularsDetails> mInvoiceDetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(mInvoice.getId(), users.getOutlet().getId(), users.getBranch().getId(), true, "DR");
                    for (TranxReceiptPerticularsDetails tranxReceiptPerticularsDetails : mInvoiceDetails) {
                        response.addProperty("particulars", tranxReceiptPerticularsDetails.getLedgerMaster().getLedgerName());
                        response.addProperty("voucher_no", mInvoice.getReceiptNo());
                        response.addProperty("voucher_id", mInvoice.getId());
                        response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                        response.addProperty("narration", mInvoice.getNarrations());
                    }
                }else {
                    System.out.println("receipt Not found");
                }
            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 1) {
                /*** No 6: Purchase Master  ***/
                TranxPurInvoice mInvoice = tranxPurInvoiceRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                response.addProperty("particulars", mInvoice.getPurchaseAccountLedger().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getVendorInvoiceNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarration());

            } else if (ledgertranxdetails.get(i).getTransactionType().getId() == 2) {
                /*** No 7: Purchase  Return   ***/
                TranxPurReturnInvoice mInvoice = tranxPurReturnsRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);
                response.addProperty("particulars", mInvoice.getPurchaseAccountLedger().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getPurRtnNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarration());

            }
            if (ledgertranxdetails.get(i).getTransactionType().getId() == 4) {
                /*** No 8: Sales  Return Master  ***/
                TranxSalesReturnInvoice mInvoice = tranxSalesReturnRepository.findByIdAndStatus(ledgertranxdetails.get(i).getTransactionId(), true);

                response.addProperty("particulars", mInvoice.getFeesAccount().getLedgerName());
                response.addProperty("voucher_no", mInvoice.getSalesReturnNo());
                response.addProperty("voucher_id", mInvoice.getId());
                response.addProperty("transaction_id", ledgertranxdetails.get(i).getTransactionType().getId());
                response.addProperty("narration", mInvoice.getNarration());

            }
//            Double drAmount=ledgerTransactionPostingsRepository.findByLedgerTypeAndStatus("DR",true);
//            Double crAmount=ledgerTransactionPostingsRepository.findByLedgerTypeAndStatus("CR",true);
            response.addProperty("debit", Math.abs(0));
            response.addProperty("credit", Math.abs(0));
            result.add(response);
        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.addProperty("opening", Math.abs(opening));
        output.add("data", result);
        return output;
    }

    /*** Get Ledger voucher Details By Transaction Id And Voucher Id ***/
    public JsonObject getLedgerVoucherDetails(HttpServletRequest request) {
        JsonObject result = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long tranxId = Long.parseLong(request.getParameter("tranx_id"));
        Long voucherId = Long.parseLong(request.getParameter("voucher_id"));
        List<TranxSalesInvoice> salesranxdetails = new ArrayList<>();
        List<TranxJournalDetails> journaltranxdetails = new ArrayList<>(); /** For Journal Details**/
        List<TranxJournalDetails> drjournaltranxdetails = new ArrayList<>(); /** For Journal Details**/
        List<TranxJournalDetails> crjournaltranxdetails = new ArrayList<>(); /** For Journal Details**/
        List<TranxContraDetails> contraranxdetails = new ArrayList<>();    /** For Contra Details**/
        List<TranxPaymentPerticularsDetails> paymentranxdetails = new ArrayList<>();  /** For Payment Details**/
        List<TranxReceiptPerticularsDetails> crreceipttranxdetails = new ArrayList<>(); /** For Receipt Details**/
        List<TranxReceiptPerticularsDetails> drreceipttranxdetails = new ArrayList<>(); /** For Receipt Details**/

        List<TranxPurInvoiceDetails> purinvoicetranxdetails = new ArrayList<>(); /** For Purchase Invoice Details**/
        List<TranxPurReturnInvoiceDetails> purRetuninvoicetranxdetails = new ArrayList<>(); /** For Purchase Retun Invoice Details**/
        List<TranxSalesReturnInvoice> salesRetuninvoicetranxdetails = new ArrayList<>(); /** For Sales Return Invoice Details**/
        LocalDate now = LocalDate.now();
        JsonObject mObject = new JsonObject();
        if (tranxId == 17) {
            // JsonObject response = new JsonObject();
            TranxSalesInvoice mInvoice = null;
            /***** No 1 :Sales Invoice ****/
            if (users.getBranch() != null)
                mInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndBranchIdAndStatus(
                        voucherId, users.getOutlet().getId(), users.getBranch().getId(), true);
            else {
                mInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndStatus(
                        voucherId, users.getOutlet().getId(), true);
            }
            JsonArray mSalesArray = new JsonArray();
            JsonObject mResponse = new JsonObject();
            mResponse.addProperty("debit", mInvoice.getTotalAmount());
            mResponse.addProperty("TranxType", "DR");
            mResponse.addProperty("particular", mInvoice.getSundryDebtors().getLedgerName());
            mResponse.addProperty("narration", "");
            mSalesArray.add(mResponse);
            List<TranxSalesInvoiceDetails> salesInvoiceDetails = salesInvoiceDetailsRepository.findByTranxSalesInvoiceIdAndStatus(mInvoice.getId(), true);
            for (TranxSalesInvoiceDetails mDetails : salesInvoiceDetails) {
                JsonObject response = new JsonObject();
                response.addProperty("credit", mDetails.getAmount());
                response.addProperty("TranxType", "CR");
                response.addProperty("particular", mDetails.getFeeHead().getFeeHeadName());
                response.addProperty("narration", "");
                mSalesArray.add(response);
            }
            mObject.add("particular_list", mSalesArray);
        } else if (tranxId == 10) {
            /***** No 2 :Journal Details ****/
            if (users.getBranch() != null) {
                drjournaltranxdetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                        voucherId, users.getOutlet().getId(), users.getBranch().getId(), true, "DR");

                crjournaltranxdetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(
                        voucherId, users.getOutlet().getId(), users.getBranch().getId(), true, "CR");
            } else {
                drjournaltranxdetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(
                        voucherId, users.getOutlet().getId(), true, "DR");
                crjournaltranxdetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(
                        voucherId, users.getOutlet().getId(), true, "CR");
            }

            JsonArray mJournalArray = new JsonArray();


            for (TranxJournalDetails journalDetails1 : drjournaltranxdetails) {
                JsonObject jsonObject = new JsonObject();
                if (journalDetails1.getType().equalsIgnoreCase("CR")) {
                    jsonObject.addProperty("credit", journalDetails1.getPaidAmount());
                } else {
                    jsonObject.addProperty("debit", journalDetails1.getPaidAmount());
                }
                jsonObject.addProperty("TranxType", journalDetails1.getType());
                jsonObject.addProperty("particular", journalDetails1.getLedgerMaster().getLedgerName());
                jsonObject.addProperty("narration", journalDetails1.getTranxJournalMaster().getNarrations());
                mJournalArray.add(jsonObject);
            }
            for (TranxJournalDetails tranxJournalDetails : crjournaltranxdetails) {
                JsonObject jsonObject1 = new JsonObject();
                if (tranxJournalDetails.getType().equalsIgnoreCase("CR")) {
                    jsonObject1.addProperty("credit", tranxJournalDetails.getPaidAmount());
                } else {
                    jsonObject1.addProperty("debit", tranxJournalDetails.getPaidAmount());
                }
                jsonObject1.addProperty("TranxType", tranxJournalDetails.getType());
                jsonObject1.addProperty("particular", tranxJournalDetails.getLedgerMaster().getLedgerName());
                jsonObject1.addProperty("narration", tranxJournalDetails.getTranxJournalMaster().getNarrations());
                mJournalArray.add(jsonObject1);
            }
            /*for (TranxJournalDetails mDetails : journaltranxdetails) {
                JsonObject response = new JsonObject();
                if (mDetails.getType().equalsIgnoreCase("DR")) {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("debit", mDetails.getPaidAmount());
                } else {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("credit", mDetails.getPaidAmount());
                }
                response.addProperty("particular", mDetails.getLedgerMaster().getLedgerName());
                response.addProperty("narration", mDetails.getTranxJournalMaster().getNarrations());
                mJournalArray.add(response);
            }*/
            mObject.add("particular_list", mJournalArray);
        } else if (tranxId == 9) {
            /***** No 3 :Contra Details ****/
            if (users.getBranch() != null) {
                contraranxdetails = tranxContraDetailsRepository.findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(voucherId, users.getOutlet().getId(), users.getBranch().getId(), true);
            } else {
                contraranxdetails = tranxContraDetailsRepository.findByLedgerMasterIdAndOutletIdAndStatus(voucherId, users.getOutlet().getId(), true);
            }
            JsonArray mcontraArray = new JsonArray();
            for (TranxContraDetails mDetails : contraranxdetails) {
                JsonObject response = new JsonObject();
                if (mDetails.getType().equalsIgnoreCase("DR")) {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("debit", mDetails.getPaidAmount());
                } else {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("credit", mDetails.getPaidAmount());
                }

                response.addProperty("particulars", mDetails.getLedgerMaster().getLedgerName());
                response.addProperty("narration", mDetails.getTranxContraMaster().getNarrations());
                mcontraArray.add(response);
            }
        } else if (tranxId == 6) {
            /***** No 4 :Payment Details ****/
            if (users.getBranch() != null) {
                paymentranxdetails = tranxPaymentPerticularsDetailsRepository.
                        findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(voucherId,
                                users.getOutlet().getId(), users.getBranch().getId(), true);
            } else {
                paymentranxdetails = tranxPaymentPerticularsDetailsRepository.findByLedgerMasterIdAndOutletIdAndStatus
                        (voucherId, users.getOutlet().getId(), true);
            }
            JsonArray mPaymentArray = new JsonArray();
            for (TranxPaymentPerticularsDetails mDetails : paymentranxdetails) {
                JsonObject response = new JsonObject();
                if (mDetails.getType().equalsIgnoreCase("DR")) {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("debit", mDetails.getPaidAmt());
                } else {
                    response.addProperty("TranxType", mDetails.getType());
                    response.addProperty("credit", mDetails.getPaidAmt());
                }
                response.addProperty("particulars", mDetails.getLedgerMaster().getLedgerName());
                response.addProperty("narration", mDetails.getTranxPaymentMaster().getNarrations());
                mPaymentArray.add(response);
            }

        } else if (tranxId == 5) {
            /***** No 5 :Receipt Details ****/


            if (users.getBranch() != null) {
                crreceipttranxdetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase
                        (voucherId, users.getOutlet().getId(), users.getBranch().getId(), true,"CR");
                drreceipttranxdetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase
                        (voucherId, users.getOutlet().getId(), users.getBranch().getId(), true,"DR");

            } else {
                crreceipttranxdetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(voucherId, users.getOutlet().getId(), true,"CR");
                drreceipttranxdetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(voucherId, users.getOutlet().getId(), true,"DR");

            }
            JsonArray mReceiptArray = new JsonArray();
            for (TranxReceiptPerticularsDetails mDetails : crreceipttranxdetails) {
                JsonObject response = new JsonObject();

                if (mDetails.getType().equalsIgnoreCase("CR")) {
                    response.addProperty("credit", mDetails.getPaidAmt());
                } else {
                    response.addProperty("debit", mDetails.getPaidAmt());
                }
                response.addProperty("TranxType", mDetails.getType());

                response.addProperty("particular", mDetails.getLedgerMaster().getLedgerName());
                response.addProperty("narration", mDetails.getTranxReceiptMaster().getNarrations());
                mReceiptArray.add(response);
            }

            for (TranxReceiptPerticularsDetails mDetails : drreceipttranxdetails) {
                JsonObject response = new JsonObject();
                if (mDetails.getType().equalsIgnoreCase("CR")) {
                    response.addProperty("credit", mDetails.getPaidAmt());
                } else {
                    response.addProperty("debit", mDetails.getPaidAmt());
                }
                response.addProperty("TranxType", mDetails.getType());

                response.addProperty("particular", mDetails.getLedgerMaster().getLedgerName());
                response.addProperty("narration", mDetails.getTranxReceiptMaster().getNarrations());
                mReceiptArray.add(response);
            }
            mObject.add("particular_list", mReceiptArray);

        } else if (tranxId == 1) {
            /***** No 6 :Purchase Invoice Details ****/
//            purinvoicetranxdetails=tranxPurInvoiceRepository.findByLedgerMasterIdAndOutletIdAndBranchIdAndStatus(voucherId, users.getOutlet().getId(), users.getBranch().getId(), true);
//            JsonArray mPurchaseInvoiceArray=new JsonArray();
//            for(TranxPurInvoiceDetails mDetails:purinvoicetranxdetails){
//                JsonObject response=new JsonObject();
//                if (mDetails.getType().equalsIgnoreCase("CR")) {
//                    response.addProperty("credit", mDetails.getPaidAmt());
//                }
//                else {
//                    response.addProperty("debit", mDetails.getPaidAmt());
//                }
//                response.addProperty("particulars", mDetails.getLedgerMaster().getLedgerName());
//                response.addProperty("narration", mDetails.getTranxReceiptMaster().getNarrations());
//                mPurchaseInvoiceArray.add(response);
//            }

        } else if (tranxId == 2) {
            /***** No 6 :Purchase Return Details ****/


        }
        JsonObject output = new JsonObject();
        output.addProperty("message", "success");
        output.addProperty("responseStatus", HttpStatus.OK.value());
        output.add("data", mObject);
        return output;
    }

    public JsonObject getSalesInvoiceDetails(HttpServletRequest request) {
        JsonObject finalObject = new JsonObject();
        JsonArray mSalesArray = new JsonArray();
        JsonObject mResponse = new JsonObject();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Long id = Long.parseLong(request.getParameter("id"));
        TranxSalesInvoice mInvoice = null;
        if (users.getBranch() != null)
            mInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndBranchIdAndStatus(
                    id, users.getOutlet().getId(), users.getBranch().getId(), true);
        else {
            mInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndStatus(
                    id, users.getOutlet().getId(), true);
        }
        List<TranxSalesInvoiceDetails> salesInvoiceDetails = salesInvoiceDetailsRepository.findByTranxSalesInvoiceIdAndStatus(id, true);
        mResponse.addProperty("debit", mInvoice.getTotalAmount());
        mResponse.addProperty("TranxType", "DR");
        mResponse.addProperty("particular", mInvoice.getSundryDebtors().getLedgerName());
        mResponse.addProperty("narration", "");
        mSalesArray.add(mResponse);
        for (TranxSalesInvoiceDetails mDetails : salesInvoiceDetails) {
            JsonObject response = new JsonObject();
            response.addProperty("credit", mDetails.getAmount());
            response.addProperty("TranxType", "CR");
            response.addProperty("particular", mDetails.getFeeHead().getFeeHeadName());
            response.addProperty("narration", "");
            mSalesArray.add(response);
        }
        finalObject.addProperty("message", "success");
        finalObject.addProperty("responseStatus", HttpStatus.OK.value());
        finalObject.add("data", mSalesArray);
        return finalObject;
    }

    public JsonObject getBankDetails(HttpServletRequest request) {

        Users users=jwtRequestFilter.getUserDataFromToken(request.getHeader("authorization").substring(7));
        JsonObject result=new JsonObject();
        JsonArray jsonArray=new JsonArray();
//       List<LedgerMaster> ledgerMaster=ledgerMasterRepository.findBySlugNameAndOutletIdAndBranchIdAndStatus("bank_account", users.getOutlet().getId(), users.getBranch().getId(), true);
//       if(ledgerMaster.size()>0)
//       {
//           for(LedgerMaster ledgerMaster1:ledgerMaster) {
//               JsonObject response = new JsonObject();
//               response.addProperty("ledgerName", ledgerMaster1.getLedgerName());
//               response.addProperty("ledgerId", ledgerMaster1.getId());
//               response.addProperty("bankName", ledgerMaster1.getBankName());
//               response.addProperty("uniqueCode", ledgerMaster1.getUniqueCode());
//               response.addProperty("principle", ledgerMaster1.getPrinciples().getPrincipleName());
//               response.addProperty("ifsc", ledgerMaster1.getIfsc());
//               response.addProperty("accountNo", ledgerMaster1.getAccountNumber());
//               response.addProperty("bankBranch", ledgerMaster1.getBankBranch());
//               jsonArray.add(response);
//           }
//           result.addProperty("responseStatus",HttpStatus.OK.value());
//           result.add("responseObject", jsonArray);
//       }
        JsonObject response = new JsonObject();
        LedgerMaster ledgerMaster=ledgerMasterRepository.findByLedgerNameIgnoreCaseAndOutletIdAndBranchIdAndStatus("cosmos bank",users.getOutlet().getId(),users.getBranch().getId(),true);
        if(ledgerMaster!=null)

        {


            response.addProperty("ledgerName", ledgerMaster.getLedgerName());
               response.addProperty("ledgerId", ledgerMaster.getId());
               response.addProperty("bankName", ledgerMaster.getBankName());
               response.addProperty("uniqueCode", ledgerMaster.getUniqueCode());
               response.addProperty("principle", ledgerMaster.getPrinciples().getPrincipleName());
               response.addProperty("ifsc", ledgerMaster.getIfsc());
               response.addProperty("accountNo", ledgerMaster.getAccountNumber());
               response.addProperty("bankBranch", ledgerMaster.getBankBranch());
               jsonArray.add(response);
        }
        result.add("responseObject",jsonArray);
        result.addProperty("responseStatus",HttpStatus.OK.value());
       return  result;
    }

    public Object getLedgerTransactionsDetails(HttpServletRequest request) {
        JsonArray result = new JsonArray();
        JsonObject finalResponse = new JsonObject();

        JsonArray response = new JsonArray();
        List<LedgerTransactionPostings> mlist = new ArrayList<>();
        Map<String, String[]> paramMap = request.getParameterMap();
        try {
            Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
            Long ledger_master_id = Long.valueOf(request.getParameter("id"));
            String startDate = "";
            String endDate = "";
            LocalDate endDatep = null;
            LocalDate startDatep = null;
            Long branchId = null;
            FiscalYear fiscalYear = null;
            Boolean flag = false;
            LedgerMaster ledgerMaster = ledgerMasterRepository.findByIdAndStatus(ledger_master_id, true);
            if (paramMap.containsKey("startDate") && paramMap.containsKey("endDate")) {
                startDate = request.getParameter("startDate");
                startDatep = LocalDate.parse(startDate);
                endDate = request.getParameter("endDate");
                endDatep = LocalDate.parse(endDate);
                flag = true;
            } else {
                List<Object[]> list = new ArrayList<>();
                fiscalYear = generateFiscalYear.getFiscalYear(LocalDate.now());
                flag = false;
            }
            if (flag == true) {
                if (users.getBranch() != null) {
                    mlist = ledgerTransactionPostingsRepository.findByDetailsBetweenDates(users.getOutlet().getId(), users.getBranch().getId(), true, ledger_master_id, startDatep, endDatep);

                } else {
                    mlist = ledgerTransactionPostingsRepository.findByDetails(users.getOutlet().getId(), true, ledger_master_id, startDatep, endDatep);
                }
            } else {
                if (users.getBranch() != null) {
                    mlist = ledgerTransactionPostingsRepository.findByDetailsBranch(users.getOutlet().getId(), users.getBranch().getId(), true, ledger_master_id);

//                branchId = users.getBranch().getId();
                } else {
                    mlist = ledgerTransactionPostingsRepository.findByDetailsFisc(users.getOutlet().getId(), true, ledger_master_id);
                }
            }
            JsonArray innerArr = new JsonArray();
            innerArr = getCommonDetails(mlist, users);
//            res.addProperty("d_start_date", startDate.toString());
//            res.addProperty("d_end_date", endDate.toString());
            Double openingStock = 0.0;
            openingStock = ledgerCommonPostings.getOpeningStock(ledger_master_id,
                    users.getOutlet().getId(), branchId, startDatep, endDatep, flag, fiscalYear);
//
            finalResponse.addProperty("crdrType", ledgerMaster.getOpeningBalType().toLowerCase());
            finalResponse.add("response", innerArr);
            finalResponse.addProperty("opening_stock", Math.abs(openingStock));
            //finalResponse.add("response", response);
            finalResponse.addProperty("responseStatus", HttpStatus.OK.value());
        } catch (Exception e) {
            e.printStackTrace();
            finalResponse.addProperty("message", "Failed To Load Data");
            finalResponse.addProperty("responseStatus", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return finalResponse;
    }
    public JsonArray getCommonDetails(List<LedgerTransactionPostings> mlist, Users users) {
        JsonArray innerArr = new JsonArray();
        TranxSalesInvoice tSalesInvoice = null;

        for (LedgerTransactionPostings ledgerTransactionDetails : mlist) {
            if (!ledgerTransactionDetails.getOperations().equalsIgnoreCase("Delete")) {
                JsonObject inside = new JsonObject();
                inside.addProperty("transaction_date", ledgerTransactionDetails.getTransactionDate().toString());


                inside.addProperty("invoice_no", ledgerTransactionDetails.getInvoiceNo());
                inside.addProperty("invoice_id", ledgerTransactionDetails.getTransactionId());// Invoice Id : 1 or 2
                Long tranx_type = ledgerTransactionDetails.getTransactionType().getId(); // Transactions Id : 1:Pur 3: Sales
                inside.addProperty("transaction_type", tranx_type);
                if (tranx_type == 1) {
                    TranxPurInvoice tranxPurInvoice;
                    if (users.getBranch() != null) {
                        tranxPurInvoice = tranxPurInvoiceRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxPurInvoice = tranxPurInvoiceRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxPurInvoice != null) {
                        inside.addProperty("particulars", tranxPurInvoice.getSundryCreditors().getLedgerName());
                        inside.addProperty("id", tranxPurInvoice.getId());
                    }
                } else if (tranx_type == 2) {
                    TranxPurReturnInvoice tranxPurReturnInvoice;
                    if (users.getBranch() != null) {
                        tranxPurReturnInvoice = tranxPurReturnsRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxPurReturnInvoice = tranxPurReturnsRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxPurReturnInvoice != null) {
                        inside.addProperty("particulars", tranxPurReturnInvoice.getSundryCreditors().getLedgerName());
                        inside.addProperty("id", tranxPurReturnInvoice.getId());
                    }
                } else if (tranx_type == 17) {
                    TranxSalesInvoice tranxSalesInvoice;
                    if (users.getBranch() != null) {
                        tranxSalesInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxSalesInvoice = tranxSalesInvoiceRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxSalesInvoice != null) {
                        inside.addProperty("particulars", tranxSalesInvoice.getSundryDebtors().getLedgerName());
                        inside.addProperty("id", tranxSalesInvoice.getId());
                    }
                } else if (tranx_type == 4) {
                    TranxSalesReturnInvoice tranxSalesReturnInvoice;
                    if (users.getBranch() != null) {
                        tranxSalesReturnInvoice = tranxSalesReturnRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxSalesReturnInvoice = tranxSalesReturnRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxSalesReturnInvoice != null) {
                        inside.addProperty("particulars", tranxSalesReturnInvoice.getSundryDebtors().getLedgerName());
                        inside.addProperty("id", tranxSalesReturnInvoice.getId());
                    }
                } else if (tranx_type == 5) {
                    List<TranxReceiptPerticularsDetails> tranxReceiptPerticularsDetails = new ArrayList<>();
                    if (users.getBranch() != null) {
                        tranxReceiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxReceiptPerticularsDetails = tranxReceiptPerticularsDetailsRepository.findByTranxReceiptMasterIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxReceiptPerticularsDetails != null && tranxReceiptPerticularsDetails.size()>0) {
                        inside.addProperty("particulars", tranxReceiptPerticularsDetails.get(0).getLedgerMaster().getLedgerName());
                        inside.addProperty("id", tranxReceiptPerticularsDetails.get(0).getId());
                        inside.addProperty("fee_receipt_no",tranxReceiptPerticularsDetails.get(0).getTranxReceiptMaster().getReceiptNo());
                    }
                }
                else if (tranx_type == 10) {
                    List<TranxJournalDetails> tranxJournalDetails = new ArrayList<>();
                    if (users.getBranch() != null) {
                        tranxJournalDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndBranchIdAndStatusAndTypeIgnoreCase(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true, "DR");
                    } else {
                        tranxJournalDetails = tranxJournalDetailsRepository.findByTranxJournalMasterIdAndOutletIdAndStatusAndTypeIgnoreCase(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true, "DR");
                    }
                    if (tranxJournalDetails != null && tranxJournalDetails.size() > 0) {
                        inside.addProperty("particulars", tranxJournalDetails.get(0).getLedgerMaster().getLedgerName());
                        inside.addProperty("id", tranxJournalDetails.get(0).getId());
                        inside.addProperty("fee_receipt_no", tranxJournalDetails.get(0).getTranxJournalMaster().getFeeReceiptNo());
                    }

                }


                else if (tranx_type == 6) {
                    TranxPaymentPerticularsDetails tranxPaymentPerticulars;
                    if (users.getBranch() != null) {
                        tranxPaymentPerticulars = tranxPaymentPerticularsDetailsRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxPaymentPerticulars = tranxPaymentPerticularsDetailsRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxPaymentPerticulars != null) {
                        inside.addProperty("particulars", tranxPaymentPerticulars.getLedgerMaster().getLedgerName());
                        inside.addProperty("id", tranxPaymentPerticulars.getId());
                    }
//                } else if (tranx_type == 7) {
//                    TranxDebitNoteDetails tranxDebitNoteDetails;
//                    if (users.getBranch() != null) {
//                        tranxDebitNoteDetails = tranxDebitNoteDetailsRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
//                    } else {
//                        tranxDebitNoteDetails = tranxDebitNoteDetailsRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
//                    }
//                    if (tranxDebitNoteDetails != null) {
//                        inside.addProperty("particulars", tranxDebitNoteDetails.getLedgerMaster().getLedgerName());
//                        inside.addProperty("id", tranxDebitNoteDetails.getId());
//                    }
//                } else if (tranx_type == 8) {
//                    TranxCreditNoteDetails tranxCreditNoteDetails;
//                    if (users.getBranch() != null) {
//                        tranxCreditNoteDetails = tranxCreditNoteDetailsRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
//                    } else {
//                        tranxCreditNoteDetails = tranxCreditNoteDetailsRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
//                    }
//                    if (tranxCreditNoteDetails != null) {
//                        inside.addProperty("particulars", tranxCreditNoteDetails.getLedgerMaster().getLedgerName());
//                        inside.addProperty("id", tranxCreditNoteDetails.getId());
//                    }
                } else if (tranx_type == 9) {
                    TranxContraDetails tranxContraDetails;
                    if (users.getBranch() != null) {
                        tranxContraDetails = tranxContraDetailsRepository.findByIdAndOutletIdAndBranchIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), users.getBranch().getId(), true);
                    } else {
                        tranxContraDetails = tranxContraDetailsRepository.findByIdAndOutletIdAndStatus(ledgerTransactionDetails.getTransactionId(), users.getOutlet().getId(), true);
                    }
                    if (tranxContraDetails != null) {
                        inside.addProperty("particulars", tranxContraDetails.getLedgerMaster().getLedgerName());
                        inside.addProperty("id", tranxContraDetails.getId());
                    }
                }
                inside.addProperty("voucher_type", ledgerTransactionDetails.getTransactionType().getTransactionName());
                if (ledgerTransactionDetails.getLedgerType().equalsIgnoreCase("CR")) {
                    inside.addProperty("credit", ledgerTransactionDetails.getAmount());
                    inside.addProperty("debit", 0);

                } else {
                    inside.addProperty("credit", 0);
                    inside.addProperty("debit", ledgerTransactionDetails.getAmount());
                }
                innerArr.add(inside);
            }
        }
        return innerArr;
    }

}

