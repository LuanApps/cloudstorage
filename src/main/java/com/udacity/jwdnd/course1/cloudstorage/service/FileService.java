package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public File getFileById(int fileId) {
        return fileMapper.getFileById(fileId);
    }

    public List<File> getAllFilesByUserId(int userId) {
        return fileMapper.getAllFilesByUserId(userId);
    }

    public boolean deleteFile(int fileId) {
        int rowsDeleted = fileMapper.deleteFile(fileId);
        return rowsDeleted > 0;
    }

    public boolean checkIfFilenameAlreadyTaken(File file){
        return fileMapper.checkIfFilenameAlreadyTaken(file);
    }

    public boolean uploadFile(File file) throws InvalidFileNameException {
        if (checkIfFilenameAlreadyTaken(file))
            return false;
        else {
            return this.fileMapper.insert(file) == 1;

        }
    }

    //As we are saving the file data as blob in our database, we need to store it as an array of bytes.
    public File createFile(MultipartFile userFile, User user) {
        File file = null;
        try {

            file = new File(
                    null,
                    userFile.getOriginalFilename(),
                    userFile.getContentType(),
                    String.valueOf(userFile.getSize()),
                    user.getUserId(),
                    userFile.getBytes());

        } catch (IOException e) {
            // @TODO log an error and throw a new exception (?)
            System.out.println(e.getMessage());
        }
        return file;
    }

    //As we are saving the files as blob in database, and because of this we need to decompose the file into a byte array, we need to build the file from bytes to download it.
    public byte[] downloadFile(int fileId){
        File file = fileMapper.getFileById(fileId);
        return file.getFileData();
    }

}
