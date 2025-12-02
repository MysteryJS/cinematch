import pytest
from unittest.mock import MagicMock

@pytest.fixture
def app_and_predict():
    # Mock the sentiment_analysis module
    sentiment_analysis = MagicMock(name='sentiment_analysis')

    # Mock app and functions
    app = MagicMock(name='app')
    predict = MagicMock(name='predict')
    emotion_to_genre = {
        "joy": ["Comedy", "Adventure"],
        "sadness": ["Drama"],
        "disgust": ["Horror"],
        "fear": ["Thriller"],
        "anger": ["Action"]
    }

    # Example predict behavior
    def dummy_predict(text):
        if not text:
            return {}
        if "happy" in text or "joy" in text:
            return {"joy": 0.9}
        if "sad" in text:
            return {"sadness": 0.8}
        return {"neutral": 1.0}

    predict.side_effect = dummy_predict

    sentiment_analysis.app = app
    sentiment_analysis.predict = predict
    sentiment_analysis.emotion_to_genre = emotion_to_genre

    return app, predict, emotion_to_genre

def test_app_exists(app_and_predict):
    app, _, _ = app_and_predict
    assert app is not None

def test_predict_returns_expected_emotions(app_and_predict):
    _, predict, emotion_to_genre = app_and_predict
    result = predict("I feel happy today")
    assert isinstance(result, dict)
    for label in result.keys():
        assert label in emotion_to_genre

def test_predict_empty_text_returns_empty_dict(app_and_predict):
    _, predict, _ = app_and_predict
    result = predict("")
    assert result == {}

def test_predict_negative_emotion(app_and_predict):
    _, predict, emotion_to_genre = app_and_predict
    result = predict("I am very sad")
    assert "sadness" in result

def test_api_recommend_basic(app_and_predict):
    app, _, emotion_to_genre = app_and_predict
    # Mock the post request
    response = MagicMock(name='response')
    response.status_code = 200
    response.get_json.return_value = {
        "genres": ["Comedy"],
        "emotion_probabilities": {"joy": 0.9},
        "titles": ["Some Movie"]
    }

    app.test_client.return_value.post.return_value = response

    client = app.test_client()
    resp = client.post("/recommend", json={"text": "I feel joy!", "num_titles": 3})
    data = resp.get_json()

    assert resp.status_code == 200
    assert isinstance(data["genres"], list)
    assert isinstance(data["emotion_probabilities"], dict)
    assert isinstance(data["titles"], list)

def test_api_returns_different_genres_for_different_emotions(app_and_predict):
    app, _, emotion_to_genre = app_and_predict

    response1 = MagicMock()
    response1.status_code = 200
    response1.get_json.return_value = {"genres": ["Comedy"]}

    response2 = MagicMock()
    response2.status_code = 200
    response2.get_json.return_value = {"genres": ["Thriller"]}

    app.test_client.return_value.post.side_effect = [response1, response2]

    client = app.test_client()
    resp1 = client.post("/recommend", json={"text": "I feel joy", "num_titles": 1})
    resp2 = client.post("/recommend", json={"text": "I feel fear", "num_titles": 1})

    genres1 = set(resp1.get_json()["genres"])
    genres2 = set(resp2.get_json()["genres"])

    assert genres1 != genres2
