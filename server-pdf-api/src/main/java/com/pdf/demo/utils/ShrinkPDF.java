package com.pdf.demo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * App for shrinking PDF files by applying jpeg compression
 *
 * @author Benjamin Nanes, bnanes@emory.edu
 */
public final class ShrinkPDF {


    private float compQual = -1;
    private static float compQualDefault = 0.2f;


    public PDDocument shrinkMe(File file)
            throws FileNotFoundException, IOException {
        if (compQual < 0)
            compQual = compQualDefault;
        final RandomAccessBufferedFileInputStream rabfis =
                new RandomAccessBufferedFileInputStream(file);
        final PDFParser parser = new PDFParser(rabfis);
        parser.parse();
        final PDDocument doc = parser.getPDDocument();
        final PDPageTree pages = doc.getPages();
        final ImageWriter imgWriter;
        final ImageWriteParam iwp;

        final Iterator<ImageWriter> jpgWriters =
                ImageIO.getImageWritersByFormatName("jpeg");
        imgWriter = jpgWriters.next();
        iwp = imgWriter.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(compQual);
        for (PDPage p : pages) {
            scanResources(p.getResources(), doc, imgWriter, iwp);
        }
        return doc;
    }

    private void scanResources(
            final PDResources rList,
            final PDDocument doc,
            final ImageWriter imgWriter,
            final ImageWriteParam iwp)
            throws FileNotFoundException, IOException {
        Iterable<COSName> xNames = rList.getXObjectNames();
        for (COSName xName : xNames) {
            final PDXObject xObj = rList.getXObject(xName);
            if (xObj instanceof PDFormXObject)
                scanResources(((PDFormXObject) xObj).getResources(), doc, imgWriter, iwp);
            if (!(xObj instanceof PDImageXObject))
                continue;
            final PDImageXObject img = (PDImageXObject) xObj;
            System.out.println("Compressing image: " + xName.getName());
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgWriter.setOutput(ImageIO.createImageOutputStream(baos));
            imgWriter.write(null,
                    new IIOImage(img.getImage(), null, null), iwp);
            final ByteArrayInputStream bais =
                    new ByteArrayInputStream(baos.toByteArray());
            final PDImageXObject imgNew = JPEGFactory.createFromStream(doc, bais);
            rList.put(xName, imgNew);
        }
    }


}