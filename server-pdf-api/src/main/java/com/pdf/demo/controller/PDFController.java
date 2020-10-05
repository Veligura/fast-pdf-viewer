package com.pdf.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdf.demo.PDFFileDTO;
import com.pdf.demo.services.PDFService;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("api/pdf")
public class PDFController {
    private PDFService pdfService;
    private ObjectMapper objectMapper;

    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") final MultipartFile file) {
        try {
            this.pdfService.upload(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Ok";
    }

    @GetMapping("/all")
    public  List<PDFFileDTO> getAll (){
        String id = null;
        return  pdfService.getList();
    }
    @GetMapping("/get/{id}")
    public byte[] getPDFById(@PathVariable("id") final String id) throws IOException {
        return IOUtils.toByteArray(pdfService.getByID(id).getInputStream());
    }

}
