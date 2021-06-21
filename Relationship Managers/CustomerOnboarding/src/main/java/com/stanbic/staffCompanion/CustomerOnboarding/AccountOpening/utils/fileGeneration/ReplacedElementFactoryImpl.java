package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.fileGeneration;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/** This replaces images within an HTML file instances of ImageElement.
 * This is done so the images can be rendered properly to PDF.
 *
 * Source: https://www.netjstech.com/2021/02/html-to-pdf-java-flying-saucer-openpdf.html
 *
 * **/
@Component
public class ReplacedElementFactoryImpl implements ReplacedElementFactory {

    private Logger log = LoggerFactory.getLogger(ReplacedElementFactoryImpl.class);

    @Override
    public ReplacedElement createReplacedElement(LayoutContext layoutContext,
                                                 BlockBox blockBox,
                                                 UserAgentCallback userAgentCallback,
                                                 int cssWidth, int cssHeight) {
        Element e = blockBox.getElement();
        if (e == null) {
            return null;
        }
        String nodeName = e.getNodeName();

        // Look for img tag in the HTML
        if (nodeName.equals("img")) {
            String imagePath = e.getAttribute("src");
            log.info("imagePath-- " + imagePath.substring(imagePath.indexOf("/") + 1));
            FSImage fsImage;
            try {
                fsImage = getImageInstance(imagePath);
            } catch (BadElementException e1) {
                fsImage = null;
            } catch (IOException e1) {
                fsImage = null;
            }
            if (fsImage != null) {
                if (cssWidth != -1 || cssHeight != -1) {
                    fsImage.scale(cssWidth, cssHeight);
                }else {
                    fsImage.scale(250, 150);
                }
                return new ITextImageElement(fsImage);
            }
        }
        return null;
    }

    private FSImage getImageInstance(String imagePath) throws IOException, BadElementException, FileNotFoundException {
        InputStream input = null;
        FSImage fsImage;
        // Removing "../" from image path like "../images/ExceptionPropagation.png"
        input = new FileInputStream(getClass().getClassLoader().getResource(
                imagePath.substring(imagePath.indexOf("/") + 1)).getFile());
        final byte[] bytes = IOUtils.toByteArray(input);
        final Image image = Image.getInstance(bytes);
        fsImage = new ITextFSImage(image);
        return fsImage;
    }

    @Override
    public void reset() {}

    @Override
    public void remove(Element element) {}

    @Override
    public void setFormSubmissionListener(FormSubmissionListener formSubmissionListener) {}
}
