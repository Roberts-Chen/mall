package com.chentao.mall.mapper;

import com.chentao.mall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    /**
     * 使用inserSelective只给有值的字段赋值（会对传进来的值做非空判断）
     * @param record
     * @return
     */
    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(String username);

    int countByEmail(String email);

    User selectByUsername(String username);
}