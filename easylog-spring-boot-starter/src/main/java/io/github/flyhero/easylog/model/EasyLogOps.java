package io.github.flyhero.easylog.model;

import lombok.Data;

@Data
public class EasyLogOps {

    private String tenant;

    private String operator;

    private String bizNo;

    private String module;

    private String type;

    private String success;

    private String fail;

    private String details;

    private String condition;

}
