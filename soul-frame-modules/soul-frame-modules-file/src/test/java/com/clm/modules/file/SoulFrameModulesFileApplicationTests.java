package com.clm.modules.file;

import com.clm.modules.file.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
class SoulFrameModulesFileApplicationTests {

    @Autowired
    private FileService fileService;

    @Test
    void contextLoads() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
        String s = fileService.uploadFile(file);
        System.out.println(s);
    }

}
