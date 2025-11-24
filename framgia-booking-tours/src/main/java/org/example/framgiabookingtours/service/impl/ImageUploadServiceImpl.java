package org.example.framgiabookingtours.service.impl;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.*;
import io.imagekit.sdk.models.results.Result;

import org.example.framgiabookingtours.service.ImageUploadService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

	private ImageKit imageKit;

    public ImageUploadServiceImpl(ImageKit imageKit) {
        this.imageKit = imageKit;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileName, String folder) throws Exception {
        String base64String = Base64.getEncoder().encodeToString(file.getBytes());

        FileCreateRequest fileCreateRequest = new FileCreateRequest(
                base64String,
                fileName
        );

        fileCreateRequest.setFolder(folder);
        fileCreateRequest.setUseUniqueFileName(true);

        Result result = imageKit.upload(fileCreateRequest);
        
        ResponseMetaData meta = result.getResponseMetaData();
        int httpStatusCode = meta.getHttpStatusCode();

        if (httpStatusCode == 200 || httpStatusCode == 201) {
            return result.getUrl(); 
        } else {
            String errorMessage = meta.getRaw() != null ? meta.getRaw() : "Unknown error";
            
            throw new Exception("ImageKit upload failed: " + httpStatusCode + " - " + errorMessage);
        }
    }
}