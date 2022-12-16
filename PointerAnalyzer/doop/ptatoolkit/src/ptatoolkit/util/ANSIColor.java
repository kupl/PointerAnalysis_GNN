package ptatoolkit.util;

public class ANSIColor {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BOLD_GREEN = BOLD + GREEN;
    public static final String BOLD_YELLOW = BOLD + YELLOW;

    public static String colored(String color, String s) {
        return color + s + RESET;
    }

    public static void printColoredNumber(String color, String info, int number) {
        System.out.println(info + color + number + RESET);
    }

    public static void printColoredNumber(String color, String info, long number) {
        System.out.println(info + color + number + RESET);
    }

    public static void printColored(String color, String info, Object o) {
        System.out.println(info + color + o + RESET);
    }

    public static void printColored(String color, String s) {
        System.out.println(colored(color, s));
    }
}
