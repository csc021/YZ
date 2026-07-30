package com.tf.sc.config;

import com.tf.sc.common.Constants;
import com.tf.sc.entity.User;
import com.tf.sc.mapper.UserMapper;
import com.tf.sc.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    private static final String DEFAULT_NICKNAME = "Station Master";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        migrateSchema();
        initStationMasterAccount();
        unlockAllLockedAccounts();
    }

    private void migrateSchema() {
        ensureUserColumn("username", "VARCHAR(64) NULL");
        ensureUserColumn("email", "VARCHAR(128) NULL");
        ensureUserColumn("employee_no", "VARCHAR(32) NULL");
        ensureStationColumn("brand", "VARCHAR(32) NULL");
        ensureSmsCodeColumn("email", "VARCHAR(128) NULL");
        ensureSmsCodeColumn("code", "VARCHAR(16) NULL");
        ensureSmsCodeColumn("type", "INT NULL");
        ensureSmsCodeColumn("is_used", "INT DEFAULT 0");
        ensureSmsCodeColumn("expire_time", "VARCHAR(32) NULL");
        ensureSmsCodeColumn("created_at", "VARCHAR(32) NULL");
        ensureSmsCodePhoneNullable();
        ensureParcelShelfNullable();
        ensureMailOrderTable();
        // 传感器/分区相关表
        ensureZoneTable();
        ensureParcelTypeTable();
        ensureSensorReadingTable();
        ensureSensorDataTable();
        ensureParcelColumns();
        initParcelTypes();
    }

    private void ensureUserColumn(String column, String definition) {
        if (!columnExists("user", column)) {
            jdbcTemplate.execute("ALTER TABLE `user` ADD COLUMN `" + column + "` " + definition);
        }
    }

    private void ensureStationColumn(String column, String definition) {
        if (!columnExists("station", column)) {
            jdbcTemplate.execute("ALTER TABLE `station` ADD COLUMN `" + column + "` " + definition);
        }
    }

    private void ensureSmsCodeColumn(String column, String definition) {
        if (!columnExists("sms_code", column)) {
            jdbcTemplate.execute("ALTER TABLE `sms_code` ADD COLUMN `" + column + "` " + definition);
        }
    }

    private void ensureSmsCodePhoneNullable() {
        if (columnExists("sms_code", "phone")) {
            jdbcTemplate.execute("ALTER TABLE `sms_code` MODIFY COLUMN `phone` VARCHAR(32) NULL");
        }
    }

    private void ensureParcelShelfNullable() {
        if (columnExists("parcel", "shelf_id")) {
            jdbcTemplate.execute("ALTER TABLE `parcel` MODIFY COLUMN `shelf_id` BIGINT NULL");
        }
        if (columnExists("parcel", "shelf_floor")) {
            jdbcTemplate.execute("ALTER TABLE `parcel` MODIFY COLUMN `shelf_floor` INT NULL");
        }
    }

    private void ensureMailOrderTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'mail_order'",
                Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "CREATE TABLE `mail_order` (" +
                            "`id` BIGINT PRIMARY KEY AUTO_INCREMENT," +
                            "`user_id` BIGINT NOT NULL," +
                            "`sender_name` VARCHAR(64)," +
                            "`sender_phone` VARCHAR(32)," +
                            "`sender_province` VARCHAR(64)," +
                            "`sender_city` VARCHAR(64)," +
                            "`sender_district` VARCHAR(64)," +
                            "`sender_address` VARCHAR(255)," +
                            "`receiver_name` VARCHAR(64)," +
                            "`receiver_phone` VARCHAR(32)," +
                            "`receiver_province` VARCHAR(64)," +
                            "`receiver_city` VARCHAR(64)," +
                            "`receiver_district` VARCHAR(64)," +
                            "`receiver_address` VARCHAR(255)," +
                            "`item_name` VARCHAR(128)," +
                            "`item_type` VARCHAR(64)," +
                            "`item_weight` DECIMAL(10,2)," +
                            "`carrier_id` BIGINT," +
                            "`station_id` BIGINT," +
                            "`status` INT," +
                            "`remark` VARCHAR(255)," +
                            "`created_at` VARCHAR(32)," +
                            "`updated_at` VARCHAR(32)" +
                            ")"
            );
        }
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class, tableName, columnName);
        return count != null && count > 0;
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, tableName);
        return count != null && count > 0;
    }

    private void ensureZoneTable() {
        if (tableExists("zone")) return;
        jdbcTemplate.execute(
                "CREATE TABLE `zone` (" +
                        "`id` BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "`station_id` BIGINT NOT NULL," +
                        "`code` VARCHAR(16) NOT NULL," +
                        "`name` VARCHAR(64)," +
                        "`temp_min` DECIMAL(5,1)," +
                        "`temp_max` DECIMAL(5,1)," +
                        "`humidity_min` DECIMAL(5,1)," +
                        "`humidity_max` DECIMAL(5,1)," +
                        "`shelf_id` BIGINT," +
                        "`status` INT DEFAULT 1," +
                        "`created_at` VARCHAR(32)," +
                        "UNIQUE KEY `uk_station_code` (`station_id`, `code`)" +
                        ")"
        );
        log.info("Created table: zone");
    }

    private void ensureParcelTypeTable() {
        if (tableExists("parcel_type")) return;
        jdbcTemplate.execute(
                "CREATE TABLE `parcel_type` (" +
                        "`id` BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "`name` VARCHAR(64) NOT NULL," +
                        "`icon` VARCHAR(32)," +
                        "`default_zone_id` BIGINT," +
                        "`sort` INT DEFAULT 0," +
                        "`created_at` VARCHAR(32)" +
                        ")"
        );
        log.info("Created table: parcel_type");
    }

    private void ensureSensorReadingTable() {
        if (tableExists("sensor_reading")) return;
        jdbcTemplate.execute(
                "CREATE TABLE `sensor_reading` (" +
                        "`id` BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "`zone_id` BIGINT NOT NULL," +
                        "`station_id` BIGINT NOT NULL," +
                        "`temperature` DECIMAL(5,1)," +
                        "`humidity` DECIMAL(5,1)," +
                        "`read_at` VARCHAR(32) NOT NULL," +
                        "`created_at` VARCHAR(32)," +
                        "INDEX `idx_zone_time` (`zone_id`, `read_at`)," +
                        "INDEX `idx_station_time` (`station_id`, `read_at`)" +
                        ")"
        );
        log.info("Created table: sensor_reading");
    }

    private void ensureSensorDataTable() {
        if (tableExists("t_sensor_data")) return;
        jdbcTemplate.execute(
                "CREATE TABLE `t_sensor_data` (" +
                        "`id` BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "`device_id` VARCHAR(64) NOT NULL DEFAULT 'rk2206_01'," +
                        "`temperature` DECIMAL(5,1)," +
                        "`humidity` DECIMAL(5,1)," +
                        "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "INDEX `idx_device_time` (`device_id`, `create_time`)" +
                        ")"
        );
        log.info("Created table: t_sensor_data");
    }

    private void ensureParcelColumns() {
        ensureColumn("parcel", "zone_id", "BIGINT NULL");
        ensureColumn("parcel", "parcel_type_id", "BIGINT NULL");
        ensureColumn("parcel", "sensor_temp", "DECIMAL(5,1) NULL");
        ensureColumn("parcel", "sensor_humidity", "DECIMAL(5,1) NULL");
    }

    private void ensureColumn(String table, String column, String definition) {
        if (!columnExists(table, column)) {
            jdbcTemplate.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        }
    }

    private void initParcelTypes() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM parcel_type", Integer.class);
        if (count != null && count == 0) {
            String now = DateUtil.nowStr();
            jdbcTemplate.execute(
                    "INSERT INTO parcel_type (name, icon, sort, created_at) VALUES " +
                            "('食品', 'Food', 1, '" + now + "')," +
                            "('电子产品', 'Phone', 2, '" + now + "')," +
                            "('服装', 'Shirt', 3, '" + now + "')," +
                            "('文件', 'Document', 4, '" + now + "')," +
                            "('生鲜', 'Apple', 5, '" + now + "')," +
                            "('药品', 'Medicine', 6, '" + now + "')," +
                            "('其他', 'More', 7, '" + now + "')"
            );
            log.info("Inserted default parcel types");
        }
    }

    private void initStationMasterAccount() {
        java.util.List<User> allUsers = userMapper.findAll();
        boolean hasStationMaster = allUsers.stream()
                .anyMatch(u -> Integer.valueOf(Constants.ROLE_STATION_MASTER).equals(u.getRole()));

        if (!hasStationMaster) {
            User admin = new User();
            admin.setUsername(DEFAULT_USERNAME);
            admin.setPhone(DEFAULT_USERNAME);
            admin.setEmail("admin@station.com");
            admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            admin.setNickname(DEFAULT_NICKNAME);
            admin.setRole(Constants.ROLE_STATION_MASTER);
            admin.setAuditStatus(1);
            admin.setLoginFailCount(0);
            admin.setDeletionStatus(0);
            admin.setCreatedAt(DateUtil.nowStr());
            admin.setUpdatedAt(DateUtil.nowStr());
            userMapper.insert(admin);
            log.info("Created default station master account: username={}, password={}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
        }

        User admin = userMapper.findByPhone(DEFAULT_USERNAME);
        if (admin != null) {
            if (admin.getLockUntil() != null || admin.getLoginFailCount() != null && admin.getLoginFailCount() > 0) {
                userMapper.unlockUser(admin.getId());
                log.info("Unlocked default station master account");
            }
            if (admin.getAuditStatus() != null && admin.getAuditStatus() != 1) {
                admin.setAuditStatus(1);
                admin.setUpdatedAt(DateUtil.nowStr());
                userMapper.update(admin);
                log.info("Normalized default station master account audit status");
            }
        }
    }

    private void unlockAllLockedAccounts() {
        java.util.List<User> allUsers = userMapper.findAll();
        int unlocked = 0;
        for (User user : allUsers) {
            if (user.getLockUntil() != null) {
                userMapper.unlockUser(user.getId());
                unlocked++;
            }
        }
        if (unlocked > 0) {
            log.info("Unlocked {} historical accounts", unlocked);
        }
    }
}
