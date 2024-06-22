import pip._vendor.requests
import argparse
import os
import glob
import re
import openai
import json

# plz find better to remove special character from extraction, it may influence the generation
s_char = ['\n', '\u2022', '\u201a','\u201b','\u201c', '\u201d', '\u2018', '\u2019', '\u00a0']
def urlify(s):
    # remove invalid characters
    s = re.sub(r"[^\w\s]", '', s)
    s = s.replace("\n", "")
    return s

def get_extracted_text(input, output):
    # read content file
    content = ""
    if os.path.isdir(input):
        infile = glob.glob(input + "/*.json")[0] # only one json we have now

        pathname, _ = os.path.splitext(infile)
        basename = os.path.basename(pathname)
        outfile = os.path.join(output, f'{basename}.txt')

        with open(infile, 'r') as fInt:
            data = json.load(fInt)
            for s, c in data.items():
                slide_intro = "For" + " " + s + ":"
                # TODO: organize different data types
                image = "image:"
                text = "text:"
                for key, value in c.items():
                    # TODO: image may have no extracted text
                    if 'image' in key:
                        image = image + " " + urlify(value)
                    if key == 'text':

                        text = text + " " + urlify(value)

                slide_content = slide_intro + " " + image + " " + text

                content = content + " " + slide_content

    return content, outfile

def run_chatgpt_w_text(input, output, prompt=False, completion=False):
    """ Run Chatgpt API using extracted content"""

    content, outfile = get_extracted_text(input, output)

    if completion:
        run_conversation_completion(content, prompt, outfile)
    else:
        run_converstion_chat_completion(content, prompt, outfile)

def run_conversation_completion(content, prompt, outfile):
    # TODO: separate this info
    api_endpoint = "https://api.openai.com/v1/chat/completions"
    api_key = ""

    request_headers = {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + api_key
    }

    request_data = {
        "model": "text-davinci-003",
        "prompt": prompt + content,
        "max_tokens": 1000,  # max token set
        "temperature": 0.5
    }

    response = pip._vendor.requests.post(api_endpoint, headers=request_headers, json=request_data)

    if response.status_code == 200:
        response_text = response.json()["choices"][0]["text"]

        with open(outfile, "w", encoding="utf-8") as file:
            file.write(response_text)
    else:
        print(f"Request failed with status code: {str(response.status_code)}")

def run_converstion_chat_completion(content, prompt, outfile):
    openai.api_key = ""

    messages = [{"role": "user", "content": prompt + content}]

    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo-0613",
        messages=messages,
    )

    response_message = response["choices"][0]["message"]["content"]

    with open(outfile, "w", encoding="utf-8") as file:
        file.write(response_message)

def main():
    parser = argparse.ArgumentParser()

    # required arguments
    required_args = parser.add_argument_group('required input arguments')

    # TODO: define templates for more prompts with accurate generation
    prompt = "Give you text with information about slide page, image and plain text please help me write a speech draft:"
    required_args.add_argument('input', type=str,
                               help='Path of the input directory')
    required_args.add_argument('output', type=str,
                               help='Path of the directory containing files to save generated script')

    args = parser.parse_args()
    run_chatgpt_w_text(args.input, args.output, prompt)

if __name__ == '__main__':
    main()
