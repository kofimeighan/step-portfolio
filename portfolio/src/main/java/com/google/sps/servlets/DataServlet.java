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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<String> messages;

  public void init() {
      //submitTime = new Date();
      messages = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
  throws IOException {

      Query query = new Query("Task//");//.addSort("timestamp", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
    
      for (Entity entity : results.asIterable()) {
          long id = entity.getKey().getId();
          String message = (String) entity.getProperty("message");
          messages.add(message);
      }

      response.setContentType("application/json;");
      response.getWriter().println(convertToJsonUsingGson(messages));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String clientMessage = request.getParameter("clientMessage");
      long timeStamp = System.currentTimeMillis();

      /*System.out.println(htmlMessage);
      messages.add(htmlMessage);
      Gson gson = new Gson();
      String json = convertToJson(clientMessage));*/
      Entity messageEntity = new Entity("Messages");
      messageEntity.setProperty("message", clientMessage);
      messageEntity.setProperty("timestamp", timeStamp);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(messageEntity);

      response.sendRedirect("/index.html");

    }

  private String convertToJsonUsingGson(ArrayList<String> messageList) {
      Gson gson = new Gson();
      String json = gson.toJson(messageList);
    
      return json;
  }

  private String convertToJson(ArrayList<String> messageList) {
      String json = "[ { ";
      for(int i = 0; i<(messageList.size());i= i+2){
          json += " \"message\" : ";
          json += "\""+messageList.get(i)+"\", ";
          json += " \"timestamp\" : ";
          json += "\""+messageList.get(i+1)+"\"";

          if(i !=(messageList.size()-1)){
              json += ", ";
          }
          else{
            json += " }";
          }
      }

      return json;
  }
}