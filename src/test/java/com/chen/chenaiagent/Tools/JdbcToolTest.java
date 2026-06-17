package com.chen.chenaiagent.Tools;

import com.chen.chenaiagent.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class JdbcToolTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Test
    void testJdbcTool() {
        JdbcTool jdbcTool = new JdbcTool(jdbcTemplate);
        User userById = jdbcTool.getUserById("select * from \"user\" where id=1");
        assertNotNull(userById);
    }

}