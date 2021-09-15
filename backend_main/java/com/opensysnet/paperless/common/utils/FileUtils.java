package com.opensysnet.paperless.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
public class FileUtils {

    private static String uploadPath;
    private static String uploadUrl;

    @Value("${opensysnet.sign.uploadPath}")
    public void setUploadPath(String uploadPath) {
        FileUtils.uploadPath = uploadPath;
    }

    @Value("${opensysnet.sign.url}")
    public void setUploadUrl(String uploadUrl) {
        FileUtils.uploadUrl = uploadUrl;
    }

    public static String getUploadPath() {
        return FileUtils.uploadPath;
    }

    public static String getUploadUrl() {
        return FileUtils.uploadUrl;
    }

    public static String uploadFiles(MultipartFile sign_file, String file_id) throws Exception {
        String path = uploadPath;
        OutputStream out = null;
        String fileName = "";

        try {
            String org_fileName = sign_file.getOriginalFilename();
            String[] split_fileName = org_fileName.split("\\.");
            String file_ext = split_fileName[split_fileName.length - 1];
            fileName = file_id + "." + file_ext;
            byte[] bytes = sign_file.getBytes();
            String save_full_path = path + fileName;

            File file = new File(save_full_path);
            out = new FileOutputStream(file);
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    public static Boolean deleteFiles(String filename) throws Exception {
        String path = uploadPath;

        try {
            String full_path = path + filename;

            File fd = new File(full_path);

            if (fd.exists()) {
                if ( fd.delete() ) {
                    return true;
                } else
                    return false;
            } else
                return true;
        }  catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
