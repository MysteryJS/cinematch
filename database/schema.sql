CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS search_history (
  search_id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  movie_id INT,
  searched_at TIMESTAMP DEFAULT now()
);