![image alt](https://github.com/MysteryJS/cinematch/blob/main/src/main/resources/static/logo.png)

CineMatch, a movie recommendation application.
# ✅ Features

- Search for movies, actors, and directors
- View trending movies and celebrities
- Movie quiz game
- User uploads: photos & short videos re-enacting scenes
- Actor look-alike (Face recognition)
- Sentiment analysis text

# 🤖 APIS

- [Deepface](https://github.com/serengil/deepface) for face recognition.
- [HellenicSentimentAI v2](https://huggingface.co/gsar78/HellenicSentimentAI_v2) for sentiment analysis.
- [OMDB](https://www.omdbapi.com/) for title searching.
- [Trakt.tv](https://trakt.tv/) for trending movies.
- [OpenTDB](https://opentdb.com/) for quiz creation.

# 📊 Datasets

- [Celebrity lookalike](https://huggingface.co/datasets/SaladSlayer00/celebrity_lookalike) for face recognition vectors.
- [movieswithplotsandgenres](https://huggingface.co/datasets/archich/movieswithplotsandgenres) for sentiment analysis gerne recognition.

# 🖱️ Instrutions
```sh
# Clone this repository
git clone https://github.com/MysteryJS/cinematch
```

```bash
# Go into repository
cd cinematch
```

``` bash
# Create .env file
touch .env
```

**Disclaimer:** You will need to provide your ***.env*** file. There variables you will need are listed below and you will need to generate some keys first.
- [OMDB Key](https://www.omdbapi.com/)
- [Trakt.tv Key](https://trakt.tv/)

``` bash
# .env file content
OMDB_KEY=your_OMDB_key
TRAKT_CLIENT_ID=your_trakt_key
DB_URL=your_db_url
DB_USER=your_db_user
DB_PASS=your_db_password
```

```dockerfile
# Execute the Docker compose
docker compose up
```

# ©️ License
All rights reserved. ©
