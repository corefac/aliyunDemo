package com.corefac.dao;

import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 调用阿里云表格储存的通用接口
 */
@Component
public class TableStore {
    private final Logger logger = LogManager.getLogger(TableStore.class);
    private int timeToLive = -1;
    private int maxVersions = 1;
    private boolean isAsync = false;

    // 表格存储对象
    @Autowired
    private SyncClient client;

    //建表
    public String createTable(TableMeta tableMeta) {
        try {
            TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
            CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
            CreateTableResponse createTableResponse = client.createTable(createTableRequest);
            return createTableResponse.getRequestId();
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    public CreateTableResponse createTableResponse(TableMeta tableMeta) {
        try {
            TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
            CreateTableRequest createTableRequest = new CreateTableRequest(tableMeta, tableOptions);
            CreateTableResponse createTableResponse = client.createTable(createTableRequest);
            return createTableResponse;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    //插入数据
    public String putRow(RowPutChange rowPutChange) {
        try {
            PutRowResponse putRowResponse = client.putRow(new PutRowRequest(rowPutChange));
            return putRowResponse.getTraceId();
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    public PutRowResponse putRowResponse(RowPutChange rowPutChange) {
        try {
            PutRowResponse putRowResponse = client.putRow(new PutRowRequest(rowPutChange));
            return putRowResponse;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    // 更新数据
    public String updateRow(RowUpdateChange rowUpdateChange) {
        try {
            UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
            return updateRowResponse.getRequestId();
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    public UpdateRowResponse updateRowResponse(RowUpdateChange rowUpdateChange) {
        try {
            UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
            return updateRowResponse;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }

    //取单行数据
    public Row getRow(SingleRowQueryCriteria criteria) {
        try {
            // 设置读取最新版本
            criteria.setMaxVersions(1);
            GetRowResponse getRowResponse = client.getRow(new GetRowRequest(criteria));
            Row row = getRowResponse.getRow();
            return row;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }
    public List<Row> getRange(RangeRowQueryCriteria rangeRowQueryCriteria) {
        try {
            rangeRowQueryCriteria.setMaxVersions(1);
            List<Row> rows = new ArrayList<>();
            while (true) {
                GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
                rows.addAll(getRangeResponse.getRows());
                logger.debug("rows: " + rows.size());
                logger.debug("time: " + Calendar.getInstance());
                System.out.println("rows: " + rows.size());
                System.out.println("time: " + (new Date()).toString());
                // 若nextStartPrimaryKey不为null, 则继续读取.
                if (getRangeResponse.getNextStartPrimaryKey() != null) {
                    rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
                } else {
                    break;
                }
            }
            return rows;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//            client.shutdown();
        }
        return null;
    }
    public int deleteRow(RowDeleteChange rowDeleteChange) {
        try {
            client.deleteRow(new DeleteRowRequest(rowDeleteChange));
            return 1;
        } catch (ClientException ce){
            logger.error(ce.getMessage());
        } finally {
//	        client.shutdown();
        }
        return 0;
    }
    public int count(RangeRowQueryCriteria rangeRowQueryCriteria) {
        List<Row> list = getRange(rangeRowQueryCriteria);
        return list.size();
    }

    public int getTimeToLive() {
        return timeToLive;
    }
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }
    public int getMaxVersions() {
        return maxVersions;
    }
    public void setMaxVersions(int maxVersions) {
        this.maxVersions = maxVersions;
    }
    public boolean isAsync() {
        return isAsync;
    }
    public void setAsync(boolean async) {
        isAsync = async;
    }
}
