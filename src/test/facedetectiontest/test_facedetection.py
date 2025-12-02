import subprocess
import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../..')))

required_packages = [
    "numpy",
    "pillow",
    "scipy",
    "pytest",
    "deepface",
    "tf-keras",
    "datasets",
    "opencv-python",
]

for package in required_packages:
    try:
        __import__(package if package != "tf-keras" else "tf_keras")
    except ImportError:
        subprocess.check_call([sys.executable, "-m", "pip", "install", package])

from face_detection.face_detection import app, precomputed

import pytest

def test_app_exists():
    assert app is not None

def test_precomputed_exists():
    assert precomputed is not None

