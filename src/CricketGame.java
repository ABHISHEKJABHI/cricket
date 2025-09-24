package com.cricketgame;

import com.cricketgame.engine.GameEngine;
import com.cricketgame.models.*;
import com.cricketgame.ui.Scoreboard;
import java.util.Scanner;

public class CricketGame {
    private Scanner scanner;
    private Scoreboard scoreboard;
    private GameEngine gameEngine;
    
    public CricketGame() {
        this.scanner = new Scanner(System.in);
        this.scoreboard = new Scoreboard();
        this.gameEngine = new GameEngine();
    }
    
    public Team createTeam(String teamName) {
        Team team = new Team(teamName);
        
        // Add 11 players to the team
        String[] playerNames = {
            "Rohit Sharma", "Virat Kohli", "KL Rahul", "Suryakumar", "Hardik Pandya",
            "Rishabh Pant", "Ravindra Jadeja", "Bhuvneshwar Kumar", "Jasprit Bumrah",
            "Yuzvendra Chahal", "Mohammed Shami"
        };
        
        String[] roles = {
            "Batsman", "Batsman", "Batsman", "Batsman", "All-rounder",
            "Wicketkeeper", "All-rounder", "Bowler", "Bowler", "Bowler", "Bowler"
        };
        
        for (int i = 0; i < 11; i++) {
            team.addPlayer(new Player(playerNames[i], i + 1, roles[i]));
        }
        
        return team;
    }
    
    public void playInnings(Match match, boolean isFirstInnings) {
        Team battingTeam = match.getBattingTeam();
        Team bowlingTeam = match.getBowlingTeam();
        
        System.out.println("\n=== " + (isFirstInnings ? "FIRST" : "SECOND") + " INNINGS ===");
        System.out.println(battingTeam.getName() + " is batting");
        System.out.println(bowlingTeam.getName() + " is bowling");
        
        int totalBalls = match.getTotalOvers() * 6;
        int currentBowlerIndex = 7; // Start with a bowler
        
        for (int ball = 1; ball <= totalBalls; ball++) {
            if (battingTeam.getWicketsLost() >= 10) {
                System.out.println("\nAll out!");
                break;
            }
            
            int currentOver = (ball - 1) / 6;
            int currentBallInOver = (ball - 1) % 6 + 1;
            
            if (currentBallInOver == 1) {
                System.out.println("\nOver " + (currentOver + 1) + ":");
                // Change bowler every over (simple rotation)
                currentBowlerIndex = (currentBowlerIndex % 11) + 1;
                if (currentBowlerIndex >= 11) currentBowlerIndex = 7;
            }
            
            Player batsman = battingTeam.getPlayers().get(ball % 7); // Simple rotation
            Player bowler = bowlingTeam.getPlayers().get(currentBowlerIndex);
            
            System.out.printf("Ball %d.%d: ", currentOver + 1, currentBallInOver);
            System.out.printf("%s vs %s - ", batsman.getName(), bowler.getName());
            
            gameEngine.playBall(match, batsman, bowler);
            
            // Display score after every ball
            scoreboard.displayInningsScore(battingTeam, bowlingTeam, currentOver + 1, currentBallInOver);
            
            // Pause for readability
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        scoreboard.displayFinalScore(battingTeam);
    }
    
    public void startGame() {
        System.out.println("Welcome to Java Cricket Game!");
        
        // Create teams
        Team team1 = createTeam("India");
        Team team2 = createTeam("Australia");
        
        // Set match parameters
        int totalOvers = 5; // Short match for demo
        
        Match match = new Match(team1, team2, totalOvers);
        match.startMatch();
        
        scoreboard.displayMatchInfo(match);
        
        // First innings
        playInnings(match, true);
        
        // Second innings
        match.switchInnings();
        playInnings(match, false);
        
        match.completeMatch();
        
        // Display results
        scoreboard.displayBattingStats(team1);
        scoreboard.displayBowlingStats(team2);
        scoreboard.displayBattingStats(team2);
        scoreboard.displayBowlingStats(team1);
        scoreboard.displayMatchResult(match);
    }
    
    public static void main(String[] args) {
        CricketGame game = new CricketGame();
        game.startGame();
    }
}
