package com.g4fpt.sms.product.util;

import com.g4fpt.sms.product.dto.request.ProductFilterRequest;
import com.g4fpt.sms.product.entity.Product;
import com.g4fpt.sms.product.enums.ProductStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    private ProductSpecification() {
        /* This utility class should not be instantiated */
    }

    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String normalizedKeyword = NormalizeWord.normalize(keyword);
            String pattern = "%" + normalizedKeyword + "%";

            query.distinct(true);
            var unitJoin = root.join("productUnits", JoinType.LEFT);

            Expression<String> normalizedName = NormalizeWord.normalizeSqlColumn(cb, root.get("name"));
            Expression<String> normalizedSku = NormalizeWord.normalizeSqlColumn(cb, unitJoin.get("sku"));
            Expression<String> normalizedBarcode = NormalizeWord.normalizeSqlColumn(cb, unitJoin.get("barcodeUnit"));

            return cb.or(
                    cb.like(normalizedName, pattern),
                    cb.like(normalizedSku, pattern),
                    cb.like(normalizedBarcode, pattern));
        };
    }

    public static Specification<Product> hasBrand(Long brandId) {
        return (root, query, cb) -> brandId == null
                ? cb.conjunction()
                : cb.equal(root.get("brand").get("id"), brandId);
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null
                ? cb.conjunction()
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Product> fromFilter(ProductFilterRequest filter) {
        return Specification.<Product>unrestricted()
                .and(hasKeyword(filter.getKeyword()))
                .and(hasBrand(filter.getBrandId()))
                .and(hasCategory(filter.getCategoryId()))
                .and(hasStatus(filter.getStatus()));
    }

}
