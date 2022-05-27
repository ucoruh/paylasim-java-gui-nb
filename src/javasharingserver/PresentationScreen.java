/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PresentationFrm.java
 *
 * Created on 30.Ara.2012, 05:26:03
 */
package javasharingserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import java.awt.Image;
import java.awt.Toolkit;
import javaWebServer.PPTXWorker;
import javax.swing.ImageIcon;

/**
 *
 * @author ugur.coruh
 */
public class PresentationScreen extends javax.swing.JFrame {

    /**
     * Ekranda gösterilen Slide Numarasını Gösterir
     */
    public static long TIME_INTERVAL = 3000;

    public static long LASTUPDATE;

    public static int CURRENT_SLIDE;
    /**
     * Kaçtane slide olduğunu gösterir
     */
    public static int SLIDE_COUNT;
    /**
     * Açılan sunum dosyasının yolunu tutar.
     */
    public static String PPTX_FILE;
    /**
     * Sunumun dönüştürüldüğü resimlerin kaydedildiği klasor
     */
    public static String IMG_PPT_FOLDER;
    /**
     * Resim almak için gerekli Toolkit
     */
    private Toolkit _toolkit;
    /**
     * Ekranda gösterilen Resmi tutan değişken
     */
    private Image _scaledImage;
    /**
     * Slide'ları işleyip resimleri oluşturan thread objesi
     */
    private PPTXWorker _pptxConverter;

    /**
     * Bir sonraki sunum'u gösterip istemcilerin bir sonraki slaytı görmeleri
     * için gerekli işlemleri yapar
     */
    public void NextSlide() {

        try {


            //Right arrow key code
            PresentationScreen.CURRENT_SLIDE++;
            if (PresentationScreen.CURRENT_SLIDE > PresentationScreen.SLIDE_COUNT) {
                PresentationScreen.CURRENT_SLIDE = PresentationScreen.SLIDE_COUNT;
            }
            String path = IMG_PPT_FOLDER + "\\" + PresentationScreen.CURRENT_SLIDE + ".png";
            String currPath = IMG_PPT_FOLDER + "\\" + "curr.png";
            // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            _toolkit = getToolkit();
            Image image = _toolkit.getImage(path);

            _scaledImage = image.getScaledInstance(panelSlidePage.getWidth(), panelSlidePage.getHeight(), Image.SCALE_DEFAULT);
            panelSlidePage.setText("");
            panelSlidePage.setIcon(new ImageIcon(_scaledImage));
            panelSlidePage.repaint();

            FileUtils.copyFile(new File(path), new File(currPath));

        } catch (IOException ex) {
            Logger.getLogger(PresentationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Bir önceki sunum sayfasını gösterip istemciler için önceki slide resmini
     * ayarlar
     */
    public void PrevSlide() {

        try {
            //Left arrow key code
            PresentationScreen.CURRENT_SLIDE--;
            if (PresentationScreen.CURRENT_SLIDE < 1) {
                PresentationScreen.CURRENT_SLIDE = 1;
            }
            String path = IMG_PPT_FOLDER + "\\" + PresentationScreen.CURRENT_SLIDE + ".png";
            String currPath = IMG_PPT_FOLDER + "\\" + "curr.png";
//                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            _toolkit = getToolkit();
            Image image = _toolkit.getImage(path);
            _scaledImage = image.getScaledInstance(panelSlidePage.getWidth(), panelSlidePage.getHeight(), Image.SCALE_DEFAULT);
            panelSlidePage.setText("");
            panelSlidePage.setIcon(new ImageIcon(_scaledImage));
            panelSlidePage.repaint();

            FileUtils.copyFile(new File(path), new File(currPath));

        } catch (IOException ex) {
            Logger.getLogger(PresentationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sunumu bitirip resimleri siler ve ekranı kapatır.
     */
    public void EndSlide() {

        try {
            FileUtils.deleteDirectory(new File(PresentationScreen.IMG_PPT_FOLDER));
            FileUtils.forceMkdir(new File(PresentationScreen.IMG_PPT_FOLDER));

            this.dispose();

        } catch (IOException ex) {
            Logger.getLogger(PresentationScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**@deprecated  Kullanılmıyor */
    public PresentationScreen() {
        initComponents();
    }

    /**
     * pptx den resimleri oluşturan thread çalıştırıp resimler oluşturulur ve
     * sunum formu gösterilip işlemler gösterilir ve işlem bittiğinde ilk resim
     * sunum için gösterilip hazır hale gelir
     *
     * @param pptPath    sunum dosya yolu
     * @param pptImgPath sunumun işlenip resimlerinin kayedileceği dosya yolu
     */
    public PresentationScreen(String pptPath, String pptImgPath) {


        PresentationScreen.PPTX_FILE = pptPath;
        PresentationScreen.IMG_PPT_FOLDER = pptImgPath;

        initComponents();

        _toolkit = getToolkit();

        _pptxConverter = new PPTXWorker(panelSlidePage, _scaledImage, _toolkit);

        _pptxConverter.start();

        panelSlidePage.setText("YÜKLENİYOR...");

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSlidePage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(javasharingserver.JavaSharingServerApp.class).getContext().getResourceMap(PresentationScreen.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        panelSlidePage.setBackground(resourceMap.getColor("panelSlidePage.background")); // NOI18N
        panelSlidePage.setFont(resourceMap.getFont("panelSlidePage.font")); // NOI18N
        panelSlidePage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelSlidePage.setText(resourceMap.getString("panelSlidePage.text")); // NOI18N
        panelSlidePage.setName("panelSlidePage"); // NOI18N
        panelSlidePage.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelSlidePageComponentResized(evt);
            }
        });
        panelSlidePage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panelSlidePageKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                panelSlidePageKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSlidePage, javax.swing.GroupLayout.DEFAULT_SIZE, 842, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSlidePage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped

        if (evt.getKeyChar() == 'x' || evt.getKeyChar() == 'X') {
            NextSlide();
        } else if (evt.getKeyChar() == 'z' || evt.getKeyChar() == 'Z') {
            PrevSlide();
        } else if (evt.getKeyChar() == 'q' || evt.getKeyChar() == 'Q') {
            EndSlide();
        }

        // TODO add your handling code here:

    }//GEN-LAST:event_formKeyTyped

    private void panelSlidePageKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panelSlidePageKeyTyped
    }//GEN-LAST:event_panelSlidePageKeyTyped

    private void panelSlidePageKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panelSlidePageKeyPressed
    }//GEN-LAST:event_panelSlidePageKeyPressed

    private void panelSlidePageComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelSlidePageComponentResized
        // TODO add your handling code here:
    }//GEN-LAST:event_panelSlidePageComponentResized

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        this.panelSlidePage.setBounds(evt.getComponent().getBounds());

        if (!_pptxConverter.isAlive()) {
            String path = IMG_PPT_FOLDER + "\\" + PresentationScreen.CURRENT_SLIDE + ".png";

            _toolkit = getToolkit();
            Image image = _toolkit.getImage(path);

            _scaledImage = image.getScaledInstance(evt.getComponent().getWidth(), evt.getComponent().getHeight(), Image.SCALE_DEFAULT);

            panelSlidePage.setText("");
            panelSlidePage.setIcon(new ImageIcon(_scaledImage));
            panelSlidePage.repaint();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentResized

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PresentationScreen().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel panelSlidePage;
    // End of variables declaration//GEN-END:variables
}
