package com.lsm.common;

import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

@Retention(CLASS)
@Inherited
public @interface Lazy {
 boolean value() default true;
}
