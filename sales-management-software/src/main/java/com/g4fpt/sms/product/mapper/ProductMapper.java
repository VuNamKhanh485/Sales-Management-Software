package com.g4fpt.sms.product.mapper;



import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final ProductUnitMapper productUnitMapper;

    public ProductMapper (CategoryMapper categoryMapper, BrandMapper brandMapper, ProductUnitMapper productUnitMapper) {
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
        this.productUnitMapper =  productUnitMapper;
    }

    public ProductResponse toResponse(Product product) {

        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());

        response.setCategory(
                categoryMapper.toResponse(
                        product.getCategory()));

        response.setBrand(
                brandMapper.toResponse(
                        product.getBrand()));

        response.setName(product.getName());
        response.setImageUrl(product.getImageUrl());
        response.setDescription(product.getDescription());
        response.setStatus(product.getStatus());
        response.setNote(product.getNote());

        if (product.getProductUnits() != null) {

            response.setProductUnitsResponses(
                    product.getProductUnits()
                            .stream()
                            .map(productUnitMapper::toResponse)
                            .toList()
            );
        }

        return response;
    }
}
