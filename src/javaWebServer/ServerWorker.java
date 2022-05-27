/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaWebServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import javasharingserver.PresentationScreen;
import javax.swing.JTextArea;

/**
 *
 * @author ugur.coruh
 */
public class ServerWorker extends Thread {

    /**
     * Default konfigurasyon dosyası ismi
     */
    public static String CONF_PATH = "paylasim.properties";
    /**
     * Global değişken sunucu portunu tutar
     */
    public static int SERVER_PORT;
    /**
     * Global değişken sunucu kök dizini tutar
     */
    public static String SERVER_ROOT;
    /**
     * Global değişken sunucu anasayfasını tutar
     */
    public static String SERVER_HOMEPAGE;
    /**
     * Global değişken sunucu ile ilgili logların tutulduğu dosya ismi
     */
    public static String SERVER_LOG;
    /**
     * Global değişken dizin listelenip/listelenmemesini belirten flag
     */
    public static boolean LIST_DIR_FLAG;
    /**
     * Global değişken alt dizinlerin listelenip/listlenmemesini belirten flag
     */
    public static boolean LIST_SUB_DIR_FLAG;
    /**
     * Global değişken listelenecek dosya uzantılarını virgül ile ayrılmış olarak tutar
     */
    public static String LIST_EXTENSIONS;
    /**
     * Global değişken listenelenecek klasorleri liste olarak tutar.
     */
    public static String[] FOLDER_FILTER_LIST;
    private Socket _sock;
    private ServerSocket _servSocket;
    private JTextArea _txtLog;
    private JTextArea _txtLogInfo;
    private ServerLogging infoLog;
    private PresentationScreen _presentFrm;
    public boolean isRunning;
    private File _tempIconFolder;


    /**
     * İstemcileri dinleyen sunucu objesini oluşturur ve gelen her istek içinde
     * isteği işleyen Paylaşım objesi oluşturur.
     * @param presentFrm sunumun gösterildiği ekran
     * @param txtLog ekranda logların gösterildiği komponent
     * @param listDir dizin listeleme flag'ı
     * @param extension listelenecek dosyaların uzantısı
     * @param folderFilterList listelenecek klasor listesi
     * @param dirSubDirs alt dizinlerin listelenmesi ile ilgili flag
     */
    public ServerWorker(File tempIconFolder, PresentationScreen presentFrm, JTextArea txtLog, JTextArea txtLogInfo, boolean listDir, String extension, String[] folderFilterList, boolean dirSubDirs) {

        this._txtLog = txtLog;
        this._txtLogInfo = txtLogInfo;
        this._presentFrm = presentFrm;
        this._tempIconFolder = tempIconFolder;
        
        ServerWorker.LIST_DIR_FLAG = listDir;
        ServerWorker.LIST_EXTENSIONS = extension;
        ServerWorker.FOLDER_FILTER_LIST = folderFilterList;
        ServerWorker.LIST_SUB_DIR_FLAG = dirSubDirs;
        infoLog = new ServerLogging("PaylasimLog.txt");
    }

    /**
     * paylasim.properties'de gerekli parametreleri yükler bulamaz ise default
     * parametreleri yükler
     */
    public void LoadServerParams() {

        Properties props = new Properties();
        InputStream is = null;
        String folderFilters;

        // First try loading from the current directory
        try {
            File f = new File("paylasim.properties");
            is = new FileInputStream(f);
        } catch (Exception e) {
            is = null;
        }

        try {
            if (is == null) {
                // Try loading from classpath
                is = getClass().getResourceAsStream("paylasim.properties");
            }
            // Try loading properties from the file (if found)
            props.load(is);
        } catch (Exception e) {

            infoLog.LogMessage(_txtLog, "Configuration Loading Failed Default Params Loaded!!!");
        }

        ServerWorker.SERVER_ROOT = props.getProperty("SERVER_ROOT", "C:\\Paylasim\\htdocs");
        ServerWorker.SERVER_HOMEPAGE = props.getProperty("SERVER_HOMEPAGE", "index.htm");
        ServerWorker.SERVER_LOG = props.getProperty("SERVER_LOG", "C:\\Paylasim\\htdocs");
        ServerWorker.LIST_DIR_FLAG = new Boolean(props.getProperty("LIST_DIR_FLAG", "False"));
        ServerWorker.LIST_SUB_DIR_FLAG = new Boolean(props.getProperty("LIST_SUB_DIR_FLAG", "False"));
        ServerWorker.LIST_EXTENSIONS = props.getProperty("LIST_EXTENSIONS", "");
        ServerWorker.SERVER_LOG = props.getProperty("SERVER_LOG", "C:\\Paylasim\\log\\log.txt");
        ServerWorker.SERVER_PORT = new Integer(props.getProperty("SERVER_PORT", "10080"));
        folderFilters = props.getProperty("FOLDER_FILTER_LIST", "");

        ServerWorker.FOLDER_FILTER_LIST = folderFilters.split(";");

        //check folders
        File rootFolder = new File(SERVER_ROOT);
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }

    }

    /**
     * Değiştirilen sunucu parametrelerini paylasim.properties dosyasına kaydeder.
     */
    public void SaveServerParams() {
        try {
            String folderFilters = "";
            Properties props = new Properties();

            props.setProperty("SERVER_ROOT", ServerWorker.SERVER_ROOT);
            props.setProperty("SERVER_HOMEPAGE", "" + ServerWorker.SERVER_HOMEPAGE);
            props.setProperty("LIST_DIR_FLAG", "" + ServerWorker.LIST_DIR_FLAG);
            props.setProperty("LIST_SUB_DIR_FLAG", "" + ServerWorker.LIST_SUB_DIR_FLAG);
            props.setProperty("LIST_EXTENSIONS", "" + ServerWorker.LIST_EXTENSIONS);
            props.setProperty("SERVER_LOG", "" + ServerWorker.SERVER_LOG);
            props.setProperty("SERVER_PORT", "" + ServerWorker.SERVER_PORT);

            for (int i = 0; i < ServerWorker.FOLDER_FILTER_LIST.length; i++) {

                if (i == 0 && i == ServerWorker.FOLDER_FILTER_LIST.length - 1) {
                    folderFilters += ServerWorker.FOLDER_FILTER_LIST[i];
                } else {
                    folderFilters += ";" + ServerWorker.FOLDER_FILTER_LIST[i];
                }
            }

            props.setProperty("FOLDER_FILTER_LIST", folderFilters);

            File f = new File("paylasim.properties");
            OutputStream out = new FileOutputStream(f);

            props.store(out, "PAYLASIM AYARLARI");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            _servSocket = new ServerSocket(SERVER_PORT);

            try {
                while (true) {

                    _sock = _servSocket.accept();
                    try {
                        infoLog.LogMessage(_txtLog, "[REMOTE HOST]: " + _sock.getInetAddress().toString());
                        infoLog.LogMessage(_txtLog, "[LISTNING ON PORT]: " + _sock.getPort());

                        Paylasim paylasim = new Paylasim(this._tempIconFolder, this._presentFrm, _sock, SERVER_ROOT, SERVER_HOMEPAGE, SERVER_LOG, _txtLog, infoLog, LIST_DIR_FLAG, LIST_EXTENSIONS, FOLDER_FILTER_LIST, LIST_SUB_DIR_FLAG);

                        new Thread(paylasim).start();

                    } catch (IOException e) {

                        if (!_sock.isClosed()) {
                            _sock.close();
                        }
                    }
                }
            } finally {

                if (_servSocket != null) {
                    if (!_servSocket.isClosed()) {
                        _servSocket.close(); //always close the ServerSocket
                    }
                }
            }

        } catch (IOException ex) {

            infoLog.LogMessage(_txtLogInfo, ex.getMessage());
        }

    }

    /**
     * Sunucuyu kapatır
     */
    public void CloseServer() {

        try {


            if (isRunning == false) {
                infoLog.LogMessage(_txtLogInfo, "[SERVER STATE]: ALREADY STOPPED!!!");
            }

            isRunning = false;

            if (_servSocket != null) {
                if (!_servSocket.isClosed()) {
                    this._servSocket.close();
                }
            }

            this._servSocket = null;

            infoLog.LogMessage(_txtLogInfo, "[SERVER STATE]: STOPPED...");

        } catch (IOException ex) {

            infoLog.LogMessage(_txtLogInfo, "[SERVER STATE]: CANNOT STOPPED...");

            infoLog.LogMessage(_txtLog, ex.getMessage());
        }
    }
}

