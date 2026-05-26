package com.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mapper.UserMapper;
import com.service.IUserService;
import com.service.exception.PasswordNotMatchException;
import com.service.exception.UserLimitedLoginException;
import com.service.exception.UserNotFoundException;
import com.service.exception.userRepetitionException;
import com.util.JsonResult;
import com.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/login")
    JsonResult<User> login(@RequestBody Map map, HttpServletRequest request){
        JsonResult<User> result = new JsonResult<User>();
        try{
            String username = (String)map.get("username");
            String password = (String)map.get("password");
            User u = userService.loginByUsername(username, password);
            String secret = "abcdef";
            String token = JWT.create().
                    withClaim("id", u.getUser_id()).
                    withClaim("username", u.getUsername()).
                    sign(Algorithm.HMAC256(secret));
            
            String ip = getClientIp(request);
            userMapper.updateLastLogin(u.getUser_id(), ip);
            
            result.setState(200);
            result.setMessage("登录成功！");
            result.setToken(token);
            result.setData(u);
            return result;
        } catch(UserNotFoundException e){
            result.setMessage("用户不存在");
            result.setState(4000);
            return result;
        } catch(PasswordNotMatchException e){
            result.setMessage("密码错误");
            result.setState(6000);
            return result;
        } catch(UserLimitedLoginException e){
            result.setMessage("用户被限制登录");
            result.setState(9000);
            return result;
        }
    }

    @RequestMapping("/register")
    public JsonResult<Long> reg(@RequestBody Map map){
        JsonResult<Long> result = new JsonResult<Long>();
        try {
            String password = (String)map.get("password");
            String username = (String)map.get("username");
            String sexStr = (String)map.get("sex");
            Integer sex = sexStr != null ? Integer.parseInt(sexStr) : null;
            String phone = (String)map.get("tele");
            User user = userService.reg(password, username, sex, phone);
            result.setState(200);
            result.setData(user.getUser_id());
            result.setMessage("注册成功！欢迎你 " + username);
        } catch(userRepetitionException e){
            result.setState(6000);
            result.setMessage("用户名重复！");
        } catch (Exception e) {
            // 打印完整错误信息，方便调试
            e.printStackTrace();
            result.setState(5000);
            result.setMessage("注册失败，请联系管理员");
        }
        return result;
    }

    @RequestMapping("/get_detail")//用户注册
    public JsonResult<User> findById(@RequestBody Map map){
        JsonResult<User> result = new JsonResult<User>();
        try {
            String userid = (String)map.get("id");
            Long user_id = Long.parseLong(userid);
            User user = userService.getUserById(user_id);
            if(user != null) {
                result.setState(200);
                result.setData(user);
            } else {
                result.setState(4000);
                result.setMessage("用户不存在");
            }
        } catch (Exception e) {
            result.setState(5000);
            result.setMessage("查询失败");
        }
        return result;
    }

    @RequestMapping("/update")
    public JsonResult<Integer> update(@RequestBody Map map){
        JsonResult<Integer> result = new JsonResult<Integer>();
        try {
            String userid = (String)map.get("id");
            Long user_id = Long.parseLong(userid);
            String old_password = (String)map.get("old_password");
            String password = (String)map.get("password");
            String username = (String)map.get("username");
            String nickname = (String)map.get("nickname");
            String sexStr = (String)map.get("sex");
            Integer sex = sexStr != null ? Integer.parseInt(sexStr) : null;
            String email = (String)map.get("email");
            String phone = (String)map.get("phone");
            String avatar_url = (String)map.get("avatar_url");
            
            int row = userService.up(user_id, old_password, password, username, nickname, sex, email, phone, avatar_url);
            result.setState(200);
            result.setData(row);
            result.setMessage("更新成功！");
        } catch(UserNotFoundException e){
            result.setState(4000);
            result.setMessage("用户不存在");
        } catch(PasswordNotMatchException e){
            result.setState(6000);
            result.setMessage("原密码错误");
        } catch(Exception e){
            result.setState(5000);
            result.setMessage("更新失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取客户端真实IP地址
     * 说明：当服务器部署在反向代理（如Nginx）后面时，直接使用request.getRemoteAddr()只能获取到代理服务器的IP
     * 需要通过HTTP请求头来获取真实客户端IP
     * 
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For：最常用的反向代理IP头（Nginx等常用）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // Proxy-Client-IP：Apache等代理服务器使用
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // WL-Proxy-Client-IP：WebLogic服务器使用
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // HTTP_CLIENT_IP：部分代理服务器使用
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // HTTP_X_FORWARDED_FOR：另一种标准IP头
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 兜底方案：直接获取连接的IP（可能是代理服务器IP）
            ip = request.getRemoteAddr();
        }
        // 处理多IP情况（多个代理时，第一个IP为真实客户端IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}