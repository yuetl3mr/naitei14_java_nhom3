package org.example.framgiabookingtours.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
	String uploadFile(MultipartFile file, String fileName, String folder) throws Exception;
}