DROP TABLE IF EXISTS `anotacao`;
CREATE TABLE `anotacao` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `anotacao` varchar(255) NOT NULL,
    `data` date DEFAULT NULL,
    `venda` bigint DEFAULT NULL,
    PRIMARY KEY (`id`)
);