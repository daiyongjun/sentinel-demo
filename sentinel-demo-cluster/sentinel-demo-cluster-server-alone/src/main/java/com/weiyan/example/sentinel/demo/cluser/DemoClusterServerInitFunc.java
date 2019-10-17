package com.weiyan.example.sentinel.demo.cluser;

import com.alibaba.csp.sentinel.init.InitFunc;

/**
 * 类的描述
 *
 * @author daiyongjun
 * @version 1.0
 * Created on date: 2019/10/16 17:18
 */
public class DemoClusterServerInitFunc implements InitFunc {

    private final String remoteAddress = "localhost";
    private final String groupId = "SENTINEL_GROUP";
    private final String namespaceSetDataId = "cluster-server-namespace-set";
    private final String serverTransportDataId = "cluster-server-transport-config";

    @Override
    public void init() throws Exception {

    }
}
