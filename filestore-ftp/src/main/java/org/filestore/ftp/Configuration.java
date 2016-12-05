package org.filestore.ftp;

import java.io.File;

/**
 * Created by geoffrey on 01/12/2016.
 */
public class Configuration {
    public static File getFolder(){
        String envRoot = System.getenv("FTP_ROOT");
        String path = envRoot == null || envRoot.isEmpty() ? "/Users/geoffrey/Desktop/ftp/" : envRoot;
        return new File(path);
    }
}
