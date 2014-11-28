package Juego.game;

import Juego.ai.AiSolver;
import Juego.dataobjects.ActionStatus;
import Juego.dataobjects.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Board implements Cloneable {

    public static final int BOARD_SIZE = 4;

    public static final int TARGET_POINTS = 2048;
    

    public static final int MINIMUM_WIN_SCORE = 18432;
    
    public static final int[][][] WEIGHT_MATRICES =  {
		{
			{ 3, 2, 1,  0},
			{ 2, 1, 0,  -1},
			{ 1, 0, -1,  -2},
			{ 0, -1, -2,  -3}
		},
		{
			{ 0, 1, 2, 3},
			{ -1, 0, 1, 2},
			{ -2, -1, 0, 1},
			{ -3, -2, -1, 0}
		}
};
    

    private int score=0;
    private int moves=0;
    
    private boolean won=false;
    
    private boolean over=false;
    
    private String Session=null;
    
    private int[][] boardArray;

    private final Random randomGenerator;
    

    private Integer cache_emptyCells=null;
    private String s;
    

    public Board() {
        boardArray = new int[BOARD_SIZE][BOARD_SIZE];
        randomGenerator = new Random(System.currentTimeMillis());
        
        //addRandomCell();
        //addRandomCell();
        
    }
    
    public Board(int Hint) {
        boardArray = new int[BOARD_SIZE][BOARD_SIZE];
        randomGenerator = new Random(System.currentTimeMillis());
        
        addRandomCell();
        addRandomCell();
        
    }
    
    public Board(String s) {
        boardArray = new int[BOARD_SIZE][BOARD_SIZE];
        setBoard(s);
        randomGenerator = new Random(System.currentTimeMillis());
    }
    
    public void setBoard(String s){
        String myNums[] = s.split("[^0-9]+");
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
                boardArray[i][j] = Integer.parseInt(myNums[i*4 + j + 1]);
        
    }
    

    @Override
    public Object clone() throws CloneNotSupportedException {
        Board copy = (Board)super.clone();
        copy.boardArray = clone2dArray(boardArray);
        return copy;
    }
    

    public int getScore() {
        return score;
    }
    

    public int[][] getBoardArray() {
        return clone2dArray(boardArray);
    }
    
    

    
    public double monotonicity2(){
        double[] totals = {0,0,0,0};
        
        
        // up down direction
        for (int x = 0; x < 4; x++) {
            int current = 0;
            int next = current + 1;
                while (next<4){
                    while ((next < 4) && (this.getBoardArray(x, next) ==0)){
                        next++;
                    }
                    if (next>=4){ next--; }
                    double currentValue = 0;
                    if (this.getBoardArray(x, current) !=0){
                        currentValue = Math.log(this.getBoardArray(x, current)) / Math.log(2);
                    }
                    double nextValue = 0;
                    if (this.getBoardArray(x, next) !=0){
                        nextValue = Math.log(this.getBoardArray(x, current)) / Math.log(2);
                    }
                    if (currentValue > nextValue){
                        totals[0] = totals[0] + (nextValue - currentValue);
                    }else if (nextValue > currentValue){
                        totals[1] = totals[1] + (currentValue - nextValue);
                    }
                    current = next;
                    next++;
                }
        }
        
        
        // left right direction
        for (int y = 0; y < 4; y++) {
            int current = 0;
            int next = current + 1;
                while (next<4){
                    while ((next < 4) && (this.getBoardArray(next, y) ==0)){
                        next++;
                    }
                    if (next>=4){ next--; }
                    double currentValue = 0;
                    if (this.getBoardArray(current, y) !=0){
                        currentValue = Math.log(this.getBoardArray(current, y)) / Math.log(2);
                    }
                    double nextValue = 0;
                    if (this.getBoardArray(y, next) !=0){
                        nextValue = Math.log(this.getBoardArray(current, y)) / Math.log(2);
                    }
                    if (currentValue > nextValue){
                        totals[2] = totals[2] + (nextValue - currentValue);
                    }else if (nextValue > currentValue){
                        totals[3] = totals[3] + (currentValue - nextValue);
                    }
                    current = next;
                    next++;
                }
        }
        
        return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
    }
    
    
    public double maxValue(){
        int max = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int value = this.getBoardArray(x, y);
                if (value > max){
                    max = value;
                }
                
            }
            
        }
        return Math.log(max) / Math.log(2);
    }
    
    public int smootheeee(){
        
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 10; y++) {
                int actual = this.getBoardArray(x, y);
                
            }
        }
        
        
        return 0;
    }
    
    
    
    public double smoothness(){
        double smooth = 0;
        int[] target;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (this.getBoardArray(x, y)!=0){
                    double value = Math.log(this.getBoardArray(x, y)) / Math.log(2); // Potencia de 2
                    for (int hint = 1; hint <= 2; hint++) {
                        Direction dir = AiSolver.IntToDir(hint);
                        target = findFarthestPosition(x, y, dir);
                            if ((target[0] >= 0) && (target[1] >= 0) && (target[0] < 4) && (target[1] < 4)){
                                if (this.getBoardArray(target[0], target[1]) != 0){
                                    int targetCell = this.getBoardArray(target[0], target[1]);
                                    double targetValue = Math.log(targetCell) / Math.log(2);
                                    smooth = smooth - Math.abs(value - targetValue);
                                }
                                    
                            }
                        
                    }
                }
                
            }
        }
        return smooth;
    }
    
    public int[] findFarthestPosition(int x, int y, Direction dir){
        int[] previous = {x,y};
        int[] cell = {previous[0],previous[1]};
        int h = 0;
        do{
            previous[0] = cell[0];
            previous[1] = cell[1];
            switch ( dir ) {
                case DOWN: //LEFT
                        
                        cell[0] = cell[0] + 1;
                        if (cell[0]<4){
                            if (this.getBoardArray(cell[0], cell[1]) != 0){
                                h = 1;
                                
                            }
                        }else{
                            h = 1;
                        }
                    break;
                case UP:
                        cell[0] = cell[0] - 1;
                        if (cell[0]>=0){
                            if (this.getBoardArray(cell[0], cell[1]) != 0){
                                h = 1;
                                
                            }
                        }else{
                            h = 1;
                        }
                    break;
                case RIGHT:
                        cell[1] = cell[1] + 1;
                        if (cell[1]<4){
                            if (this.getBoardArray(cell[0], cell[1]) != 0){
                                h = 1;
                            }
                        }else{
                            h = 1;
                        }
                    break;
                case LEFT:    
                        cell[1] = cell[1] - 1;
                        if (cell[0]>=0){
                            if (this.getBoardArray(cell[0], cell[1]) != 0){
                                h = 1;
                                
                            }
                        }else{
                            h = 1;
                        }
                    break;
                default:
                    break;
            }
        }while (h==0);
        return new int[] {cell[0], cell[1]};
        // Por ahora solo necesito regresar el Next
        //return new int[] {previous[0], previous[1] , cell[0], cell[1]};
    }
    

    public int move(Direction direction) {    
        int points = 0;
        
        //rotate the board to make simplify the merging algorithm
        if(direction==Direction.UP) {
            rotateLeft();
        }
        else if(direction==Direction.RIGHT) {
            rotateLeft();
            rotateLeft();
        }
        else if(direction==Direction.DOWN) {
            rotateRight();
        }
        
        for(int i=0;i<BOARD_SIZE;++i) {
            int lastMergePosition=0;
            for(int j=1;j<BOARD_SIZE;++j) {
                if(boardArray[i][j]==0) {
                    continue; //skip moving zeros
                }
                
                int previousPosition = j-1;
                while(previousPosition>lastMergePosition && boardArray[i][previousPosition]==0) { //skip all the zeros
                    --previousPosition;
                }
                
                if(previousPosition==j) {
                    //we can't move this at all
                }
                else if(boardArray[i][previousPosition]==0) {
                    //move to empty value
                    boardArray[i][previousPosition]=boardArray[i][j];
                    boardArray[i][j]=0;
                }
                else if(boardArray[i][previousPosition]==boardArray[i][j]){
                    //merge with matching value
                    boardArray[i][previousPosition]*=2;
                    boardArray[i][j]=0;
                    points+=boardArray[i][previousPosition];
                    lastMergePosition=previousPosition+1;
                    
                }
                else if(boardArray[i][previousPosition]!=boardArray[i][j] && previousPosition+1!=j){
                    boardArray[i][previousPosition+1]=boardArray[i][j];
                    boardArray[i][j]=0;
                }
            }
        }
        
        
        score+=points;
        
        //reverse back the board to the original orientation
        if(direction==Direction.UP) {
            rotateRight();
        }
        else if(direction==Direction.RIGHT) {
            rotateRight();
            rotateRight();
        }
        else if(direction==Direction.DOWN) {
            rotateLeft();
        }
        
        return points;
    }
    

    public List<Integer> getEmptyCellIds() {
        List<Integer> cellList = new ArrayList<>();
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                if(boardArray[i][j]==0) {
                    cellList.add(BOARD_SIZE*i+j);
                }
            }
        }
        
        return cellList;
    }
    

    public int getNumberOfEmptyCells() {
        if(cache_emptyCells==null) {
            cache_emptyCells = getEmptyCellIds().size();
        }
        return cache_emptyCells;
    }
    

    public boolean hasWon() {
        if(score<MINIMUM_WIN_SCORE) { //speed optimization
            return false;
        }
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                if(boardArray[i][j]>=TARGET_POINTS) {
                    return true;
                }
            }
        }
        
        return false;
    }
    

    public boolean isGameTerminated() throws CloneNotSupportedException {
        boolean terminated=false;
        
        if(hasWon()==true) {
            terminated=true;
        }
        else {
            if(getNumberOfEmptyCells()==0) { //if no more available cells
                Board copyBoard = (Board) this.clone();
                                
                if(copyBoard.move(Direction.UP)==0 
                   && copyBoard.move(Direction.RIGHT)==0 
                   && copyBoard.move(Direction.DOWN)==0 
                   && copyBoard.move(Direction.LEFT)==0) {
                    terminated=true;
                }
                
                //copyBoard=null;
            }
        }
        
        return terminated;
    }
    
    public boolean isGameLost() throws CloneNotSupportedException {
        boolean lost=false;
        
        if(hasWon()==true) {
            lost=false;
        }
        else {
            if(getNumberOfEmptyCells()==0) { //if no more available cells
                Board copyBoard = (Board) this.clone();
                                
                if(copyBoard.move(Direction.UP)==0 
                   && copyBoard.move(Direction.RIGHT)==0 
                   && copyBoard.move(Direction.DOWN)==0 
                   && copyBoard.move(Direction.LEFT)==0) {
                    lost=true;
                }
                
                //copyBoard=null;
            }
        }
        
        return lost;
    }
    

    public ActionStatus action(Direction direction) throws CloneNotSupportedException {
        ActionStatus result = ActionStatus.CONTINUE;
        
        int[][] currBoardArray = getBoardArray();
        int newPoints = move(direction);
        int[][] newBoardArray = getBoardArray();
        
        //add random cell
        boolean newCellAdded = false;
        
        if(!isEqual(currBoardArray, newBoardArray)) {
            newCellAdded = addRandomCell();
        }
        
        if(newPoints==0 && newCellAdded==false) {
            if(isGameTerminated()) {
                result = ActionStatus.NO_MORE_MOVES;
            }
            else {
                result = ActionStatus.INVALID_MOVE;
            }
        }
        else {
            if(newPoints>=TARGET_POINTS) {
                result = ActionStatus.WIN;
            }
            else {
                if(isGameTerminated()) {
                    result = ActionStatus.NO_MORE_MOVES;
                }
            }
        }
        
        return result;
    }
    

    public void setEmptyCell(int i, int j, int value) {
        if(boardArray[i][j]==0) {
            boardArray[i][j]=value;
            cache_emptyCells=null;
        }
    }
    
    public int getBoardArray(int i, int j) {
        return boardArray[i][j];
    }
    
    public void setBoardArray(int i, int j, int value) {
        boardArray[i][j]=value;
    }
    
    public void setMoves(String s) {
        moves=Integer.parseInt(s);
    }
    
    public int getMoves(){
        return moves;
    }
    
    public void setScore(String s) {
        score=Integer.parseInt(s);
    }
    
    public String getSession() {
        return Session;
    }
    
    public void setSession(String s) {
        Session=s;
    }
    
    public void setWon(String s) {
        won=Boolean.valueOf(s);
    }
    
    public void setOver(String s) {
        over=Boolean.valueOf(s);
    }
    
    public boolean getWon() {
        return won;
    }
    
    public boolean getOver() {
        return over;
    }
    
    

    private void rotateLeft() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[BOARD_SIZE-j-1][i] = boardArray[i][j];
            }
        }
        
        boardArray=rotatedBoard;
    }
    

    private void rotateRight() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[i][j]=boardArray[BOARD_SIZE-j-1][i];
            }
        }
        
        boardArray=rotatedBoard;
    }
    

    private boolean addRandomCell() {
        List<Integer> emptyCells = getEmptyCellIds();
        
        int listSize=emptyCells.size();
        
        if(listSize==0) {
            return false;
        }
        
        int randomCellId=emptyCells.get(randomGenerator.nextInt(listSize));
        int randomValue=(randomGenerator.nextDouble()< 0.9)?2:4;
        
        int i = randomCellId/BOARD_SIZE;
        int j = randomCellId%BOARD_SIZE;
        
        setEmptyCell(i, j, randomValue);
        
      return true;
    }
    

    private int[][] clone2dArray(int[][] original) { 
        int[][] copy = new int[original.length][];
        for(int i=0;i<original.length;++i) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    


    public boolean isEqual(int[][] currBoardArray, int[][] newBoardArray) {

    	boolean equal = true;
        
        for(int i=0;i<currBoardArray.length;i++) {
            for(int j=0;j<currBoardArray.length;j++) {
                if(currBoardArray[i][j]!= newBoardArray[i][j]) {
                    equal = false; //The two boards are not same.
                    return equal;
                }
            }
        }
        
        return equal;
    }
}
