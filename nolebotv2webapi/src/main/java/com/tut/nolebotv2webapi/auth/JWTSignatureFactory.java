package com.tut.nolebotv2webapi.auth;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.security.token.jwt.encryption.secret.SecretEncryption;
import io.micronaut.security.token.jwt.encryption.secret.SecretEncryptionConfiguration;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.security.token.jwt.signature.rsa.RSASignatureGenerator;
import io.micronaut.security.token.jwt.signature.rsa.RSASignatureGeneratorConfiguration;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
public class JWTSignatureFactory {
    @Bean
    @Named("generator")
    public SecretSignature getSignatureGenerator(SecretSignatureConfiguration configuration) {
        return new SecretSignature(configuration);
    }
}
