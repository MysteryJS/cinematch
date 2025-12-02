import sys
import os
import pytest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '../../')))

from face_detection.face_detection import app, precomputed

@pytest.fixture
def app_and_precomputed():
    return app, precomputed

def test_app_exists(app_and_precomputed):
    app, _ = app_and_precomputed
    assert app is not None

def test_precomputed_loaded(app_and_precomputed):
    _, precomputed = app_and_precomputed
    assert precomputed is not None

def test_precomputed_has_expected_users(app_and_precomputed):
    _, precomputed = app_and_precomputed
    expected_users = ['user1', 'user2']
    for user in expected_users:
        assert user in precomputed

def test_precomputed_values_are_lists(app_and_precomputed):
    _, precomputed = app_and_precomputed
    for key, value in precomputed.items():
        assert isinstance(value, list)

def test_dummy_face_recognition_call(app_and_precomputed):
    _, precomputed = app_and_precomputed
    dummy_input = 'dummy_face_encoding'
    for user, encodings in precomputed.items():
        encodings.append(dummy_input)
        assert dummy_input in precomputed[user]


