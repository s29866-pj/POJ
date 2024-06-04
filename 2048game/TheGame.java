import java.util.Random;
import java.util.Scanner;

class Game2048 {

    public static final int SIZE = 4;
    public static final char MOVE_LEFT = 'A';
    public static final char MOVE_RIGHT = 'D';
    public static final char MOVE_UP = 'W';
    public static final char MOVE_DOWN = 'S';

    private int[][] board;
    private Random random;
    private Scanner scanner;
    private int score;

    public Game2048() {
        // inicjalizacja planszy 4x4
        board = new int[4][4];
        
        random = new Random();

        // skaner do czytania wejscia od usera
        scanner = new Scanner(System.in);

        // wynik póki co to 0
        score = 0;
    }
