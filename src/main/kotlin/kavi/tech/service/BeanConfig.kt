package kavi.tech.service

import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.rxjava.ext.asyncsql.AsyncSQLClient
import io.vertx.rxjava.ext.asyncsql.MySQLClient
import io.vertx.rxjava.ext.mongo.MongoClient
import io.vertx.spi.cluster.consul.ConsulClusterManager
import kavi.tech.service.common.strategry.HashStrategy
import kavi.tech.service.common.strategry.PBKDF2Strategy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import tech.kavi.vs.core.VertxBeans

/**
 * 依赖参数全局初始化
 * */
class BeanConfig : VertxBeans() {

    /**
     * 部署为Consul集群模式
    JsonObject options = new JsonObject()
    .put("host", "consulAgentHost") // host on which consul agent is running, if not specified default host will be used which is "localhost".
    .put("port", consulAgentPort) // port on wich consul agent is runing, if not specified default port will be used which is "8500".
    /*
     * There's an option to utilize built-in internal caching.
     * @{Code false} - enable internal caching of event bus subscribers - this will give us better latency but stale reads (stale subsribers) might appear.
     * @{Code true} - disable internal caching of event bus subscribers - this will give us stronger consistency in terms of fetching event bus subscribers,
     * but this will result in having much more round trips to consul kv store where event bus subs are being kept.
    */
    .put("preferConsistency", false)
    /*
     * There's also an option to specify explictly host address on which given cluster manager will be operating on.
     * By defult InetAddress.getLocalHost().getHostAddress() will be executed.
     * Linux systems enumerate the loopback network interface the same way as regular LAN network interfaces, but the JDK
     * InetAddress.getLocalHost method does not specify the algorithm used to select the address returned under such circumstances, and will
     * often return the loopback address, which is not valid for network communication.
    */
    .put("nodeHost", "10.0.0.1");
     * */
    override fun vertxOptions(options: VertxOptions, vertxOptionsJson: JsonObject?): VertxOptions {
        val discoveryConfig = vertxOptionsJson.value<JsonObject>("discovery")
        if (options.eventBusOptions.isClustered && discoveryConfig != null) {
            options.clusterManager = ConsulClusterManager(discoveryConfig)
        }
        return options
    }

    /**
     * 注入router
     */
    @Bean
    fun router(vertx: Vertx) = Router.router(vertx)

    /**
     * 密码加密算法
     * */
    @Bean
    fun strategy(vertx: Vertx): HashStrategy = PBKDF2Strategy(vertx)

    /**
     * rx模式vertx
     * */
    @Bean
    fun rxVertx(vertx: Vertx) = io.vertx.rxjava.core.Vertx(vertx)

    /**
     * mysql数据库连接
     * */
    @Bean
    fun mysqlClient(rxVertx: io.vertx.rxjava.core.Vertx, @Qualifier("config") config: JsonObject): AsyncSQLClient{
        return MySQLClient.createShared(rxVertx, config.value("MYSQL", JsonObject()))
    }

    /**
     * Mongo 数据库连接
     * */
    @Bean
    fun mongoClient(rxVertx: io.vertx.rxjava.core.Vertx, @Qualifier("config") config: JsonObject): MongoClient {
        return MongoClient.createShared(rxVertx,  config.value("MONGO", JsonObject()))
    }
}