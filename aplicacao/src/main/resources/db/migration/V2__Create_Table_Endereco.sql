DROP TABLE IF EXISTS `endereco`;
CREATE TABLE `endereco` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `logradouro` varchar(100) DEFAULT NULL,
    `uf` enum('AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT','PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO') DEFAULT NULL,
    PRIMARY KEY (`id`)
);