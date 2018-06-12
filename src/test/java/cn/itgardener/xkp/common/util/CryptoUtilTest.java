/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by zhengyi on 17-8-8 下午4:18
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoUtilTest {

    @Test
    public void encrypt() throws Exception {
        String source = "encrypt test text";
        String stringEncrypted = CryptoUtil.encrypt(source);
        System.out.println(stringEncrypted);
        Assert.assertNotNull(stringEncrypted);
        String stringDecrypted = CryptoUtil.decrypt(stringEncrypted);
        System.out.println(stringDecrypted);
        Assert.assertNotNull(stringDecrypted);
    }

    @Test
    public void decrypt() throws Exception {
    }

}