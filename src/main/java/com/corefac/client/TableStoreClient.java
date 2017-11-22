package com.corefac.client;

import com.alicloud.openservices.tablestore.SyncClient;
import com.corefac.config.TableStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TableStoreClient {
    @Autowired
    private TableStoreConfig tableStoreConfig;

    @Bean
    public SyncClient getSyncClient() {
        SyncClient syncClient = new SyncClient(tableStoreConfig.getEndPoint(), tableStoreConfig.getAccessId(),
                tableStoreConfig.getAccessKey(), tableStoreConfig.getInstanceName());
        return syncClient;
    }
}
