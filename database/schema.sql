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

CREATE TABLE IF NOT EXISTS cinematchdb.favorites (
    user_id INT,
    movie_id INT,
    PRIMARY KEY (user_id, movie_id)
);

CREATE TABLE IF NOT EXISTS cinematchdb.quiz_history (
    id SERIAL PRIMARY KEY,
    user_id INT,
    score INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cinematchdb.kpis (
    kpi_id SERIAL PRIMARY KEY,
    target_type VARCHAR(20) NOT NULL,
    target_id INT NOT NULL,
    audience_engagement NUMERIC,
    star_power NUMERIC,
    awards_potential NUMERIC,
    popularity_proxy NUMERIC,
    created_at TIMESTAMP DEFAULT NOW()
);