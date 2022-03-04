package com.github.flyhero.easylog.model;

import com.github.flyhero.easylog.compare.FieldInfo;
import lombok.Data;

import java.util.List;

@Data
public class EasyLogOps {

    private String tenant;

    private String operator;

    private String bizNo;

    private String module;

    private String type;

    private String content;

    private String fail;

    private String details;

    private String condition;

}
