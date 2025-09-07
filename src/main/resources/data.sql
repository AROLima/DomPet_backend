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
('Ração Premium Cães Adultos 10kg', 'Ração completa para cães adultos, com proteínas de alta qualidade e prebióticos.', 189.90, 25, 'https://dompet-test.s3.us-east-2.amazonaws.com/royal-canin-1200x1200.png', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Gatos Castrados 3kg', 'Alimento balanceado para gatos castrados, controle de peso e saúde urinária.', 119.90, 30, 'https://dompet-test.s3.us-east-2.amazonaws.com/golden-gatos-castrados-3kg-1200x1200.png', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Filhotes Cães 2kg', 'Fórmula para crescimento saudável de filhotes, com DHA.', 79.90, 40, 'https://dompet-test.s3.us-east-2.amazonaws.com/racao-1200x1200.png', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Úmida Sachê Gatos 85g (12un)', 'Sachês completos e balanceados, alta palatabilidade.', 54.90, 35, 'https://dompet-test.s3.us-east-2.amazonaws.com/gatos-castrados-carne-1200x1200.png', 'RACAO', TRUE);

-- HIGIENE
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Areia Higiênica 4kg', 'Areia com alto poder de aglomeração e controle de odores.', 39.90, 60, 'https://dompet-test.s3.us-east-2.amazonaws.com/areia-1200x1200.png', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Shampoo Neutro 500ml', 'Shampoo suave para todos os tipos de pelagem.', 27.90, 70, 'https://dompet-test.s3.us-east-2.amazonaws.com/shampoo-neutro-1200x1200.png', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Condicionador Pelos Longos 300ml', 'Facilita a escovação e reduz nós.', 33.90, 40, 'https://dompet-test.s3.us-east-2.amazonaws.com/condicionador-1200x1200.png', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Tapete Higiênico 30un', 'Tapetes absorventes com gel superabsorvente.', 79.90, 55, 'https://dompet-test.s3.us-east-2.amazonaws.com/tapete-1200x1200.png', 'HIGIENE', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Lenço Umedecido Pet 100un', 'Para limpeza rápida de patas e pelagem.', 24.90, 90, 'https://dompet-test.s3.us-east-2.amazonaws.com/lenco-1200x1200.png', 'HIGIENE', TRUE);

-- MEDICAMENTOS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Coleira Antipulgas P', 'Proteção contra pulgas e carrapatos por até 8 semanas.', 89.90, 20, 'https://dompet-test.s3.us-east-2.amazonaws.com/coleira-antipulgas-1200x1200.png', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Spray Antipulgas 100ml', 'Ação tópica rápida contra ectoparasitas.', 64.90, 25, 'https://dompet-test.s3.us-east-2.amazonaws.com/sprayantipulgas-1200x1200.png', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Vermífugo Cães M 2cp', 'Combate verminoses intestinais comuns.', 34.90, 35, 'https://dompet-test.s3.us-east-2.amazonaws.com/vermifugocaes-1200x1200.png', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Suplemento Ômega-3 60cáps', 'Suporte à pele, pelagem e articulações.', 79.90, 28, 'https://dompet-test.s3.us-east-2.amazonaws.com/omega3-1200x1200.png', 'MEDICAMENTOS', TRUE);

-- ACESSORIOS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Coleira Ajustável Nylon M', 'Nylon resistente com fivela de engate rápido.', 29.90, 100, 'https://dompet-test.s3.us-east-2.amazonaws.com/coleitaajustavel-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Guia Retrátil 5m', 'Para cães até 20kg, travamento rápido.', 69.90, 40, 'https://dompet-test.s3.us-east-2.amazonaws.com/guia-retratil-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Peitoral Conforto M', 'Ajuste anatômico com acolchoamento.', 59.90, 32, 'https://dompet-test.s3.us-east-2.amazonaws.com/peitoral-confroto-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Comedouro Inox 700ml', 'Tigela de aço inox com base antiderrapante.', 34.90, 75, 'https://dompet-test.s3.us-east-2.amazonaws.com/comedouro-inox-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Bebedouro Portátil 500ml', 'Garrafa com dispenser para passeios.', 32.90, 50, 'https://dompet-test.s3.us-east-2.amazonaws.com/bebedouro-portatil-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Cama Pet Redonda M', 'Cama macia com bordas elevadas.', 139.90, 22, 'https://dompet-test.s3.us-east-2.amazonaws.com/cama-redonda-m-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Casinha Plástica M', 'Abrigo confortável e higiênico para áreas externas.', 249.90, 12, 'https://dompet-test.s3.us-east-2.amazonaws.com/casa-plastica-1200x1200.png', 'ACESSORIOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Transportadora Nº 2', 'Transportadora ventilada com trava de segurança.', 179.90, 18, 'https://dompet-test.s3.us-east-2.amazonaws.com/transportadora-1200x1200.png', 'ACESSORIOS', TRUE);

-- BRINQUEDOS / OUTDOOR
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo Corda Nó', 'Ajuda na mastigação e alivia o estresse.', 19.90, 120, 'https://dompet-test.s3.us-east-2.amazonaws.com/brinquedo-corda-no-1200x1200.png', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Bola Interativa com Petiscos', 'Dispensa petiscos e estimula o enriquecimento.', 44.90, 45, 'https://dompet-test.s3.us-east-2.amazonaws.com/bola-interativa-petiscos-1200x1200.png', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Varinha com Penas', 'Estimula o instinto de caça dos gatos.', 24.90, 85, 'https://dompet-test.s3.us-east-2.amazonaws.com/varinha-com-penas-1200x1200.png', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Pelúcia Interativa', 'Brinquedo macio que emite sons.', 39.90, 60, 'https://dompet-test.s3.us-east-2.amazonaws.com/pelucia-interativa-1200x1200.png', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo de Borracha', 'Resistente e ideal para mastigação.', 29.90, 100, 'https://dompet-test.s3.us-east-2.amazonaws.com/brinquedo-borracha-1200x1200.png', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo de Pelúcia', 'Brinquedo macio e aconchegante.', 49.90, 80, 'https://dompet-test.s3.us-east-2.amazonaws.com/brinquedo-pelucia-1200x1200.png', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Arranhador para Gatos', 'Arranhador com plataforma e brinquedos pendurados.', 129.90, 30, 'https://dompet-test.s3.us-east-2.amazonaws.com/arranhador-gatos-1200x1200.png', 'OUTDOOR', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Caminha para Gatos', 'Caminha confortável com design moderno.', 99.90, 25, 'https://dompet-test.s3.us-east-2.amazonaws.com/caminha-gatos-1200x1200.png', 'OUTDOOR', TRUE);
