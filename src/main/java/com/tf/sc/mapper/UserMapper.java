package com.tf.sc.mapper;

import com.tf.sc.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);

    int update(User user);

    User findById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);

    User findByPhone(@Param("phone") String phone);

    User findByEmail(@Param("email") String email);

    List<User> findAll();

    List<User> findCouriers();

    List<User> findDeletionPending();

    int updateLoginFailCount(@Param("userId") Long userId, @Param("count") Integer count);

    int lockUser(@Param("userId") Long userId, @Param("lockUntil") String lockUntil);

    int unlockUser(@Param("userId") Long userId);
}
