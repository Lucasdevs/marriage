ALTER TABLE gifts ADD COLUMN price NUMERIC(12,2);
ALTER TABLE gifts ADD CONSTRAINT gift_price_positive CHECK (price IS NULL OR price > 0);
UPDATE gifts SET price = 110.00 WHERE product_url = 'https://www.amazon.com.br/dp/B0B19274YZ';
UPDATE gifts SET price = 82.00 WHERE product_url = 'https://www.amazon.com.br/dp/B0DJ6DRH9J';
