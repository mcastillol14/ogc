package com.ogc_prototype.ogc.exception;

import org.springframework.http.HttpStatus;

public class CategoryException extends AppException {

    private CategoryException(HttpStatus status, String message) {
        super(status, message);
    }

    public static CategoryException notFound(Integer id) {
        return new CategoryException(HttpStatus.NOT_FOUND, "Categoría no encontrada con id: " + id);
    }

    public static CategoryException notFound(String slug) {
        return new CategoryException(HttpStatus.NOT_FOUND,
                "Categoría no encontrada con slug: " + slug);
    }

    public static CategoryException duplicateSlug(String slug) {
        return new CategoryException(HttpStatus.CONFLICT,
                "Ya existe una categoría con el slug '" + slug + "'");
    }
}

