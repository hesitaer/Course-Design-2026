package com.controller;

import com.entity.Product;
import com.entity.ProductQueryDTO;
import com.entity.ProductView;
import com.service.IProductService;
import com.service.exception.ProductNotFoundException;
import com.util.JsonResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class ProductController extends BaseController {
    
    @Autowired
    private IProductService iProductService;
    
    /**
     * 文物详情页面
     * @param map 包含 museumId, objectId
     * @return JsonResult<ProductView>
     */
    @RequestMapping("/searchById")
    public JsonResult<ProductView> findById(@RequestBody Map map){
        JsonResult<ProductView> result = new JsonResult<ProductView>();
        try{
            // 获取复合主键参数
            String museumIdStr = (String) map.get("museumId");
            String objectId = (String) map.get("objectId");
            
            int museumId = Integer.parseInt(museumIdStr);
            
            // 查询文物详情
            Product p = iProductService.findByProductId(museumId, objectId);
            ProductView pp = new ProductView();
            BeanUtils.copyProperties(p, pp);
            
            result.setData(pp);
            result.setState(200);
            result.setMessage("找到以下内容：");
        } catch (ProductNotFoundException e) {
            result.setState(6000);
            result.setMessage("抱歉，您查询的文物不存在！");
        } catch (Exception e) {
            result.setState(6000);
            result.setMessage("查询失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 简单关键词搜索
     */
    @GetMapping("/keyword")
    public JsonResult<List<Product>> keywordSearch(@RequestParam String keyword) {
        JsonResult<List<Product>> result = new JsonResult<>();
        try {
            List<Product> products = iProductService.searchByKeyword(keyword);
            result.setData(products);
            result.setState(200);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setState(6000);
            result.setMessage("查询失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 高级查询（分页）
     */
    @PostMapping("/advanced")
    public JsonResult<Map<String, Object>> advancedSearch(@RequestBody ProductQueryDTO queryDTO) {
        JsonResult<Map<String, Object>> result = new JsonResult<>();
        try {
            Map<String, Object> data = iProductService.findByConditionsWithPage(queryDTO);
            result.setData(data);
            result.setState(200);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setState(6000);
            result.setMessage("查询失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 多维筛选查询（不分页）
     */
    @PostMapping("/multi-filter")
    public JsonResult<List<Product>> multiFilterSearch(@RequestBody ProductQueryDTO queryDTO) {
        JsonResult<List<Product>> result = new JsonResult<>();
        try {
            List<Product> products = iProductService.findByConditions(queryDTO);
            result.setData(products);
            result.setState(200);
            result.setMessage("查询成功，共找到 " + products.size() + " 条记录");
        } catch (Exception e) {
            result.setState(6000);
            result.setMessage("查询失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 导出CSV
     */
    @PostMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCSV(@RequestBody ProductQueryDTO queryDTO) {
        try {
            String csvContent = iProductService.exportToCSV(queryDTO);
            byte[] csvBytes = csvContent.getBytes("UTF-8");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "cultural_relics_export.csv");
            headers.setContentLength(csvBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 导出JSON
     */
    @PostMapping("/export/json")
    public ResponseEntity<byte[]> exportToJSON(@RequestBody ProductQueryDTO queryDTO) {
        try {
            String jsonContent = iProductService.exportToJSON(queryDTO);
            byte[] jsonBytes = jsonContent.getBytes("UTF-8");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "cultural_relics_export.json");
            headers.setContentLength(jsonBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(jsonBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
