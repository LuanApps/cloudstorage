package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import com.udacity.jwdnd.course1.cloudstorage.service.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;
    private final UserService userService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(FileService fileService, UserService userService, NoteService noteService, CredentialService credentialService) {
        this.fileService = fileService;
        this.userService = userService;
        this.noteService = noteService;
        this.credentialService = credentialService;
    }

    @GetMapping()
    public String homeView(Model model, Authentication authentication) {
        User currentUser = userService.getUser(authentication.getName());
        if (currentUser == null) {
            // handle the case where the user is not authenticated
            return "redirect:/login";
        }
        List<File> userFiles = fileService.getAllFilesByUserId(currentUser.getUserId());
        List<Note> userNotes = noteService.getAllNotesByUserId(currentUser.getUserId());
        List<Credential> userCredentials = credentialService.getAllCredentialsByUserId(currentUser.getUserId());
        model.addAttribute("files", userFiles);
        model.addAttribute("notes", userNotes);
        model.addAttribute("credentials", userCredentials);
        return "home";
    }
}