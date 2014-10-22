/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Juego;

import Juego.ai.AiSolver;
import Juego.dataobjects.ActionStatus;
import Juego.game.Board;
import Juego.dataobjects.Direction;
import static Juego.dataobjects.Json.readJsonFromUrl;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * The main class of the Game.
 * 
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class ConsoleGame {

    /**
     * Main function of the game.
     * 
     * @param args
     * @throws CloneNotSupportedException 
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        
        System.out.println("The 2048 Game in JAVA!");
        System.out.println("======================");
        System.out.println();

        
        while(true) {
            printMenu();
            int choice;
            try {
                Scanner sc = new Scanner (System.in);     
                choice = sc.nextInt();
                switch (choice) {
                    case 1:  playGame();
                             break;
                    case 2:  calculateAccuracy();
                             break;
                    case 3:  help();
                             break;
                    case 4:  redHat();
                             break;
                    case 5:  return;
                    default: throw new Exception();
                }
            }
            catch(Exception e) {
                System.out.println("Wrong choice");
            }
        }
    }
    
    
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
  
   public static void movementJson(Board game, Direction hint) throws IOException, JSONException {
       int mov;
       mov = dirToInt(hint);
       String Session = game.getSession();
       JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/state/" + Session + "/move/" + mov + "/json");
       game.setBoard(json.get("grid").toString());
       setBoardStatus(game, json);
       printFullBoard(game, json, hint);
   }
    
    /**
     * Prints Help menu
     */
    public static void help() {
        System.out.println("Seriously?!?!?");
    }
    
    /**
     * Prints main menu
     */
    public static void printMenu() {
        System.out.println();
        System.out.println("Choices:");
        System.out.println("1. Play the 2048 Game");
        System.out.println("2. Estimate the Accuracy of AI Solver");
        System.out.println("3. Help");
        System.out.println("4. Red Hat");
        System.out.println("5. Quit");
        System.out.println();
        System.out.println("Enter a number from 1-4:");
    }
    
    /**
     * Estimates the accuracy of the AI solver by running multiple games.
     * 
     * @throws CloneNotSupportedException 
     */
    public static void calculateAccuracy() throws CloneNotSupportedException {
        int wins=0;
        int total=10;
        int moves = 0;
        System.out.println("Running "+total+" games to estimate the accuracy:");
        
        for(int i=0;i<total;++i) {
            int hintDepth = 7;
            Board theGame = new Board();
            Direction hint = AiSolver.findBestMove(theGame, hintDepth);
            ActionStatus result=ActionStatus.CONTINUE;
            while(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE) {
                moves++;
                result=theGame.action(hint);

                if(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE ) {
                    hint = AiSolver.findBestMove(theGame, hintDepth);
                }
            }

            if(result == ActionStatus.WIN) {
                ++wins;
                System.out.println("Game "+(i+1)+" - won in " + moves + " moves.");
                moves = 0;
            }
            else {
                System.out.println("Game "+(i+1)+" - lost in " + moves + " moves.");
                moves = 0;
            }
        }
        
        System.out.println(wins+" wins out of "+total+" games.");
    }
    
    public static int dirToInt (Direction hint){
        if (hint==Direction.UP){
            return 0;
        }
        else if (hint==Direction.RIGHT){
            return 1;
        }
        else if (hint==Direction.DOWN){
            return 2;
        }
        else{
            return 3;
        }

        
    }
    
    public static void setBoardStatus (Board game,JSONObject json) throws IOException, JSONException{
        game.setScore(json.get("score").toString());
        game.setWon(json.get("won").toString());
        game.setOver(json.get("over").toString());
    }
    
    public static void printFullBoard(Board game, JSONObject json, Direction hint){
        printBoard(game.getBoardArray(), game.getScore(), hint);
    }
    
    public static void redHat() throws CloneNotSupportedException, IOException, JSONException{
        
        int hintDepth = 7;
        
        System.out.println("Running Red Hat Game: ");
        
        JSONObject json = readJsonFromUrl("http://nodejs2048-universidades.rhcloud.com/hi/start/MTG/json");
        Board game = new Board(json.get("grid").toString());
        game.setSession(json.get("session_id").toString());
        setBoardStatus(game, json);

        
        Direction hint = AiSolver.findBestMove(game, hintDepth);
        movementJson(game, hint);
        setBoardStatus(game, json);
        printFullBoard(game, json, hint);
        
        while (game.getWon() == false && game.getOver() == false){
            hint = AiSolver.findBestMove(game, hintDepth);
            movementJson(game, hint);
            
        }
        
        System.out.println("Finish Red Hat! ");
        System.out.println("Won: " + game.getWon() );
        System.out.println("Over: " + game.getOver());
        
    }
    
    /**
     * Method which allows playing the game.
     * 
     * @throws CloneNotSupportedException 
     */
    public static void playGame() throws CloneNotSupportedException {
        System.out.println("Play the 2048 Game!"); 
        System.out.println("Use 8 for UP, 6 for RIGHT, 2 for DOWN and 4 for LEFT. Type a to play automatically and q to exit. Press enter to submit your choice.");
        
        int hintDepth = 7;
        Board theGame = new Board();
        Direction hint = AiSolver.findBestMove(theGame, hintDepth);
        printBoard(theGame.getBoardArray(), theGame.getScore(), hint);
        
        try {
            InputStreamReader unbuffered = new InputStreamReader(System.in, "UTF8");
            char inputChar;
            
            ActionStatus result=ActionStatus.CONTINUE;
            while(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE) {
                inputChar = (char)unbuffered.read();
                //inputChar = 'a';
                if(inputChar=='\n' || inputChar=='\r') {
                    continue;
                }
                else if(inputChar=='8') {
                    result=theGame.action(Direction.UP);
                }
                else if(inputChar=='6') {
                    result=theGame.action(Direction.RIGHT);
                }
                else if(inputChar=='2') {
                    result=theGame.action(Direction.DOWN);
                }
                else if(inputChar=='4') {
                    result=theGame.action(Direction.LEFT);
                }
                else if(inputChar=='a') {
                    result=theGame.action(hint);
                }
                else if(inputChar=='q') {
                    System.out.println("Game ended, user quit.");
                    break;
                }
                else {
                    System.out.println("Invalid key! Use 8 for UP, 6 for RIGHT, 2 for DOWN and 4 for LEFT. Type a to play automatically and q to exit. Press enter to submit your choice.");
                    continue;
                }
                
                if(result==ActionStatus.CONTINUE || result==ActionStatus.INVALID_MOVE ) {
                    hint = AiSolver.findBestMove(theGame, hintDepth);
                }
                else {
                    hint = null;
                }
                printBoard(theGame.getBoardArray(), theGame.getScore(), hint);
                
                if(result!=ActionStatus.CONTINUE) {
                    System.out.println(result.getDescription());
                }
            }
        } 
        catch (IOException e) {
            System.err.println(e);
        }
    }
    
    /**
     * Prints the Board
     * 
     * @param boardArray
     * @param score 
     * @param hint 
     */
    public static void printBoard(int[][] boardArray, int score, Direction hint) {
        System.out.println("-------------------------");
        System.out.println("Score:\t" + String.valueOf(score));
        System.out.println();
        System.out.println("Hint:\t" + hint);
        System.out.println();
        
        for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray[i].length;++j) {
                System.out.print(boardArray[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("-------------------------");
    }
}
