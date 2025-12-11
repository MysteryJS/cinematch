FROM python

WORKDIR /app

COPY requirements.txt .

RUN apt-get update

RUN pip install -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["python", "sentiment-analysis.py"]
