package com.cricketgame.engine;

import com.cricketgame.models.*;
import java.util.Random;

public class GameEngine {
    private Random random;
    private static final String[] BALL_OUTCOMES = {
        "0", "1", "2", "3", "4", "6", "W"  // W for wicket
    };
    private static final double[] PROBABILITIES = {
        0.30, 0.25, 0.15, 0.05, 0.15, 0.05, 0.05
    };
    
    public GameEngine() {
        this.random = new Random();
    }
    
    public String simulateBall() {
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        for (int i = 0; i < PROBABILITIES.length; i++) {
            cumulativeProbability += PROBABILITIES[i];
            if (rand <= cumulativeProbability) {
                return BALL_OUTCOMES[i];
            }
        }
        
        return "0"; // Default outcome
    }
    
    public int getRunsFromOutcome(String outcome) {
        if (outcome.equals("W")) return -1; // Wicket
        return Integer.parseInt(outcome);
    }
    
    public void playBall(Match match, Player batsman, Player bowler) {
        String outcome = simulateBall();
        int runs = getRunsFromOutcome(outcome);
        
        if (runs == -1) { // Wicket
            match.getBattingTeam().addWicket();
            bowler.addWicket();
            System.out.println("OUT! " + batsman.getName() + " is dismissed!");
        } else {
            match.getBattingTeam().addRuns(runs);
            batsman.addRuns(runs);
            bowler.addRunsConceded(runs);
            System.out.println(batsman.getName() + " scores " + runs + " run(s)");
        }
        
        batsman.addBallFaced();
        bowler.addBallsBowled();
        match.getBattingTeam().addBall();
    }
}
