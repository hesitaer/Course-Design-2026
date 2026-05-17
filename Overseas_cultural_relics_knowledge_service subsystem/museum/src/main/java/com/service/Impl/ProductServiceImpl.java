package com.service.Impl;

import com.entity.Product;
import com.entity.ProductQueryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.IProductService;
import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FindImageMapper findImageMapper;

    @Override
    public Product findByProduct_id(BigInteger id){
        Product product ;
        product = productMapper.findByProduct_id(id);
        if(product == null)
        {
            throw new ProductNotFoundException("该文物不存在");
        }
        if(product.getImg_url()==null) return product;
        String[] address = product.getImg_url().split(",");
        product.setImg_url(address[0]);
        product.setImg_url(findImageMapper.findImage(product.getImg_url()));
        return product;
    }

    @Override
    public List<Product> findByConditions(ProductQueryDTO queryDTO) {
        return productMapper.findByConditions(queryDTO);
    }

    @Override
    public Map<String, Object> findByConditionsWithPage(ProductQueryDTO queryDTO) {
        Map<String, Object> result = new HashMap<>();
        
        Integer pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        Integer pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
        Integer offset = (pageNum - 1) * pageSize;
        
        List<Product> products = productMapper.findByConditionsWithPage(queryDTO, offset, pageSize);
        Integer total = productMapper.countByConditions(queryDTO);
        
        result.put("list", products);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        return result;
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        return productMapper.searchByKeyword(keyword);
    }

    @Override
    public String exportToCSV(ProductQueryDTO queryDTO) {
        List<Product> products = productMapper.findByConditions(queryDTO);
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,文物名称,材质,展区,时期,产地,博物馆,图片链接,简介,规格,出处,捐赠信息,参考文献,前拥有者,制作者,制作者职业,制作者出生年份\n");
        
        for (Product product : products) {
            csv.append(product.getId()).append(",");
            csv.append(escapeCSV(product.getObject_name())).append(",");
            csv.append(escapeCSV(product.getMedium())).append(",");
            csv.append(escapeCSV(product.getObject_type())).append(",");
            csv.append(escapeCSV(product.getTime_period())).append(",");
            csv.append(escapeCSV(product.getGeography())).append(",");
            csv.append(escapeCSV(product.getMuseum())).append(",");
            csv.append(escapeCSV(product.getImg_url())).append(",");
            csv.append(escapeCSV(product.getLabel())).append(",");
            csv.append(escapeCSV(product.getDimensions())).append(",");
            csv.append(escapeCSV(product.getProvenance())).append(",");
            csv.append(escapeCSV(product.getCredit())).append(",");
            csv.append(escapeCSV(product.getBibliography())).append(",");
            csv.append(escapeCSV(product.getPrevious_owner())).append(",");
            csv.append(escapeCSV(product.getMakers_name())).append(",");
            csv.append(escapeCSV(product.getMakers_job())).append(",");
            csv.append(escapeCSV(product.getMakers_born())).append("\n");
        }
        
        return csv.toString();
    }

    @Override
    public String exportToJSON(ProductQueryDTO queryDTO) {
        List<Product> products = productMapper.findByConditions(queryDTO);
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(products);
        } catch (Exception e) {
            throw new RuntimeException("JSON导出失败", e);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
