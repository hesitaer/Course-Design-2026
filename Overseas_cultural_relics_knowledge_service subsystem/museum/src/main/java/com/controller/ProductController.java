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
}
