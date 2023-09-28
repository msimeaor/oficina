DROP TABLE IF EXISTS `veiculo`;
CREATE TABLE `veiculo` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `fabricante` enum('AUDI','BMW','CHERY','CHEVROLET','CITROEN','FIAT','FORD','HONDA','HYUNDAI','JEEP','KIA','MERCEDES','MITSUBISHI','NISSAN','PEUGEOT','RENAULT','SUBARU','TOYOTA','VOLKSWAGEN','VOLVO') NOT NULL,
   `km_atual` varchar(7) NOT NULL,
   `nome` varchar(100) NOT NULL,
   `observacao` varchar(255) DEFAULT NULL,
   `placa` varchar(7) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `UK_p5d5nk5lnqh5e36e73ck1caw9` (`nome`),
   UNIQUE KEY `UK_luoyk9d8idgi0wif7bxtefsr5` (`placa`)
);