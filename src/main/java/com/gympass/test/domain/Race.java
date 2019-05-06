package com.gympass.test.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Classe para representar a Corrida
 * 
 * @author Jose San Pedro
 */
public class Race {
        
    private final Map<String, Driver> drivers;
    private double fastestLap = Double.POSITIVE_INFINITY;
    private String fastestLapStr, fastestLapDriver;
    private List<String> raceResult;
    private int i = 0;
    private Driver driver;    
    private final int lapsToComplete;
    private double leaderRaceTime, diffToLeader;
    
    private final DecimalFormat df;   

    /**
     * Cria uma nova instância da classe
     * 
     * @param lapsToComplete número de voltas da corrida
     */
    public Race(int lapsToComplete) {        
        drivers = new HashMap<>();
        this.lapsToComplete = lapsToComplete;
        
        df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
    }
    
    /**
     * Faz a leitura do arquivo de log da corrida
     * 
     * @param filePath caminho onde fica o arquivo de log
     */
    public void readRaceLog(String filePath) {
        BufferedReader b = null;
        boolean isFirstLine = true;
        
        try {
            File input = new File(filePath);
            b = new BufferedReader(new FileReader(input));
            String readLine;
            
            while ((readLine = b.readLine()) != null) {                
                if (isFirstLine) {
                    isFirstLine = false;
                } else {                    
                    parseLogLine(readLine);
                }
            }

            //printDrivers();
        } catch (IOException ex) {
            System.out.println("Aconteceu um erro na leitura do arquivo " + filePath + ": " + ex.getMessage());
        } finally {
            try {
                if (b != null)
                    b.close();
            } catch (IOException ex) {
               System.out.println("Aconteceu um erro na liberação de recursos: " + ex.getMessage());
            } 
        }
    }
    
    /**
     * Extrai os dados de interesse de uma linha do arquivo de log e monta a estrutura interna
     * 
     * @param line 
     */
    private void parseLogLine(String line) {
        Scanner sc = new Scanner(line);    
        String driverNumber, driverName;
        int lapNumber;
        String lapTimeStr;
        float lapSpeed;               
                    
        // timestamp
        sc.next();            
        // cod - piloto
        driverNumber = sc.next();            
        sc.next();            
        driverName = sc.next();
        driver = drivers.get(driverNumber);
        if (driver == null) {
            driver = new Driver(driverNumber, driverName);
            drivers.put(driverNumber, driver);
        }

        // n. volta
        lapNumber = sc.nextInt();
        // tempo volta
        lapTimeStr = sc.next();
        double lapTime = convertLapTime(lapTimeStr);
        
        // procura a melhor volta da corrida
        if(fastestLap > lapTime) {
            fastestLap = lapTime;
            fastestLapStr = lapTimeStr;
            fastestLapDriver = driverNumber + " - " + driverName;
        }
            
        // veloc. média volta
        lapSpeed = Float.parseFloat(sc.next().replace(",", "."));

        driver.addLap(new Lap(lapNumber, lapTimeStr, lapTime, lapSpeed));        
    }

    /**
     * Analisa os dados coletados do log e gera os resultados da prova
     * 
     */
    public void compileResults() {
        raceResult = new ArrayList<>();
                            
        // gera cabeçalho
        raceResult.add(String.format("%10s%20s%20s%30s%30s%30s%30s%30s", 
                "Posição Chegada", 
                "Código Piloto", 
                "Nome Piloto", 
                "Qtde Voltas Completadas", 
                "Tempo Total de Prova(*)",
                "Melhor volta da Prova",
                "Velocidade média da Prova",
                "Dif. para o Vencedor(**)"));

        Map<String, Double> mapF = new HashMap<>();
        Map<String, Integer> mapDNF = new HashMap<>();

        // filtra os pilotos que completaram a prova e os que não em mapas diferentes
        drivers.entrySet().forEach((mapElement) -> {             
            if (mapElement.getValue().getCompletedLaps() == lapsToComplete) {
                mapF.put(mapElement.getValue().getDriverNumber(), mapElement.getValue().getRaceTime());
            } else {
                mapDNF.put(mapElement.getValue().getDriverNumber(), mapElement.getValue().getCompletedLaps());
            }
        }); 

        // ordena os pilotos que finalizaram a prova
        // de maneira crescente considerando o tempo total da corrida
        Map<String, Double> sortedFMap = sortFMap(mapF);         
        sortedFMap.entrySet().forEach((mapElement) -> { 
            driver = drivers.get(mapElement.getKey());
            i++;            
            if (i == 1) {
                leaderRaceTime = driver.getRaceTime();
                diffToLeader = 0;
            } else {
                diffToLeader = driver.getRaceTime() - leaderRaceTime;
            }
            
            raceResult.add(String.format("%15d%20s%20s%30d%30s%30s%30s%30s", 
                    i, 
                    driver.getDriverNumber(), 
                    driver.getDriverName(), 
                    driver.getCompletedLaps(), 
                    convertToHHMMSS(driver.getRaceTimeStr()),
                    driver.getFastestLapStr(),
                    driver.getAvgSpeed(),
                    "+" + df.format(diffToLeader).replace(",", ".")));            
        }); 

        // ordena os pilotos que não finalizaram a prova 
        // de maneira decrescente considerando a quantidade de voltas completadas
        Map<String, Integer> sortedDNFMap = sortDNFMap(mapDNF);
        sortedDNFMap.entrySet().forEach((mapElement) -> {             
            driver = drivers.get(mapElement.getKey());
            raceResult.add(String.format("%15s%20s%20s%30d%30s%30s%30s%30s", 
                    "NC", 
                    driver.getDriverNumber(), 
                    driver.getDriverName(), 
                    driver.getCompletedLaps(), 
                    convertToHHMMSS(driver.getRaceTimeStr()),
                    driver.getFastestLapStr(),
                    driver.getAvgSpeed(),
                    "--"));           
        });   
        
        raceResult.add("");
        raceResult.add("Melhor volta da corrida: " + getFastestLapStr() + " (" + fastestLapDriver + ")");
        raceResult.add("");
        raceResult.add("Legenda:");
        raceResult.add("- NC = Não completou a prova");
        raceResult.add("- (*) = formato HH:MM:SS.MS");
        raceResult.add("- (**) = formato SS.MS");
    }
    
    /**
     * Imprime no console os resultados da prova
     * 
     */
    public void printResults() {
        raceResult.forEach((line) -> {
            System.out.println(line);
        });
    }
    
    /**
     * Salva resultados da corrida no arquivo correspondente
     * 
     * @param filePath caminho onde será salvo o arquivo de resultados
     */
    public void saveResults(String filePath) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
            for (String line : raceResult) {
                bw.write(line + "\r\n");
            }
        } catch (IOException ex) {
            System.out.println("Aconteceu um erro na escrita do arquivo resultado.txt: " + ex.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                System.out.println("Aconteceu um erro na liberação de recursos: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Gera um novo mapa ordenado de forma crescente
     * 
     * @param map
     * @return 
     */
    private Map<String, Double> sortFMap(Map<String, Double> map) {
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        
        return sortedMap;
    }
    
    /**
     * Gera um novo mapa ordenado de forma decrescente
     * 
     * @param map
     * @return 
     */
    private Map<String, Integer> sortDNFMap(Map<String, Integer> map) {
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        
        return sortedMap;
    }
            
    public String getFastestLapStr() {
        return fastestLapStr;
    }
    
    /**
     * Faz a conversão do tempo de volta do formato M:S.MS para segundos
     * 
     * @param lapTimeStr
     * @return 
     */
    private double convertLapTime(String lapTimeStr) {
        double lapTime = 0l;
        
        // assumindo formato imutável M:ss.mmm
        String[] lapTimeArray = lapTimeStr.split("\\.");        
        String ms = lapTimeArray[1];
        
        lapTimeArray = lapTimeArray[0].split(":");
        String m = lapTimeArray[0];
        String s = lapTimeArray[1];
        
        lapTime += (Double.parseDouble(m) * 60) + Double.parseDouble(s);
        lapTime += Double.parseDouble(ms) / 1000;
        
        return lapTime;
    }
    
    /**
     * Faz a conversão do tempo de volta em formato S.MS para HH:MM:SS.MS
     * 
     * @param lapTimeStr
     * @return 
     */
    private String convertToHHMMSS(String lapTimeStr) {
        String[] strArray = lapTimeStr.split("\\.");
        int totalSecs = Integer.parseInt(strArray[0]);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d.%s", hours, minutes, seconds, strArray[1]);
    }
    
    /**
     * Imprime no console a lista de pilotos e as respectivas voltas
     */
    private void printDrivers() {
        drivers.entrySet().forEach((mapElement) -> { 
            System.out.println(mapElement.getValue().toString());
            mapElement.getValue().printLaps();
        }); 
    }
}
