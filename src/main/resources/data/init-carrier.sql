-- carrier seed data
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (1, '顺丰速运', 'SF', 1) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (2, '中通快递', 'ZTO', 2) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (3, '圆通速递', 'YTO', 3) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (4, '申通快递', 'STO', 4) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (5, '韵达速递', 'YD', 5) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (6, '极兔速递', 'JT', 6) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (7, '京东物流', 'JD', 7) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
INSERT INTO `carrier` (`id`, `name`, `code`, `sort`) VALUES (8, '邮政EMS', 'EMS', 8) ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort`=VALUES(`sort`);
