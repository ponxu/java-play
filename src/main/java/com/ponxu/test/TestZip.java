package com.ponxu.test;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by xwz on 15-7-22.
 */
public class TestZip {
    public static void main(String[] args) throws Exception {
        // 给一个zip文件 -> 解压 -> 再次压缩
        String cp = TestZip.class.getResource("/").toURI().getPath();

        String srcZipFile = cp + "test.zip";
        String tmp = cp + "tmp/";
        String newZipFile = cp + "test_new.zip";

        // ============== JDK ==========================
        unzip(srcZipFile, tmp);
        zip(tmp, newZipFile);

        // ============== apache =======================
        // TODO
    }

    public static void unzip(String zipFile, String destDir) throws Exception {
        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            String entryName = entry.getName();
            System.out.println("unzip: " + entryName);
            if (entry.isDirectory()) {
                new File(destDir + entryName).mkdirs();
            } else {
                FileOutputStream fout = new FileOutputStream(destDir + entryName);
                IOUtils.copy(zin, fout);
                fout.flush();
                fout.close();
            }
        }
        zin.close();
    }

    public static void zip(String srcDir, String destFile) throws Exception {
        FileOutputStream fout = new FileOutputStream(destFile);
        ZipOutputStream zout = new ZipOutputStream(fout);

        File rootFile = new File(srcDir);
        for (File file : rootFile.listFiles()) {
            add2zip(file, zout, srcDir);
        }

        zout.flush();
        zout.close();
    }

    private static void add2zip(File file, ZipOutputStream zout, String srcDir) throws Exception {
        String absFileName = file.getAbsolutePath();
        String fileName = absFileName.substring(srcDir.length());
        System.out.println("zip: " + fileName);

        if (file.isDirectory()) {
            ZipEntry entry = new ZipEntry(fileName + "/");
            zout.putNextEntry(entry);

            for (File f : file.listFiles()) {
                add2zip(f, zout, srcDir);
            }
        } else {
            ZipEntry entry = new ZipEntry(fileName);
            zout.putNextEntry(entry);

            FileInputStream fin = new FileInputStream(file);
            IOUtils.copy(fin, zout);
            fin.close();
        }
    }


}
