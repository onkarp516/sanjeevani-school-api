package in.truethics.ethics.ethicsapiv10.service.master_service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import in.truethics.ethics.ethicsapiv10.dto.*;
import in.truethics.ethics.ethicsapiv10.fileConfig.FileStorageService;
import in.truethics.ethics.ethicsapiv10.model.inventory.*;
import in.truethics.ethics.ethicsapiv10.model.master.*;
import in.truethics.ethics.ethicsapiv10.model.user.Users;
import in.truethics.ethics.ethicsapiv10.repository.inventory_repository.*;
import in.truethics.ethics.ethicsapiv10.repository.master_repository.*;
import in.truethics.ethics.ethicsapiv10.response.ResponseMessage;
import in.truethics.ethics.ethicsapiv10.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private static final Logger productLogger = LoggerFactory.getLogger(ProductService.class);
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtTokenUtil jwtRequestFilter;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SubgroupRepository subgroupRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcategoryRepository subcategoryRepository;
    @Autowired
    private ProductHsnRepository productHsnRepository;
    @Autowired
    private UnitsRepository unitsRepository;
    @Autowired
    private ProductUnitRepository productUnitRepository;
    @Autowired
    private TaxMasterRepository taxMasterRepository;
    @Autowired
    private PackingMasterRepository packingMasterRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ProductImagesMasterRepository imagesMasterRepository;

    public Object createProduct(MultipartHttpServletRequest request) {
        Product product = new Product();
        Map<String, String[]> paramMap = request.getParameterMap();
        ResponseMessage responseObject = new ResponseMessage();
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Branch branch = null;
        if (users.getBranch() != null)
            product.setBranch(branch);
        Outlet outlet = users.getOutlet();
        product.setOutlet(outlet);
        product.setProductName(request.getParameter("productName"));
        if (paramMap.containsKey("description"))
            product.setDescription(request.getParameter("description"));
        product.setStatus(true);
        if (paramMap.containsKey("alias")) {
            product.setAlias(request.getParameter("alias"));
        } else
            product.setAlias("NA");

        Calendar calendar = Calendar.getInstance();
        //Returns current time in millis
        long timeMilli2 = calendar.getTimeInMillis();
        product.setProductCode("" + timeMilli2);

        if (request.getParameter("groupId") != null) {
            Group group = groupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("groupId")),
                    true);
            product.setGroup(group);
        }
        if (paramMap.containsKey("subgroupId")) {
            Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subgroupId")),
                    true);
            product.setSubgroup(subgroup);
        } else {
            product.setSubgroup(null);
        }
        if (paramMap.containsKey("categoryId")) {
            Category category = categoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("categoryId")),
                    true);
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        if (paramMap.containsKey("subcategoryId")) {
            Subcategory subcategory = subcategoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subcategoryId")),
                    true);
            product.setSubcategory(subcategory);
        } else {
            product.setSubcategory(null);
        }
        if (!request.getParameter("hsnId").equalsIgnoreCase("") && request.getParameter("hsnId") != null) {
            ProductHsn productHsn = productHsnRepository.findByIdAndStatus(Long.parseLong(request.getParameter("hsnId")), true);
            product.setProductHsn(productHsn);
        }
        if (!request.getParameter("taxMasterId").equalsIgnoreCase("") && request.getParameter("taxMasterId") != null) {
            TaxMaster taxMaster = taxMasterRepository.findByIdAndStatus(Long.parseLong(request.getParameter("taxMasterId")), true);
            product.setTaxMaster(taxMaster);
        }
        product.setIsSerialNumber(Boolean.parseBoolean(request.getParameter("isSerialNo")));
        product.setIsInventory(Boolean.parseBoolean(request.getParameter("isInventory")));
        if (Boolean.parseBoolean(request.getParameter("isWarranty"))) {
            product.setWarrantyDays(Integer.parseInt(request.getParameter("nodays")));
        }
        product.setIsWarrantyApplicable(Boolean.parseBoolean(request.getParameter("isWarranty")));
        product.setIsNegativeStocks(Boolean.parseBoolean(request.getParameter("isNegativeStocks")));
        product.setIsPackings(Boolean.parseBoolean(request.getParameter("is_packaging")));
        product.setPackingCount(Integer.parseInt(request.getParameter("no_packagings")));
        product.setCreatedBy(users.getId());
        try {
            Product newProduct = productRepository.save(product);
            JsonParser parser = new JsonParser();
            List<ProductUnitPacking> unitPackingList = new ArrayList<>();
            List<PackingMaster> productPackings = new ArrayList<>();
            String jsonStr = request.getParameter("mstPackaging");
            JsonElement tradeElement = parser.parse(jsonStr);
            JsonArray array = tradeElement.getAsJsonArray();
            for (JsonElement mList : array) {
                JsonObject object = mList.getAsJsonObject();
                PackingMaster packingMaster = null;
                if (!object.get("package_id").getAsString().equalsIgnoreCase("")) {
                    packingMaster = packingMasterRepository.findByIdAndStatus(object.get("package_id").getAsLong(), true);
                    if (packingMaster != null) {
                        productPackings.add(packingMaster);
                    }
                }
                JsonArray unitsArray = object.get("units").getAsJsonArray();
                for (JsonElement mUnitsList : unitsArray) {
                    ProductUnitPacking productUnitPacking = new ProductUnitPacking();
                    JsonObject mUnitObject = mUnitsList.getAsJsonObject();
                    Units unit = unitsRepository.findByIdAndStatus(mUnitObject.get("unit_id").getAsLong(), true);
                    productUnitPacking.setPackingMaster(packingMaster);
                    productUnitPacking.setUnits(unit);
                    productUnitPacking.setUnitConversion(mUnitObject.get("unit_conv").getAsDouble());
                    productUnitPacking.setUnitConvMargn(mUnitObject.get("unit_marg").getAsDouble());
                    productUnitPacking.setMinQty(mUnitObject.get("min_qty").getAsDouble());
                    productUnitPacking.setMaxQty(mUnitObject.get("max_qty").getAsDouble());
                    productUnitPacking.setMrp(mUnitObject.get("mrp").getAsDouble());
                    productUnitPacking.setPurchaseRate(mUnitObject.get("purchase_rate").getAsDouble());
                    productUnitPacking.setSalesRate(mUnitObject.get("sales_rate").getAsDouble());
                    productUnitPacking.setMinSalesRate(mUnitObject.get("min_sales_rate").getAsDouble());
                    productUnitPacking.setDiscountInAmt(mUnitObject.get("disc_amt").getAsDouble());
                    productUnitPacking.setDiscountInPer(mUnitObject.get("disc_per").getAsDouble());
                    productUnitPacking.setOpeningQty(mUnitObject.get("opening_qty").getAsDouble());
                    productUnitPacking.setOpeningValution(mUnitObject.get("opening_valution").getAsDouble());
                    productUnitPacking.setStatus(true);
                    productUnitPacking.setProduct(newProduct);
                    productUnitPacking.setCreatedBy(users.getId());
                    unitPackingList.add(productUnitPacking);

                }
                productUnitRepository.saveAll(unitPackingList);
            }
            /* Saving Multiple images against product */
           /* FileStorageProperties fileStorageProperties = new FileStorageProperties();
            List<MultipartFile> multipartFiles = new ArrayList<>();
            multipartFiles = request.getFiles("images");
            if (multipartFiles != null && multipartFiles.size() > 0) {
                for (MultipartFile mImages : multipartFiles) {
                    fileStorageProperties.setUploadDir("./uploads" + File.separator + "products" + File.separator);
                    String imagePath = fileStorageService.storeFile(mImages, fileStorageProperties);
                    ProductImagesMaster productImagesMaster = new ProductImagesMaster();
                    if (imagePath != null) {
                        productImagesMaster.setImagePath("/uploads" + File.separator + "products" + File.separator + imagePath);
                        productImagesMaster.setProduct(newProduct);
                        imagesMasterRepository.save(productImagesMaster);
                    }
                }
            }*/
            responseObject.setMessage("Product Created Successfully");
            responseObject.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            productLogger.error("error in create product:" + e.getMessage());
            System.out.println(e.getMessage());
            responseObject.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseObject.setMessage("Internal Server Error");
        }
        return responseObject;
    }

    /* get all products of outlet */
    public Object getAllProduct(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<Product> productList = productRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        List<PurchaseProductAllData> list = new ArrayList<>();
        try {

            for (Product mProduct : productList) {
                List<ProductUnitPacking> pUnits = productUnitRepository.findByProductIdAndStatus(mProduct.getId(), true);
                List<UnitDTO> units = new ArrayList<>();
                PurchaseProductAllData pData = new PurchaseProductAllData();
                pData.setId(mProduct.getId());
                pData.setProductName(mProduct.getProductName());
                pData.setProductCode(mProduct.getProductCode());
                pData.setHsnId(mProduct.getProductHsn().getId());
                pData.setHsnNo(mProduct.getProductHsn().getHsnNumber());
                pData.setTaxMasterId(mProduct.getTaxMaster().getId());
                pData.setIgst(mProduct.getTaxMaster().getIgst());
                pData.setSgst(mProduct.getTaxMaster().getSgst());
                pData.setCgst(mProduct.getTaxMaster().getCgst());
                pData.setIsSerialNumber(mProduct.getIsSerialNumber());
                pData.setIsNegativeStocks(mProduct.getIsNegativeStocks());
                pData.setGroupId(mProduct.getGroup().getId());
                pData.setGroupName(mProduct.getGroup().getGroupName());
                pData.setSubGroupId(mProduct.getSubgroup().getId());
                pData.setSubGroupName(mProduct.getSubgroup().getSubgroupName());
                if (mProduct.getCategory() != null) {
                    pData.setCategoryId(mProduct.getCategory().getId());
                    pData.setCategoryName(mProduct.getCategory().getCategoryName());
                }
                if (mProduct.getSubcategory() != null) {
                    pData.setSubCategoryId(mProduct.getSubcategory().getId());
                    pData.setSubCategoryName(mProduct.getSubcategory().getSubcategoryName());
                }
                for (ProductUnitPacking mUnit : pUnits) {
                    UnitDTO unitData = new UnitDTO();
                    unitData.setId(mUnit.getId());
                    unitData.setUnitName(mUnit.getUnits().getUnitName());
                    unitData.setUnitType(mUnit.getUnitType());
                    units.add(unitData);
                }
                pData.setUnits(units);
                list.add(pData);
            }
        } catch (Exception e) {
            productLogger.error("Exception in getAllProduct:" + e.getMessage());
        }
        return list;
    }

    public Object updateProduct(HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        Map<String, String[]> paramMap = request.getParameterMap();
        Product product = productRepository.findByIdAndStatus(Long.parseLong(request.getParameter("productId")),
                true);
        // createProductHistory(product, "alter");
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        product.setProductName(request.getParameter("productName"));
        product.setDescription(request.getParameter("description"));
        product.setUpdatedBy(users.getId());
        product.setStatus(true);
        if (paramMap.containsKey("alias")) {
            product.setAlias(request.getParameter("alias"));
        } else {
            product.setAlias("NA");
        }
        product.setProductCode("PRT56678");
        if (request.getParameter("groupId") != null) {
            Group group = groupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("groupId")),
                    true);
            product.setGroup(group);
        }
        if (paramMap.containsKey("subgroupId")) {
            Subgroup subgroup = subgroupRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subgroupId")),
                    true);
            product.setSubgroup(subgroup);
        } else {
            product.setSubgroup(null);
        }
        if (paramMap.containsKey("categoryId")) {
            Category category = categoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("categoryId")),
                    true);
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        if (paramMap.containsKey("subcategoryId")) {
            Subcategory subcategory = subcategoryRepository.findByIdAndStatus(Long.parseLong(request.getParameter("subcategoryId")),
                    true);
            product.setSubcategory(subcategory);
        } else {
            product.setSubcategory(null);
        }
        ProductHsn productHsn = productHsnRepository.findByIdAndStatus(Long.parseLong(request.getParameter("hsnId")),
                true);
        product.setProductHsn(productHsn);
        TaxMaster taxMaster = taxMasterRepository.findByIdAndStatus(Long.parseLong(request.getParameter("taxMasterId")), true);
        product.setTaxMaster(taxMaster);
        product.setIsSerialNumber(Boolean.parseBoolean(request.getParameter("isSerialNo")));
        product.setIsWarrantyApplicable(Boolean.parseBoolean(request.getParameter("isWarranty")));
        product.setIsInventory(Boolean.parseBoolean(request.getParameter("isInventory")));
        if (Boolean.parseBoolean(request.getParameter("isWarranty"))) {
            product.setWarrantyDays(Integer.parseInt(request.getParameter("nodays")));
        } else {
            product.setWarrantyDays(0);
        }
        product.setIsNegativeStocks(Boolean.parseBoolean(request.getParameter("isNegativeStocks")));
        try {
            Product newProduct = productRepository.save(product);
            List<Long> unitId = new ArrayList<>();
            String jsonStr = request.getParameter("unit");
            JsonParser parser = new JsonParser();
            JsonElement tradeElement = parser.parse(jsonStr);
            JsonArray array = tradeElement.getAsJsonArray();
            for (JsonElement mList : array) {
                ProductUnitPacking productUnitPacking = null;
                JsonObject object = mList.getAsJsonObject();
                if (object.get("unitId").getAsLong() > 0) {
                    productUnitPacking = productUnitRepository.findByIdAndStatus(
                            object.get("unitDetailId").getAsLong(), true);
                    if (productUnitPacking == null) {
                        productUnitPacking = new ProductUnitPacking();
                    }
                    productUnitPacking.setUnitType(object.get("unitType").getAsString());
                    Units unit = unitsRepository.findByIdAndStatus(object.get("unitId").getAsLong(), true);
                    productUnitPacking.setUnits(unit);
                    productUnitPacking.setUnitConversion(object.get("unitConv").getAsDouble());
                   /* productUnit.setMinQty(object.get("minQty").getAsDouble());
                    productUnit.setMaxQty(object.get("maxQty").getAsDouble());
                    productUnit.setMinDisPer(object.get("minDisPer").getAsDouble());
                    productUnit.setMaxDisPer(object.get("maxDisPer").getAsDouble());
                    productUnit.setMinDisAmt(object.get("minDisAmt").getAsDouble());
                    productUnit.setMaxDisAmt(object.get("maxDisAmt").getAsDouble());
                    productUnit.setMrp(object.get("mrp").getAsDouble());
                    productUnit.setPurchaseRate(object.get("purchaseRate").getAsDouble());
                    productUnit.setSaleRate(object.get("saleRate").getAsDouble());
                    productUnit.setMinSaleRate(object.get("minSaleRate").getAsDouble());*/
                    productUnitPacking.setUnitConvMargn(object.get("unitConvMargn").getAsDouble());
                    productUnitPacking.setStatus(true);
                  /*  productUnit.setOpeningQty(object.getDouble("openingQty"));
                    productUnit.setOpeningRate(object.getDouble("openingRate"));
                    productUnit.setOpeningValuation(object.getDouble("openingeValuation"));
                  */
                    productUnitPacking.setProduct(newProduct);
                    ProductUnitPacking mUnit = productUnitRepository.save(productUnitPacking);
                    unitId.add(mUnit.getId());
                }
            }
            List<ProductUnitPacking> unitList = productUnitRepository.findByProductId(product.getId());
            List<Long> dbUnits = new ArrayList<>();
            for (ProductUnitPacking mUnit : unitList) {
                dbUnits.add(mUnit.getId());
            }
            List<Long> diff = new ArrayList<Long>(dbUnits.size());
            diff.addAll(dbUnits);
            diff.removeAll(unitId);
            for (Long mDiff : diff) {
                ProductUnitPacking mProductUnitPacking = productUnitRepository.findByIdAndStatus(mDiff, true);
                mProductUnitPacking.setStatus(false);
                productUnitRepository.save(mProductUnitPacking);
            }


            responseMessage.setMessage("Product updated Successfully");
            responseMessage.setResponseStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseMessage.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseMessage.setMessage("Internal Server Error");
        }
        return responseMessage;
    }

   /* private void createProductHistory(Product product, String alter) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProductId(product.getId());

    }*/

    /* Get Product by id for edit */
    public Object getProductById(HttpServletRequest request) {
        Long productId = Long.parseLong(request.getParameter("product_id"));
        Product mProduct = productRepository.findByIdAndStatus(productId, true);
        ResponseMessage responseMessage = new ResponseMessage();
        ProductDTO pData = new ProductDTO();
        try {
            if (mProduct != null) {
                pData.setId(mProduct.getId());
                pData.setProductName(mProduct.getProductName());
                pData.setProductCode(mProduct.getProductCode());
                pData.setDescription(mProduct.getDescription());
                pData.setIsWarrantyApplicable(mProduct.getIsWarrantyApplicable());
                pData.setWarrantyDays(mProduct.getWarrantyDays());
                pData.setIsSerialNumber(mProduct.getIsSerialNumber());
                pData.setIsNegativeStocks(mProduct.getIsNegativeStocks());
                pData.setIsInventory(mProduct.getIsInventory());
                pData.setAlias(mProduct.getAlias());
                pData.setHsnId(mProduct.getProductHsn().getId());
                pData.setGroupId(mProduct.getGroup().getId());
                pData.setSubGroupId(mProduct.getSubgroup().getId());
                pData.setTaxMasterId(mProduct.getTaxMaster().getId());
                if (mProduct.getCategory() != null) {
                    pData.setCategoryId(mProduct.getCategory().getId());
                }
                if (mProduct.getSubcategory() != null) {
                    pData.setSubCategoryId(mProduct.getSubcategory().getId());
                }
                List<ProductUnitDTO> list = new ArrayList<>();
                List<ProductUnitPacking> units = productUnitRepository.findByProductIdAndStatus(mProduct.getId(), true);
                for (ProductUnitPacking mUnit : units) {
                    ProductUnitDTO pUnit = new ProductUnitDTO();
                    pUnit.setUnitType(mUnit.getUnitType());
               /* pUnit.setSaleRate(mUnit.getSaleRate());
                pUnit.setMinSaleRate(mUnit.getMinSaleRate());
                pUnit.setPurchaseRate(mUnit.getPurchaseRate());
                pUnit.setMrp(mUnit.getMrp());
                pUnit.setMinQty(mUnit.getMinQty());
                pUnit.setMaxQty(mUnit.getMaxQty());
                pUnit.setMinDisPer(mUnit.getMinDisPer());
                pUnit.setMaxDisPer(mUnit.getMaxDisPer());
                pUnit.setMinDisAmt(mUnit.getMinDisAmt());
                pUnit.setMaxDisAmt(mUnit.getMaxDisAmt());*/
                    pUnit.setUnitConversion(mUnit.getUnitConversion());
                    pUnit.setUnitConvMargn(mUnit.getUnitConvMargn());
                    pUnit.setUnitId(mUnit.getUnits().getId());
                    pUnit.setUnitDetailId(mUnit.getId());
                    list.add(pUnit);
                }
                pData.setUnits(list);
                responseMessage.setMessage("success");
                responseMessage.setResponseStatus(HttpStatus.OK.value());
                responseMessage.setResponseObject(pData);
            } else {
                responseMessage.setMessage("error");
                responseMessage.setResponseStatus(HttpStatus.BAD_REQUEST.value());
                responseMessage.setResponseObject("");
            }
        } catch (Exception e) {
            productLogger.error("Exception in getProductById:" + e.getMessage());
        }
        return responseMessage;
    }

    public JsonObject getProduct(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(
                request.getHeader("Authorization").substring(7));
        List<Product> productList = productRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        List<PurchaseProductData> list = new ArrayList<>();
        JsonArray array = new JsonArray();
        JsonObject result = new JsonObject();
        for (Product mProduct : productList) {
            List<ProductUnitPacking> units = productUnitRepository.findByProductIdAndStatus(
                    mProduct.getId(), true);
            PurchaseProductData pData = new PurchaseProductData();
            JsonObject response = new JsonObject();
            response.addProperty("id", mProduct.getId());
            response.addProperty("productName", mProduct.getProductName());
            response.addProperty("productCode", mProduct.getProductCode());
            response.addProperty("isNegativeStocks", mProduct.getIsNegativeStocks());
            response.addProperty("isSerialNumber", mProduct.getIsSerialNumber());
            response.addProperty("isInventory", mProduct.getIsInventory());
            response.addProperty("igst", mProduct.getTaxMaster().getIgst());
            response.addProperty("cgst", mProduct.getTaxMaster().getCgst());
            response.addProperty("sgst", mProduct.getTaxMaster().getSgst());
            response.addProperty("hsnId", mProduct.getProductHsn().getId());
            response.addProperty("taxMasterId", mProduct.getTaxMaster().getId());
            JsonArray unitArray = new JsonArray();
            for (ProductUnitPacking mUnits : units) {
                JsonObject mUnitsList = new JsonObject();
                mUnitsList.addProperty("unitCode", mUnits.getUnits().getUnitCode());
                mUnitsList.addProperty("id", mUnits.getUnits().getId());
                unitArray.add(mUnitsList);
            }
            response.add("units", unitArray);
            array.add(response);
        }
        result.addProperty("messege", "success");
        result.addProperty("responseStatus", HttpStatus.OK.value());
        result.add("responseObject", array);
        return result;
    }

    public JsonObject getProductsOfOutlet(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        List<Product> productList = productRepository.findByOutletIdAndStatus(users.getOutlet().getId(), true);
        JsonObject finalResult = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Product mProduct : productList) {
            JsonObject mObject = new JsonObject();
            mObject.addProperty("id", mProduct.getId());
            mObject.addProperty("product_name", mProduct.getProductName());
            mObject.addProperty("search_code", mProduct.getAlias());
            mObject.addProperty("brand_name", mProduct.getGroup().getGroupName());
            mObject.addProperty("group_name", mProduct.getSubgroup() != null ?
                    mProduct.getSubgroup().getSubgroupName() : "NA");
            mObject.addProperty("category_name", mProduct.getCategory() != null ?
                    mProduct.getCategory().getCategoryName() : "NA");
            mObject.addProperty("subcategory_name", mProduct.getSubcategory() != null ?
                    mProduct.getSubcategory().getSubcategoryName() : "NA");
            jsonArray.add(mObject);
        }
        finalResult.addProperty("message", "success");
        finalResult.addProperty("responseStatus", HttpStatus.OK.value());
        finalResult.add("data", jsonArray);
        return finalResult;
    }

    public JsonObject getUnitsPackings(HttpServletRequest request) {
        Users users = jwtRequestFilter.getUserDataFromToken(request.getHeader("Authorization").substring(7));
        Product mProduct = productRepository.findByIdAndStatus(Long.parseLong
                (request.getParameter("product_id")), true);
        JsonArray packArray = new JsonArray();
        JsonObject finalResult = new JsonObject();
        JsonObject result = new JsonObject();
        /* for No Packaging */
        if (!mProduct.getIsPackings()) {
            result.addProperty("is_packaging", mProduct.getIsPackings());
            JsonObject mObject = new JsonObject();
            mObject.addProperty("id", "0");
            mObject.addProperty("pack_name", "");
            /*   product units list*/
            List<ProductUnitPacking> productUnitPackings = productUnitRepository.findByProductId(Long.parseLong
                    (request.getParameter("product_id")));
            JsonArray unitArray = new JsonArray();
            for (ProductUnitPacking mUnits : productUnitPackings) {
                JsonObject mUnitsObj = new JsonObject();
                mUnitsObj.addProperty("units_id", mUnits.getUnits().getId());
                mUnitsObj.addProperty("unit_name", mUnits.getUnits().getUnitName());
                mUnitsObj.addProperty("unit_conversion", mUnits.getUnitConversion());
                unitArray.add(mUnitsObj);
            }
            mObject.add("units", unitArray);
            packArray.add(mObject);
        } else {
            /*  product packing List*/
            List<Long> unitPackingList = new ArrayList<>();
            unitPackingList = productUnitRepository.findProductIdDistinct(Long.parseLong
                    (request.getParameter("product_id")));
            System.out.print("Size:" + unitPackingList.size());
            if (unitPackingList != null && unitPackingList.size() > 0) {
                for (Long mPack : unitPackingList) {
                    JsonObject mObject = new JsonObject();
                    PackingMaster mPacking = packingMasterRepository.findById(mPack).get();
                    mObject.addProperty("id", mPacking.getId());
                    mObject.addProperty("pack_name", mPacking.getPackName());
                    /*product units list*/
                    List<ProductUnitPacking> productUnitPackings = productUnitRepository.findByProductIdAndPackingMasterId(Long.parseLong
                            (request.getParameter("product_id")), mPack);
                    JsonArray unitArray = new JsonArray();
                    for (ProductUnitPacking mUnits : productUnitPackings) {
                        JsonObject mUnitsObj = new JsonObject();
                        mUnitsObj.addProperty("units_id", mUnits.getUnits().getId());
                        mUnitsObj.addProperty("unit_name", mUnits.getUnits().getUnitName());
                        mUnitsObj.addProperty("unit_conversion", mUnits.getUnitConversion());
                        unitArray.add(mUnitsObj);
                    }
                    /*    end of product units list*/
                    mObject.add("units", unitArray);
                    packArray.add(mObject);
                }
            }
        }
        result.add("lst_packages", packArray);
        /*     end of  product packing List*/
        finalResult.addProperty("message", "success");
        finalResult.addProperty("responseStatus", HttpStatus.OK.value());
        finalResult.add("responseObject", result);
        return finalResult;
    }
}

