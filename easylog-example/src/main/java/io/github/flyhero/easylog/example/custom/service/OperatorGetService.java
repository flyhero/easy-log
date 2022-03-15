package io.github.flyhero.easylog.example.custom.service;

import io.github.flyhero.easylog.service.IOperatorService;
import org.springframework.stereotype.Service;

/**
 * @author WangQingFei(qfwang666@163.com)
 * @date 2022/2/26 11:44
 */
@Service
public class OperatorGetService implements IOperatorService {
    @Override
    public String getOperator() {
        return "test";
    }

    @Override
    public String getTenant() {
        return "company";
    }
}
