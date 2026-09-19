package hr.dario.rockmusic.ui;

import java.util.Scanner;

public class ConsoleInput {
    public int readChoice(Scanner scanner, int min, int max) {
        int choice;
        if (min == max){
            return min;
        }
        while (true) {
            System.out.println("Choose artist [" + min + "-" + max + "]: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice <= max && choice >= min) {
                    break;
                } else {
                    System.out.println();
                    System.out.println("Please choose a number between " + min +" and " + max + ".");
                }
            } else {
                System.out.println();
                System.out.println("Please enter a number.");
                scanner.next();
            }
        }
        return choice;
    }
}
