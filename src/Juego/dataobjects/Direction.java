
package Juego.dataobjects;


public enum Direction {

    UP(0,"Up"), 

    RIGHT(1,"Right"), 

    DOWN(2,"Down"), 

    LEFT(3,"Left");

    private final int code;

    private final String description;

    private Direction(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() { 
        return description;
    }
}
