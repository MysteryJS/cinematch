from flask import Flask, request, jsonify
from deepface import DeepFace
from scipy.spatial.distance import cosine
from datasets import load_dataset
import numpy as np
from PIL import Image
import io
import os

app = Flask(__name__)

embeddings_path = 'embeddings.npy'
labels_path = 'labels.npy'

if not (os.path.exists(embeddings_path) and os.path.exists(labels_path)):
    ds = load_dataset("SaladSlayer00/celebrity_lookalike", split="train")
    embeddings = []
    labels = []
    for i, entry in enumerate(ds):
        img = entry["image"]
        if not isinstance(img, Image.Image):
            img = Image.fromarray(img)
        img = img.convert("RGB")
        arr = np.array(img)
        try:
            face_embs = DeepFace.represent(img_path=arr, model_name="VGG-Face", detector_backend="skip")
            embeddings.append(face_embs[0]['embedding'])
            labels.append(entry['label'])
        except Exception as e:
            print(f"Sample {i}: Σφάλμα DeepFace: {e}")
        if (i + 1) % 100 == 0:
            print(f"Processed {i + 1} samples...")
    np.save(embeddings_path, np.array(embeddings))
    np.save(labels_path, np.array(labels))
    print(f"Έγινε αποθήκευση {len(embeddings)} embeddings/labels!\n")
else:
    print("Embeddings/labels ήδη υπάρχουν... συνεχίζω.")

celebrity_embeddings = np.load(embeddings_path)
celebrity_labels = np.load(labels_path)

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
            detector_backend="skip",
            enforce_detection=False
        )
        if not result or "embedding" not in result[0]:
            return jsonify({"error": "No face embedding found in uploaded photo"}), 400
        query_embedding = result[0]["embedding"]

        scores = [cosine(query_embedding, emb) for emb in celebrity_embeddings]
        best_idx = np.argmin(scores)
        best_name = celebrity_labels[best_idx]
        best_score = scores[best_idx]

        return jsonify({
            "closest_celebrity": str(best_name),
            "score": float(best_score)
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=7860)