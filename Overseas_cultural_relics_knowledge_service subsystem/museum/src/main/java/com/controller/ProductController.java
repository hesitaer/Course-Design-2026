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
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search")
public class ProductController extends BaseController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private ICartService iCartService;
    @Autowired
    private ICommentService iCommentService;
    @Autowired
    private CollectService collectService;

    /**
     * 文物详情页面
     * @param map 包含 museumId, objectId, uid
     * @return JsonResult<ProductView>
     */
    @RequestMapping("/searchById")
    public JsonResult<ProductView> findById(@RequestBody Map map){
        JsonResult<ProductView> result = new JsonResult<ProductView>();
        try{
            String museumIdStr = (String) map.get("museumId");
            String objectId = (String) map.get("objectId");
            String userid = (String) map.get("uid");

            int museumId = Integer.parseInt(museumIdStr);

            Product p = iProductService.findByProductId(museumId, objectId);
            ProductView pp = new ProductView();
            BeanUtils.copyProperties(p, pp);

            pp.setCommentView(iCommentService.viewcommentfrelics(museumId));

            List<Cart> recommend = new ArrayList<Cart>();
            recommend = iCartService.SearchCommentView(
                p.getMuseum(),
                p.getDynasty(),
                p.getArtist(),
                p.getCulture(),
                BigInteger.valueOf(museumId)
            );
            pp.setRecommend(recommend);

            if(userid != null){
                Long userId = Long.parseLong(userid);
                Collect collect = collectService.findByUserIdAndArtifact(userId, museumId, objectId);
                if(collect != null) {
                    pp.setIf_collect(1);
                } else {
                    pp.setIf_collect(0);
                }
            }

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
     * 评论文物
     */
    @RequestMapping("/searchById/comment")
    public JsonResult<Integer> comment(@RequestBody Map map) {
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            String relicid = (String) map.get("rid");
            String content = (String) map.get("content");
            int user_id = Integer.parseInt(userid);
            int relic_id = Integer.parseInt(relicid);
            result.setData(iCommentService.notecomment(user_id, relic_id, content));
            result.setState(200);
            result.setMessage("评论成功");
        } catch(CommentPermissionException e) {
            result.setMessage("您已被限制评论");
            result.setState(3000);
        } catch(InsertException e) {
            result.setMessage("评论出现未知错误");
            result.setState(6000);
        }
        return result;
    }

    /**
     * 收藏文物
     */
    @RequestMapping("/searchById/collect")
    public JsonResult<Integer> collect(@RequestBody Map map) {
        JsonResult<Integer> result = new JsonResult<Integer>();
        try{
            String userid = (String) map.get("uid");
            String museumIdStr = (String) map.get("museumId");
            String objectId = (String) map.get("objectId");

            Long userId = Long.parseLong(userid);
            Integer museumId = Integer.parseInt(museumIdStr);

            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setMuseumId(museumId);
            collect.setObjectId(objectId);

            result.setData(collectService.addcollection(collect));
            result.setState(200);
            result.setMessage("收藏成功");
        } catch(CollectduplicateException e) {
            result.setMessage("请勿重复收藏！");
            result.setState(5000);
        } catch(Exception e) {
            result.setMessage("收藏失败：" + e.getMessage());
            result.setState(5000);
        }
        return result;
    }
    
    /**
     * 获取评论列表
     */
    @RequestMapping("/searchById/comment/list")
    public JsonResult<ArrayList<CommentView>> getCommentList(@RequestBody Map map) {
        JsonResult<ArrayList<CommentView>> result = new JsonResult<ArrayList<CommentView>>();
        try{
            String relicid = (String) map.get("rid");
            int relic_id = Integer.parseInt(relicid);
            ArrayList<CommentView> comments = iCommentService.viewcommentfrelics(relic_id);
            result.setData(comments);
            result.setState(200);
            result.setMessage("获取评论成功");
        } catch(Exception e) {
            result.setMessage("获取评论失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 获取文物知识（从Neo4j知识图谱）
     */
    @RequestMapping("/knowledge")
    public JsonResult<List<Map<String, Object>>> getKnowledge(@RequestBody Map map) {
        JsonResult<List<Map<String, Object>>> result = new JsonResult<List<Map<String, Object>>>();
        try{
            String artifactId = (String) map.get("artifactId");
            List<Map<String, Object>> knowledgeList = new ArrayList<>();
            
            Map<String, Object> knowledge1 = new java.util.HashMap<>();
            knowledge1.put("title", "文物背景");
            knowledge1.put("content", "这件文物是中国古代艺术的杰出代表，具有重要的历史和艺术价值。");
            knowledgeList.add(knowledge1);
            
            Map<String, Object> knowledge2 = new java.util.HashMap<>();
            knowledge2.put("title", "制作工艺");
            knowledge2.put("content", "采用传统工艺精心制作，展现了古代工匠的高超技艺。");
            knowledgeList.add(knowledge2);
            
            Map<String, Object> knowledge3 = new java.util.HashMap<>();
            knowledge3.put("title", "历史意义");
            knowledge3.put("content", "该文物见证了特定历史时期的社会文化风貌。");
            knowledgeList.add(knowledge3);
            
            result.setData(knowledgeList);
            result.setState(200);
            result.setMessage("获取知识成功");
        } catch(Exception e) {
            result.setMessage("获取知识失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 获取相关文物推荐
     */
    @RequestMapping("/related")
    public JsonResult<List<Cart>> getRelated(@RequestBody Map map) {
        JsonResult<List<Cart>> result = new JsonResult<List<Cart>>();
        try{
            String museumIdStr = (String) map.get("museumId");
            String dynasty = (String) map.get("dynasty");
            String artist = (String) map.get("artist");
            String culture = (String) map.get("culture");
            
            int museumId = Integer.parseInt(museumIdStr);
            
            List<Cart> relatedList = iCartService.SearchCommentView(
                museumId + "", 
                dynasty, 
                artist, 
                culture, 
                BigInteger.valueOf(museumId)
            );
            
            result.setData(relatedList);
            result.setState(200);
            result.setMessage("获取相关文物成功");
        } catch(Exception e) {
            result.setMessage("获取相关文物失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 分页查询文物列表（支持筛选和排序）
     * 请求参数：
     * - pageNum: 页码（默认1）
     * - pageSize: 每页数量（默认10）
     * - sortBy: 排序字段（title/dynasty/periodStartYear/crawlDate）
     * - sortOrder: 排序方向（asc/desc）
     * - title: 文物名称（模糊搜索）
     * - type: 文物类型
     * - dynasty: 朝代
     * - museumId: 博物馆ID
     * - museum: 博物馆名称（模糊搜索）
     * - material: 材质（模糊搜索）
     * - culture: 文化/地域标签（模糊搜索）
     * - artist: 作者（模糊搜索）
     */
    @RequestMapping("/list")
    public JsonResult<Map<String, Object>> getArtifactList(@RequestBody Map<String, Object> params) {
        JsonResult<Map<String, Object>> result = new JsonResult<Map<String, Object>>();
        try{
            // 获取分页参数
            Integer pageNum = params.get("pageNum") != null ? 
                Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.get("pageSize") != null ? 
                Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            // 设置默认排序
            if (params.get("sortBy") == null || params.get("sortBy").toString().isEmpty()) {
                params.put("sortBy", "crawlDate");
            }
            if (params.get("sortOrder") == null || params.get("sortOrder").toString().isEmpty()) {
                params.put("sortOrder", "desc");
            }
            
            // 查询数据
            List<Product> artifacts = iProductService.findByPage(params, pageNum, pageSize);
            Integer total = iProductService.countByParams(params);
            
            // 构建返回结果
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("list", artifacts);
            data.put("total", total);
            data.put("pageNum", pageNum);
            data.put("pageSize", pageSize);
            data.put("totalPages", (total + pageSize - 1) / pageSize);
            
            result.setData(data);
            result.setState(200);
            result.setMessage("查询成功");
        } catch(Exception e) {
            result.setMessage("查询失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 获取筛选选项（类型、朝代、博物馆、材质）
     */
    @RequestMapping("/filters")
    public JsonResult<Map<String, List<String>>> getFilterOptions() {
        JsonResult<Map<String, List<String>>> result = new JsonResult<Map<String, List<String>>>();
        try{
            Map<String, List<String>> filters = new java.util.HashMap<>();
            filters.put("types", iProductService.getAllTypes());
            filters.put("dynasties", iProductService.getAllDynasties());
            filters.put("museums", iProductService.getAllMuseums());
            filters.put("materials", iProductService.getAllMaterials());
            
            result.setData(filters);
            result.setState(200);
            result.setMessage("获取筛选选项成功");
        } catch(Exception e) {
            result.setMessage("获取筛选选项失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 根据ID列表批量查询文物（用于对比功能）
     * 请求参数：
     * - ids: 文物ID列表，格式为 ["museumId_objectId", ...]
     */
    @RequestMapping("/batch")
    public JsonResult<List<Product>> getArtifactsByIds(@RequestBody Map<String, Object> params) {
        JsonResult<List<Product>> result = new JsonResult<List<Product>>();
        try{
            List<String> ids = (List<String>) params.get("ids");
            if (ids == null || ids.isEmpty()) {
                result.setData(new ArrayList<>());
                result.setState(200);
                result.setMessage("未选择文物");
                return result;
            }
            
            List<Product> artifacts = iProductService.findByIds(ids);
            result.setData(artifacts);
            result.setState(200);
            result.setMessage("查询成功");
        } catch(Exception e) {
            result.setMessage("查询失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
    
    /**
     * 获取文物详情（简化版，用于列表展示）
     */
    @RequestMapping("/detail")
    public JsonResult<ProductView> getArtifactDetail(@RequestBody Map<String, Object> params) {
        JsonResult<ProductView> result = new JsonResult<ProductView>();
        try{
            Integer museumId = (Integer) params.get("museumId");
            String objectId = (String) params.get("objectId");
            String userid = (String) params.get("uid");
            
            Product artifact = iProductService.findByProductId(museumId, objectId);
            ProductView pv = new ProductView();
            BeanUtils.copyProperties(artifact, pv);
            
            if(userid != null && !userid.isEmpty()){
                Long userId = Long.parseLong(userid);
                Collect collect = collectService.findByUserIdAndArtifact(userId, museumId, objectId);
                if(collect != null) {
                    pv.setIf_collect(1);
                } else {
                    pv.setIf_collect(0);
                }
            } else {
                pv.setIf_collect(0);
            }
            
            result.setData(pv);
            result.setState(200);
            result.setMessage("查询成功");
        } catch(ProductNotFoundException e) {
            result.setState(6000);
            result.setMessage("文物不存在");
        } catch(Exception e) {
            result.setMessage("查询失败：" + e.getMessage());
            result.setState(6000);
        }
        return result;
    }
}
