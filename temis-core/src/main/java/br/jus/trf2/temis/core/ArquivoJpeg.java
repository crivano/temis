package br.jus.trf2.temis.core;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Global(singular = "Imagem", plural = "Imagens", gender = Gender.SHE)
public class ArquivoJpeg extends ArquivoImagem {
	public static final String IMAGE_JPEG = "image/jpeg";

	@Override
	public String extension() {
		return ".jpg";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(IMAGE_JPEG);
	}

//	public static byte[] transformImageToJpeg(byte[] oldImageData, int cx, int cy, boolean crop) {
//		final OutputSettings OUTPUT_SETTINGS = new OutputSettings(OutputEncoding.JPEG);
//		OUTPUT_SETTINGS.setQuality(90);
//
//		ImagesService imagesService = ImagesServiceFactory.getImagesService();
//
//		com.google.appengine.api.images.Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
//		Transform resize = ImagesServiceFactory.makeResize(cx, cy, 0.5, 0.5);
//
//		com.google.appengine.api.images.Image newImage = imagesService.applyTransform(resize, oldImage,
//				OUTPUT_SETTINGS);
//
//		byte[] newImageData = newImage.getImageData();
//		return newImageData;
//	}

}
