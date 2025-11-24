package org.example.framgiabookingtours.controller;

import java.util.UUID;

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
            String newFileName = UUID.randomUUID().toString() + ".jpg";
            String folderName = "test-uploads";

            String imageUrl = imageUploadService.uploadFile(imageFile, newFileName, folderName);

            return ResponseEntity.ok("Upload thành công. URL: " + imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Upload thất bại: " + e.getMessage());
        }
    }
}