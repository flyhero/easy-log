package io.github.flyhero.easylog.compare;


/**
 * @author WangQingFei(qfwang666 @ 163.com)
 * @date 2022/2/20 22:52
 */

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


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getOldFieldType() {
        return oldFieldType;
    }

    public void setOldFieldType(Class<?> oldFieldType) {
        this.oldFieldType = oldFieldType;
    }

    public Class<?> getNewFieldType() {
        return newFieldType;
    }

    public void setNewFieldType(Class<?> newFieldType) {
        this.newFieldType = newFieldType;
    }

    public Object getOldFieldVal() {
        return oldFieldVal;
    }

    public void setOldFieldVal(Object oldFieldVal) {
        this.oldFieldVal = oldFieldVal;
    }

    public Object getNewFieldVal() {
        return newFieldVal;
    }

    public void setNewFieldVal(Object newFieldVal) {
        this.newFieldVal = newFieldVal;
    }
}
