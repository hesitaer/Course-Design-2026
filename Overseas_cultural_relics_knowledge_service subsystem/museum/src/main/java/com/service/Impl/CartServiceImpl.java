package com.service.Impl;

import com.entity.Cart;
import com.entity.Product;
import com.mapper.CartMapper;

import com.mapper.ProductMapper;
import com.service.ICartService;
import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
//分类查询
    @Override
    public List<Cart> SearchProductByClass(String x,String y) {//博物馆、材质、朝代、分类输入查询
        List<Cart> result;
        if(x.equals("museum")){
            result = cartMapper.findByMuseum(y);
            if(result==null){
                throw new ProductNotFoundException("查询结果为空");
            }
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                String[] address =p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("mart") || x.equals("material")){
            result = cartMapper.findByCat1(y);
            if(result==null || result.isEmpty()){
                throw new ProductNotFoundException("查询结果为空");
            }
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("dynasty") || x.equals("time")){
            // 直接使用用户输入的原始值进行模糊查询
            // 数据库中存储的格式是"Tang（唐）"，LIKE查询可以匹配中英文部分
            result = cartMapper.findByCat2(y);
            if(result==null || result.isEmpty()){
                throw new ProductNotFoundException("查询结果为空");
            }
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("use")){
            result = cartMapper.findByCat3(y);
            if(result==null){
                throw new ProductNotFoundException("查询结果为空");
            }
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        else{
            throw new SecurityException("查询错误");
        }
    }
//排序查询
    @Override
    public List<Cart> SearchProductBySort(String x) {
        List<Cart> result ;
        if(x.equals("wordUp")){
            result = cartMapper.sortByNameUp();
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("wordDown")){
            result = cartMapper.sortByNameDown();
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("timeUp")){
            result = cartMapper.sortByTimeUp();
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        if(x.equals("timeDown")){
            result = cartMapper.sortByTimeDown();
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        else{
            throw new SecurityException("查询错误");
        }
    }
//多字段查询
    @Override
    public List<Cart> SearchProductByMulti(String museum, String cat1, String cat2, String cat3) {
        List<Cart> result;
        result = cartMapper.findByMulti(museum,cat1,cat2,cat3);
        if(result!=null){
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                String [] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        else{
            throw new ProductNotFoundException("未查询到相关文物！");
        }
    }

    @Override
    public List<Cart> SearchProductObscure(String str) {
        List<Cart> result;
        result = cartMapper.obscureSearch(str);
        if(result!=null){
            for (int i=0;i<result.size();i++) {//只选取第一张图片作为显示
                Cart p = result.get(i);
                if(p.getImg_url()==null) continue;
                 String[] address = p.getImg_url().split(",");
                p.setImg_url(address[0]);
                result.set(i,p);
            }
            return result;
        }
        else{
            throw new ProductNotFoundException("未查询到相关文物！");
        }
    }
    ///相关推荐查询
        @Override
        public List<Cart> SearchCommentView(String museum, String cat2, String makers_name, String geography, BigInteger id){
            List<Cart> commentView=new ArrayList<Cart>();
            int n=4;
            Cart cart=new Cart();
            List<BigInteger> a1=new ArrayList<BigInteger>();
            cart=cartMapper.findBygeography(geography,id,0,1);
            if(cart != null){
                if(cart.getImg_url()!=null)
                {
                    String[] address = cart.getImg_url().split(",");
                    cart.setImg_url(address[0]);
                    
                }
                commentView.add(cart);
                a1.add(cart.getId());
                n--;
            }
            cart=cartMapper.findBytime1(cat2,id,0,1);
            if(cart!=null){
                for(int i=0;i<a1.size();i++){
                    if(cart==null)
                        break;
                    if(cart.getId()==a1.get(i)){
                        cart=cartMapper.findBygeography(cat2,id,Integer.valueOf(a1.get(i).toString()),1);
                        i=-1;
                    }
                }
                if(cart!=null){
                    if(cart.getImg_url()!=null)
                    {
                        String[] address = cart.getImg_url().split(",");
                        cart.setImg_url(address[0]);
                        
                    }
                    commentView.add(cart);
                    a1.add(cart.getId());
                    n--;}
            }
            cart=cartMapper.findBymakername1(makers_name,id,0,1);
            if(cart!=null){
                for(int i=0;i<a1.size();i++){
                    if(cart==null)
                        break;
                    if(cart.getId()==a1.get(i)){
                        cart=cartMapper.findBymakername1(makers_name,id,Integer.valueOf(a1.get(i).toString()),1);
                        i=-1;
                    }
                }
                if(cart!=null){
                    if(cart.getImg_url()!=null)
                    {
                        String[] address = cart.getImg_url().split(",");
                        cart.setImg_url(address[0]);
                        
                    }
                    commentView.add(cart);
                    a1.add(cart.getId());
                    n--;
                }
            }
            while(n!=0){
                cart=cartMapper.findByMuseum1(museum,id,0,1);
                if(cart!=null){
                    for(int i=0;i<a1.size();i++){
                        if(cart==null)
                            break;
                        if(cart.getId()==a1.get(i)){
                            cart=cartMapper.findByMuseum1(museum,id,Integer.valueOf(a1.get(i).toString()),1);
                            i=-1;
                        }
                    }}
                if(cart==null){
                    break;
                }
                if(cart.getImg_url()!=null)
                {
                    String[] address = cart.getImg_url().split(",");
                    cart.setImg_url(address[0]);
                    
                }
                commentView.add(cart);
                a1.add(cart.getId());
                n--;
            }
            while(n!=0){
                cart=cartMapper.findone(id,0,1);
                for(int i=0;i<a1.size();i++){
                    if(cart==null)
                        break;
                    if(cart.getId()==a1.get(i)){
                        cart=cartMapper.findone(id,Integer.valueOf(a1.get(i).toString()),1);
                        i=-1;
                    }
                }
                if(cart.getImg_url()!=null)
                {
                    String[] address = cart.getImg_url().split(",");
                    cart.setImg_url(address[0]);
                    
                }
                commentView.add(cart);
                n--;
            }
            return commentView;
        }
    
    /**
     * 中英文朝代名称映射转换
     * @param dynasty 输入的朝代名称（中文或英文）
     * @return 转换后的朝代名称
     */
    private String translateDynasty(String dynasty) {
        if (dynasty == null) {
            return null;
        }
        String trimmed = dynasty.trim();
        
        // 首先尝试移除括号及其内容，提取纯英文部分
        String englishPart = trimmed.replaceAll("（.*?）", "").trim();
        String chinesePart = "";
        // 提取中文部分（括号内的内容）
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("（(.*?)）").matcher(trimmed);
        if (matcher.find()) {
            chinesePart = matcher.group(1);
        }
        
        // 如果已经是纯中文，直接返回
        if (isChinese(trimmed)) {
            return trimmed;
        }
        
        // 英文转中文映射（支持纯英文和带括号格式）
        if ("Tang".equals(englishPart) || "Tang Dynasty".equals(trimmed)) {
            return "唐";
        } else if ("Song".equals(englishPart) || "Song Dynasty".equals(trimmed)) {
            return "宋";
        } else if ("Yuan".equals(englishPart) || "Yuan Dynasty".equals(trimmed)) {
            return "元";
        } else if ("Ming".equals(englishPart) || "Ming Dynasty".equals(trimmed)) {
            return "明";
        } else if ("Qing".equals(englishPart) || "Qing Dynasty".equals(trimmed)) {
            return "清";
        } else if ("Northern Wei".equals(englishPart) || "Northern Wei Dynasty".equals(trimmed)) {
            return "北魏";
        } else if ("Zhou".equals(englishPart) || "Zhou Dynasty".equals(trimmed)) {
            return "周";
        } else if ("Eastern Zhou".equals(englishPart) || "Eastern Zhou Dynasty".equals(trimmed)) {
            return "东周";
        } else if ("Northern Song".equals(englishPart)) {
            return "北宋";
        } else if ("Southern Song".equals(englishPart)) {
            return "南宋";
        } else if ("Eastern Han".equals(englishPart) || "Eastern Han Dynasty".equals(trimmed)) {
            return "东汉";
        } else if ("Western Han".equals(englishPart) || "Western Han Dynasty".equals(trimmed)) {
            return "西汉";
        } else if ("Shang".equals(englishPart) || "Shang Dynasty".equals(trimmed)) {
            return "商";
        } else if ("Qin".equals(englishPart) || "Qin Dynasty".equals(trimmed)) {
            return "秦";
        } else if ("Han".equals(englishPart) || "Han Dynasty".equals(trimmed)) {
            return "汉";
        } else if ("Jin".equals(englishPart) || "Jin Dynasty".equals(trimmed)) {
            return "晋";
        } else if ("Sui".equals(englishPart) || "Sui Dynasty".equals(trimmed)) {
            return "隋";
        } else if ("Three Kingdoms".equals(englishPart)) {
            return "三国";
        } else if ("Western Jin".equals(englishPart)) {
            return "西晋";
        } else if ("Eastern Jin".equals(englishPart)) {
            return "东晋";
        } else if ("Northern and Southern".equals(englishPart)) {
            return "南北朝";
        } else if ("Five Dynasties".equals(englishPart)) {
            return "五代";
        } else if ("Republic".equals(englishPart)) {
            return "民国";
        } else if ("Neolithic".equals(englishPart)) {
            return "新石器时代";
        } else {
            // 如果有中文部分，返回中文部分；否则返回原始值
            if (!chinesePart.isEmpty()) {
                return chinesePart;
            }
            return dynasty;
        }
    }
    
    /**
     * 判断字符串是否包含中文字符
     */
    private boolean isChinese(String str) {
        for (char c : str.toCharArray()) {
            if (Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }
}

