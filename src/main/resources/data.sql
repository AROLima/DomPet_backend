
-- ADMIN (senha: Admin@123)
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version)
VALUES ('Admin', 'admin@dompet.dev',
        '$2b$10$E.iUyby25SywbG1jyoHM7uoIDs6mhFnIPDkyTg.4g9Ove6gCp3/py',
        'ADMIN', TRUE, 0);

-- RODRIGO (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version) VALUES
('Rodrigo', 'rodrigo@dompet.dev', '$2b$10$7m9nB.s8kppeEhvJ9z/wFuK1eMzH5NIo4TqI1g9XJ/CKLmeILJEvq', 'USER', TRUE, 0);

-- JÚLIA (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version) VALUES
('Júlia', 'julia@dompet.dev', '$2b$10$cZ3D8Ou65Ps/ryh0tV28fu/0g57FruZhZwp4AnIizLNusjfqCQJEm', 'USER', TRUE, 0);

-- CARLOS (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version) VALUES
('Carlos', 'carlos@dompet.dev', '$2b$10$IOalqC2na454mb7tay7EJei9McMbEkGYfoeyDm.bpuKZPKEEZH5fe', 'USER', TRUE, 0);

-- MARIA (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo, token_version) VALUES
('Maria', 'maria@dompet.dev', '$2b$10$Z.BIOEnOYI3bfXtSeTcOtOQIZItTSv24GaP2HR69evFdSVwmUIwDi', 'USER', TRUE, 0);

-- RACAO IMAGENS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Premium Cães Adultos 10kg', 'Ração completa para cães adultos, com proteínas de alta qualidade e prebióticos.', 189.90, 25, 'https://static.petnautasloja.com.br/public/nunesagropecuaria/imagens/produtos/racao-premier-nutricao-clinica-diabetes-para-caes-adultos-racas-medias-e-grandes-10-1kg-8610.jpg', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Gatos Castrados 3kg', 'Alimento balanceado para gatos castrados, controle de peso e saúde urinária.', 119.90, 30, 'https://www.petlove.com.br/images/products/266041/product/Ra%C3%A7%C3%A3o_Seca_PremieR_Pet_Golden_Gatos_Adultos_Castrados_Frango_-_10_Kg_31017079-3_1.jpg?1674072456', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Filhotes Cães 2kg', 'Fórmula para crescimento saudável de filhotes, com DHA.', 79.90, 40, 'https://www.petlove.com.br/images/products/261658/product/Ra%C3%A7%C3%A3o_Seca_PremieR_Pet_Golden_Formula_C%C3%A3es_Filhotes_Frango_e_Arroz_-_20_Kg_31014043-5.jpg?1662031665', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Úmida Sachê Gatos 85g (12un)', 'Sachês completos e balanceados, alta palatabilidade.', 54.90, 35, 'https://m.media-amazon.com/images/I/31W40KgH0WL.jpg', 'RACAO', TRUE);

-- HIGIENE
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Areia Higiênica 4kg', 'Areia com alto poder de aglomeração e controle de odores.', 39.90, 60, 'https://picsum.photos/seed/areia-higienica/600/600', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Shampoo Neutro 500ml', 'Shampoo suave para todos os tipos de pelagem.', 27.90, 70, 'https://picsum.photos/seed/shampoo-pet/600/600', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Condicionador Pelos Longos 300ml', 'Facilita a escovação e reduz nós.', 33.90, 40, 'https://picsum.photos/seed/condicionador-pet/600/600', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Tapete Higiênico 30un', 'Tapetes absorventes com gel superabsorvente.', 79.90, 55, 'https://picsum.photos/seed/tapete-higienico/600/600', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Lenço Umedecido Pet 100un', 'Para limpeza rápida de patas e pelagem.', 24.90, 90, 'https://picsum.photos/seed/lenco-umedecido/600/600', 'HIGIENE', TRUE);

-- MEDICAMENTOS IMAGENS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Coleira Antipulgas P', 'Proteção contra pulgas e carrapatos por até 8 semanas.', 89.90, 20, 'https://www.petlove.com.br/images/products/273555/product/Coleira_Antipulgas_Coveli_Bullcat_para_Gatos_-_15_g_3102801.jpg?1695058700', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Spray Antipulgas 100ml', 'Ação tópica rápida contra ectoparasitas.', 64.90, 25, 'https://www.petlove.com.br/images/products/283943/product/7898746060241.jpg?1719857288', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Vermífugo Cães M 2cp', 'Combate verminoses intestinais comuns.', 34.90, 35, 'https://images.tcdn.com.br/img/img_prod/699275/vermifugo_vermivet_plus_biovet_2g_para_caes_com_2_comprimidos_973_1_28e3b0e714f2897eb690ca22e19a6b34.jpg', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Suplemento Ômega-3 60cáps', 'Suporte à pele, pelagem e articulações.', 79.90, 28, 'https://m.media-amazon.com/images/I/51uM7PSf+gL.jpg', 'MEDICAMENTOS', TRUE);

-- ACESSORIOS 
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Coleira Ajustável Nylon M', 'Nylon resistente com fivela de engate rápido.', 29.90, 100, 'https://picsum.photos/seed/coleira-nylon/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Guia Retrátil 5m', 'Para cães até 20kg, travamento rápido.', 69.90, 40, 'https://picsum.photos/seed/guia-retratil/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Peitoral Conforto M', 'Ajuste anatômico com acolchoamento.', 59.90, 32, 'https://picsum.photos/seed/peitoral/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Comedouro Inox 700ml', 'Tigela de aço inox com base antiderrapante.', 34.90, 75, 'https://picsum.photos/seed/comedouro-inox/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Bebedouro Portátil 500ml', 'Garrafa com dispenser para passeios.', 32.90, 50, 'https://picsum.photos/seed/bebedouro-portatil/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Cama Pet Redonda M', 'Cama macia com bordas elevadas.', 139.90, 22, 'https://picsum.photos/seed/cama-pet/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Casinha Plástica M', 'Abrigo confortável e higiênico para áreas externas.', 249.90, 12, 'https://picsum.photos/seed/casinha-pet/600/600', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Transportadora Nº 2', 'Transportadora ventilada com trava de segurança.', 179.90, 18, 'https://picsum.photos/seed/transportadora/600/600', 'ACESSORIOS', TRUE);

-- BRINQUEDOS IMAGENS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo Corda Nó', 'Ajuda na mastigação e alivia o estresse.', 19.90, 120, 'https://dcdn-us.mitiendanube.com/stores/002/456/384/products/faa41714c52ecbd254419c6f6e3c4e08-5f1ec1a5b10b9f187c16868545821376-1024-1024.jpg', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Bola Interativa com Petiscos', 'Dispensa petiscos e estimula o enriquecimento.', 44.90, 45, 'https://www.petlove.com.br/images/products/268006/product/31027523726_Brinquedo_Interativo_Bola_Porta_Petisco_3.jpg?1682452177', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Varinha com Penas', 'Estimula o instinto de caça dos gatos.', 24.90, 85, 'https://img.irroba.com.br/fit-in/2000x2000/filters:fill(fff):quality(80)/brincalh/catalog/brinquedos-brincat/varinhas/penas-28-5/39142-39186-1.jpg', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Pelúcia Interativa', 'Brinquedo macio que emite sons.', 39.90, 60, 'https://m.media-amazon.com/images/I/61NpAOvJ9RL._UF894,1000_QL80_.jpg', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo de Borracha', 'Resistente e ideal para mastigação.', 29.90, 100, 'https://petbox.vteximg.com.br/arquivos/ids/164614-1000-1000/165d5e0bff31c73f1186c54c096a70763f1551fa.jpg?v=638139029467170000', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo de Pelúcia', 'Brinquedo macio e aconchegante.', 49.90, 80, 'https://www.petlove.com.br/images/products/148/product/00.jpg?1627499970', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Arranhador para Gatos', 'Arranhador com plataforma e brinquedos pendurados.', 129.90, 30, 'https://www.petlove.com.br/images/products/324946/product/1880-5.jpg?1736541080', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Caminha para Gatos', 'Caminha confortável com design moderno.', 99.90, 25, 'https://estiloeconforto.fbitsstatic.net/img/p/cama-pet-digital-print-50cm-x-40cm-para-caes-e-gatos-gato-meu-pet-183763/380998.jpg?w=700&h=700&v=no-value','OUTDOOR', TRUE);