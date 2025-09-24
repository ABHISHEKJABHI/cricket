package com.cricketgame.models;

public class Player {
    private String name;
    private int playerId;
    private String role; // batsman, bowler, all-rounder
    private int runsScored;
    private int ballsFaced;
    private int wicketsTaken;
    private int ballsBowled;
    private int runsConceded;
    
    public Player(String name, int playerId, String role) {
        this.name = name;
        this.playerId = playerId;
        this.role = role;
        this.runsScored = 0;
        this.ballsFaced = 0;
        this.wicketsTaken = 0;
        this.ballsBowled = 0;
        this.runsConceded = 0;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public int getPlayerId() { return playerId; }
    public String getRole() { return role; }
    public int getRunsScored() { return runsScored; }
    public int getBallsFaced() { return ballsFaced; }
    public int getWicketsTaken() { return wicketsTaken; }
    public int getBallsBowled() { return ballsBowled; }
    public int getRunsConceded() { return runsConceded; }
    
    public void addRuns(int runs) { 
        this.runsScored += runs; 
        this.ballsFaced++;
    }
    
    public void addBallFaced() { this.ballsFaced++; }
    public void addWicket() { this.wicketsTaken++; }
    public void addBallsBowled() { this.ballsBowled++; }
    public void addRunsConceded(int runs) { this.runsConceded += runs; }
    
    public double getStrikeRate() {
        if (ballsFaced == 0) return 0.0;
        return (double) runsScored / ballsFaced * 100;
    }
    
    public double getEconomyRate() {
        if (ballsBowled == 0) return 0.0;
        return (double) runsConceded / (ballsBowled / 6.0);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - Runs: %d, Balls: %d, SR: %.2f", 
                           name, role, runsScored, ballsFaced, getStrikeRate());
    }
}
