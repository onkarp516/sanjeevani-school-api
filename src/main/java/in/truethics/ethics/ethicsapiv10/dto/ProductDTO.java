package in.truethics.ethics.ethicsapiv10.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/* get Product by Id */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private String productCode;
    private String description;
    private Boolean isWarrantyApplicable;
    private Integer warrantyDays;
    private Boolean isSerialNumber;
    private Boolean isNegativeStocks;
    private Boolean isInventory;
    private String alias;
    private Long hsnId;
    private Long groupId;
    private Long subGroupId;
    private Long categoryId;
    private Long subCategoryId;
    private Long taxMasterId;
    private String applicableDate;
    private List<ProductUnitDTO> units = new ArrayList<>();
}
