DROP TABLE IF EXISTS `servico`;
CREATE TABLE `servico` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `nome` varchar(100) NOT NULL,
    `valor` decimal(8,2) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ktlf7q4ohqbhc2f4716md38yl` (`nome`)
);