package server.controller;

import common.network.requests.UploadRequest;
import common.network.responses.SuccessResponse;
import io.minio.errors.MinioException;
import org.springframework.web.bind.annotation.*;
import server.service.filestorage.FileStorageService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class FileStorageController {
    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public SuccessResponse upload(@RequestBody UploadRequest req)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        fileStorageService.upload(req.getDataURL(), req.getFilename());

        return new SuccessResponse();
    }

    @GetMapping("/download/{filename}")
    public String download(@PathVariable String filename)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return fileStorageService.download(filename);
    }
}
