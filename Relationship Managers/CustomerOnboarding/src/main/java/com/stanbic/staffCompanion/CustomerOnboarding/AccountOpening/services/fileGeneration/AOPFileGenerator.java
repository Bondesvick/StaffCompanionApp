package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services.fileGeneration;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.fileGeneration.AOPContentDTO;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.fileGeneration.HtmlToPdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Component
public class AOPFileGenerator{

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private HtmlToPdfConverter htmlToPdfConverter;

    private Logger log = LoggerFactory.getLogger(AOPFileGenerator.class);

    
    public void generateFile(String templateName, AOPContentDTO inputData) throws IOException {

        log.info("Generating HTML file from template");

        String generatedHTML = this.generateHTMLPage(templateName, inputData);

        this.generatePDFFromHTMLPage(generatedHTML, "OutputFile");
    }

    
    protected String generateHTMLPage(String templateName, AOPContentDTO inputData) {
        Context ctx = new Context();
        ctx.setVariable("AOPData", inputData);
        String generatedHtml = templateEngine.process(templateName, ctx);
        return generatedHtml;
    }

    
    protected void generatePDFFromHTMLPage(String htmlFile, String outputFileName) throws IOException {
        htmlToPdfConverter.convertToPDF(htmlFile, outputFileName);
    }
}
