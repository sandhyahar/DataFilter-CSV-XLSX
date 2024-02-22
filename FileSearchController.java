package com.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.API.Service.ParamMstService;
import com.API.pojo.ParamMst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileSearchController {

    @Autowired
    private ParamMstService paramMstService;

    @PostMapping("/search")
    public FileSearchResult searchFiles(@RequestParam("targetFileName") String targetFileName) {
        List<String> fileNames = new ArrayList<>();
        List<String> filePaths = new ArrayList<>();
        String paramName = "filedataPath";
        String folderPath = "";
        List<ParamMst> paramMsts = paramMstService.findByParamName(paramName);
        if (!paramMsts.isEmpty()) {
            ParamMst paramMst = paramMsts.get(0);
            String paramValue = paramMst.getParamValue();
            folderPath = paramValue;
        }

        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            try {
                searchFilesInFolder(folder, fileNames, filePaths, targetFileName);
            } catch (Exception e) {
                // Handle any exceptions that occur during file search
                e.printStackTrace();
            }
        }

        return new FileSearchResult(fileNames, filePaths);
    }
    
    private void searchFilesInFolder(File folder, List<String> fileNames, List<String> filePaths, String targetFileName) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && isFileNameMatch(file.getName(), targetFileName)) {
                    fileNames.add(file.getName());
                    filePaths.add(file.getAbsolutePath().replace("\\", "/"));
                } else if (file.isDirectory()) {
                    searchFilesInFolder(file, fileNames, filePaths, targetFileName);
                }
            }
        }
    }

    private boolean isFileNameMatch(String fileName, String targetFileName) {
        String[] targetWords = targetFileName.toLowerCase().split("\\s+"); // Split the target file name into individual words
        String fileNameLowerCase = fileName.toLowerCase();

        for (String word : targetWords) {
            if (!fileNameLowerCase.contains(word)) {
                return false; // If any word is not found in the file name, return false
            }
        }

        return true; // All words are found in the file name
    }

    public static class FileSearchResult {
        private List<String> fileNames;
        private List<String> filePaths;

        public FileSearchResult(List<String> fileNames, List<String> filePaths) {
            this.fileNames = fileNames;
            this.filePaths = filePaths;
        }

        public List<String> getFileNames() {
            return fileNames;
        }

        public List<String> getFilePaths() {
            return filePaths;
        }
        
    }
    
}