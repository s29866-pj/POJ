import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.Serializable;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;



class Player {
    void playMusic(String musicLoc){
        try {
            File musicPath = new File(musicLoc);
            if(musicPath.exists()){
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            }
            else{
                System.out.println("Nie znaleziono pliku muzycznego");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

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
     final List<LeaderBoardEntry> entries;

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
class Game2048 implements Serializable{
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

    // Zapisz grę do pliku
    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(board);
            out.writeInt(score);
            out.writeObject(playerName);
            System.out.println("Gra zapisana pomyślnie.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Załaduj grę
    public void loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            int[][] loadedBoard = (int[][]) in.readObject();
            int loadedScore = in.readInt();
            String loadedPlayerName = (String) in.readObject();

            System.arraycopy(loadedBoard, 0, board, 0, loadedBoard.length);
            score = loadedScore;
            playerName = loadedPlayerName;

            System.out.println("Gra załadowana pomyślnie.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Konstruktor inicjalizujący grę
    public Game2048(Settings gameSettings, LeaderBoard leaderBoard) {
        int size = Settings.getSize();
        board = new int[size][size];
        random = new Random();
        scanner = new Scanner(System.in);
        score = 0;
        this.leaderBoard = leaderBoard;
        this.playerName = Settings.getPlayerName();
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
        System.out.println("Naciśnij B aby zapisać grę do pliku.");
        System.out.println("Naciśnij X aby wyjść.");
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
        } else if (moveInput.equalsIgnoreCase("b")) {
            System.out.print("Podaj nazwe pliku: ");
            String filename = scanner.nextLine();
            saveGame(filename);
        } else if (moveInput.equalsIgnoreCase("x")) {
            System.exit(0);
        } else {
            System.out.println("Nieprawidłowy ruch!");
        }

        System.out.println();
        showBoard();
        return getUserMove();
    }

    // Przetwarza ruch w lewo
    public int[] processLeftMove(int[] row) {
        int size = Settings.getSize();
        int[] newRow = new int[size];
        int j = 0;

        //Przesuwa niezerowe wartości na lewą stonę
        for (int i = 0; i < size; i++) {
            if (row[i] != 0) {
                newRow[j++] = row[i];
            }
        }

        //Łączy identyczne wartości
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
        int[] newRow = reverseArray(row); // Odwraca wiersz, aby zamienić ruch w prawo na ruch w lewo
        newRow = processLeftMove(newRow); // Przetwarza odwrócony wiersz jak dla ruchu w lewo
        return reverseArray(newRow); // Ponownie odwraca wiersz, aby przywrócić oryginalny porządek
    }

    // Przetwarza ruch gracza
    public void processMove(char move) {
        int size = Settings.getSize();
        switch (move) {
            case MOVE_LEFT:
                for (int i = 0; i < size; i++) {
                    int[] newRow = processLeftMove(board[i]); // Przetwarza ruch w lewo dla każdego wiersza
                    System.arraycopy(newRow, 0, board[i], 0, size); // Kopiuje przetworzony wiersz z powrotem na planszę
                }
                break;
            case MOVE_RIGHT:
                for (int i = 0; i < size; i++) {
                    int[] newRow = processRightMove(board[i]); // Przetwarza ruch w prawo dla każdego wiersza
                    System.arraycopy(newRow, 0, board[i], 0, size); // Kopiuje przetworzony wiersz z powrotem na planszę
                }
                break;
            case MOVE_UP:
                for (int j = 0; j < size; j++) {
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }
                    int[] newRow = processLeftMove(row); // Przetwarza ruch w górę dla każdej kolumny
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i]; // Kopiuje przetworzoną kolumnę z powrotem na planszę
                    }
                }
                break;
            case MOVE_DOWN:
                for (int j = 0; j < size; j++) {
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }
                    int[] newRow = processRightMove(row); // Przetwarza ruch w dół dla każdej kolumny
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i]; // Kopiuje przetworzoną kolumnę z powrotem na planszę
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

        System.out.println("Czy chcesz ponownie rozpocząć grę z nowym użytkownikiem: (Y)es/(N)o");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            resetGame();
            getNewUserSettings();
            playerName = Settings.getPlayerName();
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
    private void getNewUserSettings() {
        System.out.println("Podaj nazwę użytkownika: ");
        String newUserName = scanner.nextLine();
        System.out.println("Podaj wielkość planszy: ");
        int size = scanner.nextInt();
        while (size <= 1) {
            System.out.println("Rozmiar planszy musi być większy niż 1. Podaj ponownie: ");
            size = scanner.nextInt();
        }
        System.out.println("Podaj wartość kafelka NR1: ");
        int value1 = scanner.nextInt();
        System.out.println("Podaj wartość kafelka NR2: ");
        int value2 = scanner.nextInt();

        scanner.nextLine();

        new Settings(size, value1, value2, newUserName);
    }

    // Metoda główna programu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LeaderBoard leaderBoard = new LeaderBoard();

        System.out.println("(N)owa gra czy (Z)aladowac grę z pliku?");
        String choice = sc.nextLine();

        if (choice.equalsIgnoreCase("Z")) {
            System.out.print("Podaj nazwe pliku: ");
            String filename = sc.nextLine();
            Game2048 gameOf2048 = new Game2048(new Settings(4, 2, 4, ""), leaderBoard); // temporary settings
            gameOf2048.loadGame(filename);
            gameOf2048.play();
        } else {
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

            Settings gameSettings = new Settings(size, value1, value2, username);
            Game2048 gameOf2048 = new Game2048(gameSettings, leaderBoard);
            String filePath =
                    "/Users/aleks277/Downloads/ocean-breeze-beat-by-jtwayne-213318.wav";
            Player play = new Player();
            play.playMusic(filePath);
            gameOf2048.play();
        }
    }
}

