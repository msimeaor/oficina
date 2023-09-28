DROP TABLE IF EXISTS `pessoa`;
CREATE TABLE `pessoa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cpf` varchar(14) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `inativo` bit(1) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `sexo` varchar(9) NOT NULL,
  `endereco` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nlwiu48rutiltbnjle59krljo` (`cpf`),
  UNIQUE KEY `UK_mc87q8fpvldpdyfo9o5633o5l` (`email`)
);