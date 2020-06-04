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

function sayHello() {
  console.log("Fetching a Hello");

  fetch('/data').then(response => response.json()).then((jsonString) => {
    console.log(jsonString);
    document.getElementById('hello-container').innerText = jsonString; });
}

async function getMessage() {
  console.log("Getting the message");
    
  const response = await fetch('/data');
  const message = await response.json();
  document.getElementById('message-container').innerText = '';
  console.log(message);

  const messageHistory = document.getElementById('data-page-message-container');
  messageHistory.innerHTML = '';
  //messageHistory.appendChild(createListElement(message));
  //Included because comment history might be implimented in the future^
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

