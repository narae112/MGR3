package com.MGR.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath,String originalFileName, byte[] fileData) throws Exception{
        System.out.println("originalFileName 이미지명 = " + originalFileName);
        UUID uuid = UUID.randomUUID(); //파일 이름 생성
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        //lastIndexOf() 메서드는 문자열에서 지정된 문자또는  문자열이 마지막으로 등장하는 위치 인덱스 반환
        //substring(4) 4부터 끝까지   abc.jpg -> jpg
        String savedFileName =  uuid.toString() + extension;
        //암호화된 uuid에 확장명 붙여서 새로운 파일명을 제작
        String fileUploadFullUrl= uploadPath + "/" + savedFileName; //경로를 포함한 저장이름
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        System.out.println("savedFileName 이미지명 = " + savedFileName);
        return savedFileName;
    }
    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }



}