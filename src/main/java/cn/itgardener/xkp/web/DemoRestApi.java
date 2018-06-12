/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.web;

import cn.itgardener.xkp.common.RestData;
import cn.itgardener.xkp.core.model.Academy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengyi on 17-7-18.
 */
@RestController
public class DemoRestApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/pathp", method = RequestMethod.GET)
    public RestData demoRe(@RequestParam(value = "pp", defaultValue = "123") String pp) {
        logger.debug("function DemoRe, method GET debug");
        logger.info("function DemoRe, method GET info");
        logger.warn("function DemoRe, method GET warn");
        logger.error("function DemoRe, method GET error");
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        return new RestData(list);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RestData demoInsert(@RequestParam(value = "name", defaultValue = "123") String name) {
        Academy academy = new Academy();
        academy.setName(name);
        return new RestData(academy);
    }
}
