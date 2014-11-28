
package Juego.ai;


import Juego.dataobjects.Direction;
import Juego.game.Board;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The AIsolver class that uses Artificial Intelligence to estimate the next move.
 */
public class AiSolver {
    
    /**
     * Player vs Computer enum class
     */
    public enum Player {
        /**
         * Computer
         */
        COMPUTER, 

        /**
         * User
         */
        USER
    }
    
public static final Map<String, Double> cache = new HashMap<>();
    
    /**
     * Method that finds the best next move.
     */
    public static Direction findBestMove(Board theBoard) throws CloneNotSupportedException {
        

        // Esto es usando algoritmo de MiniMax
        //Map<String, Object> result = minimax(theBoard, 7, Player.USER);
        
        // Esto es usando algoritmo de Minimax con AlphaBeta Pruning
        //Map<String, Object> result = alphabeta(theBoard, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, Player.USER);
        
        // Esto es usando algoritmo de Espectimax
        //System.out.println("Cache antes del Clean: " + cache.size());
        //cleanCache(theBoard);
        int x = distincTiles(theBoard);
        int y = Math.max(4, x-5);
        //System.out.println("Depth: " + x + "Depth Real: " +y+ " - Tamaño Cache: " + cache.size());
        Map<String, Object> result = espectimax(theBoard, y);
        
        return (Direction)result.get("Direction");
    }
    
    
    
    private static Map<String, Object> espectimax(Board theBoard, int depth) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        Direction bestDirection = null;
        double bestScore;
        
        bestDirection = best_direction (theBoard,depth);

        //bestScore = computer_move(theBoard,depth);
        
        
        result.put("Score", 0);
        result.put("Direction", bestDirection);
        return result;
    }
    
    private static Direction best_direction (Board theBoard, int depth) throws CloneNotSupportedException {
        double best_score = 0;
	int best_dir = -1;
        
        
        for (int dir = 0; dir < 4; dir++) {
            Board computerBoard = (Board) theBoard.clone();
            computerBoard.move(IntToDir(dir));
                if (computerBoard.isEqual(theBoard.getBoardArray(), computerBoard.getBoardArray())) {
                    continue;
                }
            double computer_score = computer_move(computerBoard, 2*depth - 1);    
                if (computer_score >= best_score){
                    	best_score = computer_score;
			best_dir = dir;
                }
        }
        
        Direction result = IntToDir(best_dir);
        
        return result;
    }
    
    
    private static double computer_move (Board theBoard, int depth) throws CloneNotSupportedException{
        double total_score = 0;
	double total_weight = 0;


        for (int x = 0; x < 4; x++) {
            
            for (int y = 0; y < 4; y++) {
                if (theBoard.getBoardArray(x,y) == 0){
                    for (int i = 0; i < 2; i++) {
                        Board playerBoard = (Board) theBoard.clone();
                        if (i==0){
                            playerBoard.setBoardArray(x,y,2);
                            double score = player_move(playerBoard, cache, depth - 1);
                            total_score = total_score + (0.9 * score); 
                            total_weight = total_weight + 0.9;   
                        }else{
                            playerBoard.setBoardArray(x,y,4);
                            double score = player_move(playerBoard, cache, depth - 1);
                            total_score = total_score + (0.1 * score); 
                            total_weight = total_weight + 0.1;   
                        }
  
                        
                    }
                }
            }
        }
        return total_weight == 0 ? 0 : total_score / total_weight;
    }
    
    
    private static int evaluate_heuristic(Board theBoard) throws CloneNotSupportedException{
        int best = 0;
        for (int i = 0; i < 2; i++) {
            int s = 0;
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    
                    s = s + (Board.WEIGHT_MATRICES[i][y][x] * theBoard.getBoardArray(x,y));
                    
                }
                
            }
            s = Math.abs(s);
                if (s>best){
                    best = s;
                }
            
        }
        
        
        return best;
    }
    
    
    public static String getBoardToString(Board theBoard){
        
        String s = "";
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                s = s + theBoard.getBoardArray(x, y) + ",";
            }
            
        }
        return s;
    }
    
    private static double player_move(Board theBoard, Map<String, Double> cache,int depth) throws CloneNotSupportedException{
        
        if (depth<=0){
            if (!theBoard.isGameLost()){
                return evaluateScore(theBoard);
            }else{
                return 0;
            }
            
        }
        
        double best_score = 0;
        
        for (int dir = 0; dir < 4; dir++) {
            Board computerBoard = (Board) theBoard.clone();
            computerBoard.move(IntToDir(dir));
                if (computerBoard.isEqual(theBoard.getBoardArray(), computerBoard.getBoardArray())) {
                    continue;
                }
                double computer_score = 0;

                Double value = cache.get(getBoardToString(computerBoard));
                Iterator  It = cache.entrySet().iterator();
                
                if (value != null){
                    computer_score = value.intValue();
                }else{
                    computer_score = computer_move(computerBoard, depth - 1);
                    cache.put(getBoardToString(computerBoard), computer_score);
                }
                
                if (computer_score > best_score){
                    best_score = computer_score;
                }
                
            
        }
        
        return best_score;
    }
    
    
    
    public static Direction IntToDir (int hint){
        if (hint==0){
            return Direction.UP;
        }
        else if (hint==1){
            return Direction.RIGHT;
        }
        else if (hint==2){
            return Direction.DOWN;
        }
        else{
            return Direction.LEFT;
        }
    }
    
    
    
    /**
     * Finds the best move by using the Minimax algorithm.
     */
    private static Map<String, Object> minimax(Board theBoard, int depth, Player player) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        
        Direction bestDirection = null;
        double bestScore;
        
        if(depth==0 || theBoard.isGameTerminated()) {
            bestScore=evaluateScore(theBoard);
        }
        else {
            if(player == Player.USER) {
                bestScore = Integer.MIN_VALUE;

                for(Direction direction : Direction.values()) {
                    Board newBoard = (Board) theBoard.clone();

                    int points=newBoard.move(direction);
                    
                    if(points==0 && newBoard.isEqual(theBoard.getBoardArray(), newBoard.getBoardArray())) {
                    	continue;
                    }

                    Map<String, Object> currentResult = minimax(newBoard, depth-1, Player.COMPUTER);
                    int currentScore=((Number)currentResult.get("Score")).intValue();
                    if(currentScore>bestScore) { //maximize score
                        bestScore=currentScore;
                        bestDirection=direction;
                    }
                }
            }
            else {
                bestScore = Integer.MAX_VALUE;

                List<Integer> moves = theBoard.getEmptyCellIds();
                if(moves.isEmpty()) {
                    bestScore=0;
                }
                int[] possibleValues = {2, 4};

                int i,j;
                int[][] boardArray;
                for(Integer cellId : moves) {
                    i = cellId/Board.BOARD_SIZE;
                    j = cellId%Board.BOARD_SIZE;

                    for(int value : possibleValues) {
                        Board newBoard = (Board) theBoard.clone();
                        newBoard.setEmptyCell(i, j, value);

                        Map<String, Object> currentResult = minimax(newBoard, depth-1, Player.USER);
                        int currentScore=((Number)currentResult.get("Score")).intValue();
                        if(currentScore<bestScore) { //minimize best score
                            bestScore=currentScore;
                        }
                    }
                }
            }
        }
        
        result.put("Score", bestScore);
        result.put("Direction", bestDirection);
        
        return result;
    }
    
    /**
     * Finds the best move bay using the Alpha-Beta pruning algorithm.
     */
    private static Map<String, Object> alphabeta(Board theBoard, int depth, int alpha, int beta, Player player) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        
        Direction bestDirection = null;
        double bestScore;
        
        if(theBoard.isGameTerminated()) {
            if(theBoard.hasWon()) {
                bestScore=Integer.MAX_VALUE; //highest possible score
            }
            else {
                bestScore=Math.min(theBoard.getScore(), 1); //lowest possible score
            }
        }
        else if(depth==0) {
            bestScore=evaluateScore(theBoard);
        }
        else {
            if(player == Player.USER) {
                for(Direction direction : Direction.values()) {
                    Board newBoard = (Board) theBoard.clone();

                    int points=newBoard.move(direction);
                    
                    if(points==0 && newBoard.isEqual(theBoard.getBoardArray(), newBoard.getBoardArray())) {
                    	continue;
                    }
                    
                    Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.COMPUTER);
                    int currentScore=((Number)currentResult.get("Score")).intValue();
                                        
                    if(currentScore>alpha) { //maximize score
                        alpha=currentScore;
                        bestDirection=direction;
                    }
                    
                    if(beta<=alpha) {
                        break; //beta cutoff
                    }
                }
                
                bestScore = alpha;
            }
            else {
                List<Integer> moves = theBoard.getEmptyCellIds();
                int[] possibleValues = {2, 4};

                int i,j;
                abloop: for(Integer cellId : moves) {
                    i = cellId/Board.BOARD_SIZE;
                    j = cellId%Board.BOARD_SIZE;

                    for(int value : possibleValues) {
                        Board newBoard = (Board) theBoard.clone();
                        newBoard.setEmptyCell(i, j, value);

                        Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.USER);
                        int currentScore=((Number)currentResult.get("Score")).intValue();
                        if(currentScore<beta) { //minimize best score
                            beta=currentScore;
                        }
                        
                        if(beta<=alpha) {
                            break abloop; //alpha cutoff
                        }
                    }
                }
                
                bestScore = beta;
                
                if(moves.isEmpty()) {
                    bestScore=0;
                }
            }
        }
        
        result.put("Score", bestScore);
        result.put("Direction", bestDirection);
        
        return result;
    }
    

    /**
     * Calculates a heuristic variance-like score that measures how clustered the
     * board is.
     */
    private static int calculateClusteringScore(int[][] boardArray) {
        int clusteringScore=0;
        
        int[] neighbors = {-1,0,1};
        
        for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray.length;++j) {
                if(boardArray[i][j]==0) {
                    continue; //ignore empty cells
                }
                
                //clusteringScore-=boardArray[i][j];
                
                //for every pixel find the distance from each neightbors
                int numOfNeighbors=0;
                int sum=0;
                for(int k : neighbors) {
                    int x=i+k;
                    if(x<0 || x>=boardArray.length) {
                        continue;
                    }
                    for(int l : neighbors) {
                        int y = j+l;
                        if(y<0 || y>=boardArray.length) {
                            continue;
                        }
                        
                        if(boardArray[x][y]>0) {
                            ++numOfNeighbors;
                            sum+=Math.abs(boardArray[i][j]-boardArray[x][y]);
                        }
                        
                    }
                }
                
                clusteringScore+=sum/numOfNeighbors;
            }
        }
        
        return clusteringScore;
    }
    
    
    
        /**
     * Estimates a heuristic score by taking into account the real score, the
     * number of empty cells and the clustering score of the board.
     */
    private static int heuristicScore(Board theBoard) throws CloneNotSupportedException {
        int actualScore = theBoard.getScore();
        int numberOfEmptyCells = theBoard.getNumberOfEmptyCells();
        int clusteringScore = calculateClusteringScore(theBoard.getBoardArray());
        //int score = (int) (actualScore+Math.log(actualScore)*numberOfEmptyCells -clusteringScore + evaluate_heuristic(theBoard));
        //int score = evaluate_heuristic(theBoard);
        double score = evaluateScore(theBoard);
        int x = (int) score;
        return Math.max(x, Math.min(actualScore, 1));
    }
    
    private static double evaluateScore (Board theBoard) throws CloneNotSupportedException{
        //double clusteringScore = Math.log(calculateClusteringScore(theBoard.getBoardArray()));
        int numberOfEmptyCells = theBoard.getNumberOfEmptyCells();
        //int actualScore = theBoard.getScore();
        //double score =  Math.log(actualScore+Math.log(actualScore)*numberOfEmptyCells -clusteringScore );
        double clustering = 0.2 * calculateClusteringScore(theBoard.getBoardArray());
        double triangleWight = evaluate_heuristic(theBoard);
        //double smoothWeight = 1.0 * theBoard.smoothness();
        //double mono2Weight  = 1.0 * theBoard.monotonicity2();
        //double emptyWeight  = 2.7 * Math.log(theBoard.getNumberOfEmptyCells());
        //double maxWeight    = 1.0 * theBoard.maxValue();
        
        double x = triangleWight -  clustering + numberOfEmptyCells; 
        
        return x;
    }
    
    private static double possibleMerges(Board theBoard){
        
        
        
        return 0.0;
    }
    
    
    private static double heuristic(Board theBoard){
        
        
        double SCORE_LOST_PENALTY = 200000.0;
        double SCORE_MONOTONICITY_POWER = 4.0;
        double SCORE_MONOTONICITY_WEIGHT = 47.0;
        double SCORE_SUM = 3.5;
        double SCORE_SUM_WEIGHT = 11.0;
        double SCORE_MERGES_WEIGHT = 700.0;
        double SCORE_EMPTY_WEIGHT = 270.0;
        
        
        double sum = 0.0;
        int empty = 0;
        int merges = 0;
        
        int prev = 0;
        int counter = 0;
        
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
            int rank = theBoard.getBoardArray(x, y);
            sum = sum + Math.pow(rank, SCORE_SUM);
            
                if (rank == 0){
                    empty++;
                }else{
                    if (prev == rank){
                        counter++;
                    }else if (counter > 0){
                        merges = merges + 1 + counter;
                        counter = 0;
                    }
                prev = rank;
                }
            
            }
        }
        
        if (counter > 0){
            merges = merges + 1 + counter;
        }
        
        
        double monotonicity_left = 0.0;
        double monotonicity_right = 0.0;
        
        for (int x = 1; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int previous = theBoard.getBoardArray(x-1, y);
                int current = theBoard.getBoardArray(x, y);
                if (previous > current){
                    monotonicity_left = monotonicity_left + Math.pow(previous, SCORE_MONOTONICITY_POWER) - Math.pow(current, SCORE_MONOTONICITY_POWER);
                } else{
                    monotonicity_right = monotonicity_right + Math.pow(current, SCORE_MONOTONICITY_POWER) - Math.pow(previous, SCORE_MONOTONICITY_POWER);
                }
            }
        }   
        
        return SCORE_LOST_PENALTY + SCORE_EMPTY_WEIGHT * empty + SCORE_MERGES_WEIGHT * merges - SCORE_MONOTONICITY_WEIGHT * Math.min(monotonicity_left, monotonicity_right) - SCORE_SUM_WEIGHT * sum;
    }
    
    
    
    public static int distincTiles(Board theBoard){
        int i = 0;
        int[] lista = new int[16];
        int[] unique = new int[16];
        int counter = 0;
        
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                lista[counter] = theBoard.getBoardArray(x, y);
                counter++;
            }
        }
        unique = toUniqueArray(lista);
        
        return unique.length;
    }
    
    public static boolean isUnique(int[] array, int num) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == num) {
                return false;
            }
        }
        return true;
    }
 
    /**
     * Convert the given array to an array with unique values â€“
     * without duplicates and Return it
     */
    public static int[] toUniqueArray(int[] array) {
        int[] temp = new int[array.length];
 
        for (int i = 0; i < temp.length; i++) {
            temp[i] = -1; // in case u have value of 0 in he array
        }
        int counter = 0;
 
        for (int i = 0; i < array.length; i++) {
            if (isUnique(temp, array[i]))
                temp[counter++] = array[i];
        }
        int[] uniqueArray = new int[counter];
 
        System.arraycopy(temp, 0, uniqueArray, 0, uniqueArray.length);
 
        return uniqueArray;
    }
    
    
    public static void cleanCache(Board theBoard){
    String s;   
    int high = highValue(stringToInt(getBoardToString(theBoard)));
      for(Iterator<Map.Entry<String, Double>> it = cache.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<String, Double> entry = it.next();
        s = entry.getKey();
        int a = highValue(stringToInt(s));
        if(a < high) {
          it.remove();
        }
      }
    }
    
    public static int[] stringToInt(String s){

            String[] items = s.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

            int[] results = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    results[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {};
            }
            
            return results;
    }
    
    public static int highValue(int[] a){
        int max = 0;
        
        for (int counter = 1; counter < a.length; counter++)
            {
                 if (a[counter] > max)
                 {
                  max = a[counter];
                 }
            }
        
        return max;
        }
    
}


