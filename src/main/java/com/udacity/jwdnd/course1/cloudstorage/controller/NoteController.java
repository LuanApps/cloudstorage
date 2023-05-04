package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private NoteService noteService;
    private UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("/saveNote")
    public String saveOrEditNote(@RequestParam("noteTitle") String title, @RequestParam("noteDescription")String description, @RequestParam("noteId") Integer noteId, Model model, Authentication authentication){
        User currentUser = userService.getUser(authentication.getName());
        if(title.length() > 20){
            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "Note can't be saved as title exceed 20 characters!");
            model.addAttribute("nav", "/home#nav-notes");
            return "result";
        }
        if(description.length() > 1000){
            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "Note can't be saved as description exceed 1000 characters!");
            model.addAttribute("nav", "/home#nav-notes");
            return "result";
        }
        if(noteId == null) {
            saveNote(title, description, model, currentUser);
        }
        else{
            updateNote(title, description, noteId, model, currentUser);
        }
        model.addAttribute("nav", "/home#nav-notes");
        return "result";
    }

    private void updateNote(String title, String description, Integer noteId, Model model, User currentUser) {
        Note userNote = noteService.createNote(currentUser, noteId, title, description);
        if (noteService.updateNote(userNote)) {
            model.addAttribute("successResponse", true);
            model.addAttribute("message", "Note updated successfully!");
        } else {
            model.addAttribute("failResponse", true);
            model.addAttribute("message", "Note can`t be modified!");
        }
    }

    private void saveNote(String title, String description, Model model, User currentUser) {
        Note userNote = noteService.createNote(currentUser, title, description);
        if (noteService.insertNote(userNote)) {
            model.addAttribute("successResponse", true);
            model.addAttribute("message", "Note saved successfully!");
        } else {
            model.addAttribute("failResponse", true);
            model.addAttribute("message", "Note can`t be saved!");
        }
    }

    @GetMapping("/delete/{noteId}")
    public String deleteNote(@PathVariable("noteId") Integer noteId, Model model, Authentication authentication){
        User currentUser = userService.getUser(authentication.getName());
        int userId = currentUser.getUserId();
        if(userId == noteService.getNoteById(noteId).getUserId()) {
            if (noteService.deleteNote(noteId)) {
                model.addAttribute("successResponse", true);
                model.addAttribute("message", "note deleted successfully!");
            } else {
                model.addAttribute("failResponse", true);
                model.addAttribute("message", "Fail to delete the note!");
            }
        }
        else {
            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "you don't have permission to access this!");
        }
        model.addAttribute("nav", "/home#nav-notes");
        return "result";
    }

}
