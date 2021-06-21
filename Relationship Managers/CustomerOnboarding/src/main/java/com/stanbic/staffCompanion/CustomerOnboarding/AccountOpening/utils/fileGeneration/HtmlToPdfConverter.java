package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.fileGeneration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;

/** Helps convert HTML files to PDF
 *
 * Source: https://www.netjstech.com/2021/02/html-to-pdf-java-flying-saucer-openpdf.html
 *
 * TODO: Move this to a background thread/Invoke from Task Queue
 * **/
@Component
public class HtmlToPdfConverter {

    private Logger log = LoggerFactory.getLogger(HtmlToPdfConverter.class);

    @Autowired
    private ReplacedElementFactoryImpl replacedElementFactory;

    public HtmlToPdfConverter(){}

    public void convertToPDF(String inputHtmlFile, String outputPDFPath) throws IOException {
        // Convert HTML to XHTML
        String xHTMLFile = this.createWellFormedHtml(inputHtmlFile);

        // Convert XHTML to PDF
        // This method writes the PDF to the path specified in the output path argument.
        // TODO: Is there a better way to do the whole process in memory and return the file directly?
        //  Instead of first writing it to file, then reading it, and then finally deleting it
        //  (because we definitely need to delete it to save space).

        convertXHTMLToPdf(xHTMLFile, outputPDFPath);

    }

    private String createWellFormedHtml(String inputHTML) throws IOException {
        Document document = Jsoup.parse(inputHTML, "UTF-8");
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        log.info("HTML parsing done...");

        return document.html();
    }

    private void convertXHTMLToPdf(String xhtml, String outputPdfPath) throws IOException {
        OutputStream outputStream = null;

        // TODO: Do not do this
        try {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);

            // Register custom ReplacedElementFactory implementation
            sharedContext.setReplacedElementFactory(this.replacedElementFactory);
            sharedContext.getTextRenderer().setSmoothingThreshold(0);

            // Register additional font
            renderer.getFontResolver().addFont(
                    getClass().getClassLoader()
                            .getResource("fonts/PRISTINA.ttf").toString(), // TODO: Add the fonts to class path
                    true
            );

            // Set base URL to resolve the relative URLs
            String baseUrl = FileSystems.getDefault()
                    .getPath("F:\\",
                            "Anshu\\NetJs\\Programs\\",
                            "src\\main\\resources\\css") // TODO: Update this
                    .toUri()
                    .toURL()
                    .toString();
            renderer.setDocumentFromString(xhtml, baseUrl);
            renderer.layout();

            outputStream = new FileOutputStream(outputPdfPath);
            renderer.createPDF(outputStream);

            log.info("PDF creation completed");

        }finally {
            if(outputStream != null)
                outputStream.close();
        }
    }
}
