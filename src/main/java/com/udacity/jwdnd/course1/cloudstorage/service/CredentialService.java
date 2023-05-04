package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {

    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;
    private Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public Credential createCredential(String url, String userName, String password, Integer userId) {

        String key = generateKey();
        String encryptedPassword = encryptionService.encryptValue(password, key);
        Credential credential = null;
        credential = new Credential(
                null,
                url,
                userName,
                key,
                encryptedPassword,
                userId
        );
        return credential;
    }

    public Credential createCredential(String url, String userName, String password, Integer userId, Integer credentialId) {

        String key = generateKey();
        String encryptedPassword = encryptionService.encryptValue(password, key);
        Credential credential = null;
        credential = new Credential(
                credentialId,
                url,
                userName,
                key,
                encryptedPassword,
                userId
        );
        return credential;
    }
    public Boolean insertCredential(Credential credential){
        int rows = credentialMapper.insertCredentials(credential);
        return (rows > 0);
    }

    public String generateKey() {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public Credential getCredentialById(int credentialId){
        return  credentialMapper.getCredentialById(credentialId);
    }

    public List<Credential> getAllCredentialsByUserId(int userId){
        return credentialMapper.getAllCredentialsByUserId(userId);
    }

    public Boolean updateCredential(Credential credential){
        int rowsUpdated = credentialMapper.updateCredentials(credential);
        return (rowsUpdated > 0);
    }

    public boolean deleteCredential(int credentialId){
        int rowsDeleted = credentialMapper.delete(credentialId);
        return (rowsDeleted > 0);
    }

    public String decryptPassword(Credential credential) {
        return encryptionService.decryptValue(credential.getPassword(), credential.getKey());
    }

}
