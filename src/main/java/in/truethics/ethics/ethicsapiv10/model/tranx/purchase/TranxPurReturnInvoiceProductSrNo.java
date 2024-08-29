package in.truethics.ethics.ethicsapiv10.model.tranx.purchase;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.inventory.Product;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.master.TransactionTypeMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tranx_pur_return_invoice_product_sr_no_tbl")
public class TranxPurReturnInvoiceProductSrNo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "pur_return_invoice_id")
    @JsonManagedReference
    private TranxPurReturnInvoice purReturnInvoice;

    @ManyToOne
    @JoinColumn(name = "pur_return_invoice_details_id")
    @JsonManagedReference
    private TranxPurReturnInvoiceDetails tranxPurReturnInvoiceDetails;

    @ManyToOne
    @JoinColumn(name = "transaction_type_master_id")
    @JsonManagedReference
    private TransactionTypeMaster transactionTypeMaster;

    @ManyToOne
    @JoinColumn(name = "tranx_pur_invoice_id")
    @JsonManagedReference
    private TranxPurInvoice tranxPurInvoice;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    private String serialNo;
    private LocalDateTime purReturnCreatedAt;
    private String transactionStatus; //purchase or sales or counter sales
    private String operations;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Boolean status;
}
