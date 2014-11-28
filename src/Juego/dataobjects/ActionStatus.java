
package Juego.dataobjects;


public enum ActionStatus {

    CONTINUE(0, "Successful move, the game continues."),

    WIN(1, "You won, the game ended!"),

    NO_MORE_MOVES(2,"No more moves, the game ended!"),

    INVALID_MOVE(3,"Invalid move!");

    private final int code;

    private final String description;

    private ActionStatus(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
