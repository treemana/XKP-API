/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.web;

import cn.itgardener.xkp.common.util.JsonUtil;
import cn.itgardener.xkp.core.model.Academy;
import cn.itgardener.xkp.core.model.Manager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * Created by zhengyi on 17-7-25.
 */
public class ManageApiTest extends RootApiTest {

    @Autowired
    private ManageApi manageApi;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(manageApi);
        mockMvc = MockMvcBuilders.standaloneSetup(manageApi).build();
        Assert.assertNotNull(mockMvc);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void academy() throws Exception {
        // postAcademy
        Academy academy = new Academy();
        academy.setName("test");
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/academy")
                .contentType(MediaType.APPLICATION_JSON).content(JsonUtil.getJsonString(academy)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        int academyId = Integer.parseInt(super.getData(mvcResult).toString());
        Assert.assertNotEquals(0, academyId);

        // getAcademy
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/academy"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List list = (List) super.getData(mvcResult);
        Assert.assertNotNull(list);
        Assert.assertNotEquals(0, list.size());

        // deleteAcademy
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/academy/" + academyId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assert.assertEquals(true, super.getData(mvcResult));
    }

    @Test
    public void manager() throws Exception {
        Manager manager = new Manager();

    }
}