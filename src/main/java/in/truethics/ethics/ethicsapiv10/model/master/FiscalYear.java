package in.truethics.ethics.ethicsapiv10.model.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import in.truethics.ethics.ethicsapiv10.model.inventory.ProductOpeningStocks;
import in.truethics.ethics.ethicsapiv10.model.sales.TranxSalesInvoiceDetails;
import in.truethics.ethics.ethicsapiv10.model.tranx.purchase.TranxPurInvoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fiscal_year_tbl")
public class FiscalYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int monthStart;
    private int monthEnd;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private String fiscalYear;
    private LocalDate fiscalYearEndDate;
    private String abbreviation;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean status;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxPurInvoice> tranxPurInvoices;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProductOpeningStocks> productOpeningStocks;


    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<TranxSalesInvoiceDetails> tranxSalesInvoiceDetails;
}
