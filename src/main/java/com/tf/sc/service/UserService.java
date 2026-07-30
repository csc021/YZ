package com.tf.sc.service;

import com.tf.sc.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    User findByPhone(String phone);

    User findByEmail(String email);

    boolean register(User user);

    User login(String phone, String password);

    User courierLogin(String username, String password);

    /**
     * @deprecated Use courierLogin.
     */
    @Deprecated
    User adminLogin(String username, String password);

    boolean registerCourier(User user);

    /**
     * @deprecated Use registerCourier.
     */
    @Deprecated
    boolean adminRegister(User user);

    boolean updateUser(User user);

    boolean updateAvatar(Long userId, String avatarUrl);

    User findById(Long id);

    List<User> findAll();

    List<User> findCouriers();

    List<User> findDeletionPending();

    boolean lockUser(Long userId);

    boolean unlockUser(Long userId);

    boolean changePassword(Long userId, String oldPassword, String newPassword);

    boolean resetPassword(String phone, String newPassword);

    boolean auditUser(Long userId, Integer auditStatus, String rejectReason);

    boolean requestDeletion(Long userId);

    boolean applyEmployee(Long userId);

    boolean cancelEmployee(Long userId);
}
