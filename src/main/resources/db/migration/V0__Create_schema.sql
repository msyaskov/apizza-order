CREATE TABLE pizzas (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    date TIMESTAMP,
    price DOUBLE PRECISION NOT NULL
);

CREATE TABLE order_pizzas (
    order_id UUID REFERENCES orders (id),
    pizza_id UUID REFERENCES pizzas (id)
)