package com.gympass.test.domain;

/**
 * Classe para representar a Volta
 * 
 * @author Jose San Pedro
 */
public class Lap {    
        
    private final int lapNumber;
    private final String lapTimeStr;
    private final double lapTime;
    private final float lapSpeed;

    public Lap(int lapNumber, String lapTimeStr, double lapTime, float lapSpeed) {        
        this.lapNumber = lapNumber;
        this.lapTimeStr = lapTimeStr;
        this.lapTime = lapTime;
        this.lapSpeed = lapSpeed;
    }
        
    public int getLapNumber() {
        return lapNumber;
    }
    
    public String getlapTimeStr() {
        return lapTimeStr;
    }
    
    public double getLapTime() {
        return lapTime;
    }
    
    public float getLapSpeed() {
        return lapSpeed;
    }

    @Override
    public String toString() {
        return "Lap{lapNumber=" + lapNumber + ", lapTimeStr=" + lapTimeStr + ", lapTime=" + lapTime + ", lapSpeed=" + lapSpeed + '}';
    }
}
