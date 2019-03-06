/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.common.util.TokenUtil;
import cn.itgardener.xkp.core.mapper.ClassMapper;
import cn.itgardener.xkp.core.mapper.ManagerMapper;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.vo.ManagerVo;
import cn.itgardener.xkp.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengyi on 17-7-29.
 */
@Service
public class ManagerServiceImpl implements ManagerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ManagerMapper managerMapper;
    private final ClassMapper classMapper;

    @Autowired
    public ManagerServiceImpl(ManagerMapper managerMapper, ClassMapper classMapper) {
        this.managerMapper = managerMapper;
        this.classMapper = classMapper;
    }

    @Override
    public Map<String, Object> postManager(Manager manager) throws XkpException {
        Manager managerCondition = new Manager();
        managerCondition.setUsername(manager.getUsername());
        List<Manager> managers = managerMapper.selectByCondition(managerCondition);
        if (0 < managers.size()) {
            throw new XkpException("该管理员账户已经存在!");
        }
        Map<String, Object> rtv = null;
        manager.setPassword(TokenUtil.getPassword());
        if (null != manager.getAcademyId() && null == manager.getClassId() && null == manager.getGrade()) {
            manager.setStatus(true);
        } else {
            manager.setStatus(false);
        }
        int flag = managerMapper.insert(manager);
        if (0 < flag) {
            rtv = new HashMap<>();
            rtv.put("username", manager.getUsername());
            rtv.put("password", manager.getPassword());
        }
        return rtv;
    }

    @Override
    public List<Map<String, Object>> getManager(Manager manager) {
        List<Map<String, Object>> rtv = new ArrayList<>();
        List<Manager> managers = managerMapper.selectByCondition(manager);
        for (Manager managerTmp : managers) {
            Map<String, Object> oneManager = new HashMap<>();
            oneManager.put("systemId", managerTmp.getSystemId());
            oneManager.put("username", managerTmp.getUsername());
            rtv.add(oneManager);
        }
        return rtv;
    }

    @Override
    public List<String> getGrade() {
        return managerMapper.selectGrade();
    }

    @Override
    public Map<String, String> putManagerReset(int systemId) {
        Map<String, String> rtv = null;
        Manager manager = new Manager();
        manager.setSystemId(systemId);
        manager.setPassword(TokenUtil.getPassword());
        int flag = managerMapper.updatePasswordBySystemId(manager);
        if (0 < flag) {
            manager = managerMapper.selectBySystemId(systemId);
            rtv = new HashMap<>();
            rtv.put("username", manager.getUsername());
            rtv.put("password", manager.getPassword());
        }
        return rtv;
    }

    @Override
    public Map<String, Object> postLogin(Manager manager) throws XkpException {
        Map<String, Object> rtv = null;
        List<Manager> managers = managerMapper.selectByCondition(manager);
        if (null != managers && 1 == managers.size()) {
            manager = managers.get(0);
            if (manager.getStatus()) {
                manager.setToken(TokenUtil.getToken());
                if (0 < managerMapper.updateTokenBySystemId(manager)) {
                    rtv = new HashMap<>();
                    manager = managers.get(0);
                    rtv.put("systemId", manager.getSystemId());
                    rtv.put("token", manager.getToken());
                    rtv.put("type", manager.getType());
                    rtv.put("grade", manager.getGrade());
                    rtv.put("academyId", manager.getAcademyId());
                    rtv.put("specialtyId", manager.getSpecialtyId());
                    rtv.put("classId", manager.getClassId());
                }
            } else {
                throw new XkpException("当前用户已被禁用!");
            }
        } else {
            throw new XkpException("用户名或密码不正确!");
        }
        return rtv;
    }

    @Override
    public boolean putManagerChange(ManagerVo managerVo) throws XkpException {
        boolean rtv = false;
        Manager manager = managerMapper.selectBySystemId(managerVo.getSystemId());
        if (manager.getPassword().equals(managerVo.getOldPassword())) {
            manager.setPassword(managerVo.getNewPassword());
            if (0 < managerMapper.updatePasswordBySystemId(manager)) {
                rtv = true;
            }
        } else {
            throw new XkpException("旧密码不正确!");
        }

        return rtv;
    }

    @Override
    public boolean deleteManager(int systemId) {
        boolean rtv = false;
        if (0 < managerMapper.deleteBySystemId(systemId)) {
            rtv = true;
        }
        return rtv;
    }

    @Override
    public boolean deleteGrade(String grade) {
        managerMapper.deleteByGrade(grade);
        classMapper.deleteByGrade(grade);

        return true;
    }

    @Override
    public boolean putLogin(ManagerVo managerVo) {
        boolean rtv = false;
        managerVo.setType("C");

        ManagerVo oldManagerVo = new ManagerVo();
        oldManagerVo.setType(managerVo.getType());
        oldManagerVo.setStatus(!managerVo.getStatus());

        if (0 < managerMapper.updateByCondition(oldManagerVo) && 0 < managerMapper.updateByCondition(managerVo)) {
            rtv = true;
        }
        return rtv;
    }

    @Override
    public boolean postGrade(Manager manager) {
        boolean rtv = false;
        manager.setUsername(manager.getGrade());
        if (0 < managerMapper.insert(manager)) {
            rtv = true;
        }
        return rtv;
    }

}
