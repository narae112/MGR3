package com.MGR.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfanityListLoader {
    public static List<String> loadProfanityList(String filePath) {
        List<String> profanityList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                profanityList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profanityList;
    }

    public static void main(String[] args) {
        String filePath = "unsafe.txt";
        List<String> profanityList = loadProfanityList(filePath);
        // 필요할 때 욕설 목록 사용
        System.out.println(profanityList);
    }
}
