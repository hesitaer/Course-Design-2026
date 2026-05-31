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
    public Product findByProductId(Integer museumId, String objectId){
        Product product = productMapper.findByProductId(museumId, objectId);
        if(product == null) {
            throw new ProductNotFoundException("该文物不存在");
        }
        if(product.getImageUrl() != null) {
            String[] address = product.getImageUrl().split(",");
            product.setImageUrl(address[0]);
        }
        return product;
    }

    @Override
    public List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findByPage(params, offset, pageSize);
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
            }
        }
        return products;
    }

    @Override
    public Integer countByParams(Map<String, Object> params) {
        return productMapper.countByParams(params);
    }

    @Override
    public List<Product> findByIds(List<String> ids) {
        List<Product> products = productMapper.findByIds(ids);
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
            }
        }
        return products;
    }

    @Override
    public List<String> getAllTypes() {
        return productMapper.getAllTypes();
    }

    @Override
    public List<String> getAllDynasties() {
        return productMapper.getAllDynasties();
    }

    @Override
    public List<String> getAllMuseums() {
        return productMapper.getAllMuseums();
    }

    @Override
    public List<String> getAllMaterials() {
        return productMapper.getAllMaterials();
    }

    @Override
    public List<Product> findByConditions(ProductQueryDTO queryDTO) {
        List<Product> products = productMapper.findByConditions(queryDTO);
        processImageUrls(products);
        return products;
    }

    @Override
    public Map<String, Object> findByConditionsWithPage(ProductQueryDTO queryDTO) {
        Map<String, Object> result = new HashMap<>();

        Integer pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        Integer pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10;
        Integer offset = (pageNum - 1) * pageSize;

        List<Product> products = productMapper.findByConditionsWithPage(queryDTO, offset, pageSize);
        Integer total = productMapper.countByConditions(queryDTO);

        processImageUrls(products);

        result.put("list", products);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return result;
    }

    @Override
    public List<Product> searchByKeyword(String keyword) {
        List<Product> products = productMapper.searchByKeyword(keyword);
        processImageUrls(products);
        return products;
    }

    @Override
    public String exportToCSV(ProductQueryDTO queryDTO) {
        List<Product> products = productMapper.findByConditions(queryDTO);

        StringBuilder csv = new StringBuilder();
        csv.append("museumId,objectId,title,artist,dynasty,period,type,material,culture,museum,location,imageUrl\n");

        for (Product product : products) {
            csv.append(escapeCSV(product.getMuseumId() != null ? product.getMuseumId().toString() : "")).append(",");
            csv.append(escapeCSV(product.getObjectId())).append(",");
            csv.append(escapeCSV(product.getTitle())).append(",");
            csv.append(escapeCSV(product.getArtist())).append(",");
            csv.append(escapeCSV(product.getDynasty())).append(",");
            csv.append(escapeCSV(product.getPeriod())).append(",");
            csv.append(escapeCSV(product.getType())).append(",");
            csv.append(escapeCSV(product.getMaterial())).append(",");
            csv.append(escapeCSV(product.getCulture())).append(",");
            csv.append(escapeCSV(product.getMuseum())).append(",");
            csv.append(escapeCSV(product.getLocation())).append(",");
            csv.append(escapeCSV(product.getImageUrl())).append("\n");
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

    private void processImageUrls(List<Product> products) {
        for (Product product : products) {
            if(product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
            }
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
