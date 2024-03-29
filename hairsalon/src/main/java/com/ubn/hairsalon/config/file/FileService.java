package com.ubn.hairsalon.config.file;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fileOutputStream = new FileOutputStream(fileUploadFullUrl);
        fileOutputStream.write(fileData);
        fileOutputStream.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            String fileName = deleteFile.getName();
            deleteFile.delete();
            log.info(fileName + " 파일이 정상적으로 삭제되었습니다.");
        } else {
            log.info("존재하지 않는 파일입니다.");
        }
    }

    public byte[] readFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
}
