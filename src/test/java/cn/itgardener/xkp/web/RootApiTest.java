/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.web;

import cn.itgardener.xkp.common.util.JsonUtil;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by zhengyi on 17-8-25 下午3:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RootApiTest {

    MockMvc mockMvc;
    MvcResult mvcResult;

    Object getData(MvcResult mvcResult) throws UnsupportedEncodingException {
        Assert.assertNotNull(mvcResult);
        Assert.assertNotNull(mvcResult.getResponse());
        String jsonString = mvcResult.getResponse().getContentAsString();
        Assert.assertNotNull(jsonString);
        Map<String, Object> map = JsonUtil.getMapFromJson(jsonString);
        Assert.assertEquals(0, Integer.parseInt(map.get("code").toString()));
        Object data = map.get("data");
        Assert.assertNotNull(data);
        return data;
    }
}
