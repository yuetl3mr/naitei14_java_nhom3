package org.example.framgiabookingtours.controller;

import java.io.IOException;

import org.example.framgiabookingtours.service.ImageUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test/upload")
public class ImageKitTestController {

    private final ImageUploadService imageUploadService;

    public ImageKitTestController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadTest(
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
        	String folderName = "test-uploads";
            String baseFileName = imageFile.getOriginalFilename(); // <<< Lấy tên file gốc

            String imageUrl = imageUploadService.uploadFile(imageFile, baseFileName, folderName);

            return ResponseEntity.ok("Upload thành công. URL: " + imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Upload thất bại: " + e.getMessage());
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Upload thất bại do lỗi không xác định: " + e.getMessage());
        }
    }
}