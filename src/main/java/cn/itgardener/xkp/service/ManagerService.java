/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.vo.ManagerVo;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengyi on 17-7-29.
 */
public interface ManagerService {

    List<Map<String, Object>> getManager(Manager manager);

    List<String> getGrade();

    Map<String, Object> postManager(Manager manager) throws XkpException;

    Map<String, Object> postLogin(Manager manager) throws XkpException;

    Map<String, String> putManagerReset(int systemId);

    boolean postGrade(Manager manager);

    boolean putManagerChange(ManagerVo managerVo) throws XkpException;

    boolean putLogin(ManagerVo managerVo);

    boolean deleteManager(int systemId);

    boolean deleteGrade(String grade);
}
