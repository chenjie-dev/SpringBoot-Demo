package com.chenjie.elasticsearch.utils;

public class PageUtil {
    public static int checkPageNum(Integer inputPageNum) {
        return (inputPageNum == null || inputPageNum < 0) ? 1 : inputPageNum;
    }

    public static int checkPageSize(Integer inputPageSize) {
        return (inputPageSize == null || inputPageSize < 0) ? 10 : inputPageSize;
    }
}
