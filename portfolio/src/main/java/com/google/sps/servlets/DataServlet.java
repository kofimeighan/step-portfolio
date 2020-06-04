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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages;
  //add constant names

  public void init() {
      messages = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
              throws IOException {

      Query query = new Query("Messages");//.setLimit(3);//.addSort("timestamp", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      List<Entity> limitedResults = results.asList(FetchOptions.Builder.withLimit(5));

      List<Comments> messagesEntityList = new ArrayList<Comments>();
      for (Entity entity : limitedResults) {
          long id = entity.getKey().getId();
          String name = (String) entity.getProperty("name");
          String email = (String) entity.getProperty("email");
          String message = (String) entity.getProperty("clientMessage");
          long timeStamp = (long) entity.getProperty("timestamp");

          Comments commentsObject = new Comments(name, email, message, timeStamp);
          messagesEntityList.add(commentsObject);
      }
      //adding a list of objects
      response.setContentType("application/json;");
     
      response.getWriter().println(convertToJsonUsingGson(messagesEntityList));
      
      
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String clientMessage = request.getParameter("clientMessage");
      String name = request.getParameter("name");
      String email = request.getParameter("email");
      long timeStamp = System.currentTimeMillis();

      Entity messageEntity = new Entity("Messages");
      messageEntity.setProperty("name", name);
      messageEntity.setProperty("email", email);
      messageEntity.setProperty("clientMessage", clientMessage);
      messageEntity.setProperty("timestamp", timeStamp);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(messageEntity); 

      response.setContentType("text/html");
      if(!clientMessage.equals(' ')){
        messages.add(clientMessage+"\n");
      }
    
      for(int i=0; i< messages.size(); i++) {
        response.getWriter().println(messages.get(i));
      }

      response.sendRedirect("/index.html");
    }

  private String convertToJsonUsingGson(List<Comments> messageList) {
      Gson gson = new Gson();
      String json = gson.toJson(messageList);
    
      return json;
  }

  private String convertToJson(List<String> messageList) {
      String json = "[ { ";

      for(int i = 0; i<(messageList.size()); i= i+2){
          json += " \"message\" : ";
          json += "\""+messageList.get(i)+"\", ";
          json += " \"timestamp\" : ";
          json += "\""+messageList.get(i+1)+"\"";

          if (i !=(messageList.size()-1)){
              json += ", ";
          }
          else {
              json += " }";
          }
      }

      return json;
  }
}