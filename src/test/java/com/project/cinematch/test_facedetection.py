import io
import numpy as np
from unittest.mock import patch
from PIL import Image

from app import app, precomputed



def make_fake_image():
    img = Image.new("RGB", (100, 100), color=(255, 0, 0))
    buffer = io.BytesIO()
    img.save(buffer, format="JPEG")
    buffer.seek(0)
    return buffer



def test_match_missing_photo():
    client = app.test_client()
    resp = client.post("/match")

    assert resp.status_code == 400
    assert b"Missing 'photo'" in resp.data



@patch("app.DeepFace.represent")
def test_match_success(mock_represent):


    mock_represent.return_value = [{
        "embedding": np.array([0.1, 0.2, 0.3])
    }]


    precomputed.clear()
    precomputed.extend([
        {"name": "CelebrityA", "embedding": [0.0, 0.2, 0.3]},
        {"name": "CelebrityB", "embedding": [1.0, 1.0, 1.0]},
    ])

    client = app.test_client()
    image = make_fake_image()

    resp = client.post(
        "/match",
        data={"photo": (image, "test.jpg")},
        content_type="multipart/form-data"
    )

    json_data = resp.get_json()

    assert resp.status_code == 200
    assert json_data["closest_celebrity"] == "CelebrityA"
    assert "score" in json_data



@patch("app.DeepFace.represent")
def test_match_no_embedding(mock_represent):
    mock_represent.return_value = [{}]  # missing "embedding"

    client = app.test_client()
    image = make_fake_image()

    resp = client.post(
        "/match",
        data={"photo": (image, "test.jpg")},
        content_type="multipart/form-data"
    )

    assert resp.status_code == 400
    assert b"No face embedding found" in resp.data



@patch("app.DeepFace.represent")
def test_match_deepface_exception(mock_represent):
    mock_represent.side_effect = RuntimeError("DeepFace failed")

    client = app.test_client()
    image = make_fake_image()

    resp = client.post(
        "/match",
        data={"photo": (image, "test.jpg")},
        content_type="multipart/form-data"
    )

    assert resp.status_code == 500
    assert b"DeepFace failed" in resp.data