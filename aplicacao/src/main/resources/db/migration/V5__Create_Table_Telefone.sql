DROP TABLE IF EXISTS `telefone`;
CREATE TABLE `telefone` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `numero` varchar(11) DEFAULT NULL,
    `pessoa` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_bpqxm7divmbgtv17ykdtte47o` (`numero`)
);