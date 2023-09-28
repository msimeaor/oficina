ALTER TABLE anotacao
ADD KEY `FK878jii7hh0x0i4geaemx74eem` (`venda`),
ADD CONSTRAINT `FK878jii7hh0x0i4geaemx74eem` FOREIGN KEY (`venda`) REFERENCES `venda` (`id`);

ALTER TABLE pessoa
ADD KEY `FKdsei0yntk08w3g8c1wi0n1n4j` (`endereco`),
ADD CONSTRAINT `FKdsei0yntk08w3g8c1wi0n1n4j` FOREIGN KEY (`endereco`) REFERENCES `endereco` (`id`);

ALTER TABLE telefone
ADD KEY `FKsln9e6hlfckpvdf5avgcri0vv` (`pessoa`),
ADD CONSTRAINT `FKsln9e6hlfckpvdf5avgcri0vv` FOREIGN KEY (`pessoa`) REFERENCES `pessoa` (`id`);

ALTER TABLE venda
ADD KEY `FKaulv4lnlddivj7h67xr2c61bp` (`pessoa`),
ADD KEY `FKri70akl9q6t9anf6a7msybknm` (`veiculo`),
ADD CONSTRAINT `FKaulv4lnlddivj7h67xr2c61bp` FOREIGN KEY (`pessoa`) REFERENCES `pessoa` (`id`),
ADD CONSTRAINT `FKri70akl9q6t9anf6a7msybknm` FOREIGN KEY (`veiculo`) REFERENCES `veiculo` (`id`);

ALTER TABLE venda_servico
ADD KEY `FKdm5rqxxo0ndio1kjrcwsv3hyh` (`servico_id`),
ADD KEY `FK5y1t07a17m3uaqkf8r61n07jq` (`venda_id`),
ADD CONSTRAINT `FK5y1t07a17m3uaqkf8r61n07jq` FOREIGN KEY (`venda_id`) REFERENCES `venda` (`id`),
ADD CONSTRAINT `FKdm5rqxxo0ndio1kjrcwsv3hyh` FOREIGN KEY (`servico_id`) REFERENCES `servico` (`id`);