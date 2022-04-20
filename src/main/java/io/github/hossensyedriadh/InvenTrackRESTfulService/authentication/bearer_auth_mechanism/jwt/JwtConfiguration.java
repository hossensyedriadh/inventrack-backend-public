package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Log4j
@Configuration
public class JwtConfiguration {
    @Value("${application.security.jwt.keystore-location}")
    private String keyStorePath;

    @Value("${application.security.jwt.keystore-password}")
    private String keyStorePassphrase;

    @Value("${application.security.jwt.key-alias}")
    private String keyAlias;

    @Value("${application.security.jwt.private-key-passphrase}")
    private String privateKeyPassphrase;

    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath);
            keyStore.load(resourceAsStream, keyStorePassphrase.toCharArray());

            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            log.error("Unable to load keystore: {}", e);
        } catch (CertificateException e) {
            log.error("Invalid Certificate: {}", e);
            e.printStackTrace();
        } catch (IOException ioException) {
            log.error("Unexpected exception: {}", ioException);
            ioException.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm not found: {}", e);
            e.printStackTrace();
        }

        throw new RuntimeException("Keystore exception");
    }

    @Bean
    public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());

            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (UnrecoverableKeyException e) {
            log.error("Unrecoverable key exception: {}", e);
            e.printStackTrace();
        } catch (KeyStoreException e) {
            log.error("Keystore exception: {}", e);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm not found: {}", e);
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to load RSA Private Key");
    }

    @Bean
    public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
        try {
            Certificate certificate = keyStore.getCertificate(keyAlias);
            PublicKey publicKey = certificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
        } catch (KeyStoreException e) {
            log.error("Keystore exception: {}", e);
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to load RSA Public Key");
    }
}
