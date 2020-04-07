package kavi.tech.service.mybatis.mapper;


import kavi.tech.service.mybatis.entity.Group;

import java.util.List;

public interface GroupMapper {
    List<Group> list();

    Integer add(Group group);
}
