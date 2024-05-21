package com.MGR;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class FilePathExampleTest {
    @Test
    void testFilePath() {
        // 파일이 저장되는 디렉토리 경로
        String directoryPath = "C:/MGR/ticket";

        // 해당 디렉토리에 있는 파일 목록을 가져옴
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        // 파일 목록 출력
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) { // 파일인 경우에만 출력
                    System.out.println("파일 경로: " + file.getAbsolutePath());
                }
            }
        } else {
            System.out.println("해당 경로에 파일이 존재하지 않습니다.");
        }
    }
}