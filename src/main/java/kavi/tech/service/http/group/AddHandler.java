package kavi.tech.service.http.group;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import kavi.tech.service.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import tech.kavi.vs.web.ControllerHandler;
import tech.kavi.vs.web.HandlerRequest;

@HandlerRequest(path = "/add", method = HttpMethod.POST)
public class AddHandler extends ControllerHandler {

    @Autowired
    private GroupService groupService;

    @Override
    public void handle(RoutingContext routingContext){
        String name;
        try {
            name = routingContext.getBodyAsJson().getString("name");
        } catch (Exception e) {
            routingContext.fail(e);
            return;
        }
        groupService.add(name).subscribe(it -> {
            routingContext.response().end(it.toString());
        }, e-> routingContext.response().end(e.getMessage()));
    }
}
