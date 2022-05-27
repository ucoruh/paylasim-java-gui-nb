/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaWebServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javasharingserver.PresentationScreen;
import javax.swing.JTextArea;

/**
 *
 * @author ugur.coruh
 */
public class Paylasim implements Runnable {

    /**
     * Sunucu kök dizin
     */
    private String Server_root = "";
    /**
     * Sunucu anasayfası
     */
    private String Server_Home = "";
    /**
     * İstemcinin bağlı olduğu socket
     */
    private Socket _ServerSocket;
    /**
     * İstek bilgisi String ex: GET /someFile HTTP/1.1
     */
    private String RequestFromClient = "";
    /**
     * İstenen dosya
     */
    private String File_Name = "";
    /**
     * Şu anki dizin
     */
    private String DIR_HTTP = "";
    /**
     * Host ismi
     */
    private String NOM_HOTE = "";

    private int position1 = 0;
    private int position2 = 0;
    private BufferedReader _buffReadin;
    private JTextArea _logArea;
    private boolean _listDir;
    private String _extension;
    private String[] _folderfilterList;
    private boolean _dirSubDirs;
   
    private ServerLogging infoLog;
    private PresentationScreen _presentFrm;
    private File _tempIconFolder;
    
    /**
     *
     * Sunucu objesini oluşturur thread objesidir.
     * Run metodu içerisinde istekleri işler.
     *
     * @param presntFrm
     * @param s
     * @param Sroot
     * @param Shome
     * @param Slog
     * @param logArea
     * @param infoLog
     * @param listDir
     * @param extension
     * @param folderfilterList
     * @param dirSubDirs
     * @throws UnknownHostException
     * @throws IOException
     */
    public Paylasim(File tempIconFolder,PresentationScreen presntFrm, Socket s, String Sroot, String Shome,String Slog, JTextArea logArea,ServerLogging infoLog, boolean listDir, String extension, String[] folderfilterList, boolean dirSubDirs) throws UnknownHostException, IOException {

        try {
           this.infoLog = infoLog;
           this._presentFrm = presntFrm;
           this._tempIconFolder = tempIconFolder;
           
            //initailzing the class Fileds
            Server_root = Sroot;
            Server_Home = Shome;
            
            this._extension = extension;
            this._listDir = listDir;
            this._logArea = logArea;
            this._folderfilterList = folderfilterList;
            this._dirSubDirs = dirSubDirs;
            _ServerSocket = s;
            //Getting the String sent by client
            _buffReadin = new BufferedReader(new InputStreamReader(_ServerSocket.getInputStream()));
            //String to be sent by the server

            NOM_HOTE = InetAddress.getLocalHost().toString();

//            run(); //invoking method run() to execute the thread code

        } catch (IndexOutOfBoundsException ie) {
            //handling Error :Bad Request
            ServerLogging lg = new ServerLogging();
            //call LogEvent Method From Class log
            lg.LogEvent(Slog, "BAD REQUEST", 2);
        }

    }

    /**
     * Verilen istek içerisindeki istek parametrelerini siler
     * @param pth
     * @return
     */
    public String DontCareParams(String pth) {

        if (!pth.contains("?")) {
            return pth;
        }

        String pthNew = "";
        int indx;
        indx = pth.indexOf("?");

        pthNew = pth.substring(0, indx);

        return pthNew;
    }

    /**
     * @deprecated 
     * @param pth
     * @return
     */
    public String DecodeURL(String pth) {
        /* DecodeURL Method used to Decode URL's having space in their body
        ex: /File%20Having%20space.htm = /File Having space.htm
         */
        String pathWithSpace = "";

        //testing if the request file contain %20
        if (pth.indexOf("%20") == -1) {

            return pth; //returning the same file name
        } else {
            StringTokenizer set = new StringTokenizer(pth, "%20");

            int cont = set.countTokens();

            if (!set.hasMoreTokens()) {

                return pth;
            } else {
                try {

                    for (int j = 0; j < cont; j++) {

                        /*we build a string replacing %20 with
                        space as the original File */

                        pathWithSpace += set.nextToken() + " ";
                    }
                } catch (NoSuchElementException ne) {
                }

            }
            return pathWithSpace.substring(0, pathWithSpace.length() - 1);
        }
    }

    /**
     * Gelen isteklerin işlendiği fonksiyon
     */
    public synchronized void run() {

        try {
            //initializing in & out with String received from client and String that will be sent to it
            _buffReadin = new BufferedReader(new InputStreamReader(_ServerSocket.getInputStream()));

            //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);

        } catch (IOException ioe) {

            infoLog.LogMessage(_logArea,"IO Exception Occured..." + ioe.getMessage());
        }

        try {

            //Getting The System File Separator ex : on windows \ ,on linux /
            String FILE_SEPERATOR = System.getProperty("file.separator");

            //Getting the request string sent by client
            RequestFromClient = _buffReadin.readLine();

            position1 = RequestFromClient.indexOf("/") + 1;
            position2 = RequestFromClient.indexOf("HTTP") - 1;

            //file_Name contain the path of requested file
            File_Name = Server_root + FILE_SEPERATOR + RequestFromClient.substring(position1, position2).replace('/', '\\');

            File_Name = this.DontCareParams(File_Name);

            //Create Timeout for long operations
//            if(File_Name.contains("ppt_img\\curr.png"))
//             _ServerSocket.setSoTimeout(20000);

            //Decoding the file
            // File_Name = this.DecodeURL(File_Name);
            File_Name = URLDecoder.decode(File_Name, "UTF-8");

            //Setting the Name of actual Directory
            DIR_HTTP = RequestFromClient.substring(position1, position2).trim();

            infoLog.LogMessage(_logArea,"[CLIENT REQUEST]: " + RequestFromClient);
            infoLog.LogMessage(_logArea,"[REQUESTED FILE]: " + File_Name);
            infoLog.LogMessage(_logArea,"*******************************************************************************");


            File f = new File(File_Name);
            FileOperations fl = new FileOperations();

            fl.setDirectory_Name(DIR_HTTP);
            //testing if the requested file is a directory
            if (f.isDirectory()) {

                if (new File(File_Name + Server_Home).exists()) //if the welcome page exist we will send its content
                {
                    //fl.ReadFile(this._presentFrm, File_Name + Server_Home, NOM_HOTE, soc.getOutputStream(), RequestFromClient.substring(position1, position2),_logArea,infoLog);
                    fl.ReadFile(this._tempIconFolder,this._presentFrm, File_Name + Server_Home, NOM_HOTE, _ServerSocket.getOutputStream(), Server_Home.substring(Server_Home.lastIndexOf(".")+1,Server_Home.length()) ,_logArea,infoLog);
                } else {

                    if (_listDir) {
                        fl.Listdir(File_Name, _ServerSocket.getOutputStream(), NOM_HOTE, _extension, _folderfilterList, _dirSubDirs);
                    } else {
                        fl.DirectoryListingDenied(_ServerSocket.getOutputStream(), NOM_HOTE);//directory listing denied
                    }
                }
                //otherwise we list the content of the directory
            } else {
                //if the requested file is a file we read its content and we sent it to the client almost (browser)
                //fl.ReadFile(this._presentFrm,File_Name, NOM_HOTE, soc.getOutputStream(), RequestFromClient.substring(position1, position2), _logArea,infoLog);
                fl.ReadFile(this._tempIconFolder,this._presentFrm,File_Name, NOM_HOTE, _ServerSocket.getOutputStream(), File_Name.substring(File_Name.lastIndexOf(".")+1,File_Name.length()), _logArea,infoLog);
            }


        } catch (IOException ioe) {

            infoLog.LogMessage(_logArea,ioe.getMessage());

        } finally {
            try {
                _ServerSocket.close(); //we always close the socket to free the bandwidth
            } catch (IOException ioe) {

            infoLog.LogMessage(_logArea,ioe.getMessage());

            }
        }
    }
}//End class ServeurWeb

