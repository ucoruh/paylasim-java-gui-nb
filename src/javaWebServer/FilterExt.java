/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaWebServer;

/**
 * Klasor ve Dosya'ları filtreleyen sınıf
 *@version 1.0
 * @author ugur.coruh
 */
import java.io.File;
import java.io.FilenameFilter;

/**
 * Sunucu üzerindeki dosya ve klasorler için filtreleme yapan sınıftır.
 * @author ugur.coruh
 */
public class FilterExt implements FilenameFilter {

    /**
     * virgüller ile ayrılmış uzantılar txt,doc gibi eğer boş ise herşeyi gösterir
     */
    private String _ext = "";
    /**
     * Gösterilecek klasor listesi eğer boş ise hepsi gösterilir
     */
    private String[] _folderFilters;
    /**
     * Alt dizinlerin gösterilip gösterilmemesini sağlayan fonksiyon
     */
    private boolean _subDir;
    
    /**
     * Dosya veya Klasoru kabul eden fonksiyon
     * @param dir
     * @param name
     * @return
     */
    public boolean accept(File dir, String name) {

        boolean findFlag = false;
        String longPath = dir.getPath() + "\\" + name;

        //disable pptImg folder directory listing
        if (longPath.contains("ppt_img")) {
            return false;
        }

        String fileName = name;
        String ext = "";
        int mid = fileName.lastIndexOf(".");
        ext = fileName.substring(mid + 1, fileName.length());

        if (this._folderFilters.length > 0) {
            for (int k = 0; k < this._folderFilters.length; k++) {

                if (this._subDir) {
                    if (longPath.contains(this._folderFilters[k])) {
                        findFlag = true;
                    }
                } else {
                    if (this._folderFilters[k].equals(longPath)) {
                        findFlag = true;
                    }
                }

            }
        }

        if (ext.equals(name) && findFlag == true) {
            return true;
        } else if (ext.equals(name) && findFlag == false) {
            return false;
        }

        String[] extList = this._ext.split(",");

        if (extList.length > 0 && !extList[0].equals("")) {

            for (int i = 0; i < extList.length; i++) {

                if (extList[i].equals(ext)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }

    }
    /**
     * Dosya uzantılarının set edildiği fonksiyon
     * @param exts virgüller ile ayrılmış uzantılar txt,doc gibi
     */
    public void setExtension(String exts) {
        this._ext = exts;
    }
    /**
     * Klasor listesinin ayarlandığı fonksiyon
     * @param folderFilters gösterilecek klasor listesi boş ise hepsi gösterilir
     */
    public void setFolderFilters(String[] folderFilters) {
        this._folderFilters = folderFilters;
    }
    /**
     * Alt dizinlerini gösterilip gösterilmediğini ayarlamak için kullanılan
     * fonksiyon.
     * @param subDir altdizinleri göstermek için true değilse false kullanılır.
     */
    public void setSubDirFlag(boolean subDir) {
        this._subDir = subDir;
    }
    
}
