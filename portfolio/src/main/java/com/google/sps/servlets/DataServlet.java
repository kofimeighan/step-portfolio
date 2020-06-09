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

@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private List<String> messages;
    private List<Comment> messagesEntityList;
    private int amtOfComments;
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String CLIENT_MESSAGE = "clientMessage";
    private static final String TIMESTAMP = "timestamp";
    private static final String ID = "id";
    private static final String TABLE_NAME = "Messages";

    public void init() {
        messages = new ArrayList<String>();
        messagesEntityList = new ArrayList<Comment>();
        amtOfComments = 0;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        //TO-DO:figure out how to sort queries based on timestamp
        Query query = new Query(TABLE_NAME);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        List<Entity> limitedResults = results.asList(FetchOptions.Builder.withLimit(amtOfComments));

        for (Entity entity : limitedResults) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty(NAME);
            String email = (String) entity.getProperty(EMAIL);
            String message = (String) entity.getProperty(CLIENT_MESSAGE);
            long timeStamp = (long) entity.getProperty(TIMESTAMP);

            Comment comment = new Comments(id, name, email, message, timeStamp);
            messagesEntityList.add(comment);
        }

        response.setContentType("application/json;");
        response.getWriter().println(convertToJsonUsingGson(messagesEntityList));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
        String name = request.getParameter(NAME);
        String email = request.getParameter(EMAIL);
        String clientMessage = request.getParameter(CLIENT_MESSAGE);
        long timeStamp = System.currentTimeMillis();
        amtOfComments = Integer.parseInt(request.getParameter("commentAmount"));

        Entity messageEntity = new Entity(TABLE_NAME);
        messageEntity.setProperty(NAME, name);
        messageEntity.setProperty(EMAIL, email);
        messageEntity.setProperty(CLIENT_MESSAGE, clientMessage);
        messageEntity.setProperty(TIMESTAMP, timeStamp);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(messageEntity); 

        response.setContentType("text/html");

        //checking for empty messages
        if(!clientMessage.isEmpty()){
            messages.add(clientMessage+"\n");
        }

        //printing each individual message
        for(int i=0; i<messages.size(); i++) {
            response.getWriter().println(messages.get(i));
        }
        //TODO:
        //look at a better way to clean the boxes
        //get the input # from html and send to JS without redirecting so that multiple users
        //can use the site at once
        response.sendRedirect("/index.html");
    }

    private String convertToJsonUsingGson(List<Comments> messageList) {
        Gson gson = new Gson();
        String json = gson.toJson(messageList);
      
        return json;
    }
}