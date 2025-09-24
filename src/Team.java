package com.cricketgame.models;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private int totalRuns;
    private int wicketsLost;
    private int oversPlayed;
    private int ballsPlayed;
    
    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.totalRuns = 0;
        this.wicketsLost = 0;
        this.oversPlayed = 0;
        this.ballsPlayed = 0;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    public List<Player> getPlayers() { return players; }
    public String getName() { return name; }
    public int getTotalRuns() { return totalRuns; }
    public int getWicketsLost() { return wicketsLost; }
    public int getOversPlayed() { return oversPlayed; }
    public int getBallsPlayed() { return ballsPlayed; }
    
    public void addRuns(int runs) { totalRuns += runs; }
    public void addWicket() { wicketsLost++; }
    public void addBall() { 
        ballsPlayed++;
        if (ballsPlayed % 6 == 0) {
            oversPlayed = ballsPlayed / 6;
        }
    }
    
    public double getRunRate() {
        if (oversPlayed == 0) return 0.0;
        return (double) totalRuns / oversPlayed;
    }
    
    public Player getPlayerByName(String name) {
        return players.stream()
                     .filter(p -> p.getName().equalsIgnoreCase(name))
                     .findFirst()
                     .orElse(null);
    }
}
