package kavi.tech.service.http.user;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import kavi.tech.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import tech.kavi.vs.web.ControllerHandler;
import tech.kavi.vs.web.HandlerRequest;

@HandlerRequest(path = "/list", method = HttpMethod.GET)
public class ListHandler extends ControllerHandler {

    @Autowired
    private UserService userService;

    @Override
    public void handle(RoutingContext routingContext){
        userService.listAll().subscribe(it -> {
            routingContext.response().end(it.toString());
        }, e-> routingContext.response().end(e.getMessage()));
    }
}
