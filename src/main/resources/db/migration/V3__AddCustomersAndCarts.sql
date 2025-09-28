-- Insert sample customers
INSERT INTO customers (name, email, password, dob, phone_number, created_at, updated_at)
VALUES 
('Alice Johnson', 'alice@example.com', 'hashed_password1', '1990-05-14', '1234567890', NOW(), NOW()),
('Bob Smith', 'bob@example.com', 'hashed_password2', '1985-11-22', '0987654321', NOW(), NOW()),
('Charlie Brown', 'charlie@example.com', 'hashed_password3', '1992-07-01', '5551234567', NOW(), NOW());

-- Create a cart for each customer
INSERT INTO cart (customer_id, created_at, updated_at)
SELECT id, NOW(), NOW()
FROM customers
WHERE email IN ('alice@example.com', 'bob@example.com', 'charlie@example.com');
