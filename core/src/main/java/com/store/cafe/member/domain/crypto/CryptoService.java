package com.store.cafe.member.domain.crypto;

public interface CryptoService {

    String encrypt(String plainText);

    String decrypt(String cipherText);
}
