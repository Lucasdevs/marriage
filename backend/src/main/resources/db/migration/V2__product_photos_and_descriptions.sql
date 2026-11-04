ALTER TABLE gifts ADD COLUMN image_url VARCHAR(500);
ALTER TABLE gifts ADD COLUMN product_url VARCHAR(500);

UPDATE gifts SET
 name = 'Pratos fundos Tramontina Leonora — 6 peças',
 description = 'Seis pratos de porcelana branca da linha Leonora, com 21 cm de diâmetro e bordas reforçadas. Um conjunto para servir nossas refeições e receber quem amamos.',
 image_url = 'images/pratos-tramontina-leonora.jpg',
 product_url = 'https://www.amazon.com.br/dp/B0B19274YZ'
WHERE id = 1 AND name = 'Jogo de pratos';

UPDATE gifts SET
 name = 'Copos canelados de vidro — 6 peças',
 description = 'Conjunto de seis copos transparentes com textura canelada. Feitos de vidro, são uma opção para água, sucos e outras bebidas, no dia a dia ou em encontros especiais.',
 image_url = 'images/copos-canelados.jpg',
 product_url = 'https://www.amazon.com.br/dp/B0DJ6DRH9J'
WHERE id = 2 AND name = 'Jogo de copos';
