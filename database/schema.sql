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