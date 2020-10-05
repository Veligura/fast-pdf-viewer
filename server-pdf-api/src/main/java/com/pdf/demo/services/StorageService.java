package com.pdf.demo.services;


import com.pdf.demo.PDFFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    PDFFileDTO store(MultipartFile file);

    Stream<Path> loadAll();

    Resource loadAsResource(Path FilePath);

    File multipartToFile(MultipartFile multipart) throws IOException, IllegalStateException;

    void deleteAll();

}