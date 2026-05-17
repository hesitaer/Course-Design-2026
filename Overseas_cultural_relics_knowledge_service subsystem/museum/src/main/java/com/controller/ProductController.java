package com.controller;

import com.entity.*;
import com.service.CollectService;
import com.service.ICartService;
import com.service.ICommentService;
import com.service.IProductService;
import com.service.exception.CollectduplicateException;
import com.service.exception.CommentPermissionException;
import com.service.exception.InsertException;
import com.service.exception.ProductNotFoundException;
import com.util.JsonResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class ProductController extends BaseController{
    @Autowired
    private IProductService iProductService;
    @Autowired
    private ICartService iCartService;
    @Autowired
    private ICommentService iCommentService;
    @Autowired
    private CollectService collectService;

    @RequestMapping("/searchById")
    public JsonResult<ProductView> findById(@RequestBody Map map){
        JsonResult<ProductView> result = new JsonResult<ProductView>();
        try{
            String relicid = (String) map.get("rid");
            String userid = (String) map.get("uid");
            int relic_id = Integer.parseInt(relicid);
            Product p = new Product();
            p = iProductService.findByProduct_id(BigInteger.valueOf(relic_id));
            ProductView pp = new ProductView();
            BeanUtils.copyProperties(p, pp);
            pp.setCommentView(iCommentService.viewcommentfrelics(relic_id));
            List<Cart> recommend=new ArrayList<Cart>();
            recommend=iCartService.SearchCommentView(p.getMuseum(),p.getCat2(),p.getMakers_name(),p.getGeography(),p.getId());
            pp.setRecommend(recommend);
            if(userid!=null){
                int user_id = Integer.parseInt(userid);
                Collect collect = collectService.findByuidandrid(user_id,relic_id);
                if(collect!=null)
                    pp.setIf_collect(1);
                else  pp.setIf_collect(0);
            }
            result.setData(pp);
            result.setState(200);
            result.setMessage("找到以下内容：");
        }catch (ProductNotFoundException e){
            result.setState(6000);
            result.setMessage("抱歉，您查询的文物不存在！");
        }
        return result;
    }

    @RequestMapping("/searchById/comment")
    public JsonResult<Integer> comment(@RequestBody Map map ){
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            String relicid = (String) map.get("rid");
            String content = (String) map.get("content");
            int user_id = Integer.parseInt(userid);
            int relic_id = Integer.parseInt(relicid);
            result.setData(iCommentService.notecomment(user_id,relic_id,content));
            result.setState(200);
            result.setMessage("评论成功");
        }catch(CommentPermissionException e){
            result.setMessage("您已被限制评论");
            result.setState(3000);
        }catch(InsertException e)
        {
            result.setMessage("评论出现未知错误");
            result.setState(6000);
        }
        return result;
    }

    @RequestMapping("/searchById/collect")
    public JsonResult<Integer> collect(@RequestBody Map map ){
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            String relicid = (String) map.get("rid");
            int user_id = Integer.parseInt(userid);
            int relic_id = Integer.parseInt(relicid);
            Collect collect = new Collect();
            collect.setRid(relic_id);
            collect.setUid(user_id);
            result.setData(collectService.addcollection(collect));
            result.setState(200);
            result.setMessage("收藏成功");
        } catch(CollectduplicateException e){
            result.setMessage("请勿重复收藏！");
            result.setState(5000);
        }
        return result;
    }

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
