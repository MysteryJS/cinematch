import sys
import os
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../../sentiment_analysis")))

from sentiment_analysis import sentiment_analysis
app = sentiment_analysis.app
predict = sentiment_analysis.predict
emotion_to_genre = sentiment_analysis.emotion_to_genre

@pytest.fixture
def client():
    app.testing = True
    return app.test_client()

def test_predict_returns_emotions():
    result = predict("I feel happy and excited today")
    assert isinstance(result, dict)
    assert len(result) > 0
    for label in result.keys():
        assert label in emotion_to_genre

def test_predict_empty_text():
    result = predict("")
    assert isinstance(result, dict)
    assert len(result) == 0

def test_predict_negative_emotion():
    result = predict("I am very sad and disappointed")
    assert isinstance(result, dict)
    assert "sadness" in result or "disappointment" in result

def test_api_recommend_basic(client):
    response = client.post("/recommend", json={
        "text": "I feel so much joy today!",
        "num_titles": 3
    })
    assert response.status_code == 200
    data = response.get_json()
    assert isinstance(data["genres"], list)
    assert isinstance(data["emotion_probabilities"], dict)
    assert isinstance(data["titles"], list)

def test_api_returns_different_genres_for_different_emotions(client):
    response1 = client.post("/recommend", json={
        "text": "I feel joy and excitement",
        "num_titles": 1
    })
    response2 = client.post("/recommend", json={
        "text": "I feel fear and disgust",
        "num_titles": 1
    })
    assert response1.status_code == 200
    assert response2.status_code == 200
    genres1 = set(response1.get_json()["genres"])
    genres2 = set(response2.get_json()["genres"])
    assert genres1 != genres2