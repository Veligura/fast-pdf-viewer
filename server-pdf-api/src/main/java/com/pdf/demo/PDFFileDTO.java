package com.pdf.demo;

import java.nio.file.Path;

public class PDFFileDTO {
    public String id;
    public Path path;
    public String fileName;
    public Integer pages;
    public long size;

    public PDFFileDTO(Path path, String fileName, long size) {
        this.path = path;
        this.fileName = fileName;
        this.size = size;
    }

    public PDFFileDTO(String id, Path path, String fileName, Integer pages) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.pages = pages;
    }
}
