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

function onLoad() {
  checkLoginStatus();
  getLimitedMessage();
  initMap();
} 

async function getLimitedMessage() {
	let amountOfComments = document.getElementById("commentAmount").value
  const response = await fetch('/data?amtOfComments='+amountOfComments);
  const messages = await response.json();
  document.getElementById('message-container').innerText = '';

	document.getElementById('data-page-message-container').innerHTML= '';
  const messageHistory = 
    document.getElementById('data-page-message-container');
 
  messages.forEach(message => {
    messageHistory.appendChild(createListElement(message));
  });
}

async function checkLoginStatus() {
  const response = await fetch('/login');
  const buttonHTML = await response.text();
	
  document.getElementById("submit").innerHTML = ' ';
 	document.getElementById("submit").innerHTML = buttonHTML;
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

//global variable for map functions
let map;

function initMap() {
  map = new google.maps.Map(
    document.getElementById('map'), 
    {center: {lat: 37.421903, lng: -122.084674}, zoom: 18,
    mapTypeId: "satellite"});
  console.log('done????');
}

function codeAddress(address) {
  let goTo = address;
  if(goTo == undefined) {
    goTo = document.getElementById('address').value;
  }

  let geocoder = new google.maps.Geocoder();
  geocoder.geocode( { 'address': goTo }, function(results, status) {
    if (status == 'OK') {
      map.setCenter(results[0].geometry.location);
      map.setZoom(18);
      var marker = new google.maps.Marker({
          map: map,
          position: results[0].geometry.location
      });
    } else {
      alert('Geocode was not successful for the following reason: ' + status);
    }
  });
}