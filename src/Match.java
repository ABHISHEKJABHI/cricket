package com.cricketgame.models;

public class Match {
    private Team team1;
    private Team team2;
    private int totalOvers;
    private Team battingTeam;
    private Team bowlingTeam;
    private boolean isMatchCompleted;
    
    public Match(Team team1, Team team2, int totalOvers) {
        this.team1 = team1;
        this.team2 = team2;
        this.totalOvers = totalOvers;
        this.isMatchCompleted = false;
    }
    
    public void startMatch() {
        // Team 1 bats first
        battingTeam = team1;
        bowlingTeam = team2;
    }
    
    public void switchInnings() {
        if (battingTeam == team1) {
            battingTeam = team2;
            bowlingTeam = team1;
        } else {
            battingTeam = team1;
            bowlingTeam = team2;
        }
    }
    
    // Getters
    public Team getTeam1() { return team1; }
    public Team getTeam2() { return team2; }
    public int getTotalOvers() { return totalOvers; }
    public Team getBattingTeam() { return battingTeam; }
    public Team getBowlingTeam() { return bowlingTeam; }
    public boolean isMatchCompleted() { return isMatchCompleted; }
    
    public void completeMatch() { isMatchCompleted = true; }
    
    public Team getWinner() {
        if (!isMatchCompleted) return null;
        
        if (team1.getTotalRuns() > team2.getTotalRuns()) {
            return team1;
        } else if (team2.getTotalRuns() > team1.getTotalRuns()) {
            return team2;
        } else {
            return null; // Tie
        }
    }
}
