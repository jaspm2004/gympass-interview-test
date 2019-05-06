package com.gympass.test;

import com.gympass.test.domain.Race;

/**
 * Classe principal
 * 
 * @author Jose San Pedro
 */
public class Main {
    
    public static void main(String[] args) {
        
        Race race = new Race(4);
        race.readRaceLog("corrida.log");
        race.compileResults();
        race.printResults();
        race.saveResults("resultado.txt");        
    }
}