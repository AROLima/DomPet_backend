-- ==== USUÁRIOS (senha entre parênteses p/ teste) ====
-- ADMIN (senha: Admin@123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Admin', 'admin@dompet.dev', '$2b$10$E.iUyby25SywbG1jyoHM7uoIDs6mhFnIPDkyTg.4g9Ove6gCp3/py', 'ADMIN', TRUE);

-- RODRIGO (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Rodrigo', 'rodrigo@dompet.dev', '$2b$10$7m9nB.s8kppeEhvJ9z/wFuK1eMzH5NIo4TqI1g9XJ/CKLmeILJEvq', 'USER', TRUE);

-- JÚLIA (senha: 123456)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Júlia', 'julia@dompet.dev', '$2b$10$cZ3D8Ou65Ps/ryh0tV28fu/0g57FruZhZwp4AnIizLNusjfqCQJEm', 'USER', TRUE);

-- CARLOS (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Carlos', 'carlos@dompet.dev', '$2b$10$IOalqC2na454mb7tay7EJei9McMbEkGYfoeyDm.bpuKZPKEEZH5fe', 'USER', TRUE);

-- MARIA (senha: cliente123)
INSERT INTO usuarios (nome, email, senha, role, ativo) VALUES
('Maria', 'maria@dompet.dev', '$2b$10$Z.BIOEnOYI3bfXtSeTcOtOQIZItTSv24GaP2HR69evFdSVwmUIwDi', 'USER', TRUE);

-- RACAO
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Premium Cães Adultos 10kg', 'Ração completa para cães adultos, com proteínas de alta qualidade e prebióticos.', 189.90, 25, 'https://picsum.photos/seed/racao-caes-10kg/600/600', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Gatos Castrados 3kg', 'Alimento balanceado para gatos castrados, controle de peso e saúde urinária.', 119.90, 30, 'https://picsum.photos/seed/racao-gatos-3kg/600/600', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Filhotes Cães 2kg', 'Fórmula para crescimento saudável de filhotes, com DHA.', 79.90, 40, 'https://picsum.photos/seed/racao-filhotes/600/600', 'RACAO', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Ração Úmida Sachê Gatos 85g (12un)', 'Sachês completos e balanceados, alta palatabilidade.', 54.90, 35, 'https://picsum.photos/seed/sache-gatos/600/600', 'RACAO', TRUE);

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

-- MEDICAMENTOS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Coleira Antipulgas P', 'Proteção contra pulgas e carrapatos por até 8 semanas.', 89.90, 20, 'https://picsum.photos/seed/antipulgas-coleira/600/600', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Spray Antipulgas 100ml', 'Ação tópica rápida contra ectoparasitas.', 64.90, 25, 'https://picsum.photos/seed/spray-antipulgas/600/600', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Vermífugo Cães M 2cp', 'Combate verminoses intestinais comuns.', 34.90, 35, 'https://picsum.photos/seed/vermifugo-caes/600/600', 'MEDICAMENTOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Suplemento Ômega-3 60cáps', 'Suporte à pele, pelagem e articulações.', 79.90, 28, 'https://picsum.photos/seed/omega3-pet/600/600', 'MEDICAMENTOS', TRUE);

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

-- BRINQUEDOS
INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Brinquedo Corda Nó', 'Ajuda na mastigação e alivia o estresse.', 19.90, 120, 'https://picsum.photos/seed/corda-no/600/600', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Bola Interativa com Petiscos', 'Dispensa petiscos e estimula o enriquecimento.', 44.90, 45, 'https://picsum.photos/seed/bola-interativa/600/600', 'BRINQUEDOS', TRUE);

INSERT INTO produtos (nome, descricao, preco, estoque, imagem_url, categoria, ativo) VALUES
('Varinha com Penas', 'Estimula o instinto de caça dos gatos.', 24.90, 85, 'https://picsum.photos/seed/varinha-penas/600/600', 'BRINQUEDOS', TRUE);
 