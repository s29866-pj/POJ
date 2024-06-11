import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Klasa Settings przechowuje ustawienia gry
class Settings {
    private static int size; // Rozmiar planszy
    private static int fieldValue1; // Pierwsza wartość startowa
    private static int fieldValue2; // Druga wartość startowa
    private static String playerName; // Nazwa gracza

    public Settings(int size, int fieldValue1, int fieldValue2, String playerName) {
        Settings.size = size;
        Settings.fieldValue1 = fieldValue1;
        Settings.fieldValue2 = fieldValue2;
        Settings.playerName = playerName;
    }

    // Metody statyczne do pobierania ustawień gry
    public static int getSize() {
        return size;
    }

    public static int getFieldValue1() {
        return fieldValue1;
    }

    public static int getFieldValue2() {
        return fieldValue2;
    }

    public static String getPlayerName() {
        return playerName;
    }
}

// Klasa LeaderBoard przechowuje i wyświetla wyniki graczy
class LeaderBoard {
    private final List<LeaderBoardEntry> entries;

    public LeaderBoard() {
        entries = new ArrayList<>();
    }

    // Dodaje wpis do tablicy wyników
    public void addEntry(String name, int score) {
        entries.add(new LeaderBoardEntry(name, score));
        // Sortowanie wpisów malejąco według wyniku
        entries.sort((e1, e2) -> Integer.compare(e2.getScore(), e1.getScore()));
    }

    // Wyświetla tablicę wyników
    public void display() {
        System.out.println("=== Leaderboard ===");
        for (LeaderBoardEntry entry : entries) {
            System.out.printf("%s: %d%n", entry.getName(), entry.getScore());
        }
    }
}

// Klasa pomocnicza do przechowywania wpisów tablicy wyników
class LeaderBoardEntry {
    private final String name;
    private final int score;

    public LeaderBoardEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}

// Klasa główna gry 2048
class Game2048 {
    public static final char MOVE_LEFT = 'A';
    public static final char MOVE_RIGHT = 'D';
    public static final char MOVE_UP = 'W';
    public static final char MOVE_DOWN = 'S';

    private final int[][] board;
    private final Random random;
    private final Scanner scanner;
    private int score;
    private final LeaderBoard leaderBoard;
    private String playerName;

    // Konstruktor inicjalizujący grę
    public Game2048(Settings gameSettings, LeaderBoard leaderBoard) {
        int size = gameSettings.getSize();
        board = new int[size][size];
        random = new Random();
        scanner = new Scanner(System.in);
        score = 0;
        this.leaderBoard = leaderBoard;
        this.playerName = gameSettings.getPlayerName();
    }

    // Wyświetla aktualny stan planszy
    public void showBoard() {
        int size = Settings.getSize();
        for (int i = 0; i < size; i++) {
            System.out.print("-------");
        }
        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print("|");
            for (int j = 0; j < size; j++) {
                System.out.print("      |");
            }
            System.out.println();

            System.out.print("|");
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    System.out.printf("  %-3s |", "");
                } else {
                    System.out.printf("  %-3d |", board[i][j]);
                }
            }
            System.out.println();

            System.out.print("|");
            for (int j = 0; j < size; j++) {
                System.out.print("      |");
            }
            System.out.println();

            for (int j = 0; j < size; j++) {
                System.out.print("-------");
            }
            System.out.println();
        }
        System.out.println("Score: " + score);
        System.out.println();
    }

    // Dodaje losową cyfrę do planszy
    public void addRandomDigit(int digit) {
        int size = Settings.getSize();
        int i = random.nextInt(size);
        int j = random.nextInt(size);

        while (board[i][j] != 0) {
            i = random.nextInt(size);
            j = random.nextInt(size);
        }

        board[i][j] = digit;
    }

    // Szuka określonej wartości na planszy
    public boolean searchOnBoard(int x) {
        int size = Settings.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == x) {
                    return true;
                }
            }
        }
        return false;
    }

    // Sprawdza, czy gra została wygrana
    public boolean gameWon() {
        return searchOnBoard(2048);
    }

    // Sprawdza, czy użytkownik może wykonać ruch
    public boolean userCanMakeAMove() {
        int size = Settings.getSize();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (board[i][j] == board[i][j + 1] || board[i][j] == board[i + 1][j]) {
                    return true;
                }
            }
        }
        for (int j = 0; j < size - 1; j++) {
            if (board[size - 1][j] == board[size - 1][j + 1]) {
                return true;
            }
        }
        for (int i = 0; i < size - 1; i++) {
            if (board[i][size - 1] == board[i + 1][size - 1]) {
                return true;
            }
        }
        return false;
    }

    // Sprawdza, czy gra się skończyła
    public boolean isGameOver() {
        if (gameWon()) {
            return true;
        }
        if (searchOnBoard(0)) {
            return false;
        }
        return !userCanMakeAMove();
    }

    // Pobiera ruch użytkownika
    public char getUserMove() {
        System.out.println("Wybierz ruch: ");
        System.out.println("W/w: Do góry");
        System.out.println("S/s: W dół");
        System.out.println("A/a: Lewo");
        System.out.println("D/d: Prawo");
        System.out.print("Podaj ruch: ");

        String moveInput = scanner.nextLine();
        if (moveInput.equalsIgnoreCase("a") ||
                moveInput.equalsIgnoreCase("w") ||
                moveInput.equalsIgnoreCase("s") ||
                moveInput.equalsIgnoreCase("d")) {
            return moveInput.toUpperCase().charAt(0);
        }

        System.out.println("Nieprawidłowy ruch!");
        System.out.println();
        showBoard();
        return getUserMove();
    }

    // Przetwarza ruch w lewo
    public int[] processLeftMove(int[] row) {
        int size = Settings.getSize();
        int[] newRow = new int[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (row[i] != 0) {
                newRow[j++] = row[i];
            }
        }

        for (int i = 0; i < size - 1; i++) {
            if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                newRow[i] *= 2;
                score += newRow[i];
                for (j = i + 1; j < size - 1; j++) {
                    newRow[j] = newRow[j + 1];
                }
                newRow[size - 1] = 0;
            }
        }
        return newRow;
    }

    // Odwraca tablicę
    public int[] reverseArray(int[] arr) {
        int[] reverseArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reverseArr[i] = arr[arr.length - i - 1];
        }
        return reverseArr;
    }

    // Przetwarza ruch w prawo
    public int[] processRightMove(int[] row) {
        int size = Settings.getSize();
        int[] newRow = reverseArray(row);
        newRow = processLeftMove(newRow);
        return reverseArray(newRow);
    }

    // Przetwarza ruch gracza
    public void processMove(char move) {
        int size = Settings.getSize();
        switch (move) {
            case MOVE_LEFT:
                for (int i = 0; i < size; i++) {
                    int[] newRow = processLeftMove(board[i]);
                    System.arraycopy(newRow, 0, board[i], 0, size);
                }
                break;
            case MOVE_RIGHT:
                for (int i = 0; i < size; i++) {
                    int[] newRow = processRightMove(board[i]);
                    System.arraycopy(newRow, 0, board[i], 0, size);
                }
                break;
            case MOVE_UP:
                for (int j = 0; j < size; j++) {
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }
                    int[] newRow = processLeftMove(row);
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i];
                    }
                }
                break;
            case MOVE_DOWN:
                for (int j = 0; j < size; j++) {
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }
                    int[] newRow = processRightMove(row);
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i];
                    }
                }
                break;
        }
    }

    // Główna metoda gry
    public void play() {
        int value1 = Settings.getFieldValue1();
        int value2 = Settings.getFieldValue2();
        addRandomDigit(value1);
        addRandomDigit(value2);

        while (!isGameOver()) {
            showBoard();
            char move = getUserMove();
            processMove(move);
            if (random.nextInt(100) % 2 == 0) {
                addRandomDigit(value1);
            } else {
                addRandomDigit(value2);
            }
        }

        showBoard();
        if (gameWon()) {
            System.out.println("WYGRAŁEŚ!");
        } else {
            System.out.println("PRZEGRAŁEŚ!");
        }

        // Dodanie wyniku do tablicy wyników i jej wyświetlenie
        leaderBoard.addEntry(playerName, score);
        leaderBoard.display();

        System.out.println("Czy chcesz ponownie rozpocząć grę z nowym użytkownikiem: Yes/No");
        if (scanner.nextLine().equalsIgnoreCase("Yes")) {
            resetGame();
            Settings newSettings = getNewUserSettings();
            playerName = newSettings.getPlayerName();
            play();
        }
    }

    // Resetuje grę do stanu początkowego
    private void resetGame() {
        int size = Settings.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
        score = 0;
    }

    // Pobiera nowe ustawienia od użytkownika
    private Settings getNewUserSettings() {
        System.out.println("Podaj nazwę użytkownika: ");
        String newUserName = scanner.nextLine();
        System.out.println("Podaj wielkość planszy: ");
        int size = scanner.nextInt();
        while (size <= 1) {
            System.out.println("Rozmiar planszy musi być większy niż 1. Podaj ponownie: ");
            size = scanner.nextInt();
        }
        System.out.println("Podaj wartość nr1: ");
        int value1 = scanner.nextInt();
        System.out.println("Podaj wartość nr2: ");
        int value2 = scanner.nextInt();

        scanner.nextLine(); // Konsumuje pozostały znak nowej linii

        return new Settings(size, value1, value2, newUserName);
    }

    // Metoda główna programu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Podaj nazwę użytkownika: ");
        String username = sc.nextLine();
        System.out.println("Podaj wielkość planszy: ");
        int size = sc.nextInt();
        while (size <= 1) {
            System.out.println("Rozmiar planszy musi być większy niż 1. Podaj ponownie: ");
            size = sc.nextInt();
        }
        System.out.println("Podaj wartość nr1: ");
        int value1 = sc.nextInt();
        System.out.println("Podaj wartość nr2: ");
        int value2 = sc.nextInt();

        sc.nextLine(); // Konsumuje pozostały znak nowej linii

        Settings gameSettings = new Settings(size, value1, value2, username);
        LeaderBoard leaderBoard = new LeaderBoard();
        Game2048 gameOf2048 = new Game2048(gameSettings, leaderBoard);
        gameOf2048.play();
    }
}
