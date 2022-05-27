/*
 * JavaSharingServerView.java
 */
package javasharingserver;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import javaWebServer.IPInfo;
import javaWebServer.ServerWorker;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import com.jtechlabs.ui.widget.directorychooser.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import javaWebServer.JarUtils;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

/**
 * The application's main frame.
 */
public class JavaSharingServerView extends FrameView {

    /**Global Istekleri Karsilayan Thread Objesi */
    private ServerWorker _serverThread;
    /**Dizin Listelemede Gösterilecek Klasor Listesi bos ise hepsi gosterilir*/
    private Vector _filteredFolderList;
    /**Secili olan klasor indeksi*/
    private int _selectedFolderIndex;
    /**Sunumun gosterilecegi ikinci Frame objesi */
    private PresentationScreen _presentationScreen;

    private File _tempIconFolder;

    /**Kullanıcının ihtiyac duyacağı ip port ve diğer link bilgilerini gösterip
     * işletim sistemi ile ilgili bilgileride gösterir port yönlendirmesi ise
     * dış ip kullanılacak ise bunuda bilgi için gösterir. Parametre almaz
     */
    public void GeneralEnvorimentInformation() {

        //Getting The System User Connected informations
        String USER_HOME = System.getProperty("user.home");
        String USER_NAME = System.getProperty("user.name");
        String USER_TIMEZONE = System.getProperty("user.timezone");

        //Getting The Operating System informations
        String OS_NAME = System.getProperty("os.name");
        String OS_ARCH = System.getProperty("os.arch");
        String OS_VERS = System.getProperty("os.version");


        txtLogInfo.append("\nPAYLAŞIM SUNUCUSU 1.0 \n");

        txtLogInfo.append("[PLATFORM]\n");
        txtLogInfo.append("İŞLETİM SİSTEMİ=" + OS_NAME + "\n");
        txtLogInfo.append("VERSİYON=" + OS_VERS + "\n");
        txtLogInfo.append("ARCH=" + OS_ARCH + "\n");
        txtLogInfo.append("\n");

        txtLogInfo.append("[KULLANICI]");
        txtLogInfo.append("İSİM=" + USER_NAME + "\n");
        txtLogInfo.append("HOME_DIRECTORY=" + USER_HOME + "\n");
        txtLogInfo.append("TIMEZONE=" + USER_TIMEZONE + "\n");

        txtLogInfo.append("\n");

        txtLogInfo.append("[SUNUCU PORT]: " + ServerWorker.SERVER_PORT + "\n");
        txtLogInfo.append("[SUNUCU ANASAYFA]: " + ServerWorker.SERVER_HOMEPAGE + "\n");
        txtLogInfo.append("[SUNUCU KÖK DİZİN]: " + ServerWorker.SERVER_ROOT + "\n");
        txtLogInfo.append("[SUNUCU HATA LOG]: " + ServerWorker.SERVER_LOG + "\n");

        txtLogInfo.append("[YEREL IP]: " + IPInfo.GetLocalIPs() + "\n");
        txtLogInfo.append("[GERÇEK IP]: " + IPInfo.GetRealIP() + "\n");

        txtLogInfo.append("[SUNUM ADRESİ ]: http://<IP>:" + ServerWorker.SERVER_PORT + "/sunum \n");
        txtLogInfo.append("[KONTROL ADRESİ ]: http://<IP>:" + ServerWorker.SERVER_PORT + "/cont \n");

        txtLogInfo.append("[SERVER STATE]: STARTED...\n");
        txtLogInfo.append("\n*******************************************************************************\n");
    }

    /**
     * paylasim.properties property dosyasından gerekli konfigurasyonları yükler
     * eğer konfigurasyon dosyası yok ise default parametreleri yükler ve ekrandaki
     * bileşenleri doldurur.
     */
    public void GetConfigurationInformation() {

        try {

            _serverThread.LoadServerParams();

            txtCfgPath.setText(ServerWorker.CONF_PATH);
            txtServRootPath.setText(ServerWorker.SERVER_ROOT);
            txtLogFilePath.setText(ServerWorker.SERVER_LOG);
            txtServHomePage.setText(ServerWorker.SERVER_HOMEPAGE);
            txtServPort.setText("" + ServerWorker.SERVER_PORT);
            chkBoxDirList.setSelected(ServerWorker.LIST_DIR_FLAG);

            if (ServerWorker.LIST_DIR_FLAG) {

                txtExtensionList.setEditable(false);
                listFolderFilter.setEnabled(false);
                btnAddFolderFilt.setEnabled(false);
                btnRemoveFolderFilt.setEnabled(false);
                chkBoxDirSubFolders.setEnabled(false);

            } else {

                txtExtensionList.setEditable(true);
                listFolderFilter.setEnabled(true);
                btnAddFolderFilt.setEnabled(true);
                btnRemoveFolderFilt.setEnabled(true);
                chkBoxDirSubFolders.setEnabled(true);

            }

            chkBoxDirSubFolders.setSelected(ServerWorker.LIST_SUB_DIR_FLAG);

            txtExtensionList.setText(ServerWorker.LIST_EXTENSIONS);

            _filteredFolderList.clear();

            for (int i = 0; i < ServerWorker.FOLDER_FILTER_LIST.length; i++) {
                if (!ServerWorker.FOLDER_FILTER_LIST[i].equals("")) {
                    _filteredFolderList.add(ServerWorker.FOLDER_FILTER_LIST[i]);
                }
            }

            listFolderFilter.setListData(_filteredFolderList);

        } catch (Exception ex) {

            txtLog.append("[CONFIGURATION FILE NOT FOUND]!!!\n");
            //Logger.getLogger(ServThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Konfigurasyon dosyasını okuyup bununla ilgili sunucu için gerekli thread
     * ayağa kaldırıp bilgileri ekranda gösterir.
     */
    public void StartServer() {

        if (_serverThread != null) {
            if (_serverThread.isRunning == true) {
                txtLogInfo.append("SERVER ALREADY RUNNING!!!");
                return;
            }
        }

        String[] folderList = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

        _serverThread = new ServerWorker(this._tempIconFolder,this._presentationScreen, txtLog, txtLogInfo, chkBoxDirList.isSelected(), txtExtensionList.getText(), folderList, chkBoxDirSubFolders.isSelected());

        GetConfigurationInformation();

        if (!_serverThread.isAlive()) {
            _serverThread.start();
        }

        _serverThread.isRunning = true;



        GeneralEnvorimentInformation();
    }

    /**
     * Sunucuyu durdurur
     */
    public void StopServer() {

        if (_serverThread.isAlive()) {

            _serverThread.CloseServer();

            _serverThread.stop();
        }

    }

    /**
     * Sunucunu çalışıp çalışmadığını thread durumundan kontrol eder çalışıyorsa
     * true çalışmıyorsa false döner
     * @return true/çalışıyor false/çalışmıyor
     */
    public boolean isServerRunning() {

        if (_serverThread.isAlive()) {
            return true;
        } else {
            return false;
        }
    }

    public void ExportJar(String exportFolder, String jarFolder) {

        try {


            File expFolder = new File(exportFolder);
            if (!expFolder.exists()) {
                expFolder = new File(exportFolder);
            }

            expFolder.mkdirs();

            BufferedImage imageBuffer = ImageIO.read(getClass().getResourceAsStream("/img/css.png"));
            File outputfile = new File(exportFolder + File.pathSeparator + "css.png");
            ImageIO.write(imageBuffer, "png", outputfile);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Ana Frame Contructor. Sunucu objesi burada oluşturulur.
     * @param app
     */
    public JavaSharingServerView(SingleFrameApplication app) {
        super(app);

        JarFile currentJar = null;
        try {

            String tempDirPath = System.getProperty("java.io.tmpdir");

            _tempIconFolder  = new File(tempDirPath + File.separator + "PaylasimIcon");
            _tempIconFolder.mkdirs();

            String jarPath = JavaSharingServerView.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            currentJar = new JarFile(jarPath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        try {
            JarUtils.copyResourcesToDirectory(currentJar, "img", _tempIconFolder.getPath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        _filteredFolderList = new Vector();

        String[] folderList = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

        initComponents();

        _serverThread = new ServerWorker(this._tempIconFolder, this._presentationScreen, txtLog, txtLogInfo, chkBoxDirList.isSelected(), txtExtensionList.getText(), folderList, chkBoxDirSubFolders.isSelected());

        GetConfigurationInformation();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    /**
     * Hakkında kutusunu gösterir
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JavaSharingServerApp.getApplication().getMainFrame();
            aboutBox = new JavaSharingServerAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JavaSharingServerApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelMon = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtLogInfo = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        panelConf = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        chkBoxDirList = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        txtExtensionList = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        listFolderFilter = new javax.swing.JList();
        jLabel8 = new javax.swing.JLabel();
        btnAddFolderFilt = new javax.swing.JButton();
        btnRemoveFolderFilt = new javax.swing.JButton();
        chkBoxDirSubFolders = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCfgPath = new javax.swing.JTextField();
        txtServPort = new javax.swing.JTextField();
        txtServRootPath = new javax.swing.JTextField();
        txtServHomePage = new javax.swing.JTextField();
        txtLogFilePath = new javax.swing.JTextField();
        btnSetRoot = new javax.swing.JButton();
        btnSetLogFile = new javax.swing.JButton();
        panelDoc = new javax.swing.JPanel();
        btnOpenPPTX = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        mnStartServer = new javax.swing.JMenuItem();
        mnStopServer = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        panelMon.setName("panelMon"); // NOI18N

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(javasharingserver.JavaSharingServerApp.class).getContext().getResourceMap(JavaSharingServerView.class);
        txtLogInfo.setBackground(resourceMap.getColor("txtLogInfo.background")); // NOI18N
        txtLogInfo.setColumns(20);
        txtLogInfo.setEditable(false);
        txtLogInfo.setFont(resourceMap.getFont("txtLogInfo.font")); // NOI18N
        txtLogInfo.setRows(5);
        txtLogInfo.setName("txtLogInfo"); // NOI18N
        jScrollPane3.setViewportView(txtLogInfo);

        jSplitPane1.setTopComponent(jScrollPane3);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtLog.setBackground(resourceMap.getColor("txtLog.background")); // NOI18N
        txtLog.setColumns(20);
        txtLog.setEditable(false);
        txtLog.setFont(resourceMap.getFont("txtLog.font")); // NOI18N
        txtLog.setRows(5);
        txtLog.setName("txtLog"); // NOI18N
        jScrollPane1.setViewportView(txtLog);

        jSplitPane1.setRightComponent(jScrollPane1);

        javax.swing.GroupLayout panelMonLayout = new javax.swing.GroupLayout(panelMon);
        panelMon.setLayout(panelMonLayout);
        panelMonLayout.setHorizontalGroup(
            panelMonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
        );
        panelMonLayout.setVerticalGroup(
            panelMonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("panelMon.TabConstraints.tabTitle"), panelMon); // NOI18N

        panelConf.setName("panelConf"); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        chkBoxDirList.setText(resourceMap.getString("chkBoxDirList.text")); // NOI18N
        chkBoxDirList.setName("chkBoxDirList"); // NOI18N
        chkBoxDirList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxDirListActionPerformed(evt);
            }
        });

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtExtensionList.setBackground(resourceMap.getColor("txtExtensionList.background")); // NOI18N
        txtExtensionList.setText(resourceMap.getString("txtExtensionList.text")); // NOI18N
        txtExtensionList.setName("txtExtensionList"); // NOI18N
        txtExtensionList.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtExtensionListCaretUpdate(evt);
            }
        });
        txtExtensionList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtExtensionListActionPerformed(evt);
            }
        });
        txtExtensionList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtExtensionListFocusLost(evt);
            }
        });

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        listFolderFilter.setName("listFolderFilter"); // NOI18N
        listFolderFilter.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFolderFilterValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listFolderFilter);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        btnAddFolderFilt.setText(resourceMap.getString("btnAddFolderFilt.text")); // NOI18N
        btnAddFolderFilt.setName("btnAddFolderFilt"); // NOI18N
        btnAddFolderFilt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFolderFiltActionPerformed(evt);
            }
        });

        btnRemoveFolderFilt.setText(resourceMap.getString("btnRemoveFolderFilt.text")); // NOI18N
        btnRemoveFolderFilt.setName("btnRemoveFolderFilt"); // NOI18N
        btnRemoveFolderFilt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveFolderFiltActionPerformed(evt);
            }
        });

        chkBoxDirSubFolders.setText(resourceMap.getString("chkBoxDirSubFolders.text")); // NOI18N
        chkBoxDirSubFolders.setName("chkBoxDirSubFolders"); // NOI18N
        chkBoxDirSubFolders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBoxDirSubFoldersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddFolderFilt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemoveFolderFilt, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkBoxDirList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtExtensionList, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkBoxDirSubFolders))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkBoxDirList, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBoxDirSubFolders))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtExtensionList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnRemoveFolderFilt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddFolderFilt))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtCfgPath.setEditable(false);
        txtCfgPath.setText(resourceMap.getString("txtCfgPath.text")); // NOI18N
        txtCfgPath.setName("txtCfgPath"); // NOI18N

        txtServPort.setText(resourceMap.getString("txtServPort.text")); // NOI18N
        txtServPort.setName("txtServPort"); // NOI18N
        txtServPort.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtServPortCaretUpdate(evt);
            }
        });
        txtServPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServPortActionPerformed(evt);
            }
        });
        txtServPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServPortFocusLost(evt);
            }
        });

        txtServRootPath.setEditable(false);
        txtServRootPath.setText(resourceMap.getString("txtServRootPath.text")); // NOI18N
        txtServRootPath.setName("txtServRootPath"); // NOI18N
        txtServRootPath.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtServRootPathCaretUpdate(evt);
            }
        });
        txtServRootPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServRootPathActionPerformed(evt);
            }
        });

        txtServHomePage.setText(resourceMap.getString("txtServHomePage.text")); // NOI18N
        txtServHomePage.setName("txtServHomePage"); // NOI18N
        txtServHomePage.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtServHomePageCaretUpdate(evt);
            }
        });
        txtServHomePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServHomePageActionPerformed(evt);
            }
        });
        txtServHomePage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServHomePageFocusLost(evt);
            }
        });

        txtLogFilePath.setEditable(false);
        txtLogFilePath.setText(resourceMap.getString("txtLogFilePath.text")); // NOI18N
        txtLogFilePath.setName("txtLogFilePath"); // NOI18N
        txtLogFilePath.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtLogFilePathCaretUpdate(evt);
            }
        });
        txtLogFilePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLogFilePathActionPerformed(evt);
            }
        });

        btnSetRoot.setText(resourceMap.getString("btnSetRoot.text")); // NOI18N
        btnSetRoot.setName("btnSetRoot"); // NOI18N
        btnSetRoot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetRootActionPerformed(evt);
            }
        });

        btnSetLogFile.setText(resourceMap.getString("btnSetLogFile.text")); // NOI18N
        btnSetLogFile.setName("btnSetLogFile"); // NOI18N
        btnSetLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetLogFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                                    .addComponent(txtCfgPath, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                    .addComponent(txtServPort, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                                    .addComponent(txtServRootPath, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                                    .addComponent(txtServHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtLogFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(279, 279, 279)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSetRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSetLogFile, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(298, 298, 298))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCfgPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtServPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtServHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServRootPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btnSetRoot))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(btnSetLogFile))
                    .addComponent(txtLogFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelConfLayout = new javax.swing.GroupLayout(panelConf);
        panelConf.setLayout(panelConfLayout);
        panelConfLayout.setHorizontalGroup(
            panelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelConfLayout.setVerticalGroup(
            panelConfLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelConfLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jTabbedPane1.addTab(resourceMap.getString("panelConf.TabConstraints.tabTitle"), panelConf); // NOI18N

        panelDoc.setName("panelDoc"); // NOI18N

        btnOpenPPTX.setFont(resourceMap.getFont("btnOpenPPTX.font")); // NOI18N
        btnOpenPPTX.setText(resourceMap.getString("btnOpenPPTX.text")); // NOI18N
        btnOpenPPTX.setName("btnOpenPPTX"); // NOI18N
        btnOpenPPTX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenPPTXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDocLayout = new javax.swing.GroupLayout(panelDoc);
        panelDoc.setLayout(panelDocLayout);
        panelDocLayout.setHorizontalGroup(
            panelDocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDocLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(btnOpenPPTX, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                .addGap(112, 112, 112))
        );
        panelDocLayout.setVerticalGroup(
            panelDocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDocLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnOpenPPTX, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(371, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("panelDoc.TabConstraints.tabTitle"), panelDoc); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(javasharingserver.JavaSharingServerApp.class).getContext().getActionMap(JavaSharingServerView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        mnStartServer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnStartServer.setText(resourceMap.getString("mnStartServer.text")); // NOI18N
        mnStartServer.setName("mnStartServer"); // NOI18N
        mnStartServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnStartServerMouseClicked(evt);
            }
        });
        mnStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnStartServerActionPerformed(evt);
            }
        });
        fileMenu.add(mnStartServer);

        mnStopServer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnStopServer.setText(resourceMap.getString("mnStopServer.text")); // NOI18N
        mnStopServer.setName("mnStopServer"); // NOI18N
        mnStopServer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mnStopServerMouseClicked(evt);
            }
        });
        mnStopServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnStopServerActionPerformed(evt);
            }
        });
        fileMenu.add(mnStopServer);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 603, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void mnStartServerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnStartServerMouseClicked
        //Start Server..
        // TODO add your handling code here:
    }//GEN-LAST:event_mnStartServerMouseClicked

    private void mnStopServerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnStopServerMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_mnStopServerMouseClicked

    private void mnStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnStartServerActionPerformed

        try {

            txtExtensionList.setEditable(false);
            listFolderFilter.setEnabled(false);
            btnAddFolderFilt.setEnabled(false);
            btnRemoveFolderFilt.setEnabled(false);
            chkBoxDirSubFolders.setEnabled(false);
            StartServer();

        } catch (Exception ex) {

            txtLog.append(ex.getMessage());
            txtLog.append("" + ex.getCause());

        }
        // TODO add your handling code here:
    }//GEN-LAST:event_mnStartServerActionPerformed

    private void mnStopServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnStopServerActionPerformed

        try {

            txtExtensionList.setEditable(true);
            listFolderFilter.setEnabled(true);
            btnAddFolderFilt.setEnabled(true);
            btnRemoveFolderFilt.setEnabled(true);
            chkBoxDirSubFolders.setEnabled(true);

            ServerWorker.SERVER_ROOT = txtServRootPath.getText();
            ServerWorker.SERVER_LOG = txtLogFilePath.getText();
            ServerWorker.SERVER_HOMEPAGE = txtServHomePage.getText();
            ServerWorker.SERVER_PORT = new Integer(txtServPort.getText());

            ServerWorker.LIST_DIR_FLAG = chkBoxDirList.isSelected();
            ServerWorker.LIST_SUB_DIR_FLAG = chkBoxDirSubFolders.isSelected();

            ServerWorker.LIST_EXTENSIONS = txtExtensionList.getText();

            ServerWorker.FOLDER_FILTER_LIST = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

            _serverThread.SaveServerParams();

            StopServer();

        } catch (Exception ex) {

            txtLog.append("[ERROR]!!!\n" + ex.getMessage());
        }


        // TODO add your handling code here:
    }//GEN-LAST:event_mnStopServerActionPerformed

    private void chkBoxDirListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxDirListActionPerformed

        try {

            if (chkBoxDirList.isSelected()) {

                ServerWorker.LIST_DIR_FLAG = true;

                txtExtensionList.setEditable(false);
                listFolderFilter.setEnabled(false);
                btnAddFolderFilt.setEnabled(false);
                btnRemoveFolderFilt.setEnabled(false);
                chkBoxDirSubFolders.setEnabled(false);

            } else {

                ServerWorker.LIST_DIR_FLAG = false;

                txtExtensionList.setEditable(true);
                listFolderFilter.setEnabled(true);
                btnAddFolderFilt.setEnabled(true);
                btnRemoveFolderFilt.setEnabled(true);
                chkBoxDirSubFolders.setEnabled(true);
            }

            ServerWorker.SERVER_ROOT = txtServRootPath.getText();
            ServerWorker.SERVER_LOG = txtLogFilePath.getText();
            ServerWorker.SERVER_HOMEPAGE = txtServHomePage.getText();
            ServerWorker.SERVER_PORT = new Integer(txtServPort.getText());

            ServerWorker.LIST_DIR_FLAG = chkBoxDirList.isSelected();
            ServerWorker.LIST_SUB_DIR_FLAG = chkBoxDirSubFolders.isSelected();

            ServerWorker.LIST_EXTENSIONS = txtExtensionList.getText();

            ServerWorker.FOLDER_FILTER_LIST = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }

        } catch (Exception ex) {

            txtLog.append("[ERROR]!!!\n" + ex.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_chkBoxDirListActionPerformed

    private void btnOpenPPTXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenPPTXActionPerformed


        File f = FileSystemView.getFileSystemView().getHomeDirectory();
        JFileChooser fileChoose = new JFileChooser(f);

        int result = fileChoose.showOpenDialog(btnOpenPPTX);

        if (result == JFileChooser.APPROVE_OPTION) {
            f = fileChoose.getSelectedFile();


            File folderImg = new File(ServerWorker.SERVER_ROOT + "\\ppt_img\\");
            if (!folderImg.exists()) {
                folderImg.mkdirs();
            }

            _presentationScreen = new PresentationScreen(f.getPath(), ServerWorker.SERVER_ROOT + "\\ppt_img\\");
            _presentationScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            _presentationScreen.setBounds(0, 0, screenSize.width, screenSize.height - 50);

            _presentationScreen.show();
        }

        StopServer();
        StartServer();

    }//GEN-LAST:event_btnOpenPPTXActionPerformed

    private void btnAddFolderFiltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFolderFiltActionPerformed

        File f = FileSystemView.getFileSystemView().getHomeDirectory();

        if ((f = JDirectoryChooser.showDialog(this.btnAddFolderFilt, new File(ServerWorker.SERVER_ROOT))) != null) {

            _filteredFolderList.add(f.getPath());
            listFolderFilter.setListData(_filteredFolderList);

            ServerWorker.FOLDER_FILTER_LIST = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

            _serverThread.SaveServerParams();


            if (isServerRunning()) {
                StopServer();
                StartServer();
            }

        }

    }//GEN-LAST:event_btnAddFolderFiltActionPerformed

    private void btnRemoveFolderFiltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveFolderFiltActionPerformed

        if (_selectedFolderIndex != -1) {

            _filteredFolderList.remove(_selectedFolderIndex);
            listFolderFilter.setListData(_filteredFolderList);

            ServerWorker.FOLDER_FILTER_LIST = (String[]) _filteredFolderList.toArray(new String[_filteredFolderList.size()]);

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }

        }

    }//GEN-LAST:event_btnRemoveFolderFiltActionPerformed

    private void listFolderFilterValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFolderFilterValueChanged

        _selectedFolderIndex = evt.getFirstIndex();

    }//GEN-LAST:event_listFolderFilterValueChanged

    private void chkBoxDirSubFoldersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxDirSubFoldersActionPerformed

        if (chkBoxDirSubFolders.isSelected()) {
            ServerWorker.LIST_SUB_DIR_FLAG = true;
        } else {
            ServerWorker.LIST_SUB_DIR_FLAG = false;
        }

    }//GEN-LAST:event_chkBoxDirSubFoldersActionPerformed

    private void txtServPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServPortActionPerformed
    }//GEN-LAST:event_txtServPortActionPerformed

    private void txtServRootPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServRootPathActionPerformed
    }//GEN-LAST:event_txtServRootPathActionPerformed

    private void txtServHomePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServHomePageActionPerformed
    }//GEN-LAST:event_txtServHomePageActionPerformed

    private void txtLogFilePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLogFilePathActionPerformed
    }//GEN-LAST:event_txtLogFilePathActionPerformed

    private void txtExtensionListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtExtensionListActionPerformed
    }//GEN-LAST:event_txtExtensionListActionPerformed

    private void btnSetRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetRootActionPerformed

        File f = FileSystemView.getFileSystemView().getHomeDirectory();

        if ((f = JDirectoryChooser.showDialog(this.btnAddFolderFilt, new File(ServerWorker.SERVER_ROOT))) != null) {

            txtServRootPath.setText(f.getPath());

            ServerWorker.SERVER_ROOT = txtServRootPath.getText();

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }


        }
// TODO add your handling code here:
    }//GEN-LAST:event_btnSetRootActionPerformed

    private void btnSetLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetLogFileActionPerformed

        JFileChooser fChoose = new JFileChooser();

        int result = fChoose.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            txtLogFilePath.setText(fChoose.getSelectedFile().getPath());

            ServerWorker.SERVER_LOG = txtLogFilePath.getText();

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }
        }

// TODO add your handling code here:
    }//GEN-LAST:event_btnSetLogFileActionPerformed

    private void txtServPortCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtServPortCaretUpdate
    }//GEN-LAST:event_txtServPortCaretUpdate

    private void txtServRootPathCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtServRootPathCaretUpdate
    }//GEN-LAST:event_txtServRootPathCaretUpdate

    private void txtServHomePageCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtServHomePageCaretUpdate
    }//GEN-LAST:event_txtServHomePageCaretUpdate

    private void txtLogFilePathCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtLogFilePathCaretUpdate
    }//GEN-LAST:event_txtLogFilePathCaretUpdate

    private void txtExtensionListCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtExtensionListCaretUpdate
    }//GEN-LAST:event_txtExtensionListCaretUpdate

    private void txtServPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServPortFocusLost


        if (!txtServPort.getText().equals("") && ServerWorker.SERVER_PORT != Integer.parseInt(txtServPort.getText())) {

            ServerWorker.SERVER_PORT = new Integer(txtServPort.getText());

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }

        }

        // TODO add your handling code here:
    }//GEN-LAST:event_txtServPortFocusLost

    private void txtServHomePageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServHomePageFocusLost

        if (!ServerWorker.SERVER_HOMEPAGE.equals(txtServHomePage.getText())) {

            ServerWorker.SERVER_HOMEPAGE = txtServHomePage.getText();

            _serverThread.SaveServerParams();

            if (isServerRunning()) {
                StopServer();
                StartServer();
            }
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_txtServHomePageFocusLost

    private void txtExtensionListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtExtensionListFocusLost

        if (!ServerWorker.LIST_EXTENSIONS.equals(txtExtensionList.getText())) {
            ServerWorker.LIST_EXTENSIONS = txtExtensionList.getText();
            _serverThread.SaveServerParams();
            if (isServerRunning()) {
                StopServer();
                StartServer();
            }

        }


        // TODO add your handling code here:
    }//GEN-LAST:event_txtExtensionListFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddFolderFilt;
    private javax.swing.JButton btnOpenPPTX;
    private javax.swing.JButton btnRemoveFolderFilt;
    private javax.swing.JButton btnSetLogFile;
    private javax.swing.JButton btnSetRoot;
    private javax.swing.JCheckBox chkBoxDirList;
    private javax.swing.JCheckBox chkBoxDirSubFolders;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList listFolderFilter;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnStartServer;
    private javax.swing.JMenuItem mnStopServer;
    private javax.swing.JPanel panelConf;
    private javax.swing.JPanel panelDoc;
    private javax.swing.JPanel panelMon;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField txtCfgPath;
    private javax.swing.JTextField txtExtensionList;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtLogFilePath;
    private javax.swing.JTextArea txtLogInfo;
    private javax.swing.JTextField txtServHomePage;
    private javax.swing.JTextField txtServPort;
    private javax.swing.JTextField txtServRootPath;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
