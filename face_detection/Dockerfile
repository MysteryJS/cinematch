FROM python:3.13.9-slim

WORKDIR /app

COPY requirements.txt .

RUN apt-get update && apt-get install -y libgl1 libglx-mesa0

RUN pip install -r requirements.txt

COPY . .

EXPOSE 5001

CMD ["python", "face_detection.py"]
