package com.github.flyhero.easylog.compare;

import lombok.Data;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/20 22:52
 */

@Data
public class FieldInfo {

    private String fieldName;

    private Class<?> oldFieldType;

    private Class<?> newFieldType;

    private Object oldFieldVal;

    private Object newFieldVal;
}
