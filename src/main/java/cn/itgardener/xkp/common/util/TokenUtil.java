/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.common.util;

import cn.itgardener.xkp.core.mapper.ManagerMapper;
import cn.itgardener.xkp.core.model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhengyi on 17-7-29.
 */
@Component
public class TokenUtil {

    private static ManagerMapper managerMapper;

    @Autowired
    public void setManagerMapper(ManagerMapper managerMapper) {
        TokenUtil.managerMapper = managerMapper;
    }

    public static Manager getManagerByToken(HttpServletRequest request) {
        Manager manager = null;
        String token = request.getHeader("token");
        if (null != token) {
            Manager managerCondition = new Manager();
            managerCondition.setToken(token);
            List<Manager> managers = managerMapper.selectByCondition(managerCondition);
            if (1 == managers.size()) {
                manager = managers.get(0);
            }
        }
        return manager;
    }

    public static String getPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String getToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
