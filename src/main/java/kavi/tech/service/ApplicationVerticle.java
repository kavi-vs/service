package kavi.tech.service;


import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import kavi.tech.service.http.HttpController;
import kavi.tech.service.ms.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tech.kavi.vs.core.LauncherVerticle;
import tech.kavi.vs.mybatis.MybatisDataSourceBean;
import tech.kavi.vs.web.HandlerRequestAnnotationBeanName;

@Import({BeanConfig.class, MybatisDataSourceBean.class})
@ComponentScan(nameGenerator= HandlerRequestAnnotationBeanName.class)
public class ApplicationVerticle extends LauncherVerticle {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Router router;

    @Autowired
    private HttpController httpController;

    @Autowired
    @Qualifier("config")
    private JsonObject config;

    @Override
    public void start() throws Exception {
        super.start();
        router.route().handler(BodyHandler.create());
        httpController.create(router, "/http");

        /**
         * 服务代理方式获取数据
         * */
        UserService.Factory.create(vertx).list(new JsonObject(), it -> {
            if (it.succeeded()) {
                logger.info(it.result());
            } else {
                logger.error(it.cause());
            }
        });


        Integer port = config.getInteger("HTTP.PORT");
        port = port != null ? port : 80;
        vertx.createHttpServer().requestHandler(router).listen(port, ar -> {
            if (ar.succeeded()) {
                logger.info("Success create http server port:" + ar.result().actualPort());
            } else {
                logger.error(ar.cause());
            }
        });
    }

    public static void main(String[] args ) {
        launcher(ApplicationVerticle.class);
    }
}
