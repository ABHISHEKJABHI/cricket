package com.cricketgame.ui;

import com.cricketgame.models.*;

public class Scoreboard {
    public void displayMatchInfo(Match match) {
        System.out.println("\n=== CRICKET MATCH ===");
        System.out.println(match.getTeam1().getName() + " vs " + match.getTeam2().getName());
        System.out.println("Overs: " + match.getTotalOvers());
        System.out.println("=====================\n");
    }
    
    public void displayInningsScore(Team battingTeam, Team bowlingTeam, int currentOver, int currentBall) {
        System.out.printf("\n%s: %d/%d (%d.%d overs)\n", 
                         battingTeam.getName(), 
                         battingTeam.getTotalRuns(), 
                         battingTeam.getWicketsLost(),
                         currentOver, currentBall);
        System.out.printf("Run Rate: %.2f\n", battingTeam.getRunRate());
    }
    
    public void displayFinalScore(Team team) {
        System.out.printf("\n%s Final Score: %d/%d in %d.%d overs\n", 
                         team.getName(),
                         team.getTotalRuns(),
                         team.getWicketsLost(),
                         team.getOversPlayed(),
                         team.getBallsPlayed() % 6);
        System.out.printf("Run Rate: %.2f\n", team.getRunRate());
    }
    
    public void displayBattingStats(Team team) {
        System.out.println("\n=== BATTING STATISTICS ===");
        for (Player player : team.getPlayers()) {
            if (player.getBallsFaced() > 0) {
                System.out.printf("%-15s: %d runs (%d balls) SR: %.2f\n",
                                player.getName(),
                                player.getRunsScored(),
                                player.getBallsFaced(),
                                player.getStrikeRate());
            }
        }
    }
    
    public void displayBowlingStats(Team team) {
        System.out.println("\n=== BOWLING STATISTICS ===");
        for (Player player : team.getPlayers()) {
            if (player.getBallsBowled() > 0) {
                System.out.printf("%-15s: %d/%d (%d.%d overs) Econ: %.2f\n",
                                player.getName(),
                                player.getWicketsTaken(),
                                player.getRunsConceded(),
                                player.getBallsBowled() / 6,
                                player.getBallsBowled() % 6,
                                player.getEconomyRate());
            }
        }
    }
    
    public void displayMatchResult(Match match) {
        Team winner = match.getWinner();
        if (winner != null) {
            System.out.println("\n*** " + winner.getName() + " WINS THE MATCH! ***");
            int margin = winner.getTotalRuns() - 
                        (winner == match.getTeam1() ? match.getTeam2().getTotalRuns() : match.getTeam1().getTotalRuns());
            System.out.println("Won by " + margin + " runs");
        } else {
            System.out.println("\n*** MATCH TIED! ***");
        }
    }
}
