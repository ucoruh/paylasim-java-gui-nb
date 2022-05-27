package javaWebServer;

import java.io.*;
import java.util.*;
import java.util.Date;
import javasharingserver.PresentationScreen;
import javax.swing.JTextArea;

/**
 * Dosya okuyup HTML işlemlerinin olduğu sınıftır.
 *@version 1.0
 *@author ugur.coruh
 */
public class FileOperations {

    private String DirIcon = "dir.png";
    /**
     * Okunan byte sayısı
     */
    private int _numReadByte;
    /**
     * Dosyadan okunan byte'ları tutar
     */
    private byte[] _buffer;
    /**
     * Şuanki dizini tutar
     */
    private String Directory_Name = "";
    /**
     * MIME_TYPE bilgisini tutar
     */
    private String MIME_TYPE = "";
    /**
     * Gelen istekler CGI veya PHP işlemi olabilir bunun için sabitler
     */
    private final int CGI_PROG = 1;
    private final int PHP_PROG = 2;
    /**
     * Mail bilgisi gerekise buradaki mail html içerisinde gösterilebilir.
     */
    private String mailto = "support@mail.com";//Hold the web server administrator e-mail

    /**
     * Contructor fonksiyon
     */
    public FileOperations() {
        _numReadByte = 0;
        _buffer = new byte[1024];
    }

    /**
     * Creates a new and empty directory in the default temp directory using the
     * given prefix. This methods uses {@link File#createTempFile} to create a
     * new tmp file, deletes it and creates a directory for it instead.
     *
     * @param prefix The prefix string to be used in generating the diretory's
     * name; must be at least three characters long.
     * @return A newly-created empty directory.
     * @throws IOException If no directory could be created.
     */
    public static File createTempDir(String prefix)
            throws IOException {
        String tmpDirStr = System.getProperty("java.io.tmpdir");
        if (tmpDirStr == null) {
            throw new IOException(
                    "System property 'java.io.tmpdir' does not specify a tmp dir");
        }

        File tmpDir = new File(tmpDirStr);
        if (!tmpDir.exists()) {
            boolean created = tmpDir.mkdirs();
            if (!created) {
                throw new IOException("Unable to create tmp dir " + tmpDir);
            }
        }

        File resultDir = null;
        int suffix = (int) System.currentTimeMillis();
        int failureCount = 0;
        do {
            resultDir = new File(tmpDir, prefix + suffix % 10000);
            suffix++;
            failureCount++;
        } while (resultDir.exists() && failureCount < 50);

        if (resultDir.exists()) {
            throw new IOException(failureCount +
                    " attempts to generate a non-existent directory name failed, giving up");
        }
        boolean created = resultDir.mkdir();
        if (!created) {
            throw new IOException("Failed to create tmp directory");
        }

        return resultDir;
    }

    /**
     * Şu an kullanılan klasoru ayarlar
     * @param Dir   Kullanılan klasor
     */
    public void setDirectory_Name(String Dir) {
        Directory_Name = Dir;
    }

    /**
     * Şu an kullanılan klasor ismin geri döner.
     * @return Current_directory
     */
    public String getDirectory_Name() {
        return Directory_Name;
    }

    /**
     * İstek için gerekli HTML header'ı oluşturup string olarak döner
     * @param htmlDoc   html header bilgisi için gerekli uzunluk buradan hesaplanır
     * @return html header string olarak geri döner.
     */
    public String CreateHTMLHeader(StringBuilder htmlDoc) {

        StringBuilder hdrDoc = new StringBuilder();
        ///////////////////////////////////////////////////////////////////////
        /**Test the file extension, then get the appropriate mime type **/
        Mimes mim = new Mimes();
        MIME_TYPE = mim.getMimes("notfile.html");

        /** Sending a Http Response of type
         *
         * HTTP/1.x 200 OK + crlf
         * Date : xx/xx/xxxx + crlf
         * Server : serverName + crlf
         * content-length : X bytes + crlf
         * content-type : mime type + crlf
         *
         * */
        hdrDoc.append("HTTP/1.1 200 OK\r\n");
        hdrDoc.append("Date: " + new Date().toString() + "\r\n");
        hdrDoc.append("Server: PAYLASIM 1.0\n");
        hdrDoc.append("Accept-Ranges: bytes\r\n");
        hdrDoc.append("content-length: " + String.valueOf(new String(htmlDoc.toString()).getBytes().length) + "\r\n");
        hdrDoc.append("Content-Type: " + MIME_TYPE + "\r\n");
        hdrDoc.append("\r\n");

        ///////////////////////////////////////////////////////////////////////

        return hdrDoc.toString();

    }

    /**
     * Eğer istenen dosya bulunuamazsa istediğiniz dosya bulunamadı sayfasını
     * oluşturup istemciye gönderir.
     * @param outS outputstream
     * @param HOST_NAME
     * @throws IOException
     */
    public void FileNotFound(OutputStream outS, String HOST_NAME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();
        StringBuilder htmlDoc = new StringBuilder();

        htmlDoc.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\r\n");
        htmlDoc.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
        htmlDoc.append("<head>\r\n");
        //        htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n");
        htmlDoc.append("<META http-equiv=content-type content=text/html;charset=windows-1254>\r\n");

        htmlDoc.append("<Title>404 DOSYA BULUNAMADI</Title>\r\n");
        htmlDoc.append("</head>\r\n");

        htmlDoc.append("<body bgcolor='#008080'>\r\n");
        htmlDoc.append("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>\r\n");

        htmlDoc.append("<div align='center'><center></p>\r\n");//div start

        htmlDoc.append("<table border='1' width='700' bgcolor='#004080'><TR>\r\n");//table begin

        htmlDoc.append("<td align='center'>");//td start
        htmlDoc.append("<p align='center'>");
        htmlDoc.append("<font color='#FFFFFE' size='6'>");
        htmlDoc.append("<strong>404 Dosya Bulunamadi</strong></font></p>");
        htmlDoc.append("<p align='left'>");
        htmlDoc.append("<font color='#FFFFFF'>");
        htmlDoc.append("<strong>İstediğiniz için gerekli dosya PAYLASIM'da bulunamadi." +
                "Lütfen adresi kontrol ediniz. Sorun çozulmez ise Sunucuyu Açan Kişiye Durumu Bildiriniz");
        htmlDoc.append("<a href='mailto:>" + mailto + "'>" + mailto + "</a></strong></font></p><p>&nbsp;</p></td>");//td start

        htmlDoc.append("</TR></Table>\r\n");//table begin

        htmlDoc.append("</center></div>\r\n");//div end

        htmlDoc.append("</html>" + "\r\n");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        outS.write(new String(hdrDoc.toString()).getBytes());
        outS.write(new String(htmlDoc.toString()).getBytes());
        outS.flush();


    }

    /**
     * Eğer dizin listemeleye izin verilmemişse bunun için gerekli sayfayı
     * oluşturup istemciye döner.
     * 
     * @param outS
     * @param HOST_NAME
     * @throws IOException
     */
    void DirectoryListingDenied(OutputStream outS, String HOST_NAME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();
        StringBuilder htmlDoc = new StringBuilder();

        htmlDoc.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\r\n");
        htmlDoc.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
        htmlDoc.append("<head>\r\n");
        //        htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n");
        htmlDoc.append("<META http-equiv=content-type content=text/html;charset=windows-1254>\r\n");

        htmlDoc.append("<Title>DİZİN LİSTELEME ENGELLENDİ</Title>\r\n");
        htmlDoc.append("</head>\r\n");

        htmlDoc.append("<body bgcolor='#008080'>\r\n");
        htmlDoc.append("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>\r\n");

        htmlDoc.append("<div align='center'><center></p>\r\n");//div start

        htmlDoc.append("<table border='1' width='700' bgcolor='#004080'><TR>\r\n");//table begin

        htmlDoc.append("<td align='center'>");//td start
        htmlDoc.append("<p align='center'>");
        htmlDoc.append("<font color='#FFFFFE' size='6'>");
        htmlDoc.append("<strong>Dizin Listeleme Engellendi</strong></font></p>");
        htmlDoc.append("<p align='left'>");
        htmlDoc.append("<font color='#FFFFFF'>");
        htmlDoc.append("<strong>Dizin Listeleme Şu an Aktif Değil. Sunucuyu Açan Kişiye Danışınız ");
        htmlDoc.append("<a href='mailto:>" + mailto + "'>" + mailto + "</a></strong></font></p><p>&nbsp;</p></td>");//td start

        htmlDoc.append("</TR></Table>\r\n");//table begin

        htmlDoc.append("</center></div>\r\n");//div end

        htmlDoc.append("</html>" + "\r\n");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        outS.write(new String(hdrDoc.toString()).getBytes());
        outS.write(new String(htmlDoc.toString()).getBytes());
        outS.flush();



    }

    /**
     * Sunumu kontrol eden butonların bulunduğu sayfayı oluşturur.
     * http://<IP>:<PORT>/cont
     * @param outS
     * @param HOST_NAME
     * @throws IOException
     */
    void ContollerPage(OutputStream outS, String HOST_NAME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();
        StringBuilder htmlDoc = new StringBuilder();

        htmlDoc.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\r\n");
        htmlDoc.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
        htmlDoc.append("<head>\r\n");
//        htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n");
        htmlDoc.append("<META http-equiv=content-type content=text/html;charset=windows-1254>\r\n");

        htmlDoc.append("<style>\r\n");
        htmlDoc.append(".btn {\r\n");
        htmlDoc.append("font:arial;\r\n");
        htmlDoc.append("width:250px;\r\n");
        htmlDoc.append("height:250px;\r\n");
        htmlDoc.append("font-size: 16px;\r\n");
        htmlDoc.append("}\r\n");
        htmlDoc.append("</style>\r\n");
        htmlDoc.append("<script>\r\n");
        htmlDoc.append("//create HTTP request for javascript\r\n");
        htmlDoc.append("function makeHttpObject() {\r\n");
        htmlDoc.append("try {return new XMLHttpRequest();}\r\n");
        htmlDoc.append("catch (error) {}\r\n");
        htmlDoc.append("try {return new ActiveXObject(\"Msxml2.XMLHTTP\");}\r\n");
        htmlDoc.append("catch (error) {}\r\n");
        htmlDoc.append("try {return new ActiveXObject(\"Microsoft.XMLHTTP\");}\r\n");
        htmlDoc.append("catch (error) {}\r\n");
        htmlDoc.append("throw new Error(\"Could not create HTTP request object.\");\r\n");
        htmlDoc.append("}\r\n");

        htmlDoc.append("function NextSlide()\r\n");
        htmlDoc.append("{\r\n");

        //htmlDoc.append("document.getElementById(\"nextBtn\").disabled = true;\r\n");

        htmlDoc.append(" reqImage = new Image(); \n");
        htmlDoc.append(" reqImage.src = \"/next\";\n");

        //htmlDoc.append("setTimeout(enableNext(){document.getElementById(\"nextBtn\").disabled = false;},3000);\r\n");

//        htmlDoc.append("var request = makeHttpObject();\r\n");
//        htmlDoc.append("request.open(\"GET\", \"/next\", false);\r\n");
//        htmlDoc.append("request.send(null);\r\n");

        htmlDoc.append("}\r\n");

        htmlDoc.append("function PrevSlide()\r\n");
        htmlDoc.append("{\r\n");

        //htmlDoc.append("document.getElementById(\"prevBtn\").disabled = true;\r\n");

        htmlDoc.append(" reqImage = new Image(); \n");
        htmlDoc.append(" reqImage.src = \"/prev\";\n");

        //htmlDoc.append("setTimeout(enablePrev(){document.getElementById(\"prevBtn\").disabled = false;},3000);\r\n");

//        htmlDoc.append("var request = makeHttpObject();\r\n");
//        htmlDoc.append("request.open(\"GET\", \"/prev\", false);\r\n");
//        htmlDoc.append("request.send(null);\r\n");

        htmlDoc.append("}\r\n");

        //END SLIDE
        htmlDoc.append("function EndSlide()\r\n");
        htmlDoc.append("{\r\n");

        //htmlDoc.append("document.getElementById(\"endBtn\").disabled = true;\r\n");

        htmlDoc.append(" reqImage = new Image(); \n");
        htmlDoc.append(" reqImage.src = \"/end\";\n");

        //htmlDoc.append("setTimeout(enableEnd(){document.getElementById(\"endBtn\").disabled = false;},3000);\r\n");

//        htmlDoc.append("var request = makeHttpObject();\r\n");
//        htmlDoc.append("request.open(\"GET\", \"/end\", false);\r\n");
//        htmlDoc.append("request.send(null);\r\n");

        htmlDoc.append("}\r\n");

        htmlDoc.append("</script>\r\n");
        htmlDoc.append("</head>\r\n");
        htmlDoc.append("<body>\r\n");
        htmlDoc.append("<button id=\"prevBtn\" onclick=\"PrevSlide()\" class=\"btn\">PREV SLIDE</button>\r\n");
        htmlDoc.append("<button id=\"nextBtn\" onclick=\"NextSlide()\" class=\"btn\">NEXT SLIDE</button>\r\n");
        htmlDoc.append("<button id=\"endBtn\" onclick=\"EndSlide()\" class=\"btn\">END SLIDE</button>\r\n");
        htmlDoc.append("</body>\r\n");
        htmlDoc.append("</html>\r\n");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        outS.write(new String(hdrDoc.toString()).getBytes());
        outS.write(new String(htmlDoc.toString()).getBytes());
        outS.flush();

    }

    /**
     * Sunumu gösteren HTML sayfa
     * http://<IP>:<PORT>/sunum
     * @param outS
     * @param HOST_NAME
     * @throws IOException
     */
    void PresentationPage(OutputStream outS, String HOST_NAME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();
        StringBuilder htmlDoc = new StringBuilder();

        htmlDoc.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\r\n");
        htmlDoc.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
        htmlDoc.append("<head>\r\n");
//        htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n");
        htmlDoc.append("<META http-equiv=content-type content=text/html;charset=windows-1254>\r\n");

        htmlDoc.append("<Title>SUNUM SAYFASI</Title>\r\n");
        htmlDoc.append("<script language='JavaScript'><!--\r\n");
        htmlDoc.append("function reloadpic()\r\n");
        htmlDoc.append("{\r\n");
        htmlDoc.append("var timestamp = new Date().getTime();\r\n");


        htmlDoc.append("document.images[\"slidePic\"].src = \"/ppt_img/curr.png?random=\" + timestamp;\r\n");

//        htmlDoc.append("var request = makeHttpObject();\r\n");
//        htmlDoc.append("request.open(\"GET\", \"/slide\", false);\r\n");
//        htmlDoc.append("request.send(null);\r\n");
//        htmlDoc.append("document.images[\"slidePic\"].src = request.responseText+\"?random=\"+timestamp;\r\n");

        //"print(request.responseText);

        htmlDoc.append("setTimeout(\"reloadpic();\",1000);\r\n");
        htmlDoc.append("}\r\n");

        //create HTTP request for javascript
//        htmlDoc.append("function makeHttpObject() {\r\n");
//        htmlDoc.append("try {return new XMLHttpRequest();}\r\n");
//        htmlDoc.append("catch (error) {}\r\n");
//        htmlDoc.append("try {return new ActiveXObject(\"Msxml2.XMLHTTP\");}\r\n");
//        htmlDoc.append("catch (error) {}\r\n");
//        htmlDoc.append("try {return new ActiveXObject(\"Microsoft.XMLHTTP\");}\r\n");
//        htmlDoc.append("catch (error) {}\r\n");
//        htmlDoc.append(" throw new Error(\"Could not create HTTP request object.\");\r\n");
//        htmlDoc.append("}\r\n");

        htmlDoc.append("onload = reloadpic;\r\n");
        htmlDoc.append("//--></script>\r\n");
        htmlDoc.append("</head>\r\n");

        htmlDoc.append("<body bgcolor='#008080' onLoad=\"setTimeout('reloadpic()',2000)\" >\r\n");
        htmlDoc.append("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>\r\n");

        htmlDoc.append("<div align='center'><center></p>\r\n");//div start

        htmlDoc.append("<table border='1' width='1200' height='500' bgcolor='#004080'><TR>\r\n");//table begin

        htmlDoc.append("<td align='center'>" +
                "<img src=\"/ppt_img/curr.png\"id=\"slidePic\" " +
                "name=\"slidePic\"  " +
                "alt=\"SUNUM BİTMİŞTİR\" " +
                "vspace=\"0\" " +
                "hspace=\"0\"  " +
                "border=\"0\" " +
                "width=\"100%\" " +
                "height=\"100%\" ></img></td>");//td start

        htmlDoc.append("</TR></Table>\r\n");//table begin

        htmlDoc.append("</center></div>\r\n");//div end

        htmlDoc.append("</html>" + "\r\n");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        outS.write(new String(hdrDoc.toString()).getBytes());
        outS.write(new String(htmlDoc.toString()).getBytes());
        outS.flush();

    }

    /**
     * @deprecated 
     * @param outS
     * @param HOST_NAME
     * @throws IOException
     */
     void CurrentSlideNumber(OutputStream outS, String HOST_NAME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();
        StringBuilder htmlDoc = new StringBuilder();

        htmlDoc.append(PresentationScreen.IMG_PPT_FOLDER + PresentationScreen.CURRENT_SLIDE + ".png");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        outS.write(new String(hdrDoc.toString()).getBytes());
        outS.write(new String(htmlDoc.toString()).getBytes());
        outS.flush();

    }

    /**
     * 200 OK Cevabını oluşturur.
     * @param OUT_HROK
     * @param FILE_LENGTH
     * @param FILE_MIME
     * @throws IOException
     */
    public void HttpResponseOK(OutputStream OUT_HROK, File FILE_LENGTH, String FILE_MIME) throws IOException {

        StringBuilder hdrDoc = new StringBuilder();

        /**Test the file extension, then get the appropriate mime type **/
        Mimes mim = new Mimes();
        MIME_TYPE = mim.getMimes(FILE_MIME);

        /** Sending a Http Response of type
         *
         * HTTP/1.x 200 OK + crlf
         * Date : xx/xx/xxxx + crlf
         * Server : serverName + crlf
         * content-length : X bytes + crlf
         * content-type : mime type + crlf
         *
         * */
        hdrDoc.append("HTTP/1.1 200 OK\r\n");
        hdrDoc.append("Date: " + new Date().toString() + "\r\n");
        hdrDoc.append("Server: PAYLASIM 1.0\n");
        hdrDoc.append("Accept-Ranges: bytes\r\n");
        hdrDoc.append("content-length: " + String.valueOf(FILE_LENGTH.length()) + "\r\n");
        hdrDoc.append("Content-Type: " + MIME_TYPE + "\r\n");
        hdrDoc.append("\r\n");

        ///////////////////////////////////////////////////////////////////////

        OUT_HROK.write(new String(hdrDoc.toString()).getBytes());
        OUT_HROK.flush();

    }

    /**
     * İstek yapılan dosyanın exe mi olduğunu kontrol eder. Exe ise CGI
     * işlemi olarak işlenecektir.
     * @param FILE_MIME  istek yapılan dosyanın ismi
     * @return  CGI ise true değilse false
     */
    public boolean isCgiProg(String FILE_MIME) {

        int FILE_EXTENS_POS = (int) FILE_MIME.lastIndexOf(".");

        if (FILE_MIME.substring(FILE_EXTENS_POS + 1, FILE_MIME.length()).equals("exe")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * PHP isteği olup olmadığını kontrol ediyor.
     * @param FILE_MIME
     * @return
     */
    public boolean isPHPProg(String FILE_MIME) {

        int FILE_EXTENS_POS = (int) FILE_MIME.lastIndexOf(".");

        if (FILE_MIME.substring(FILE_EXTENS_POS + 1, FILE_MIME.length()).equals("php")) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Eğer CGI veya PHP isteği ise onu işliyor.
     * @param CGI_PHPFile
     * @param ToBrowser
     * @param CGI_PHP
     * @throws IOException
     */
    public void ProcessCgi(String CGI_PHPFile, OutputStream ToBrowser, int CGI_PHP) throws IOException {

        /**
         * Method ProcessCgi according to the CGI_PHP varaiable ,either directly execute
         * the file if it is a cgi program or call the php program wich execute the php script
         * then send the result to the server
         */
        Runtime r = Runtime.getRuntime(); //creating an object Runtime by calling the getRuntime Method
        String cgiContent = "";
        Process p = null;//win32 process initialised to null

        switch (CGI_PHP) {

            case CGI_PROG:
                p = r.exec(CGI_PHPFile);
                break;
            case PHP_PROG:
                p = r.exec("C:\\php\\php.exe " + CGI_PHPFile);
                break;
        }
        /*we redirect the program STDOUT  to a bufferedReader */

        BufferedReader brcgi = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((cgiContent = brcgi.readLine()) != null) {

            /** we eliminate useless data generated by the program */
            if (cgiContent.startsWith("Status") || cgiContent.startsWith("Content") || cgiContent.startsWith("X-Powered-By")) {

                ToBrowser.write("".getBytes());
                ToBrowser.flush();

            } else {

                //we send the data redirected from the program STDOUT to the client
                ToBrowser.write((cgiContent + "\r\n").getBytes());
                ToBrowser.flush();
            }
        }
        p.destroy();//we destroy the process to free memory

    }

    /**
     * ReadFile dosya okuyup cgi veya php scriptlerini çalıştırıp sonuçlarını istemciye döner
     * @param presentFrm
     * @param File_Name
     * @param HOST_NAME
     * @param priw
     * @param File_Mime_Type
     * @param logArea
     * @param infoLog
     * @throws IOException
     */
    public void ReadFile(File tempIconFolder, PresentationScreen presentFrm, String File_Name, String HOST_NAME, OutputStream priw, String File_Mime_Type, JTextArea logArea, ServerLogging infoLog) throws IOException {

        File f = new File(File_Name);

        if (f == null) {

            infoLog.LogMessage(logArea, "The File doesn't exist");

        } else {

            //HANDLE PRESENTATION CASE...
            if (f.getPath().equals(ServerWorker.SERVER_ROOT + "\\" + "sunum")) {
                //create sunum pages
                this.PresentationPage(priw, HOST_NAME);

                return;
            }

            //HANDLE NEXT COMMAND...
            if (f.getPath().equals(ServerWorker.SERVER_ROOT + "\\" + "next")) {
                //create sunum pages

                if (PresentationScreen.LASTUPDATE + PresentationScreen.TIME_INTERVAL < java.lang.System.currentTimeMillis()) {

                    presentFrm.NextSlide();

                    PresentationScreen.LASTUPDATE = java.lang.System.currentTimeMillis();
                }
                return;
            }


            //HANDLE PREV COMMAND...
            if (f.getPath().equals(ServerWorker.SERVER_ROOT + "\\" + "prev")) {

                if (PresentationScreen.LASTUPDATE + PresentationScreen.TIME_INTERVAL < java.lang.System.currentTimeMillis()) {

                    presentFrm.PrevSlide();

                    PresentationScreen.LASTUPDATE = java.lang.System.currentTimeMillis();
                }
                return;
            }

            //HANDLE END COMMAND...
            if (f.getPath().equals(ServerWorker.SERVER_ROOT + "\\" + "end")) {

                presentFrm.EndSlide();

                return;
            }

            //HANDLE CONT
            if (f.getPath().equals(ServerWorker.SERVER_ROOT + "\\" + "cont")) {
                this.ContollerPage(priw, HOST_NAME);
                return;
            }

            if (f.getPath().contains("icons")) {
                String iconName = f.getName();
                try {
                    f = new File(tempIconFolder.getPath() + "\\" + iconName);
                    if (!f.exists()) {
                        f = new File(tempIconFolder.getPath() + "\\" + "file.png");
                    }
                } catch (Exception ex) {
                }
            }

            if (!f.exists()) {
                /** Requested File doesn't exist => calling the method FileNotFound  **/
                this.FileNotFound(priw, HOST_NAME);

            } else {

                if (f.canRead()) {

                    /**Testing if the file is a CGI program **/
                    if (isCgiProg(f.getPath())) {
                        /** calling the method ProcessCgi with option CGI_PROG **/
                        this.ProcessCgi(f.getPath(), priw, CGI_PROG);
                    } /**Testing if the file is a PHP file **/
                    else if (isPHPProg(f.getPath())) {

                        /** calling the method ProcessCgi with option PHP_PROG **/
                        this.ProcessCgi(f.getPath(), priw, PHP_PROG);

                    } else {

                        /** case of a simple file => readed then it's content sent to the client **/
                        FileInputStream fich = new FileInputStream(f.getPath());


                        /** calling method HttpResponseOK */
                        this.HttpResponseOK(priw, f, File_Mime_Type);


                        /**
                         * while is not end of file, method read store number of bytes equivalent to the
                         * buffer length in buffer variable then it's content is sent by method write
                         */
                        while ((_numReadByte = fich.read(_buffer, 0, _buffer.length)) != -1) {

                            priw.write(_buffer, 0, _numReadByte);

                            _buffer = null;

                            _buffer = new byte[1024];//The buffer can contain  1 KB

                            priw.flush();
                        }

                        fich.close(); //File readed must be closed


                    }
                }
            }
        }

    }

    /**
     * 
     * @param Fold
     * @return
     */
    public String DirectoryToList(String Fold) {

        /** Method DirectoryToList return the name of the actual directory that will be listed*/
        StringTokenizer set = new StringTokenizer(Fold, "/");
        String ActualDirectory = "";
        int cont = set.countTokens();

        if (!set.hasMoreTokens()) {
            return Fold;
        } else {
            Vector DirectoryParse = new Vector();
            DirectoryParse.setSize(cont);

            try {

                for (int j = 0; j < cont; j++) {

                    DirectoryParse.addElement(set.nextToken());
                }

                ActualDirectory = DirectoryParse.lastElement().toString();
            } catch (NoSuchElementException nsee) {
            }

            return ActualDirectory;
        }
    }

    /**
     * Dizin listemeyi sağlayan fonksiyondur dizin için gerekli HTML'i oluşturup
     * istemciye geri döner
     * @param directory
     * @param pr
     * @param HOST_NAME_LINK
     * @param extension
     * @param folderList
     * @param dirSubDirs
     * @throws IOException
     */
    public void Listdir(String directory, OutputStream pr, String HOST_NAME_LINK, String extension, String[] folderList, boolean dirSubDirs) throws IOException {

        File DIR_FILE = new File(directory);

        String File_Separ_String = System.getProperty("file.separator");
        String ActualDir = this.DirectoryToList(this.getDirectory_Name());

        StringBuilder htmlDoc = new StringBuilder();
        StringBuilder hdrDoc = new StringBuilder();

        //Icons_Path = Icons_Path + File_Separ_String;

        if (DIR_FILE.isDirectory()) {

            htmlDoc.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>\r\n");
            htmlDoc.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
            htmlDoc.append("<head>\r\n");
//        htmlDoc.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\r\n");
            htmlDoc.append("<META http-equiv=content-type content=text/html;charset=windows-1254>\r\n");

            htmlDoc.append("<h1>" + HOST_NAME_LINK + "- /" + this.getDirectory_Name() + "</h1>\r\n");
            htmlDoc.append("</head><body><HR><BR>\r\n");

            String[] File_List = null;

            FilterExt ftr = new FilterExt();
            ftr.setExtension(extension);
            ftr.setFolderFilters(folderList);
            ftr.setSubDirFlag(dirSubDirs);

            File_List = new String[DIR_FILE.list(ftr).length];
            File_List = DIR_FILE.list(ftr);

            //send The Heading => Name Last Modified Size
            htmlDoc.append("<Table border=0>\r\n");
            htmlDoc.append("<Table border=0>\r\n");
            htmlDoc.append("<TR>\r\n");
            htmlDoc.append("<TD width='50'> </TD>\r\n");
            htmlDoc.append("<TD><strong>Name</strong></TD>\r\n");
            htmlDoc.append("<TD align='center'><strong>Last Modified</strong></TD>\r\n");
            htmlDoc.append("<TD align='center' width='50'><strong>Size</strong></TD>\r\n");
            htmlDoc.append("</TR>\r\n");

            for (int i = 0; i < File_List.length; i++) {
                htmlDoc.append("<TR>\r\n");
                if (new File(directory + File_Separ_String + File_List[i]).isDirectory()) {
                    //List sub-directories founded into the current directory
                    if (File_List[i].equals("icons")) {
                        /** if the actual directory is icons , we send a blank cells*/
                        htmlDoc.append("<TD width='50'></TD>\r\n");
                        htmlDoc.append("<TD></TD>\r\n");
                        htmlDoc.append("<TD align='center' width='300'></TD>\r\n");
                        htmlDoc.append("<TD align='center' width='50'></TD>\r\n");
                    } else {
                        /** Listing directories*/
                        htmlDoc.append("<TD width='50'><img src=/icons/" + DirIcon + " width='44' height='44'></TD>\r\n");
                        htmlDoc.append("<TD><a href='" + ActualDir + '/' + File_List[i] + "'>" + File_List[i] + "</a>" + "</TD>\r\n");
                        htmlDoc.append("<TD align='center' width='300'>" + new Date(new File(directory + File_Separ_String + File_List[i]).lastModified()).toString() + "</TD>\r\n");
                        htmlDoc.append("<TD align='center' width='50'><font color=red><strong>&lt;DIR&gt;</strong></font></TD>\r\n");
                    }//fin test

                } else {
                    //Listing files founded into the directory

                    String ext = "";
                    int mid = File_List[i].lastIndexOf(".");
                    ext = File_List[i].substring(mid + 1, File_List[i].length());

                    ext = ext.toLowerCase();

                    htmlDoc.append("<TD width='50'><img src=/icons/" + ext + ".png width='44' height='44'></TD>\r\n");

                    htmlDoc.append("<TD><a href='" + ActualDir + '/' + File_List[i] + "'>" + File_List[i] + "</a>" + "</TD>\r\n");
                    htmlDoc.append("<TD  align='center' width='300'>" + new Date(new File(directory + File_Separ_String + File_List[i]).lastModified()).toString() + "</TD>\r\n");
                    htmlDoc.append("<TD align='center' width='50'><font color=red><strong>" + String.valueOf(new File(directory + File_Separ_String + File_List[i]).length()) + "</font></strong></TD>\r\n");
                }
                htmlDoc.append("</TR>\r\n");

            }
        }

        htmlDoc.append("</Table>" + "<Hr><BR>PAYLASIM v1.0 at " + HOST_NAME_LINK + " </body></html>\r\n");

        hdrDoc.append(CreateHTMLHeader(htmlDoc));

        pr.write(new String(hdrDoc.toString()).getBytes());
        pr.write(new String(htmlDoc.toString()).getBytes());
        pr.flush();

    }
}
	