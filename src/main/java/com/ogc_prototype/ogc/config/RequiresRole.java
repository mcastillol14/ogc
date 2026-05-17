package com.ogc_prototype.ogc.config;

import com.ogc_prototype.ogc.model.enums.Role;

import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {
    Role[] value();
}
