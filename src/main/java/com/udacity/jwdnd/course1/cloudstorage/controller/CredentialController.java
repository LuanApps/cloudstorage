package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.service.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    private CredentialService credentialService;
    private UserService userService;

    public CredentialController(CredentialService credentialService, UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping("/saveCredential")
    public String saveOrEditCredential(@RequestParam("url") String url, @RequestParam("username") String userName, @RequestParam("password") String password, @RequestParam("credentialId") Integer credentialId, Model model, Authentication authentication){
        User currentUser = userService.getUser(authentication.getName());
        int userId = currentUser.getUserId();
        if(credentialId == null) {
            saveCredential(url, userName, password, model, userId);
        }
        else {
            updateCredential(url, userName, password, credentialId, model, userId);
        }
        model.addAttribute("nav", "/home#nav-credentials");
        return "result";
    }

    private void saveCredential(String url, String userName, String password, Model model, Integer userId) {
        Credential credential;
        credential = credentialService.createCredential(url, userName, password, userId);
        if(credentialService.insertCredential(credential)){
            model.addAttribute("successResponse", true);
            model.addAttribute("message", "Credential saved successfully!");
        }
        else{
            model.addAttribute("failResponse", true);
            model.addAttribute("message", "Failed to save the credential!");
        }
    }

    private void updateCredential(String url, String userName, String password, Integer credentialId, Model model, Integer userId) {
        Credential credential;
        credential = credentialService.createCredential(url, userName, password, userId, credentialId);
        if(credentialService.updateCredential(credential)){
            model.addAttribute("successResponse", true);
            model.addAttribute("message", "Credential updated successfully!");
        }
        else{
            model.addAttribute("failResponse", true);
            model.addAttribute("message", "Failed to update the credential!");
        }
    }

    @GetMapping("/delete/{credentialId}")
    public String deleteCredential(@PathVariable("credentialId") Integer credentialId, Model model, Authentication authentication){
        User currentUser = userService.getUser(authentication.getName());
        int userId = currentUser.getUserId();
        if(userId == credentialService.getCredentialById(credentialId).getUserId()) {
            if (credentialService.deleteCredential(credentialId)) {
                model.addAttribute("successResponse", true);
                model.addAttribute("message", "Credential deleted successfully!");
            } else {
                model.addAttribute("failResponse", true);
                model.addAttribute("message", "Fail to delete the credential!");
            }
        }
        else {
            model.addAttribute("errorResponse", true);
            model.addAttribute("message", "you don't have permission access this!");
        }
        model.addAttribute("nav", "/home#nav-credentials");
        return "result";
    }

    @GetMapping("/decrypt")
    public void decryptPassword(HttpServletResponse response, Authentication authentication,
                                @ModelAttribute Credential credential) throws IOException {
        User currentUser = userService.getUser(authentication.getName());
        credential = credentialService.getCredentialById(credential.getCredentialId());
        int userId = currentUser.getUserId();
        if(userId == credential.getUserId()){
            String decryptedPassword = credentialService.decryptPassword(credential);
            response.getWriter().println(decryptedPassword);
        }
    }

}
