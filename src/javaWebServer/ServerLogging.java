package javaWebServer;

import java.io.*;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *Loglama için kullanılan sınıftır
 * @author ugur.coruh
 */
public class ServerLogging {

    
    private final int FATAL_ERROR = 1;
    private final int BADREQUEST_ERROR = 2;
    private final int UNHANDLED_ERROR = 3;
    private final int ERROR = 4;
    private String LogFilePath;

    /**
     * Loglama objesi create eder
     */
    public ServerLogging() {
    }

    /**
     * Loglama objesi oluşturur
     * @param path Log dosya yolu
     */
    public ServerLogging(String path) {

        this.LogFilePath = path;

    }

    /**
     * Verilen log dosyasına mesajı ve mesaj tipini loglar
     * @param LogFilePath
     * @param Msg
     * @param ErrorNumber
     * @throws IOException
     */
    public void LogEvent(String LogFilePath, String Msg, int ErrorNumber) throws IOException {

//logEvent Method used to save the error occured in the server into a log file

//creating a new file instance 
        File logFile = new File(LogFilePath);
        BufferedWriter Bw = null;
        Date logDate = new Date();

//Today contain the actual date
        String ToDay = logDate.toLocaleString();

        if (!logFile.exists()) {
//if the file doesn't exist we create a new file
            FileOutputStream fp = new FileOutputStream(LogFilePath);
        }

        if (logFile.canWrite()) {
//creating an instance of FileWriter with the option append=true 
            FileWriter fw = new FileWriter(LogFilePath, true);

            Bw = new BufferedWriter(fw);

        }

        switch (ErrorNumber) {

//According to the ErrorNumber,a specific message is writed 

            case FATAL_ERROR:
                Bw.write("[" + ToDay + "] " + "[FATAL ERROR]" + "  " + Msg + "\r\n");
                break;
            case BADREQUEST_ERROR:
                Bw.write("[" + ToDay + "] " + "[REQUESTE ERROR]" + " " + Msg + "\r\n");
                break;
            case UNHANDLED_ERROR:
                Bw.write("[" + ToDay + "] " + "[UNHANDLED ERROR]" + " " + Msg + "\r\n");
                break;
            case ERROR:
                Bw.write("[" + ToDay + "] " + "[ERROR]" + " " + Msg + "\r\n");
                break;
        }

        Bw.close(); //closing File log

    }

    /**
     * 
     * @param txtLog logun gösterileceği textbox
     * @param Msg mesaj içeiği
     */
    public void LogMessage(JTextArea txtLog, String Msg) {

        try {

            File logFile = new File(LogFilePath);
            BufferedWriter Bw = null;
            Date logDate = new Date();

            String ToDay = logDate.toLocaleString();

            if (!logFile.exists()) {
                FileOutputStream fp = new FileOutputStream(LogFilePath);
            }

            if (logFile.canWrite()) {
                FileWriter fw = new FileWriter(LogFilePath, true);

                Bw = new BufferedWriter(fw);

            }

            Bw.write("[" + ToDay + "]" + Msg + "\r\n");

            if(txtLog.getText().length()>2000)
            txtLog.setText("");

            txtLog.append("[" + ToDay + "]" + Msg + "\r\n");

            Bw.close(); 
            
        } catch (Exception ex) {
        }


    }
}

