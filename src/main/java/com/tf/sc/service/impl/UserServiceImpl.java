package com.tf.sc.service.impl;

import com.tf.sc.common.Constants;
import com.tf.sc.entity.User;
import com.tf.sc.mapper.StationStaffMapper;
import com.tf.sc.mapper.UserMapper;
import com.tf.sc.service.UserService;
import com.tf.sc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StationStaffMapper stationStaffMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public boolean register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Constants.ROLE_USER);
        user.setAuditStatus(1);
        user.setLoginFailCount(0);
        user.setDeletionStatus(0);
        user.setCreatedAt(DateUtil.nowStr());
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.insert(user) > 0;
    }

    @Override
    public User login(String phone, String password) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            return null;
        }
        if (user.getRole() != null && !Integer.valueOf(Constants.ROLE_USER).equals(user.getRole())) {
            return null;
        }
        User authenticated = authenticate(user, password);
        if (authenticated != null && authenticated.getRole() == null) {
            authenticated.setRole(Constants.ROLE_USER);
        }
        return authenticated;
    }

    @Override
    public User courierLogin(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            user = userMapper.findByPhone(username);
        }
        if (user == null) {
            return null;
        }
        boolean allowedRole = Integer.valueOf(Constants.ROLE_COURIER).equals(user.getRole())
                || Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(user.getRole());
        if (!allowedRole) {
            return null;
        }
        return authenticate(user, password);
    }

    @Override
    @Deprecated
    public User adminLogin(String username, String password) {
        return courierLogin(username, password);
    }

    @Override
    public boolean registerCourier(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Constants.ROLE_COURIER);
        user.setAuditStatus(1);
        user.setLoginFailCount(0);
        user.setDeletionStatus(0);
        user.setCreatedAt(DateUtil.nowStr());
        user.setUpdatedAt(DateUtil.nowStr());
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            user.setPhone(user.getUsername());
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Deprecated
    public boolean adminRegister(User user) {
        return registerCourier(user);
    }

    @Override
    public boolean updateUser(User user) {
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        user.setAvatar(avatarUrl);
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public List<User> findCouriers() {
        return userMapper.findCouriers();
    }

    @Override
    public List<User> findDeletionPending() {
        return userMapper.findDeletionPending();
    }

    @Override
    public boolean lockUser(Long userId) {
        return userMapper.lockUser(userId, DateUtil.format(LocalDateTime.now().plusHours(1))) > 0;
    }

    @Override
    public boolean unlockUser(Long userId) {
        return userMapper.unlockUser(userId) > 0;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean resetPassword(String phone, String newPassword) {
        User user = userMapper.findByPhone(phone);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLoginFailCount(0);
        user.setLockUntil(null);
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean auditUser(Long userId, Integer auditStatus, String rejectReason) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        if (Integer.valueOf(1).equals(auditStatus) && Integer.valueOf(0).equals(user.getAuditStatus())) {
            user.setRole(Constants.ROLE_COURIER);
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                user.setUsername(user.getPhone());
            }
            if (user.getEmployeeNo() == null || user.getEmployeeNo().trim().isEmpty()) {
                user.setEmployeeNo(generateEmployeeNo());
            }
        }
        if (Integer.valueOf(1).equals(auditStatus) && Integer.valueOf(3).equals(user.getAuditStatus())) {
            user.setRole(Constants.ROLE_USER);
            stationStaffMapper.deleteByUserId(user.getId());
        }
        if (Integer.valueOf(2).equals(auditStatus) && Integer.valueOf(3).equals(user.getAuditStatus())) {
            user.setAuditStatus(1);
            user.setRejectReason(rejectReason);
            user.setUpdatedAt(DateUtil.nowStr());
            return userMapper.update(user) > 0;
        }
        user.setAuditStatus(auditStatus);
        user.setRejectReason(rejectReason);
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean requestDeletion(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null || Integer.valueOf(2).equals(user.getDeletionStatus())) {
            return false;
        }
        user.setDeletionStatus(1);
        user.setDeletionTime(DateUtil.nowStr());
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean applyEmployee(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null || !Integer.valueOf(Constants.ROLE_USER).equals(user.getRole())) {
            return false;
        }
        if (Integer.valueOf(0).equals(user.getAuditStatus())) {
            return false;
        }
        user.setAuditStatus(0);
        user.setRejectReason(null);
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean cancelEmployee(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        if (Integer.valueOf(0).equals(user.getAuditStatus()) && Integer.valueOf(Constants.ROLE_USER).equals(user.getRole())) {
            user.setAuditStatus(1);
            user.setRejectReason(null);
            user.setUpdatedAt(DateUtil.nowStr());
            return userMapper.update(user) > 0;
        }
        if (!Integer.valueOf(Constants.ROLE_COURIER).equals(user.getRole())) {
            return false;
        }
        user.setAuditStatus(3);
        user.setRejectReason(null);
        user.setUpdatedAt(DateUtil.nowStr());
        return userMapper.update(user) > 0;
    }

    private User authenticate(User user, String password) {
        if (user.getLockUntil() != null && !DateUtil.isBeforeNow(user.getLockUntil())) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setLoginFailCount(0);
            user.setLockUntil(null);
            userMapper.updateLoginFailCount(user.getId(), 0);
            userMapper.unlockUser(user.getId());
            return user;
        }
        int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
        userMapper.updateLoginFailCount(user.getId(), failCount);
        if (failCount >= 5) {
            userMapper.lockUser(user.getId(), DateUtil.format(LocalDateTime.now().plusHours(1)));
        }
        return null;
    }

    private String generateEmployeeNo() {
        String ts = String.valueOf(System.currentTimeMillis());
        String suffix = String.format("%04d", (int) (Math.random() * 10000));
        return "KP" + ts.substring(ts.length() - 10) + suffix;
    }
}
