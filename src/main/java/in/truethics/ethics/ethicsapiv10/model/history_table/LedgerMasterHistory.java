package in.truethics.ethics.ethicsapiv10.model.history_table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ledger_master_history_tbl")
public class LedgerMasterHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ledgerMasterId;
    private Long studentId;
    private String ledgerName;
    private String ledgerCode;
    private String uniqueCode;
    private String mailingName;
    private String openingBalType;
    private Double openingBal;
    private String address;
    private Long pincode;
    private String email;
    private Long mobile;
    private Boolean taxable;
    private String gstin;
    private String stateCode;
    private Long registrationType;
    private LocalDate dateOfRegistration;
    private String pancard;
    private String bankName;
    private String accountNumber;
    private String ifsc;
    private String bankBranch;
    private String taxType;
    private String slugName;
    private Long createdBy;
    private String operationType;

    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;
    private String underPrefix;
    private Boolean isDeleted;
    private Boolean isDefaultLedger;
    private Boolean isPrivate;
    /* pune visit new changes */
    private Integer creditDays;
    private String applicableFrom; //from billDate or deliveryDate
    private String foodLicenseNo;
    private Boolean tds;
    private LocalDate tdsApplicableDate;
    private Boolean tcs;
    private LocalDate tcsApplicableDate;
    private String district;
    /* End .... */
    private String principlesName;
    private String principlesGroupName;
    private String foundationsName;
    private Long  branchId;
    private Long outletId;
    private String balancingMethodName;
    private String associateGroupName;

}
