import java.util.Random;
import java.util.Scanner;

class Settings {
    private static int size;
    private static int fieldValue1;
    private static int fieldValue2;

    public Settings(int size, int fieldValue1, int fieldValue2) {
        Settings.size = size;
        Settings.fieldValue1 = fieldValue1;
        Settings.fieldValue2 = fieldValue2;
    }

    public static int getSize() {
        return size;
    }

    public static int getFieldValue1() {
        return fieldValue1;
    }

    public static int getFieldValue2() {
        return fieldValue2;
    }
}

class Game2048{

    public static final char MOVE_LEFT = 'A';
    public static final char MOVE_RIGHT = 'D';
    public static final char MOVE_UP = 'W';
    public static final char MOVE_DOWN = 'S';

    private final int[][] board;
    private final Random random;
    private final Scanner scanner;
    private int score;

    public Game2048(Settings gameSettings) {
        int size = gameSettings.getSize();
        board = new int[size][size];
        random = new Random();
        scanner = new Scanner(System.in);
        score = 0;
    }

    public void showBoard() {

        int newSize = Settings.getSize();
        // górny separator
        for (int i = 0; i < newSize; i++) {
            System.out.print("-------");
        }
        System.out.println();

        // rząd
        for (int i = 0; i < newSize; i++) {
            // puste miejsce przed rzędem
            System.out.print("|");
            for (int j = 0; j < newSize; j++) {
                System.out.print("      |");
            }
            System.out.println();

            // linia przerywająca
            System.out.print("|");
            for (int j = 0; j < newSize; j++) {
                if (board[i][j] == 0) {
                    System.out.printf("  %-3s |", "");
                } else {
                    System.out.printf("  %-3s |", "" + board[i][j]);
                }
            }
            System.out.println();

            // puste miejsce po rzędzie
            System.out.print("|");
            for (int j = 0; j < newSize; j++) {
                System.out.print("      |");
            }
            System.out.println();


            // dolny separator
            for (int j = 0; j < newSize; j++) {
                System.out.print("-------");
            }
            System.out.println();
        }
        //Drukowanie wyniku
        System.out.println("Score: " + score);
        System.out.println();
    }

    public void addRandomDigit(int digit) {
        // losowe współrzędne
        int i = random.nextInt(4);
        int j = random.nextInt(4);

        // generujemy tak długo jak miejsce na planszy jest zajęte
        while (board[i][j] != 0) {
            i = random.nextInt(4);
            j = random.nextInt(4);
        }

        // zapisujemy lokalizacje
        board[i][j] = digit;
    }

    public boolean searchOnBoard(int x) {
        // szukamy wartości x na planszy
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == x) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean gameWon() {
        // sprawdzamy, czy jest 2048 na planszy
        return searchOnBoard(2048);
    }

    public boolean userCanMakeAMove() {
        // sprawdzamy plansze
        int size = Settings.getSize();
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                // jeśli dwie sąsiednie lokalizacje mają tę samą wartość, zwróć wartość true
                if (board[i][j] == board[i][j + 1] ||
                        board[i][j] == board[i + 1][j]) {
                    return true;
                }
            }
        }
        // to samo ale w ostatnim rzędzie
        for (int j = 0; j < size - 1; j++) {
            if (board[size - 1][j] == board[size - 1][j + 1]) {
                return true;
            }
        }

        // w ostatniej kolumnie
        for (int i = 0; i < size - 1; i++) {
            if (board[i][size - 1] == board[i + 1][size - 1]) {
                return true;
            }
        }

        return false;
    }

    public boolean isGameOver() {
        // jest płytka 2048
        if (gameWon()) {
            return true;
        }

        // jest puste miejsce na planszy z wartością 0
        if (searchOnBoard(0)) {
            return false;
        }

        // jezeli user moze się ruszyć to nie koniec gry
        return !userCanMakeAMove();
    }

    public char getUserMove() {
        // pokaz wszytskie mozliwe ruchy
        System.out.println("Wybierz ruch: ");
        System.out.println("W/w: Do góry");
        System.out.println("S/s: W dół");
        System.out.println("A/a: Lewo");
        System.out.println("D/d: Prawo");
        System.out.print("Podaj ruch: ");

        // czytanie inputow
        String moveInput = scanner.nextLine();
        if (moveInput.equalsIgnoreCase("a") ||
                moveInput.equalsIgnoreCase("w") ||
                moveInput.equalsIgnoreCase("s") ||
                moveInput.equalsIgnoreCase("d")) {
            return moveInput.toUpperCase().charAt(0);
        }

        System.out.println("Nieprawidłowy ruch!");
        System.out.println();

        // drukuj plansze
        showBoard();

        // i teraz wczytaj ruch usera
        return getUserMove();
    }

    public int[] processLeftMove(int[] row) {
        int size = Settings.getSize();
        // kopiowanie wartości różnych od 0
        int[] newRow = new int[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (row[i] != 0) {
                newRow[j++] = row[i];
            }
        }

        // łączenie wartości w nowym rzędzie
        for (int i = 0; i < 3; i++) {
            if (newRow[i] != 0 && newRow[i] == newRow[i + 1]) {
                newRow[i] = 2 * newRow[i]; // a)
                score += newRow[i]; // zwiększenie wyniku
                // kopiowanie pozostałych wartości  // b)
                for (j = i + 1; j < 3; j++) {
                    newRow[j] = newRow[j + 1];
                }
                // c) ustawienie ostatniej lokalizacji tego rzędu na 0
                newRow[3] = 0;
            }
        }
        return newRow;
    }

    public int[] reverseArray(int[] arr) {
        // odwrócenie tablicy
        int[] reverseArr = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            reverseArr[i] = arr[arr.length - i - 1];
        }
        return reverseArr;
    }

    public int[] processRightMove(int[] row) {
        // kopiowanie wszystkich wartości różnych od 0
        int size = Settings.getSize();
        int[] newRow = new int[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (row[i] != 0) {
                newRow[j++] = row[i];
            }
        }

        // odwrócenie rzędu
        newRow = reverseArray(newRow);

        // przetworzenie ruchu w lewo
        newRow = processLeftMove(newRow);

        // odwrócenie rzędu
        return reverseArray(newRow);
    }

    public void processMove(char move) {
        int size = Settings.getSize();
        switch (move) {
            case MOVE_LEFT: {
                // dla każdego rzędu
                for (int i = 0; i < size; i++) {
                    // uzyskaj nowy rząd
                    int[] newRow = processLeftMove(board[i]);
                    // skopiuj wartości z nowego rzędu do rzędu
                    System.arraycopy(newRow, 0, board[i], 0, size);
                }
            }
            break;
            case MOVE_RIGHT: {
                // dla każdego rzędu
                for (int i = 0; i < size; i++) {
                    // uzyskaj nowy rząd
                    int[] newRow = processRightMove(board[i]);
                    // skopiuj wartości z nowego rzędu do rzędu
                    System.arraycopy(newRow, 0, board[i], 0, size);
                }
            }
            break;
            case MOVE_UP: {
                // dla każdej kolumny
                for (int j = 0; j < size; j++) {
                    // utwórz rząd z wartości kolumny
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }

                    // przetwórz ruch w lewo na tym rzędzie
                    int[] newRow = processLeftMove(row);

                    // skopiuj wartości z powrotem do kolumny
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i];
                    }
                }
            }
            break;
            case MOVE_DOWN: {
                // dla każdej kolumny
                for (int j = 0; j < size; j++) {
                    // utwórz rząd z wartości kolumny
                    int[] row = new int[size];
                    for (int i = 0; i < size; i++) {
                        row[i] = board[i][j];
                    }

                    // przetwórz ruch w prawo na tym rzędzie
                    int[] newRow = processRightMove(row);

                    // skopiuj wartości z powrotem do kolumny
                    for (int i = 0; i < size; i++) {
                        board[i][j] = newRow[i];
                    }
                }
            }
            break;
        }
    }

    public void play() {
        // ustaw planszę - dodaj losowe kafelki

        int value1 = Settings.getFieldValue1();
        int value2 = Settings.getFieldValue2();
        addRandomDigit(value1);
        addRandomDigit(value2);

        // dopóki gra się nie skończy
        while (!isGameOver()) {
            // pokaż planszę
            showBoard();

            // poproś użytkownika o ruch
            char move = getUserMove();

            // przetwórz ruch
            processMove(move);

            // dodaj losowe kafelki
            int r = random.nextInt(100);
            if (r % 2 == 0) {
                addRandomDigit(value1);
            } else {
                addRandomDigit(value2);
            }
        }

        if (gameWon()) {
            System.out.println("WYGRAŁEŚ!");
        } else {
            System.out.println("PRZEGRAŁEŚ!");
        }
    }

    public static void main(String[] args) {
        System.out.println("Podaj wielkość planszy: ");
        int size = new Scanner(System.in).nextInt();
        System.out.println("Podaj wartość nr1: ");
        int value1 = new Scanner(System.in).nextInt();
        System.out.println("Podaj wartość nr2: ");
        int value2 = new Scanner(System.in).nextInt();

        Settings gameSettings = new Settings(size, value1, value2);
        Game2048 gameOf2048 = new Game2048(gameSettings);

        gameOf2048.play();
    }
}
