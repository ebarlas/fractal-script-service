package org.barlas.fractal.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.FactoryBean;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

    @Override
    public ObjectMapper getObject() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getSerializationConfig().withSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
