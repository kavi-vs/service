package kavi.tech.service.service;

import kavi.tech.service.mybatis.entity.Group;
import kavi.tech.service.mybatis.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import tech.kavi.vs.mybatis.AsyncHelperKt;

@Service
public class GroupService {
    @Autowired
    private GroupMapper groupMapper;

    /**
     * 查询所有用户结果
     * */
    public Observable<Integer> add(String name) {
        Group group = new Group();
        group.setName(name);
        return AsyncHelperKt.observableAsync(() -> groupMapper.add(group));
    }
}
