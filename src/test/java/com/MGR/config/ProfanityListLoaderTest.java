package com.MGR.config;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfanityListLoaderTest {
    @Test
    public void testProfanityFilter() {
        // 욕설 필터링에 사용할 욕설 목록을 파일에서 읽어옵니다.
        List<String> profanityList = loadProfanityList("unsafe.txt");

        // 테스트할 문자열을 준비합니다.
        String cleanText = "욕설1";
        String textWithProfanity = "이 문장에는 욕설1이 포함되어 있습니다.";

        // 욕설이 없는 문자열을 테스트합니다.
        boolean resultCleanText = containsProfanity(cleanText, profanityList);
        assertFalse(resultCleanText);

        // 욕설이 포함된 문자열을 테스트합니다.
        boolean resultTextWithProfanity = containsProfanity(textWithProfanity, profanityList);
        assertTrue(resultTextWithProfanity);
    }

    // 주어진 문자열에 욕설이 포함되어 있는지 확인하는 메서드입니다.
    private boolean containsProfanity(String text, List<String> profanityList) {
        for (String profanity : profanityList) {
            if (text.contains(profanity)) {
                return true;
            }
        }
        return false;
    }

    // 파일에서 욕설 목록을 읽어오는 메서드입니다.
    private List<String> loadProfanityList(String filePath) {
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

}