/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaWebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Kullanıcıya kullanması için gerekli IP bilgilerini gösteren fonksiyonların
 * bulunduğu sınıftır.
 * @author ugur.coruh
 */
public class IPInfo {

    /**
     * @deprecated  statik kullanım olduğu için gerek yok
     */
    public IPInfo() {
    }

    /**
     * http://automation.whatismyip.com/n09230945.asp adresini kullanarak dış IP
     * yi kullanıcıya döner
     * @return dış ip
     */
    public static String GetRealIP() {
        {
            BufferedReader in = null;
            try {
                URL getPublicIP = new URL("http://automation.whatismyip.com/n09230945.asp");
                URLConnection c = getPublicIP.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
                in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                //the site is in plain text so you do not have to worry about html tags.
                String inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (IOException ex) {
                Logger.getLogger(IPInfo.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(IPInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        }
    }

    /**
     * Local olarak kullanılan IP'leri kullanıcıya string olarak döner
     * @return
     */
    public static String GetLocalIPs() {

        String ipAdress = "";

        try {

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface current = interfaces.nextElement();

                System.out.println(current);

                if (!current.isUp() || current.isLoopback() || current.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = current.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress current_addr = addresses.nextElement();
                    if (current_addr.isLoopbackAddress()) {
                        continue;
                    }

                    if (current_addr instanceof Inet6Address) {
                        continue;
                    }

                    ipAdress = ipAdress + " [" + current_addr.getHostAddress() + "] ";

                }


            }

            return ipAdress;

        } catch (IOException ex) {
            Logger.getLogger(IPInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
