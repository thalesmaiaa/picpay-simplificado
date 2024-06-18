package com.example.picpay.services;


import com.example.picpay.exceptions.MissingFieldException;

import java.lang.reflect.Field;
import java.util.Objects;

public class AbstractValidationService {


    protected void validateFields(Object obj) {
        Class<?> clazz = obj.getClass();

        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);


                if (Objects.isNull(value)) {
                    throw new MissingFieldException(field.getName() + " is missing.");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
