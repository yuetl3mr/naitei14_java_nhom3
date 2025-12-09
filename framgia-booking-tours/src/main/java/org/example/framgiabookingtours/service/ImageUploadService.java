package org.example.framgiabookingtours.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
	String uploadFile(MultipartFile file, String fileName, String folder) throws IOException;
}