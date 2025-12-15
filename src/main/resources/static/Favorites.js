function addToFavorites(imdbId) {
    fetch(`/api/favorites/${imdbId}`, { method: 'POST' })
        .then(() => alert("Added to favorites!"))
        .catch(err => console.error(err));
}
