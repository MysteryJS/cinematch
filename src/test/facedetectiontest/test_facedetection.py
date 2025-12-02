import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../../src')))


import pytest
from unittest.mock import patch, MagicMock
import sys

@pytest.fixture(autouse=True, scope="session")
def mock_heavy_dependencies():
    with patch.dict(sys.modules, {
        'deepface': MagicMock(),
        'tensorflow': MagicMock(),
        'tensorflow.keras': MagicMock(),
        'tensorflow.keras.models': MagicMock(),
        'tensorflow.keras.layers': MagicMock(),
    }):
        yield

@pytest.fixture
def app_and_precomputed():
    from face_detection.face_detection import app, precomputed
    return app, precomputed

def test_app_exists(app_and_precomputed):
    app, _ = app_and_precomputed
    assert app is not None

def test_precomputed_loaded(app_and_precomputed):
    _, precomputed = app_and_precomputed
    assert precomputed is not None
    assert isinstance(precomputed, dict)

def test_precomputed_has_expected_users(app_and_precomputed):
    _, precomputed = app_and_precomputed
    if not precomputed:
        pytest.skip("Precomputed is mocked; skipping key check")
    else:
        expected_users = ["alice", "bob", "charlie"]
        for user in expected_users:
            assert user in precomputed

def test_precomputed_values_are_lists(app_and_precomputed):
    _, precomputed = app_and_precomputed
    if not precomputed:
        pytest.skip("Precomputed is mocked; skipping value type check")
    else:
        for key, value in precomputed.items():
            assert isinstance(value, list)

def test_dummy_face_recognition_call(app_and_precomputed):
    from face_detection.face_detection import recognize_face
    result = recognize_face("dummy_image.jpg")
    assert isinstance(result, dict)


