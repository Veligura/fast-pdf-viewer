package com.pdf.demo.services;

import com.pdf.demo.PDFFileDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PDFService {
    void upload (MultipartFile file) throws IOException;

    List<PDFFileDTO> getList();

    Resource getByID(String id) throws IOException;
}
