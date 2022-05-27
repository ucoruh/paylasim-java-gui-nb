package javaWebServer;

/**
 *MIME tipleriyle ilgili fonksiyonları barındıran sınıftır.
 * @author ugur.coruh
 */
public class Mimes {

    private int FILE_EXTENS_POS;
    private String FILE_EXTENSION;

    /**
     * MIME tipleri için gerekli objeyi oluşturur.
     */
    public Mimes() {
    }
/**
 * Verilen dosyanın MIME tipini döner
 * @param FILE_MIME
 * @return string MIME_TYPE
 */
    public String getMimes(String FILE_MIME) {

        FILE_EXTENS_POS = (int) FILE_MIME.lastIndexOf(".");

        FILE_EXTENSION = FILE_MIME.substring(FILE_EXTENS_POS + 1, FILE_MIME.length());


        if (FILE_EXTENSION.equals("htm") ||
                FILE_EXTENSION.equals("html")) {
            return "text/html";
        } else if (FILE_EXTENSION.equals("py")) {
            return "text/html";
        } else if (FILE_EXTENSION.equals("xsl")) {
            return "text/xsl";
        } else if (FILE_EXTENSION.equals("gif")) {
            return "image/gif";
        } else if (FILE_EXTENSION.equals("jpeg") ||
                FILE_EXTENSION.equals("jpg") ||
                FILE_EXTENSION.equals("jpe")) {
            return "image/jpeg";
        } else if (FILE_EXTENSION.equals("png")) {
            return "image/png";
        } else if (FILE_EXTENSION.equals("class") ||
                FILE_EXTENSION.equals("bin") ||
                FILE_EXTENSION.equals("dms") ||
                FILE_EXTENSION.equals("lha") ||
                FILE_EXTENSION.equals("lzh")) {
            return "application/octet-stream";
        } else if (FILE_EXTENSION.equals("jnlp")) {
            return "application/x-java-jnlp-file";
        } else if (FILE_EXTENSION.equals("doc")) {
            return "application/msword";
        } else if (FILE_EXTENSION.equals("oda")) {
            return "application/oda";
        } else if (FILE_EXTENSION.equals("jnlp")) {
            return "application/x-java-jnlp-file";
        } else if (FILE_EXTENSION.equals("pdf")) {
            return "application/pdf";
        } else if (FILE_EXTENSION.equals("ai") ||
                FILE_EXTENSION.equals("eps") ||
                FILE_EXTENSION.equals("ps")) {
            return "application/postscript";
        } else if (FILE_EXTENSION.equals("smi") ||
                FILE_EXTENSION.equals("smil")) {
            return "application/smil";
        } else if (FILE_EXTENSION.equals("xls")) {
            return "application/vnd.ms-excel";
        } else if (FILE_EXTENSION.equals("ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (FILE_EXTENSION.equals("wbxml")) {
            return "application/vnd.wap.wbxml";
        } else if (FILE_EXTENSION.equals("wmlc")) {
            return "application/vnd.wap.wmlc";
        } else if (FILE_EXTENSION.equals("wmlsc")) {
            return "application/vnd.wap.wmlscriptc";
        } else if (FILE_EXTENSION.equals("bcpio")) {
            return "application/x-bcpio";
        } else if (FILE_EXTENSION.equals("vcd")) {
            return "application/x-cdlink";
        } else if (FILE_EXTENSION.equals("pgn")) {
            return "application/x-chess-pgn";
        } else if (FILE_EXTENSION.equals("cpio")) {
            return "application/x-cpio";
        } else if (FILE_EXTENSION.equals("csh")) {
            return "application/x-csh";
        } else if (FILE_EXTENSION.equals("dcr") ||
                FILE_EXTENSION.equals("dir") ||
                FILE_EXTENSION.equals("dxr")) {
            return "application/x-director";
        } else if (FILE_EXTENSION.equals("dvi")) {
            return "application/x-dvi";
        } else if (FILE_EXTENSION.equals("spl")) {
            return "application/x-futuresplash";
        } else if (FILE_EXTENSION.equals("gtar")) {
            return "application/x-gtar";
        } else if (FILE_EXTENSION.equals("hdf")) {
            return "application/x-hdf";
        } else if (FILE_EXTENSION.equals("js")) {
            return "application/x-javascript";
        } else if (FILE_EXTENSION.equals("skp") ||
                FILE_EXTENSION.equals("skd") ||
                FILE_EXTENSION.equals("skt") ||
                FILE_EXTENSION.equals("skm")) {
            return "application/x-koan";
        } else if (FILE_EXTENSION.equals("latex")) {
            return "application/x-latex";
        } else if (FILE_EXTENSION.equals("nc") ||
                FILE_EXTENSION.equals("cdf")) {
            return "application/x-netcdf";
        } else if (FILE_EXTENSION.equals("sh")) {
            return "application/x-sh";
        } else if (FILE_EXTENSION.equals("shar")) {
            return "application/x-shar";
        } else if (FILE_EXTENSION.equals("swf")) {
            return "application/x-shockwave-flash";
        } else if (FILE_EXTENSION.equals("sit")) {
            return "application/x-stuffit";
        } else if (FILE_EXTENSION.equals("sv4cpio")) {
            return "application/x-sv4cpio";
        } else if (FILE_EXTENSION.equals("sv4crc")) {
            return "application/x-sv4crc";
        } else if (FILE_EXTENSION.equals("tar")) {
            return "application/x-tar";
        } else if (FILE_EXTENSION.equals("tcl")) {
            return "application/x-tcl";
        } else if (FILE_EXTENSION.equals("tex")) {
            return "application/x-tex";
        } else if (FILE_EXTENSION.equals("texinfo") ||
                FILE_EXTENSION.equals("texi")) {
            return "application/x-texinfo";
        } else if (FILE_EXTENSION.equals("t") ||
                FILE_EXTENSION.equals("tr") ||
                FILE_EXTENSION.equals("roff")) {
            return "application/x-troff";
        } else if (FILE_EXTENSION.equals("man")) {
            return "application/x-troff-man";
        } else if (FILE_EXTENSION.equals("me")) {
            return "application/x-troff-me";
        } else if (FILE_EXTENSION.equals("ms")) {
            return "application/x-troff-ms";
        } else if (FILE_EXTENSION.equals("ustar")) {
            return "application/x-ustar";
        } else if (FILE_EXTENSION.equals("src")) {
            return "application/x-wais-source";
        } else if (FILE_EXTENSION.equals("zip")) {
            return "application/zip";
        } else if (FILE_EXTENSION.equals("au") ||
                FILE_EXTENSION.equals("snd")) {
            return "audio/basic";
        } else if (FILE_EXTENSION.equals("mid") ||
                FILE_EXTENSION.equals("midi") ||
                FILE_EXTENSION.equals("kar")) {
            return "audio/midi";
        } else if (FILE_EXTENSION.equals("mpga") ||
                FILE_EXTENSION.equals("mp2") ||
                FILE_EXTENSION.equals("mp3")) {
            return "audio/mpeg";
        } else if (FILE_EXTENSION.equals("aif") ||
                FILE_EXTENSION.equals("aiff") ||
                FILE_EXTENSION.equals("aifc")) {
            return "audio/x-aiff";
        } else if (FILE_EXTENSION.equals("ram") ||
                FILE_EXTENSION.equals("rm")) {
            return "audio/x-pn-realaudio";
        } else if (FILE_EXTENSION.equals("rpm")) {
            return "audio/x-pn-realaudio-plugin";
        } else if (FILE_EXTENSION.equals("ra")) {
            return "audio/x-realaudio";
        } else if (FILE_EXTENSION.equals("wav")) {
            return "audio/x-wav";
        } else if (FILE_EXTENSION.equals("pdb") ||
                FILE_EXTENSION.equals("xyz")) {
            return "chemical/x-pdb";
        } else if (FILE_EXTENSION.equals("bmp")) {
            return "image/bmp";
        } else if (FILE_EXTENSION.equals("ief")) {
            return "image/ief";
        } else if (FILE_EXTENSION.equals("png")) {
            return "image/png";
        } else if (FILE_EXTENSION.equals("tiff") ||
                FILE_EXTENSION.equals("tif")) {
            return "image/tiff";
        } else if (FILE_EXTENSION.equals("wbmp")) {
            return "image/vnd.wap.wbmp";
        } else if (FILE_EXTENSION.equals("ras")) {
            return "image/x-cmu-raster";
        } else if (FILE_EXTENSION.equals("pnm")) {
            return "image/x-portable-anymap";
        } else if (FILE_EXTENSION.equals("pbm")) {
            return "image/x-portable-bitmap";
        } else if (FILE_EXTENSION.equals("pgm")) {
            return "image/x-portable-graymap";
        } else if (FILE_EXTENSION.equals("ppm")) {
            return "image/x-portable-pixmap";
        } else if (FILE_EXTENSION.equals("rgb")) {
            return "image/x-rgb";
        } else if (FILE_EXTENSION.equals("xbm")) {
            return "image/x-xbitmap";
        } else if (FILE_EXTENSION.equals("xpm")) {
            return "image/x-xpixmap";
        } else if (FILE_EXTENSION.equals("xwd")) {
            return "image/x-xwindowdump";
        } else if (FILE_EXTENSION.equals("igs") ||
                FILE_EXTENSION.equals("iges")) {
            return "model/iges";
        } else if (FILE_EXTENSION.equals("msh") ||
                FILE_EXTENSION.equals("mesh") ||
                FILE_EXTENSION.equals("silo")) {
            return "model/mesh";
        } else if (FILE_EXTENSION.equals("wrl") ||
                FILE_EXTENSION.equals("vrml")) {
            return "model/vrml";
        } else if (FILE_EXTENSION.equals("css")) {
            return "text/css";
        } else if (FILE_EXTENSION.equals("asc") ||
                FILE_EXTENSION.equals("txt")) {
            return "text/plain";
        } else if (FILE_EXTENSION.equals("rtx")) {
            return "text/richtext";
        } else if (FILE_EXTENSION.equals("rtf")) {
            return "text/rtf";
        } else if (FILE_EXTENSION.equals("sgm") ||
                FILE_EXTENSION.equals("sgml")) {
            return "text/sgml";
        } else if (FILE_EXTENSION.equals("tsv")) {
            return "text/tab-separated-values";
        } else if (FILE_EXTENSION.equals("wml")) {
            return "text/vnd.wap.wml";
        } else if (FILE_EXTENSION.equals("wmls")) {
            return "text/vnd.wap.wmlscript";
        } else if (FILE_EXTENSION.equals("etx")) {
            return "text/x-setext";
        } else if (FILE_EXTENSION.equals("xml")) {
            return "text/xml";
        } else if (FILE_EXTENSION.equals("mpeg") ||
                FILE_EXTENSION.equals("mpg") ||
                FILE_EXTENSION.equals("mpe")) {
            return "video/mpeg";
        } else if (FILE_EXTENSION.equals("qt") ||
                FILE_EXTENSION.equals("mov")) {
            return "video/quicktime";
        } else if (FILE_EXTENSION.equals("avi")) {
            return "video/x-msvideo";
        } else if (FILE_EXTENSION.equals("movie")) {
            return "video/x-sgi-movie";
        } else if (FILE_EXTENSION.equals("ice")) {
            return "x-conference/x-cooltalk";
        } else {
            return "text/plain";
        }
        /**
        if the file extension is unknown we return the default mime type = "text/plain"
         */
    }
}
