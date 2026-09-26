package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.SkinPreviewResponseDTO;
import com.mariayasmim.estetica.service.SkinPreviewRateLimiter;
import com.mariayasmim.estetica.service.SkinPreviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Iterator;

@RestController
@RequestMapping("/api/skin-preview")
public class SkinPreviewController {

    private static final long MAX_IMAGE_BYTES = 8L * 1024 * 1024;
    private static final int MAX_IMAGE_DIMENSION = 6_000;

    private final SkinPreviewService skinPreviewService;
    private final SkinPreviewRateLimiter rateLimiter;

    public SkinPreviewController(SkinPreviewService skinPreviewService, SkinPreviewRateLimiter rateLimiter) {
        this.skinPreviewService = skinPreviewService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping(value = "/public", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generate(@RequestParam("photo") MultipartFile photo, HttpServletRequest request) {
        if (!rateLimiter.allow(clientKey(request), System.currentTimeMillis())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Você já pediu algumas simulações. Tente novamente daqui a pouco.");
        }
        if (photo == null || photo.isEmpty() || photo.getSize() > MAX_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma foto de até 8 MB.");
        }

        String mimeType = photo.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(mimeType) && !MediaType.IMAGE_PNG_VALUE.equals(mimeType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use uma foto JPEG ou PNG.");
        }

        try {
            validateImage(photo);
            SkinPreviewService.GeneratedImage generated = skinPreviewService.generate(photo.getBytes(), mimeType);
            return ResponseEntity.ok(new SkinPreviewResponseDTO(generated.imageBase64(), generated.mimeType()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler essa imagem.");
        }
    }

    private void validateImage(MultipartFile photo) throws IOException {
        try (ImageInputStream imageInput = ImageIO.createImageInputStream(photo.getInputStream())) {
            if (imageInput == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo de imagem inválido.");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arquivo de imagem inválido.");
            ImageReader reader = readers.next();
            try {
                reader.setInput(imageInput, true, true);
                String format = reader.getFormatName();
                boolean matchesMime = MediaType.IMAGE_JPEG_VALUE.equals(photo.getContentType())
                        ? format.equalsIgnoreCase("JPEG") || format.equalsIgnoreCase("JPG")
                        : format.equalsIgnoreCase("PNG");
                if (!matchesMime) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O conteúdo do arquivo não corresponde a uma foto JPEG ou PNG.");
                }
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0 || width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "A foto deve ter dimensões de até 6000 × 6000 pixels.");
                }
            } finally {
                reader.dispose();
            }
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String firstAddress = forwardedFor.split(",", 2)[0].trim();
            if (!firstAddress.isBlank()) return firstAddress;
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
