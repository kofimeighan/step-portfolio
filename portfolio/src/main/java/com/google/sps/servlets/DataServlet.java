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

  //private final Date submitTime = new Date();
  private ArrayList<String> messages;

  public void init() {
    //submitTime = new Date();
    messages = new ArrayList<String>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {

    //Date submitTime = new Date();
    response.setContentType("application/json");
    String json = convertToJson(messages);
    response.getWriter().println(json);

    //response.setContentType("text/html;");
    //response.getWriter().println("<h1>Hello world!</h1>");
    //response.getWriter().println("<body>"+json+"</body>");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String htmlMessage = request.getParameter("clientMessage");
    System.out.println(htmlMessage);
    messages.add(htmlMessage);
    //Gson gson = new Gson();
    String json = convertToJson(messages);

    response.sendRedirect("/index.html");

    }

  private String convertToJson(ArrayList<String> messageList) {
    String json = "{ \"message\" :";
    for(int i = 0; i<messageList.size();i++){
      json += " \"message\" : ";
      json += "\""+messageList.get(i)+"\""+", ";
      if(i!=(messageList.size()-1)){
        json += ", ";
        }
      else{
        json += " }";
        }
    }

    return json;
  }

}
