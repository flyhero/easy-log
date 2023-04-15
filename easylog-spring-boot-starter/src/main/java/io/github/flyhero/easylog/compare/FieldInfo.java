package io.github.flyhero.easylog.compare;

import lombok.Data;

/**
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/20 22:52
 */

@Data
public class FieldInfo {

    /**
     * 属性名
     */
    private String fieldName;

    /**
     * 老的属性类型
     */
    private Class<?> oldFieldType;

    /**
     * 新的属性类型
     */
    private Class<?> newFieldType;

    /**
     * 老的属性值
     */
    private Object oldFieldVal;

    /**
     * 新的属性值
     */
    private Object newFieldVal;
}
