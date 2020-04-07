package kavi.tech.service.http;

import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import tech.kavi.vs.web.Controller;
import tech.kavi.vs.web.HandlerScan;

@HandlerScan({"user", "group"})
public class HttpController extends Controller{
    @Autowired
    Vertx vertx;

    @Override
    public Vertx getVertx() {
        return vertx;
    }
}