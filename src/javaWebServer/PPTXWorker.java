/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaWebServer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javasharingserver.PresentationScreen;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 *PPTX dosyasını okuyup resimlere dönüştüren thread'i oluşturan sınıftır
 * @author ugur.coruh
 */
public class PPTXWorker extends Thread {

    private JLabel _panelSlidePage;
    private Image _scaledImage;
    private Toolkit _toolkit;
    private String _pptx_file;
    private String _img_ppt_folder;

/**
 *
 * Aldığı pptx dosyasını belirtilen klasore ayrı ayrı belirtilen ölçekte resimler
 * şeklinde kaydeder.
 * @param scale resim ölçeği
 * @param pptxFile sunum dosyası
 * @param destImgFolder resimlerin tutulacağı klasor
 * @return sunum sayısı
 */
    private int ConvertPPTX2PNG(float scale, String pptxFile, String destImgFolder) {
        {
            FileOutputStream out = null;
            XSLFSlide[] slide = null;

            try {

//                float scale = 4;

                _panelSlidePage.setText("Processing " + pptxFile);
                _panelSlidePage.repaint();

                XMLSlideShow ppt = new XMLSlideShow(OPCPackage.open(pptxFile));
                Dimension pgsize = ppt.getPageSize();
                int width = (int) (pgsize.width * scale);
                int height = (int) (pgsize.height * scale);
                slide = ppt.getSlides();
                for (int i = 0; i < slide.length; i++) {
                    String title = slide[i].getTitle();

                    _panelSlidePage.setText("Rendering slide " + slide.length + "/" + (i + 1) + (title == null ? "" : ": " + title));
                    _panelSlidePage.repaint();

                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = img.createGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    graphics.setColor(Color.white);
                    graphics.clearRect(0, 0, width, height);

                    graphics.scale(scale, scale);
                    slide[i].draw(graphics);
//                    int sep = file.lastIndexOf(".");
//                    String fname = file.substring(0, sep == -1 ? file.length() : sep) + "-" + (i + 1) + ".png";
                    String fname = destImgFolder + "" + (i + 1) + ".png";
                    out = new FileOutputStream(fname);
                    ImageIO.write(img, "png", out);
                    out.close();
                }
                _panelSlidePage.setText("Done");
                _panelSlidePage.repaint();

            } catch (IOException ex) {
                Logger.getLogger(PPTXWorker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(PPTXWorker.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(PPTXWorker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return slide.length;
        }
    }

    /**
     * Sunum işleyen tread objesini oluşturur.
     * @param panelSlidePage sunumun gösterileceği panel
     * @param scaledImage sunum ölçeği
     * @param toolkit resimi almak için gerekli toolkit.
     */
    public PPTXWorker(JLabel panelSlidePage, Image scaledImage, Toolkit toolkit) {

        this._panelSlidePage = panelSlidePage;
        this._scaledImage = scaledImage;
        this._toolkit = toolkit;
        this._pptx_file = PresentationScreen.PPTX_FILE;
        this._img_ppt_folder = PresentationScreen.IMG_PPT_FOLDER;

    }

    /**
     * @deprecated 
     */
    public PPTXWorker() {
    }

    /**
     * Sunum'u işleyen thread için run metodu
     */
    @Override
    public void run() {


        try {
            //clear ppt img directory

            FileUtils.deleteDirectory(new File(this._img_ppt_folder));
            FileUtils.forceMkdir(new File(this._img_ppt_folder));

            //create images
            PresentationScreen.SLIDE_COUNT = ConvertPPTX2PNG(2, this._pptx_file, this._img_ppt_folder);

            PresentationScreen.CURRENT_SLIDE = 1;

            String path = this._img_ppt_folder + "\\" + PresentationScreen.CURRENT_SLIDE + ".png";
            String currPath = this._img_ppt_folder + "\\" + "curr.png";
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            Image image = _toolkit.getImage(path);
            _scaledImage = image.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_DEFAULT);
            _panelSlidePage.setText("");
            _panelSlidePage.setIcon(new ImageIcon(_scaledImage));

            FileUtils.copyFile(new File(path), new File(currPath));

        } catch (IOException ex) {
            Logger.getLogger(PresentationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
