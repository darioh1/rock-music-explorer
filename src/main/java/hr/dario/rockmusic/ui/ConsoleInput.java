package hr.dario.rockmusic.ui;

import java.util.Scanner;

public class ConsoleInput {
    public String readArtistName(Scanner scanner) {
        return scanner.nextLine();
    }
    public int readChoice(Scanner scanner, int min, int max, String what) {
        if (min == max){
            return min;
        }
        while (true) {
            System.out.println("Choose " + what + "[" + min + "-" + max + "]: ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice <= max && choice >= min){
                    return choice;
                }
                System.out.println();
                System.out.println("Please enter a number between " + min +" and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println("Please enter only one number.");
            }
        }
    }
}
