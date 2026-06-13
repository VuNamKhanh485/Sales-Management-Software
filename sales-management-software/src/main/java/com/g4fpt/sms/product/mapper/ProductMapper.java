package com.g4fpt.sms.product.mapper;



import com.g4fpt.sms.product.dto.request.ProductRequest;
import com.g4fpt.sms.product.dto.response.ProductResponse;
import com.g4fpt.sms.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final ProductUnitMapper productUnitMapper;


    public ProductRequest toRequest(ProductResponse productResponse) {

        if (productResponse == null) {
            return null;
        }

        ProductRequest request = new ProductRequest();

        request.setCategoryId(productResponse.getCategory().getId()
        );

        request.setBrandId(productResponse.getBrand().getId());

        request.setName(productResponse.getName());
        request.setImageUrl(productResponse.getImageUrl());
        request.setDescription(productResponse.getDescription());
        request.setStatus(productResponse.getStatus());
        request.setNote(productResponse.getNote());

        if (productResponse.getProductUnitsResponses() != null) {
            request.setProductUnitsRequest(
                    productResponse.getProductUnitsResponses()
                            .stream()
                            .map(productUnitMapper::toRequest)
                            .toList()
            );
        }

        return request;
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
