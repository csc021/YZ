package com.tf.sc.controller;

import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Result;
import com.tf.sc.utils.FileUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileController {
    private static final Path UPLOAD_DIR = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath();

    @RequireRole({"0", "1", "2"})
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error("File is required");
        }
        Files.createDirectories(UPLOAD_DIR);
        String fileName = FileUtil.fileName(file.getOriginalFilename());
        Path target = UPLOAD_DIR.resolve(fileName).normalize();
        if (!target.startsWith(UPLOAD_DIR)) {
            return Result.error("Invalid file path");
        }
        file.transferTo(target.toFile());
        return Result.success(request.getContextPath() + "/files/" + fileName);
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<FileSystemResource> download(@PathVariable String fileName) {
        Path path = UPLOAD_DIR.resolve(fileName).normalize();
        if (!path.startsWith(UPLOAD_DIR)) {
            return ResponseEntity.badRequest().build();
        }
        File file = path.toFile();
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(resolveMediaType(fileName))
                .body(new FileSystemResource(file));
    }

    @RequireRole({"0", "1", "2"})
    @DeleteMapping("/{fileName:.+}")
    public Result<Boolean> delete(@PathVariable String fileName) throws IOException {
        Path path = UPLOAD_DIR.resolve(fileName).normalize();
        if (!path.startsWith(UPLOAD_DIR)) {
            return Result.error("Invalid file path");
        }
        return Result.success(Files.deleteIfExists(path));
    }

    private MediaType resolveMediaType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return MediaType.valueOf("image/webp");
        if (lower.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        if (lower.endsWith(".bmp")) return MediaType.valueOf("image/bmp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
