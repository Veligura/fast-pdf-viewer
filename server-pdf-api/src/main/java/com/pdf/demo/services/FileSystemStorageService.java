package com.pdf.demo.services;

import com.pdf.demo.PDFFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;



@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation = Paths.get("./file-storage");
    public FileSystemStorageService(){
        this.deleteAll();
        this.init();
    }


    @Override
    public PDFFileDTO store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            String hash = UUID.randomUUID().toString();
            Path createdDirectory = Files.createDirectory(rootLocation.resolve(hash));
            Files.copy(file.getInputStream(), createdDirectory.resolve(file.getOriginalFilename()));
            Path filePath = createdDirectory.resolve(file.getOriginalFilename());
            System.out.println(filePath);
            return new PDFFileDTO(filePath, file.getOriginalFilename(), file.getSize());

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }


    @Override
    public Resource loadAsResource(Path filePath) {
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + filePath);

            }
        } catch (MalformedURLException e) {
            throw new StorageException("Could not read file: " + filePath, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }
}