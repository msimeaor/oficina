DROP TABLE IF EXISTS `venda`;
CREATE TABLE `venda` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `data_entrega` date NOT NULL,
 `data_inicio` date NOT NULL,
 `desconto` decimal(8,2) DEFAULT NULL,
 `forma_pagamento` enum('BOLETO','CHEQUE','CREDITO','DEBITO','DINHEIRO','PIX','TRANSFERENCIA') NOT NULL,
 `qtd_parcelas` int DEFAULT NULL,
 `valor_total` decimal(8,2) DEFAULT NULL,
 `pessoa` bigint DEFAULT NULL,
 `veiculo` bigint DEFAULT NULL,
 PRIMARY KEY (`id`)
);