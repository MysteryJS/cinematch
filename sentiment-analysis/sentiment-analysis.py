import torch
import pandas as pd
from flask import Flask, request, jsonify

from transformers import AutoTokenizer, AutoConfig, XLMRobertaForSequenceClassification, PreTrainedModel
from torch import nn
from torch.nn import Dropout

app = Flask(__name__)
df = pd.read_csv("movieswithplotsandgenres.csv")

# Define the CustomModel class which is predicting Both SENTIMENT POLARITY &  EMOTIONS
class CustomModel(XLMRobertaForSequenceClassification):
    def __init__(self, config, num_emotion_labels):
        super(CustomModel, self).__init__(config)
        self.num_emotion_labels = num_emotion_labels
        self.dropout_emotion = nn.Dropout(config.hidden_dropout_prob)
        self.emotion_classifier = nn.Sequential(
            nn.Linear(config.hidden_size, 512),
            nn.Mish(),
            nn.Dropout(0.3),
            nn.Linear(512, num_emotion_labels)
        )
        self._init_weights(self.emotion_classifier[0])
        self._init_weights(self.emotion_classifier[3])
    def _init_weights(self, module):
        if isinstance(module, nn.Linear):
            module.weight.data.normal_(mean=0.0, std=self.config.initializer_range)
            if module.bias is not None:
                module.bias.data.zero_()
    def forward(self, input_ids=None, attention_mask=None, labels=None):
        outputs = self.roberta(input_ids=input_ids, attention_mask=attention_mask)
        sequence_output = outputs[0]
        if len(sequence_output.shape) != 3:
            raise ValueError(f"Expected sequence_output to have 3 dimensions, got {sequence_output.shape}")

        cls_hidden_states = sequence_output[:, 0, :]
        cls_hidden_states = self.dropout_emotion(cls_hidden_states)
        emotion_logits = self.emotion_classifier(cls_hidden_states)

        if labels is not None:
            class_weights = torch.tensor([1.0] * self.num_emotion_labels).to(labels.device)
            loss_fct = nn.BCEWithLogitsLoss(pos_weight=class_weights)
            loss = loss_fct(emotion_logits, labels)
            return {"loss": loss, "emotion_logits": emotion_logits}
        return {"emotion_logits": emotion_logits}

model_dir = "gsar78/HellenicSentimentAI_v2"
tokenizer = AutoTokenizer.from_pretrained(model_dir)
config = AutoConfig.from_pretrained(model_dir)
model = CustomModel.from_pretrained(model_dir, config=config, num_emotion_labels=18)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)

# Function to predict emotion
def predict(texts):

    inputs = tokenizer(texts, padding=True, truncation=True, return_tensors="pt", max_length=512)
    inputs = {k: v.to(device) for k, v in inputs.items()}
    
    # Get model predictions
    model.eval()
    with torch.no_grad():
        outputs = model(**inputs)

    emotion_logits = outputs["emotion_logits"]
    emotion_probs = torch.sigmoid(emotion_logits)
    emotion_probs_list = (emotion_probs * 100).tolist()[0]  # Get the first (and only) sample and convert to %
    emotion_labels = [
        'joy', 'trust', 'excitement', 'gratitude', 'hope', 'love', 'pride',
        'anger', 'disgust', 'fear', 'sadness', 'anxiety', 'frustration', 'guilt',
        'disappointment', 'surprise', 'anticipation', 'neutral'
    ]

    threshold = 10.0

    emotion_results = {label: prob for label, prob in zip(emotion_labels, emotion_probs_list) if prob > threshold}
    return emotion_results

all_genres = set()
emotion_to_genre = {
    'joy': ['Comedy', 'Family'],
    'trust': ['Drama'],
    'excitement': ['Action', 'Adventure'],
    'gratitude': ['Biography', 'Family'],
    'hope': ['Drama', 'Fantasy'],
    'love': ['Romance'],
    'pride': ['Biography', 'Sport'],
    'anger': ['Thriller', 'Crime'],
    'disgust': ['Horror'],
    'fear': ['Horror', 'Thriller'],
    'sadness': ['Drama'],
    'anxiety': ['Thriller', 'Mystery'],
    'frustration': ['Drama'],
    'guilt': ['Drama'],
    'disappointment': ['Drama'],
    'surprise': ['Adventure'],
    'anticipation': ['Thriller', 'Adventure'],
    'neutral': ['Documentary']
}

@app.route("/recommend", methods=["POST"])
def recommend():
    data = request.get_json()
    sample_text = data.get("text", "")
    num_titles = int(data.get("num_titles", 10))

    emotion_results = predict(sample_text)
    all_genres = set()
    for label in emotion_results:
        all_genres.update(emotion_to_genre[label])
    genres_from_analysis = list(all_genres)
    desired_genres = set(genres_from_analysis)

    def at_least_two_from_analysis(genres_str):
        movie_genres = set(tag.strip() for tag in genres_str.split(','))
        common = movie_genres & desired_genres
        return len(common) >= 1

    filtered_df = df[df['Genres'].apply(at_least_two_from_analysis)]

    num_titles = min(num_titles, len(filtered_df))
    random_titles = filtered_df['Film_title'].sample(n=num_titles, random_state=None).tolist()

    return jsonify({
        "genres": genres_from_analysis,
        "emotion_probabilities": emotion_results,
        "titles": random_titles
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)