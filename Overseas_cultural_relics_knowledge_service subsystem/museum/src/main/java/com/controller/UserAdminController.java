package com.controller;

import com.entity.CollectView;
import com.entity.CommentView;
import com.entity.User;
import com.service.CollectService;
import com.service.ICommentService;
import com.service.IUserService;
import com.service.exception.*;
import com.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user_admin")
public class UserAdminController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICommentService iCommentService;

    @Autowired
    private CollectService collectService;

    @RequestMapping("/update")
    public JsonResult<Integer> update(@RequestBody Map map){
        JsonResult<Integer> result = new JsonResult<>();
        try{
            String a = (String)map.get("id");
            int user_id = Integer.parseInt(a) ;
            String old_pass =(String)map.get("oldPassword");
            String user_password = (String)map.get("newPassword");
            String user_name = (String)map.get("name");
            String s = (String)map.get("sex");
            int sex = Integer.parseInt(s);
            String user_tel = (String)map.get("tel");
            int row = iUserService.up(Long.valueOf(user_id), old_pass, user_password, user_name, null, sex, null, user_tel, null);
            result.setData(row);
            result.setState(200);
            result.setMessage("更新成功！");
        }
        catch(PasswordNotMatchException e){
            result.setState(6000);
            result.setMessage("原密码输入错误！");
        }catch(UsernameCompetedException e){
            result.setState(5000);
            result.setMessage("用户名已存在，请重新修改！");
        }
        catch(ServiceException e){
            result.setState(4000);
            result.setMessage("更新失败！");
        }
        return result;
    }

    @RequestMapping("/comment")
    public JsonResult<ArrayList<CommentView>> findComment(@RequestBody Map map){
        String userid = (String) map.get("username");
        int user_id = Integer.parseInt(userid);
        JsonResult<ArrayList<CommentView>> result = new JsonResult<>();
        ArrayList<CommentView> ans = iCommentService.viewcommentfuser(user_id);
        result.setData(ans);
        result.setState(200);
        result.setMessage("以下为评论内容：");
        return result;
    }

    @RequestMapping("/collect")
    public JsonResult<List<CollectView>> findCollect(@RequestBody Map map){
        String userid = (String) map.get("id");
        Long userId = Long.parseLong(userid);
        JsonResult<List<CollectView>> result = new JsonResult<>();
        List<CollectView> ans = collectService.collectionfromuid(userId);
        result.setState(200);
        result.setData(ans);
        result.setMessage("以下为收藏文物：");
        return result;
    }

    @RequestMapping("/deleteCollect")
    public JsonResult<Integer> deleteCollect(@RequestBody Map map){
        JsonResult<Integer> result = new JsonResult<>();
        try{
            String userid = (String) map.get("uid");
            String museumIdStr = (String) map.get("museumId");
            String objectId = (String) map.get("objectId");

            Long userId = Long.parseLong(userid);
            Integer museumId = Integer.parseInt(museumIdStr);

            result.setData(collectService.removecollection(
                collectService.findByUserIdAndArtifact(userId, museumId, objectId).getFavoriteId()
            ));
            result.setState(200);
            result.setMessage("取消收藏成功");
        }catch(DeleteLoss e){
            result.setState(4000);
            result.setMessage("收藏文物未找到");
        } catch(Exception e){
            result.setState(5000);
            result.setMessage("取消收藏失败：" + e.getMessage());
        }
        return result;
    }

    @RequestMapping("/deleteComment")
    public JsonResult<Integer> deleteComment(@RequestBody Map map){
        JsonResult<Integer> result = new JsonResult<>();
        try{
            String cid = (String) map.get("cid");
            System.out.println("cid="+cid);
            Integer comment_id = Integer.parseInt(cid);
            result.setData(iCommentService.deletecomment(comment_id));
            result.setMessage("删除评论成功");
            result.setState(200);
        }catch(DeleteLoss e){
            result.setState(4000);
            result.setMessage("评论未查询到");
        }catch(DeleteException e){
            result.setState(6000);
            result.setMessage("删除错误");
        }
        return result;
    }
}
