package com.chen.chenaiagent.Tools;

import com.chen.chenaiagent.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Slf4j
public class JdbcTool {
    public JdbcTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private JdbcTemplate jdbcTemplate;
    @Tool(description = "Query user information from database by SQL")
    public User getUserById(@ToolParam(description = "SQL query to fetch user data")String sql) {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        if (maps.isEmpty()) {
            log.error("用户不存在");
            return null;
            }
        User user = new User();
        user.setId(Long.parseLong(maps.get(0).get("id").toString()));
        user.setUsername(maps.get(0).get("username").toString());
        user.setPasswordHash(maps.get(0).get("password_hash").toString());
        user.setNickname(maps.get(0).get("nickname").toString());
        user.setEmail(maps.get(0).get("email").toString());
        user.setPhone(maps.get(0).get("phone").toString());
        user.setAvatarUrl(maps.get(0).get("avatar_url").toString());
        user.setGender(Integer.parseInt(maps.get(0).get("gender").toString()));
        user.setRole(maps.get(0).get("role").toString());
        user.setStatus(maps.get(0).get("status").toString());
        return user;
    }
}

