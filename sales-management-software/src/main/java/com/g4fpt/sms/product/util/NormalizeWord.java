package com.g4fpt.sms.product.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;

public final class NormalizeWord{
    private NormalizeWord(){}
    // Chuẩn hóa 1 cột trong SQL: lowercase -> gộp khoảng trắng thừa
    public static Expression<String> normalizeSqlColumn(CriteriaBuilder cb, Expression<String> column) {
        return cb.function(
                "regexp_replace", String.class,
                cb.lower(column),
                cb.literal("\\s+"),
                cb.literal(" ")
        );
    }

    // Chuẩn hóa keyword phía Java: lowercase -> gộp khoảng trắng thừa
    public static String normalize(String input) {
        return input.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ");
    }
}
