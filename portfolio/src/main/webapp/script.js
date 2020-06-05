// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

async function getLimitedMessage() {
  /*Leaving these comments in so when I go and try to fix the way it is fetched 
  I remeber the syntax.
  let amountOfComments = document.getElementById("commentAmount").value;
  ?amountOfComments='+ amountOfComments);*/

  const response = await fetch('/data');
  const messages = await response.json();
  document.getElementById('message-container').innerText = '';

  const messageHistory = 
    document.getElementById('data-page-message-container');
 
  messages.forEach(message => {
    messageHistory.appendChild(createListElement(message));
  });
}

function createListElement(text) {
  const textElement = document.createElement('li');
  textElement.innerText = text.message;

  const titleElement = document.createElement('span');
  titleElement.innerText = '';

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteMessage(text);

    textElement.remove();
  });

  textElement.appendChild(titleElement);
  textElement.appendChild(deleteButtonElement);
  return textElement;
}

function deleteMessage(message) {
  const params = new URLSearchParams();
  params.append('id', message.id);
  fetch('/delete-data', {method: 'POST', body: params});
}