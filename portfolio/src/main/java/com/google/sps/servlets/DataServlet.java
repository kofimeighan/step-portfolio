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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String CLIENTMESSAGE = "clientMessage";
    private static final String TIMESTAMP = "timestamp";
    private static final String TABLENAME = "Messages";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        PrintWriter out = response.getWriter();
        UserService userService = UserServiceFactory.getUserService();
        //TO-DO:figure out how to sort queries based on timestamp
      
        Query query = new Query(TABLENAME);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        //getting a query with the correct amount of comments
        int amtOfComments = Integer.parseInt(request.getParameter("amtOfComments"));
        List<Entity> limitedResults = results.asList(FetchOptions.Builder.withLimit(amtOfComments));

        List<Comment> messagesEntityList = new ArrayList<Comment>();
        for (Entity entity : limitedResults) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty(NAME);
            String email = userService.getCurrentUser().getEmail();
            String message = (String) entity.getProperty(CLIENTMESSAGE);
            long timeStamp = (long) entity.getProperty(TIMESTAMP);

            Comment comments = new Comment(id, name, email, message, timeStamp);
            messagesEntityList.add(comments);
        }


        response.setContentType("application/json");
        response.getWriter().println(convertToJsonUsingGson(messagesEntityList));   
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
     	  UserService userService = UserServiceFactory.getUserService();
        String name = request.getParameter(NAME);
        String email = userService.getCurrentUser().getEmail();
        String clientMessage = request.getParameter(CLIENTMESSAGE);
        long timeStamp = System.currentTimeMillis();
        response.setContentType("text/html");

        Entity messageEntity = new Entity(TABLENAME);
        messageEntity.setProperty(NAME, name);
        messageEntity.setProperty(EMAIL, email);
        messageEntity.setProperty(CLIENTMESSAGE, clientMessage);
        messageEntity.setProperty(TIMESTAMP, timeStamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(messageEntity); 

        //checking for empty messages
        List<String> messages = new ArrayList<String>();
        if(!clientMessage.equals(" ")){
            messages.add(clientMessage+"\n");
        }

        //printing each individual message
        for(int i=0; i< messages.size(); i++) {
            response.getWriter().println(messages.get(i));
        }
        response.sendRedirect("/leaveamessage.html");
    }

    private String convertToJsonUsingGson(List<Comment> messageList) {
        Gson gson = new Gson();
        String json = gson.toJson(messageList);
      
        return json;
    }
}