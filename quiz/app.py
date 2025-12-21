import os
import re
from flask import Flask, request, jsonify
from langchain_core.prompts import PromptTemplate
from langchain_groq import ChatGroq
from langchain_core.output_parsers import BaseOutputParser

GROQ_API_KEY = os.getenv("GROQ_API_KEY")
if not GROQ_API_KEY:
    raise RuntimeError("GROQ_API_KEY env variable missing")

class QuizParser(BaseOutputParser):
    def parse(self, text: str):
        text = text.strip()
        questions = re.findall(r"<Question-(\d+)>(.*?)<option-1>", text, re.DOTALL)
        all_options = []
        for i in range(1, len(questions) + 1):
            question_options = []
            for j in range(1, 5):
                option_pattern = f"<option-{j}>(.*?)</option-{j}>"
                matches = re.findall(option_pattern, text, re.DOTALL)
                if len(matches) >= i:
                    question_options.append(matches[i-1].strip())
            all_options.append(question_options)
        answers = re.findall(r"<answer-(\d+)>(.*?)</answer-\d+>", text, re.DOTALL)
        quiz = []
        for i, (q_num, q_text) in enumerate(questions):
            q_opts = all_options[i] if i < len(all_options) else []
            q_ans = "N/A"
            for ans_num, ans_text in answers:
                if int(ans_num) == int(q_num):
                    q_ans = ans_text.strip()
                    break
            quiz.append({
                "question": q_text.strip(),
                "options": q_opts,
                "answer": q_ans,
                "type": "multiple_choice"
            })
        return quiz

def create_mcq_prompt_template():
    template = """
    You are an expert quiz maker for movies & general knowledge.
    Create a quiz with 10 multiple-choice questions
    about the following concept/context: {quiz_context} having Medium level of question.

    Rules:
    - Each question must be unique and cover different aspects of the context.
    - Avoid common textbook questions.
    - Make sure the correct answer is one of the four options provided.
    - Each question must be truly about the provided context!
    - Be very specific with the formatting.

    Follow this EXACT format:

    <Questions:>
        <Question-1>Your first question here?
            <option-1>Option A text</option-1>
            <option-2>Option B text</option-2>
            <option-3>Option C text</option-3>
            <option-4>Option D text</option-4>
        
        <Question-2>Your second question here?
            <option-1>Option A text</option-1>
            <option-2>Option B text</option-2>
            <option-3>Option C text</option-3>
            <option-4>Option D text</option-4>
    
    <Answers:>
        <answer-1>Exact text of correct option for question 1</answer-1>
        <answer-2>Exact text of correct option for question 2</answer-2>
    """
    return PromptTemplate.from_template(template)

def create_quiz_chain():
    parser = QuizParser()
    prompt = create_mcq_prompt_template()
    llm = ChatGroq(
        model="llama-3.3-70b-versatile",
        temperature=0.9,
        top_p=0.95,
        groq_api_key=GROQ_API_KEY
    )
    return prompt | llm | parser

# -- Flask app --
app = Flask(__name__)

@app.route("/quiz", methods=["POST"])
def quiz_route():
    data = request.json
    if not data or not data.get('context'):
        return jsonify({"error": "Missing 'context'"}), 400

    context = data['context'].strip()
    try:
        chain = create_quiz_chain()
        quiz = chain.invoke({"quiz_context": context})
        return jsonify({"quiz": quiz})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=7860)