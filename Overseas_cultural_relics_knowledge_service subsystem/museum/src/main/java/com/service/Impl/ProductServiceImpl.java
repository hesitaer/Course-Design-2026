package com.service.Impl;

import com.entity.Cart;
import com.entity.Product;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.IProductService;

import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

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
            product.setImageUrl(findImageMapper.findImage(product.getImageUrl()));
        }
        return product;
    }


}
