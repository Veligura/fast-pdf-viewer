package com.pdf.demo.services;

import com.pdf.demo.PDFFileDTO;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;


import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PDFServiceImplementation implements  PDFService {
    private StorageService storageService;
    private HashMap<String, PDFFileDTO> pdfs;

    public PDFServiceImplementation(StorageService storageService) {
        this.storageService = storageService;
        this.pdfs = new HashMap<String, PDFFileDTO>();
    }

    @Override
    public void upload(MultipartFile file) throws IOException {
        PDFFileDTO pdfDTO = storageService.store(file);
        PDDocument PDFDocument = PDDocument.load(Files.newInputStream(pdfDTO.path));
        pdfDTO.pages = PDFDocument.getNumberOfPages();
        PDFDocument.close();
        String id = UUID.randomUUID().toString();
        pdfDTO.id = id;
        this.pdfs.put(id, pdfDTO);
    }

    @Override
    public List<PDFFileDTO> getList() {
        return new ArrayList<>(pdfs.values());
    }

    @Override
    public Resource getByID(String id)  {
        PDFFileDTO pdfFileDTO = this.pdfs.get(id);

        Resource pdfFileResource  =  storageService.loadAsResource(pdfFileDTO.path);

        return pdfFileResource;
    }




}
