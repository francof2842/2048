package Juego.game;

import Juego.dataobjects.ActionStatus;
import Juego.dataobjects.Direction;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the Game 2048.
 */
public class Board implements Cloneable {
    /**
     * The size of the board
     */
    public static final int BOARD_SIZE = 4;
    
    /**
     * The maximum combination in which the game terminates
     */
    public static final int TARGET_POINTS = 2048;
    
    /**
     * The theoretical minimum win score until the target point is reached
     */
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
    
    /**
     * The score so far
     */
    private int score=0;
    private int moves=0;
    
    private boolean won=false;
    
    private boolean over=false;
    
    private String Session=null;
    
    /**
     * The board values
     */
    private int[][] boardArray;
    
    /**
     * Random Generator which is used in the creation of random cells
     */
    //private final Random randomGenerator;
    
    /**
     * It caches the number of empty cells
     */
    private Integer cache_emptyCells=null;
    private String s;
    
    /**
     * Constructor without arguments. It initializes randomly the Board
     */
    public Board() {
        boardArray = new int[BOARD_SIZE][BOARD_SIZE];
        //randomGenerator = new Random(System.currentTimeMillis());
        
        addRandomCell();
        addRandomCell();
        
    }
    
    public Board(String s) {
        boardArray = new int[BOARD_SIZE][BOARD_SIZE];
        setBoard(s);
        //randomGenerator = new Random(System.currentTimeMillis());
    }
    
    public void setBoard(String s){
        String myNums[] = s.split("[^0-9]+");
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
                boardArray[i][j] = Integer.parseInt(myNums[i*4 + j + 1]);
        
    }
    
    /**
     * Deep clone
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Board copy = (Board)super.clone();
        copy.boardArray = clone2dArray(boardArray);
        return copy;
    }
    
    /**
     * Getter for score attribute
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Getter for BoardArray
     */
    public int[][] getBoardArray() {
        return clone2dArray(boardArray);
    }
    
    /**
     * Getter for RandomGenerator field
     */
    //public Random getRandomGenerator() {
    //    return randomGenerator;
    //}
    
    /**
     * Performs one move (up, down, left or right).
     */
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
    
    /**
     * Returns the Ids of the empty cells. The cells are numbered by row.
     */
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
    
    /**
     * Counts the number of empty cells
     */
    public int getNumberOfEmptyCells() {
        if(cache_emptyCells==null) {
            cache_emptyCells = getEmptyCellIds().size();
        }
        return cache_emptyCells;
    }
    
    /**
     * Checks if any of the cells in the board has value equal or larger than the
     * target.
     */
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
    
    /**
     * Checks whether the game is terminated
     */
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
    
    /**
     * Performs an Up, Right, Down or Left move
     */
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
    
    /**
     * Sets the value to an empty cell. 
     */
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
    
    
    /**
     * Rotates the board on the left
     */
    private void rotateLeft() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[BOARD_SIZE-j-1][i] = boardArray[i][j];
            }
        }
        
        boardArray=rotatedBoard;
    }
    
    /**
     * Rotates the board on the right
     */
    private void rotateRight() {
        int[][] rotatedBoard = new int[BOARD_SIZE][BOARD_SIZE];
        
        for(int i=0;i<BOARD_SIZE;++i) {
            for(int j=0;j<BOARD_SIZE;++j) {
                rotatedBoard[i][j]=boardArray[BOARD_SIZE-j-1][i];
            }
        }
        
        boardArray=rotatedBoard;
    }
    
    /**
     * Creates a new Random Cell
     */
    private boolean addRandomCell() {
//        List<Integer> emptyCells = getEmptyCellIds();
//        
//        int listSize=emptyCells.size();
//        
//        if(listSize==0) {
//            return false;
//        }
//        
//        //int randomCellId=emptyCells.get(randomGenerator.nextInt(listSize));
//        //int randomValue=(randomGenerator.nextDouble()< 0.9)?2:4;
//        
//        int i = randomCellId/BOARD_SIZE;
//        int j = randomCellId%BOARD_SIZE;
//        
//        setEmptyCell(i, j, randomValue);
//        
      return true;
    }
    
    /**
     * Clones a 2D array
     */
    private int[][] clone2dArray(int[][] original) { 
        int[][] copy = new int[original.length][];
        for(int i=0;i<original.length;++i) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
    

    
    /**
     * Checks whether the two input boards are same.
     */
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
