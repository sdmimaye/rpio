package com.github.sdmimaye.rpio.server.util.io;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class ZipUtils {
    public void unzip(String zipfile, String output, String password) throws IOException {
        File outdir = new File(output);
        if (!outdir.exists() && !outdir.mkdir()) {
            throw new IOException("Unabled to create output directory: " + outdir.getAbsolutePath());
        }
        File outdirtemp = new File(outdir.getAbsolutePath() + "-temp");
        if (!outdirtemp.exists() && !outdirtemp.mkdir()) {
            throw new IOException("Unabled to create temp-output directory: " + outdir.getAbsolutePath());
        }

        try {
            ZipFile file = new ZipFile(zipfile);
            if (StringUtils.isNotEmpty(password)) {
                file.setPassword(password);
            }
            file.extractAll(outdirtemp.getAbsolutePath());
            FileUtils.copyDirectory(outdirtemp, outdir);
        } catch (ZipException e) {
            throw new IOException(e);
        }finally {
            if(outdirtemp.exists())
                FileUtils.deleteDirectory(outdirtemp);
        }
    }

    public void zip(String input, String output, String password) throws IOException {
        try {
            File directory = new File(input);
            if (!directory.exists() || !directory.isDirectory())
                throw new RuntimeException("Invalid Argument (input): " + input);

            ZipFile file = new ZipFile(output);
            ZipParameters parameters = new ZipParameters();
            if (StringUtils.isNotEmpty(password)) {
                parameters.setPassword(password);
                parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            }
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            file.addFolder(directory.getAbsolutePath(), parameters);
            if (StringUtils.isNotEmpty(password)) {
                file.setPassword(password);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}