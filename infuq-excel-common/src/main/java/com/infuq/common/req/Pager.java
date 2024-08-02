package com.infuq.common.req;


import lombok.Data;

@Data
public class Pager {

    private int offset;

    private int rowCount;
}
