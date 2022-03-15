package io.github.flyhero.easylog.compare;

import java.util.List;

/**
 * 比较器
 * @author qfwang666@163.com
 * @date 2022/2/20 22:51
 */
public interface Equator {

    /**
     * 获取两个对象中对应属性值的不同
     *
     * @param oldObj 老的对象
     * @param newObj 新的对象
     * @return 不同值的属性集合
     */
    List<FieldInfo> getDiffField(Object oldObj, Object newObj);
}
