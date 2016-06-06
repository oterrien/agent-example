package com.ote.test;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyPropertyChange {

    String method();
}
