/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego.dataobjects;

/**
 *
 * @author francof2842
 */
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;

public class Json {
    

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

   public String getHTML(String urlToRead) {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      String result = "";
      try {
         url = new URL(urlToRead);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         while ((line = rd.readLine()) != null) {
            result += line;
         }
         rd.close();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }
   
   public void getInitialState() throws IOException, JSONException {
       JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/start/MTG/json");

   }

   public static void main(String args[]) throws IOException, JSONException
   {
       
    JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/start/MTG/json");
    System.out.println(json.toString());
    System.out.println(json.get("grid"));
    System.out.println(json.get("score"));
    System.out.println(json.get("group"));
   }
}
