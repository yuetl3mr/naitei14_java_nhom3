package org.example.framgiabookingtours.service.impl;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.*;
import io.imagekit.sdk.models.results.Result;

import org.example.framgiabookingtours.service.ImageUploadService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

	private ImageKit imageKit;

	public ImageUploadServiceImpl(ImageKit imageKit) {
		this.imageKit = imageKit;
	}

	@Override
	public String uploadFile(MultipartFile file, String fileName, String folder) throws IOException {

		if (file.isEmpty()) {
			throw new IOException("File upload không được rỗng.");
		}
		try {
			String base64String = Base64.getEncoder().encodeToString(file.getBytes());

			FileCreateRequest fileCreateRequest = new FileCreateRequest(base64String, fileName);

			fileCreateRequest.setFolder(folder);
			fileCreateRequest.setUseUniqueFileName(true);

			Result result = imageKit.upload(fileCreateRequest);

			if (result != null && result.getUrl() != null) {
				return result.getUrl();
			} else {
				String errorMessage = result != null && result.getResponseMetaData().getRaw() != null
						? result.getResponseMetaData().getRaw()
						: "Unknown error in result.";
				throw new IOException("ImageKit upload thành công nhưng không có URL: " + errorMessage);
			}

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			// Bắt các lỗi chung từ SDK (ví dụ: Bad Request, Auth Failed)
			throw new IOException("Upload thất bại do lỗi SDK/máy chủ: " + e.getMessage(), e);
		}
	}
}