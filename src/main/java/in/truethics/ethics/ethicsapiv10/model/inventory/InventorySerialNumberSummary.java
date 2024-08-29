package in.truethics.ethics.ethicsapiv10.model.inventory;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import in.truethics.ethics.ethicsapiv10.model.master.TransactionTypeMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "inventory_serial_number_summary_tbl")
public class InventorySerialNumberSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonManagedReference
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "outlet_id")
    @JsonManagedReference
    private Outlet outlet;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonManagedReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    @JsonManagedReference
    private TransactionTypeMaster transactionType; // Purchase, sale, Purchase Return or Sale Return

    @ManyToOne
    @JoinColumn(name = "packaging_id")
    @JsonManagedReference
    private PackingMaster packingMaster;

    private Boolean status;
    private String serialNo;
    private String tranxAction; //in or out


}
