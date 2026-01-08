![image alt](https://github.com/MysteryJS/cinematch/blob/main/src/main/resources/static/logo.png)

CineMatch, a movie recommendation application.
# For more information you can visit our [wiki page](https://github.com/MysteryJS/cinematch/wiki)

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
