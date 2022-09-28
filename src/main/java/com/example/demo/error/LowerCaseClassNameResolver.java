package com.example.demo.error;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

public class LowerCaseClassNameResolver extends TypeIdResolverBase{

    @Override
    public Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public String idFromValue(Object arg0) {
        return arg0.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String idFromValueAndType(Object arg0, Class<?> arg1) {
        return idFromValue(arg0);
    }

}
