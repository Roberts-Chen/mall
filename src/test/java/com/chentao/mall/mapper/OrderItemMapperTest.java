package com.chentao.mall.mapper;

import com.chentao.mall.pojo.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class OrderItemMapperTest {

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void deleteByPrimaryKey() {

    }

    @Test
    void insert() {
    }

    @Test
    void insertSelective() {
    }

    @Test
    void selectByPrimaryKey() {
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(1);
        System.out.println(orderItem);
        if (jdbcTemplate == null) {
            System.out.println("is null");
        }
    }

    @Test
    void updateByPrimaryKeySelective() {
    }

    @Test
    void updateByPrimaryKey() {
    }
}