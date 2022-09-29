package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token;

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
public class BearerTokenConfiguration {
    @Value("${bearer-authentication.keystore.location}")
    private String keyStorePath;

    @Value("${bearer-authentication.keystore.passphrase}")
    private String keyStorePassphrase;

    @Value("${bearer-authentication.keystore.key-alias}")
    private String keyAlias;

    @Value("${bearer-authentication.keystore.private-key-passphrase}")
    private String privateKeyPassphrase;

    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.keyStorePath);
            keyStore.load(resourceStream, this.keyStorePassphrase.toCharArray());
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            log.error("Unable to load keystore: {}", e);
            throw new RuntimeException("Unable to load keystore", e);
        } catch (CertificateException e) {
            e.printStackTrace();
            log.error("Invalid Certificate: {}", e);
            throw new RuntimeException("Invalid Certificate", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IO exception: {}", e);
            throw new RuntimeException("IO exception", e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("Algorithm not found: {}", e);
            throw new RuntimeException("Algorithm not found", e);
        }
    }

    @Bean
    public RSAPrivateKey tokenSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(this.keyAlias, this.privateKeyPassphrase.toCharArray());

            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            log.error("Unrecoverable key exception: {}", e);
            throw new RuntimeException("Unrecoverable key", e);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            log.error("Keystore exception: {}", e);
            throw new RuntimeException("Bad keystore", e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            log.error("Algorithm not found: {}", e);
            throw new RuntimeException("Algorithm not found", e);
        }

        throw new RuntimeException("Invalid RSA Private Key");
    }

    @Bean
    public RSAPublicKey tokenValidationKey(KeyStore keyStore) {
        try {
            Certificate certificate = keyStore.getCertificate(this.keyAlias);
            PublicKey publicKey = certificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
            log.error("Keystore exception: {}", e);
            throw new RuntimeException("Bad Keystore", e);
        }

        throw new RuntimeException("Unable to load RSA Public Key");
    }
}
