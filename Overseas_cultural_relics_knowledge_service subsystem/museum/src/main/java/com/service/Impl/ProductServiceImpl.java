package com.service.Impl;

import com.entity.Product;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.IProductService;
import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FindImageMapper findImageMapper;
    
    /**
     * 通过复合主键查询文物详情
     * @param museumId 博物馆ID
     * @param objectId 文物编号
     * @return Product 文物对象
     */
    @Override
    public Product findByProductId(Integer museumId, String objectId){
        Product product = productMapper.findByProductId(museumId, objectId);
        if(product == null) {
            throw new ProductNotFoundException("该文物不存在");
        }
        // 处理图片URL
        if(product.getImageUrl() != null) {
            String[] address = product.getImageUrl().split(",");
            product.setImageUrl(address[0]);
            // 注释掉图片表查询，因为该表不存在
            // product.setImageUrl(findImageMapper.findImage(product.getImageUrl()));
        }
        return product;
    }
    
    /**
     * 分页查询文物列表（支持筛选和排序）
     */
    @Override
    public List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findByPage(params, offset, pageSize);
        // 处理图片URL
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
                product.setImageUrl(findImageMapper.findImage(product.getImageUrl()));
            }
        }
        return products;
    }
    
    /**
     * 查询文物总数
     */
    @Override
    public Integer countByParams(Map<String, Object> params) {
        return productMapper.countByParams(params);
    }
    
    /**
     * 根据多个ID批量查询文物（用于对比功能）
     */
    @Override
    public List<Product> findByIds(List<String> ids) {
        List<Product> products = productMapper.findByIds(ids);
        // 处理图片URL
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
                product.setImageUrl(findImageMapper.findImage(product.getImageUrl()));
            }
        }
        return products;
    }
    
    /**
     * 获取所有文物类型
     */
    @Override
    public List<String> getAllTypes() {
        return productMapper.getAllTypes();
    }
    
    /**
     * 获取所有朝代
     */
    @Override
    public List<String> getAllDynasties() {
        return productMapper.getAllDynasties();
    }
    
    /**
     * 获取所有博物馆
     */
    @Override
    public List<String> getAllMuseums() {
        return productMapper.getAllMuseums();
    }
    
    /**
     * 获取所有材质
     */
    @Override
    public List<String> getAllMaterials() {
        return productMapper.getAllMaterials();
    }
}
