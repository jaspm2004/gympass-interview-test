package com.gympass.test.domain;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe para representar o Piloto
 * 
 * @author Jose San Pedro
 */
public class Driver {
    private final String driverNumber;
    private final String driverName;
    private final List<Lap> laps;   // voltas dadas pelo piloto na prova
    private double raceTime = 0l;   // em segundos
    private final DecimalFormat df;   
    private double fastestLap = Double.POSITIVE_INFINITY;
    private String fastestLapStr;   
    float avgSpeed = 0;

    public Driver(String driverNumber, String driverName) {
        df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        
        this.laps = new ArrayList<>();
        this.driverNumber = driverNumber;
        this.driverName = driverName;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public String getDriverName() {
        return driverName;
    }
    
    public void addLap(Lap lap) {
        laps.add(lap);
        
        // procura a melhor volta da corrida
        if(fastestLap > lap.getLapTime()) {
            fastestLap = lap.getLapTime();
            fastestLapStr = lap.getlapTimeStr();
        }
        
        // atualiza tempo total
        raceTime += lap.getLapTime();
    }

    public String getFastestLapStr() {
        return fastestLapStr;
    }
        
    public int getCompletedLaps() {
        return laps.size();
    }
    
    public double getRaceTime() {
        return raceTime;
    }
    
    public String getRaceTimeStr() {
        return df.format(raceTime).replace(",", ".");
    }

    public void printLaps() {        
        laps.forEach((lap) -> {            
            System.out.println(lap.toString());
        });        
    }
    
    public String getAvgSpeed() {        
        laps.forEach((lap) -> {
            avgSpeed += lap.getLapSpeed();            
        });
        
        avgSpeed = avgSpeed / laps.size();        
        return df.format(avgSpeed);
    }
    
    @Override
    public String toString() {        
        return "Driver{" + "driverNumber=" + driverNumber 
                + ", driverName=" + driverName 
                + ", raceTime=" + getRaceTimeStr()
                + ", fastestLap=" + fastestLapStr 
                + ", avgSpeed=" + getAvgSpeed() + '}';
    }
}
