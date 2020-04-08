package kavi.tech.service.service;

import io.vertx.core.json.JsonArray;
import kavi.tech.service.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Single;
import tech.kavi.vs.mybatis.AsyncHelperKt;
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有用户结果
     * */
    public Single<JsonArray> listAll() {
        return AsyncHelperKt.singleAsync(userMapper::list).map(JsonArray::new);
    }
}
