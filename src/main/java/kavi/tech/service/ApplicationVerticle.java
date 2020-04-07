package kavi.tech.service;


import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import kavi.tech.service.http.HttpController;
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

        Integer port = 80;
        try {
            port = config.getInteger("HTTP.PORT");
        } catch (Exception e) {
            logger.error(e);
        }
        vertx.createHttpServer().requestHandler(router).listen(port);
    }

    public static void main(String[] args ) {
        launcher(ApplicationVerticle.class);
    }
}
