package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;


@Controller
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/fileUpload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile file, Model model, Authentication authentication) {
        User currentUser = userService.getUser(authentication.getName());
        File newFile = fileService.createFile(file, currentUser);
        if(fileService.uploadFile(newFile)) {
            model.addAttribute("successResponse", true);
            model.addAttribute("message", "File uploaded successfully!");
        }
        else {
            model.addAttribute("failResponse", true);
            model.addAttribute("message", "A File with this name already exist!");
        }
        model.addAttribute("nav", "/home#nav-files");
        return "result";
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable int fileId, Authentication authentication) throws FileNotFoundException {
        User currentUser = userService.getUser(authentication.getName());
        int userId = currentUser.getUserId();
        if(userId == fileService.getFileById(fileId).getUserId()) {
            byte[] fileContent = fileService.downloadFile(fileId);
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            File file = fileService.getFileById(fileId);
            String contentType = file.getContentType();
            String filename = file.getFileName();
            long fileSize = Long.parseLong(file.getFileSize());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(fileSize)
                    .body(resource);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable("fileId") Integer fileId, Model model, Authentication authentication){
        User currentUser = userService.getUser(authentication.getName());
        int userId = currentUser.getUserId();
        if(userId == fileService.getFileById(fileId).getUserId()) {
            if (fileService.deleteFile(fileId)) {
                model.addAttribute("successResponse", true);
                model.addAttribute("message", "File deleted successfully!");
            } else {
                model.addAttribute("FailResponse", true);
                model.addAttribute("message", "Fail to delete the file!");
            }
        }
        else {
            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "you don't have permission to delete this file!");
        }
        model.addAttribute("nav", "/home#nav-files");
        return "result";
    }

    @ControllerAdvice
    public class FileUploadExceptionAdvice {

        @Autowired
        private FileService fileService;

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {

            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "File size exceeds the size limit!");
            model.addAttribute("nav", "/home#nav-files");
            return "result";
        }
    }

}