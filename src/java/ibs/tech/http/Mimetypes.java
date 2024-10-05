/*
 * Class: Mimetypes.java
 */

// package:
package ibs.tech.http;

// imports:


/******************************************************************************
 * Handling for all Mimetypes. <BR/>
 *
 * @version     $Id: Mimetypes.java,v 1.3 2007/07/10 19:25:59 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 20060205
 ******************************************************************************
 */
public abstract class Mimetypes extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Mimetypes.java,v 1.3 2007/07/10 19:25:59 kreimueller Exp $";


    /**
     * Type of form data: standard data coming from POST request. <BR/>
     */
    public static final Mimetype[] MIMETYPES =
    {
        // possible mime types for msg files:
        new Mimetype ("application/msoutlook", "msg",
                      "Outlook Nachricht"),
        new Mimetype ("application/x-msg", "", // "msg",
                      "x-msg"),
        new Mimetype ("zz-application/zz-winassoc-MSG", "", // "msg",
                      "zz-winassoc-MSG"),

        new Mimetype ("application/acad", "dwg",
                      "AutoCAD-Dateien (nach NCSA)"),
        new Mimetype ("application/applefile", "",
                      "AppleFile-Dateien"),
        new Mimetype ("application/astound", "asd, asn",
                      "Astound-Dateien"),
        new Mimetype ("application/dsptype", "tsp",
                      "TSP-Dateien"),
        new Mimetype ("application/dxf", "dxf",
                      "AutoCAD-Dateien (nach CERN)"),
        new Mimetype ("application/futuresplash", "spl",
                      "Flash Futuresplash-Dateien"),
        new Mimetype ("application/gzip", "gz",
                      "GNU Zip-Dateien"),
        new Mimetype ("application/listenup", "ptlk",
                      "Listenup-Dateien"),
        new Mimetype ("application/mac-binhex40", "hqx",
                      "Macintosh Binärdateien"),
        new Mimetype ("application/mbedlet", "mbd",
                      "Mbedlet-Dateien"),
        new Mimetype ("application/mif", "mif",
                      "FrameMaker Interchange Format Dateien"),
        new Mimetype ("application/vnd.ms-excel", "xls",
                      "Microsoft Excel Dateien"),
        new Mimetype ("application/msexcel", "xls, xla",
                      "Microsoft Excel Dateien"),
        new Mimetype ("application/mshelp", "hlp, chm",
                      "Microsoft Windows Hilfe Dateien"),
        new Mimetype ("application/mspowerpoint", "ppt, ppz, pps, pot",
                      "Microsoft Powerpoint Dateien"),
        new Mimetype ("application/msword", "doc, dot",
                      "Microsoft Word Dateien"),
        new Mimetype ("application/octet-stream", "bin, exe, com, dll, class",
                      "Ausführbare Dateien"),
        new Mimetype ("application/oda", "oda",
                      "Oda-Dateien"),
        new Mimetype ("application/pdf", "pdf",
                      "Adobe PDF-Dateien"),
        new Mimetype ("application/postscript", "ai, eps, ps",
                      "Adobe PostScript-Dateien"),
        new Mimetype ("application/rtc", "rtc",
                      "RTC-Dateien"),
        new Mimetype ("application/rtf", "rtf",
                      "Microsoft RTF-Dateien"),
        new Mimetype ("application/studiom", "smp",
                      "Studiom-Dateien"),
        new Mimetype ("application/toolbook", "tbk",
                      "Toolbook-Dateien"),
        new Mimetype ("application/vocaltec-media-desc", "vmd",
                      "Vocaltec Mediadesc-Dateien"),
        new Mimetype ("application/vocaltec-media-file", "vmf",
                      "Vocaltec Media-Dateien"),
        new Mimetype ("application/xhtml+xml", "htm, html, shtml, xhtml",
                      "XHTML-Dateien"),
        new Mimetype ("application/xml", "xml",
                      "XML-Dateien"),
        new Mimetype ("application/x-bcpio", "bcpio",
                      "BCPIO-Dateien"),
        new Mimetype ("application/x-compress", "z",
                      "zlib-komprimierte Dateien"),
        new Mimetype ("application/x-cpio", "cpio",
                      "CPIO-Dateien"),
        new Mimetype ("application/x-csh", "csh",
                      "C-Shellscript-Dateien"),
        new Mimetype ("application/x-director", "dcr, dir, dxr",
                      "Macromedia Director-Dateien"),
        new Mimetype ("application/x-dvi", "dvi",
                      "DVI-Dateien"),
        new Mimetype ("application/x-envoy", "evy",
                      "Envoy-Dateien"),
        new Mimetype ("application/x-gtar", "gtar",
                      "GNU tar-Archivdateien"),
        new Mimetype ("application/x-hdf", "hdf",
                      "HDF-Dateien"),
        new Mimetype ("application/x-httpd-php", "php, phtml",
                      "PHP-Dateien"),
        new Mimetype ("application/x-javascript", "js",
                      "serverseitige JavaScript-Dateien"),
        new Mimetype ("application/x-latex", "latex",
                      "LaTeX-Quelldateien"),
        new Mimetype ("application/x-macbinary", "bin",
                      "Macintosh Binärdateien"),
        new Mimetype ("application/x-mif", "mif",
                      "FrameMaker Interchange Format Dateien"),
        new Mimetype ("application/x-netcdf", "nc, cdf",
                      "Unidata CDF-Dateien"),
        new Mimetype ("application/x-nschat", "nsc",
                      "NS Chat-Dateien"),
        new Mimetype ("application/x-sh", "sh",
                      "Bourne Shellscript-Dateien"),
        new Mimetype ("application/x-shar", "shar",
                      "Shell-Archivdateien"),
        new Mimetype ("application/x-shockwave-flash", "swf, cab",
                      "Flash Shockwave-Dateien"),
        new Mimetype ("application/x-sprite", "spr, sprite",
                      "Sprite-Dateien"),
        new Mimetype ("application/x-stuffit", "sit",
                      "Stuffit-Dateien"),
        new Mimetype ("application/x-supercard", "sca",
                      "Supercard-Dateien"),
        new Mimetype ("application/x-sv4cpio", "sv4cpio",
                      "CPIO-Dateien"),
        new Mimetype ("application/x-sv4crc", "sv4crc",
                      "CPIO-Dateien mit CRC"),
        new Mimetype ("application/x-tar", "tar",
                      "tar-Archivdateien"),
        new Mimetype ("application/x-tcl", "tcl",
                      "TCL Scriptdateien"),
        new Mimetype ("application/x-tex", "tex",
                      "TeX-Dateien"),
        new Mimetype ("application/x-texinfo", "texinfo, texi",
                      "Texinfo-Dateien"),
        new Mimetype ("application/x-troff", "t, tr, roff",
                      "TROFF-Dateien (Unix)"),
        new Mimetype ("application/x-troff-man", "man, troff",
                      "TROFF-Dateien mit MAN-Makros (Unix)"),
        new Mimetype ("application/x-troff-me", "me, troff",
                      "TROFF-Dateien mit ME-Makros (Unix)"),
        new Mimetype ("application/x-troff-ms", "me, troff",
                      "TROFF-Dateien mit MS-Makros (Unix)"),
        new Mimetype ("application/x-ustar", "ustar",
                      "tar-Archivdateien (Posix)"),
        new Mimetype ("application/x-wais-source", "src",
                      "WAIS Quelldateien"),
        new Mimetype ("application/x-www-form-urlencoded", "",
                      "HTML-Formulardaten an CGI"),
        new Mimetype ("application/zip", "zip",
                      "ZIP-Archivdateien"),
        new Mimetype ("audio/basic", "au, snd",
                      "Sound-Dateien"),
        new Mimetype ("audio/echospeech", "es",
                      "Echospeed-Dateien"),
        new Mimetype ("audio/tsplayer", "tsi",
                      "TS-Player-Dateien"),
        new Mimetype ("audio/voxware", "vox",
                      "Vox-Dateien"),
        new Mimetype ("audio/x-aiff", "aif, aiff, aifc",
                      "AIFF-Sound-Dateien"),
        new Mimetype ("audio/x-dspeeh", "dus, cht",
                      "Sprachdateien"),
        new Mimetype ("audio/x-midi", "mid, midi",
                      "MIDI-Dateien"),
        new Mimetype ("audio/x-mpeg", "mp2",
                      "MPEG-Dateien"),
        new Mimetype ("audio/x-pn-realaudio", "ram, ra",
                      "RealAudio-Dateien"),
        new Mimetype ("audio/x-pn-realaudio-plugin", "rpm",
                      "RealAudio-Plugin-Dateien"),
        new Mimetype ("audio/x-qt-stream", "stream",
                      "Quicktime-Streaming-Dateien"),
        new Mimetype ("audio/x-wav", "wav",
                      "WAV-Dateien"),
        new Mimetype ("drawing/x-dwf", "dwf",
                      "Drawing-Dateien"),
        new Mimetype ("image/cis-cod", "cod",
                      "CIS-Cod-Dateien"),
        new Mimetype ("image/cmu-raster", "ras",
                      "CMU-Raster-Dateien"),
        new Mimetype ("image/fif", "fif",
                      "FIF-Dateien"),
        new Mimetype ("image/gif", "gif",
                      "GIF-Dateien"),
        new Mimetype ("image/ief", "ief",
                      "IEF-Dateien"),
        new Mimetype ("image/jpeg", "jpeg, jpg, jpe",
                      "JPEG-Dateien"),
        new Mimetype ("image/png", "png",
                      "PNG-Dateien"),
        new Mimetype ("image/tiff", "tiff, tif",
                      "TIFF-Dateien"),
        new Mimetype ("image/vasa", "mcf",
                      "Vasa-Dateien"),
        new Mimetype ("image/vnd.wap.wbmp", "wbmp",
                      "Bitmap-Dateien (WAP)"),
        new Mimetype ("image/x-freehand", "fh4, fh5, fhc",
                      "Freehand-Dateien"),
        new Mimetype ("image/x-portable-anymap", "pnm",
                      "PBM Anymap Dateien"),
        new Mimetype ("image/x-portable-bitmap", "pbm",
                      "PBM Bitmap Dateien"),
        new Mimetype ("image/x-portable-graymap", "pgm",
                      "PBM Graymap Dateien"),
        new Mimetype ("image/x-portable-pixmap", "ppm",
                      "PBM Pixmap Dateien"),
        new Mimetype ("image/x-rgb", "rgb",
                      "RGB-Dateien"),
        new Mimetype ("image/x-windowdump", "xwd",
                      "X-Windows Dump"),
        new Mimetype ("image/x-xbitmap", "xbm",
                      "XBM-Dateien"),
        new Mimetype ("image/x-xpixmap", "xpm",
                      "XPM-Dateien"),
        new Mimetype ("message/external-body", "",
                      "Nachricht mit externem Inhalt"),
        new Mimetype ("message/http", "",
                      "HTTP-Headernachricht"),
        new Mimetype ("message/news", "",
                      "Newsgroup-Nachricht"),
        new Mimetype ("message/partial", "",
                      "Nachricht mit Teilinhalt"),
        new Mimetype ("message/rfc822", "", // "msg",
                      "Nachricht nach RFC 2822"),
        new Mimetype ("model/vrml", "wrl",
                      "Visualisierung virtueller Welten (VRML)"),
        new Mimetype ("multipart/alternative", "",
                      "mehrteilige Daten gemischt"),
        new Mimetype ("multipart/byteranges", "",
                      "mehrteilige Daten mit Byte-Angaben"),
        new Mimetype ("multipart/digest", "",
                      "mehrteilige Daten / Auswahl"),
        new Mimetype ("multipart/encrypted", "",
                      "mehrteilige Daten verschlüsselt"),
        new Mimetype ("multipart/form-data", "",
                      "mehrteilige Daten aus HTML-Formular (z.B. File-Upload)"),
        new Mimetype ("multipart/mixed", "",
                      "mehrteilige Daten gemischt"),
        new Mimetype ("multipart/parallel", "",
                      "mehrteilige Daten parallel"),
        new Mimetype ("multipart/related", "",
                      "mehrteilige Daten / verbunden"),
        new Mimetype ("multipart/report", "",
                      "mehrteilige Daten / Bericht"),
        new Mimetype ("multipart/signed", "",
                      "mehrteilige Daten / bezeichnet"),
        new Mimetype ("multipart/voice-message", "",
                      "mehrteilige Daten / Sprachnachricht"),
        new Mimetype ("text/comma-separated-values", "csv",
                      "kommaseparierte Datendateien"),
        new Mimetype ("text/css", "css",
                      "CSS Stylesheet-Dateien"),
        new Mimetype ("text/html", "htm, html, shtml",
                      "HTML-Dateien"),
        new Mimetype ("text/javascript", "js",
                      "JavaScript-Dateien"),
        new Mimetype ("text/plain", "txt",
                      "reine Textdateien"),
        new Mimetype ("text/richtext", "rtx",
                      "Richtext-Dateien"),
        new Mimetype ("text/rtf", "rtf",
                      "Microsoft RTF-Dateien"),
        new Mimetype ("text/tab-separated-values", "tsv",
                      "tabulator-separierte Datendateien"),
        new Mimetype ("text/vnd.wap.wml", "wml",
                      "WML-Dateien (WAP)"),
        new Mimetype ("application/vnd.wap.wmlc", "wmlc",
                      "WMLC-Dateien (WAP)"),
        new Mimetype ("text/vnd.wap.wmlscript", "wmls",
                      "WML-Scriptdateien (WAP)"),
        new Mimetype ("application/vnd.wap.wmlscriptc", "wmlsc",
                      "WML-Script-C-dateien (WAP)"),
        new Mimetype ("text/xml", "xml",
                      "XML-Dateien"),
        new Mimetype ("text/xml-external-parsed-entity", "",
                      "extern geparste XML-Dateien"),
        new Mimetype ("text/x-setext", "etx",
                      "SeText-Dateien"),
        new Mimetype ("text/x-sgml", "sgm, sgml",
                      "SGML-Dateien"),
        new Mimetype ("text/x-speech", "talk, spc",
                      "Speech-Dateien"),
        new Mimetype ("video/mpeg", "mpeg, mpg, mpe",
                      "MPEG-Dateien"),
        new Mimetype ("video/quicktime", "qt, mov",
                      "Quicktime-Dateien"),
        new Mimetype ("video/vnd.vivo", "viv, vivo",
                      "Vivo-Dateien"),
        new Mimetype ("video/x-msvideo", "avi",
                      "Microsoft AVI-Dateien"),
        new Mimetype ("video/x-sgi-movie", "movie",
                      "Movie-Dateien"),
        new Mimetype ("workbook/formulaone", "vts, vtts",
                      "FormulaOne-Dateien"),
        new Mimetype ("x-world/x-3dmf", "3dmf, 3dm, qd3d, qd3",
                      "3DMF-Dateien"),
        new Mimetype ("x-world/x-vrml", "wrl",
                      "Visualisierung virtueller Welten (VRML) (veralteter MIME-Typ, aktuell ist model/vrml)"),
    };



    /**************************************************************************
     * Get the mimetype for a specific file. <BR/>
     * The file name can be a fully qualified path + file name, just a file
     * name or only the extension itself.
     *
     * @param   fileName    The file to search for.
     *
     * @return  The mimetype,
     *          <CODE>null</CODE> if the extension could not be found.
     */
    public static String getMimetype (String fileName)
    {
        String mimetype = null;
        String extension = null;
        int pos = 0;

        // check if the file name contains a '.':
        if ((pos = fileName.lastIndexOf ('.')) >= 0)
        {
            // get just the extension:
            extension = fileName.substring (pos + 1);
        } // if
        else
        {
            // the whole string is used as extension:
            extension = fileName;
        } // else
//System.out.println ("Extension: " + extension);

        // loop through all mime types and search for the extension:
        for (int i = 0; mimetype == null && i < Mimetypes.MIMETYPES.length; i++)
        {
            mimetype = Mimetypes.MIMETYPES[i].getMimetype (extension);
        } // for i
//System.out.println ("Mimetype: " + mimetype);

        // return the result:
        return mimetype;
    } // getMimetype

} // class Mimetypes
