package com.mattermost.integration.figma.security.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataEncryptionServiceImplTest {

    private static final String TEXT = "text";

    @Autowired
    private DataEncryptionServiceImpl testedInstance;

    @Test
    public void shouldEncryptAndDecryptString() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptedText = testedInstance.encrypt(TEXT);
        String decryptedText = testedInstance.decrypt(encryptedText);

        assertEquals(TEXT, decryptedText);
    }
}