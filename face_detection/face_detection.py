from flask import Flask, request, jsonify
from deepface import DeepFace
from scipy.spatial.distance import cosine
from datasets import load_dataset
import numpy as np
from PIL import Image
import io
import pickle

app = Flask(__name__)

hf = load_dataset("SaladSlayer00/celebrity_lookalike")
raw_celebrities = hf["train"]

CACHE_FILE = "embeddings_cache.pkl"

try:
    with open(CACHE_FILE, "rb") as f:
        precomputed = pickle.load(f)
    print(f"Loaded {len(precomputed)} celebrity embeddings from cache.")
except FileNotFoundError:
    print("Cache not found. Precomputing celebrity embeddings...")
    precomputed = []
    for entry in raw_celebrities:
        img = entry["image"]
        embedding_result = DeepFace.represent(
            img_path=np.array(img),
            model_name="VGG-Face",
            enforce_detection=False
        )
        if embedding_result and "embedding" in embedding_result[0]:
            embedding = embedding_result[0]["embedding"]
            precomputed.append({
                "name": entry["label"],
                "embedding": embedding
            })
        else:
            print(f"Missing embedding for {entry['label']}")
    with open(CACHE_FILE, "wb") as f:
        pickle.dump(precomputed, f)
    print(f"Saved {len(precomputed)} celebrity embeddings to cache.")

@app.route("/match", methods=["POST"])
def match_celebrity():
    if "photo" not in request.files:
        return jsonify({"error": "Missing 'photo' in form-data"}), 400
    photo = request.files["photo"].read()
    img = Image.open(io.BytesIO(photo)).convert("RGB")
    img_array = np.array(img)
    try:
        result = DeepFace.represent(
            img_path=img_array,
            model_name="VGG-Face",
            enforce_detection=False
        )
        if not result or "embedding" not in result[0]:
            return jsonify({"error": "No face embedding found in uploaded photo"}), 400
        query_embedding = result[0]["embedding"]

        best_name, best_score = None, float("inf")
        for entry in precomputed:
            emb = np.array(entry["embedding"])
            score = cosine(query_embedding, emb)
            if score < best_score:
                best_score = score
                best_name = entry["name"]

        return jsonify({
            "closest_celebrity": best_name,
            "score": best_score
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
