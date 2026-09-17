package hr.dario.rockmusic;

import hr.dario.rockmusic.api.MusicBrainzClient;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=================================");
        System.out.println("       ROCK MUSIC EXPLORER");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Enter artist name: ");
        String artistName = scanner.nextLine();
        System.out.println();
        System.out.println("Searching for: " + artistName);
        MusicBrainzClient client = new MusicBrainzClient();
        client.searchArtist(artistName);
    }
}
