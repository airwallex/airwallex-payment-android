package com.airwallex.android;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// Just for test
public class JWTUtils {

    //airwallex_3ds_mobile
    private static String apiKey = "4b59a0f3-a25b-4b1e-8c07-33377187ad57";
    private static String apiIdentifier = "5e9d9fb612537c30ac7eb2a8";
    private static String orgUnitId = "5e9d9fb6be0e86347f60609a";

    public static String generateJWT() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // We will sign our JWT with our API Key
        byte[] apiKeySecretBytes = apiKey.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes,
                signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(randomUUIDString)
                .setIssuedAt(now)
                .setIssuer(apiIdentifier)
                .claim("OrgUnitId", orgUnitId)
                .claim("ReturnUrl", "http://requestbin.net/r/uu7y0yuu")
                .claim("ObjectifyPayload", false)
                //				.claim("ReferenceId", "jiaxli"+randomUUIDString)
                .signWith(signingKey, signatureAlgorithm);
        return builder.compact();
    }
}
