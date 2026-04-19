package com.wave.user_service.configurations;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

@Configuration
public class JwtConfig {
    @Value("${jwt.private-key-file}")
    private Resource privateKeyFile;

    @Value("${jwt.public-key-file}")
    private Resource publicKeyFile;

    @Bean
    public RSAKey rsaKey() throws Exception {
        String privPem = Files.readString(privateKeyFile.getFile().toPath())
            .replaceAll("-----\\w+ PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(privPem)
        );
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
            .generatePrivate(privSpec);

        String pubPem = Files.readString(publicKeyFile.getFile().toPath())
            .replaceAll("-----\\w+ PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(
            Base64.getDecoder().decode(pubPem)
        );
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
            .generatePublic(pubSpec);

        String kid = UUID.randomUUID().toString();

        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(kid)
            .build();
    }

    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey.toPublicJWK());
    }
}

