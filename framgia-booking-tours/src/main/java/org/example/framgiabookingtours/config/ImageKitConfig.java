package org.example.framgiabookingtours.config;

import io.imagekit.sdk.ImageKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageKitConfig {

	@Value("${imagekit.publicKey}")
	private String publicKey;

	@Value("${imagekit.privateKey}")
	private String privateKey;

	@Value("${imagekit.urlEndpoint}")
	private String urlEndpoint;

	@Bean
	ImageKit imageKit() {
		io.imagekit.sdk.config.Configuration config = new io.imagekit.sdk.config.Configuration(publicKey, privateKey,
				urlEndpoint);

		ImageKit imageKit = ImageKit.getInstance();
		imageKit.setConfig(config);
		return imageKit;
	}
}