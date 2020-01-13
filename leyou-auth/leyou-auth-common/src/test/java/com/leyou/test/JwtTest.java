package com.leyou.test;

import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import com.leyou.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "F:\\ideaWork\\leyouProject\\rsa\\rsa.pub";

    private static final String priKeyPath = "F:\\ideaWork\\leyouProject\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3NjgxNTExMH0.CDFXLeldE7i7FVu-s-gm_Q5lNmD4ij3iirci9Gr_QU_c3HpzqKYSZxZeJ-WXKth-8ckPKjMenE0ytVi1BcwahYLAWxgld287tAdlVe2cJ9Q6UAXQ03vYNz-M7SjHRrDoWRv4v9GU9bcFTm6_M17XK0qupRKyc8sFkMehAeNyd5E";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
