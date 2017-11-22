package com.corefac.dao;

import com.alicloud.openservices.tablestore.model.PrimaryKey;

import java.util.List;

/**
 * 表格存储DAO接口, 定义一些基本表格储存操作
 */
public interface BaseTableOperation<T> {
    void createTable();
    int putRow(T t);
    int updateRow(T t);

    /**
     * 获取主键的过滤列表
     */
    List<String> getFilterList();

    /**
     * 读取数据时补充主键的字段
     */
    T fillPrimaryKeyValue(T t, PrimaryKey primaryKey);
}
