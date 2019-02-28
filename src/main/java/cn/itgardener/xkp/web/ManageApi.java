/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.web;

import cn.itgardener.xkp.common.ErrorMessage;
import cn.itgardener.xkp.common.RestData;
import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.common.util.JsonUtil;
import cn.itgardener.xkp.common.util.TokenUtil;
import cn.itgardener.xkp.core.mapper.AcademyMapper;
import cn.itgardener.xkp.core.mapper.ClassMapper;
import cn.itgardener.xkp.core.mapper.SpecialtyMapper;
import cn.itgardener.xkp.core.model.Academy;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.Specialty;
import cn.itgardener.xkp.core.model.vo.ManagerVo;
import cn.itgardener.xkp.service.HistoryService;
import cn.itgardener.xkp.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhengyi on 17-7-24.
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
public class ManageApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AcademyMapper academyMapper;
    private final ClassMapper classMapper;
    private final SpecialtyMapper specialtyMapper;
    private final HistoryService historyService;
    private final ManagerService managerService;

    @Autowired
    public ManageApi(AcademyMapper academyMapper, ManagerService managerService, SpecialtyMapper specialtyMapper,
                     ClassMapper classMapper, HistoryService historyService) {
        this.academyMapper = academyMapper;
        this.managerService = managerService;
        this.specialtyMapper = specialtyMapper;
        this.classMapper = classMapper;
        this.historyService = historyService;
    }

    @RequestMapping(value = "/academy", method = RequestMethod.POST)
    public RestData postAcademy(@RequestBody Academy academy, HttpServletRequest request) {
        logger.info("POST postAcademy : " + JsonUtil.getJsonString(academy));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        int flag = academyMapper.insert(academy);
        if (0 < flag) {
            return new RestData(academy.getSystemId());
        } else {
            return new RestData(1, ErrorMessage.POST_ACADEMY_FAILED);
        }
    }

    @RequestMapping(value = "/academy", method = RequestMethod.GET)
    public RestData getAcademy(HttpServletRequest request) {
        logger.info("GET getAcademy : ");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        List<Academy> academies = academyMapper.selectAll();
        return new RestData(academies);
    }

    @RequestMapping(value = "/academy/{systemId}", method = RequestMethod.GET)
    public RestData getAcademyName(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("GET getAcademyName : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        String data = null;
        Academy academy = academyMapper.selectAcademyById(systemId);
        if (null != academy) {
            data = academy.getName();
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/academy/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteAcademy(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteAcademy : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = false;
        if (0 < academyMapper.deleteBySystemId(systemId)) {
            data = true;
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/manager", method = RequestMethod.POST)
    public RestData postManager(@RequestBody Manager manager, HttpServletRequest request) {
        logger.info("POST postManager : " + JsonUtil.getJsonString(manager));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if ("A".equals(currentUser.getType())) {
            if (null == manager.getAcademyId() || (null != manager.getSpecialtyId() ^ null != manager.getClassId())
                    || (null != manager.getSpecialtyId() ^ null != manager.getGrade())) {
                return new RestData(1, ErrorMessage.PARAMATER_ERROR);
            }
        } else if ("B".equals(currentUser.getType())) {
            if (null == manager.getAcademyId() || null == manager.getSpecialtyId() || null == manager.getClassId() || null == manager.getGrade()) {
                return new RestData(1, ErrorMessage.PARAMATER_ERROR);
            }
        } else {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        Map<String, Object> data = null;
        try {
            data = managerService.postManager(manager);
        } catch (XkpException e) {
            return new RestData(1, e.getMessage());
        }
        if (null != data) {
            return new RestData(data);
        } else {
            return new RestData(1, ErrorMessage.POST_MANAGER_FAILED);
        }
    }

    @RequestMapping(value = "/manager", method = RequestMethod.GET)
    public RestData getManager(Manager manager, HttpServletRequest request) {
        logger.info("GET getManager : " + JsonUtil.getJsonString(manager));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if ("A".equals(currentUser.getType())) {
            if (null == manager.getAcademyId() || (null != manager.getSpecialtyId() ^ null != manager.getClassId())
                    || (null != manager.getSpecialtyId() ^ null != manager.getGrade())) {
                return new RestData(1, ErrorMessage.PARAMATER_ERROR);
            }
        } else if ("B".equals(currentUser.getType())) {
            if (null == manager.getAcademyId() || null == manager.getSpecialtyId() || null == manager.getClassId() || null == manager.getGrade()) {
                return new RestData(1, "请精确到班级!");
            }
        } else {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        List<Map<String, Object>> data = managerService.getManager(manager);
        return new RestData(data);
    }

    @RequestMapping(value = "/manager/reset/{systemId}", method = RequestMethod.PUT)
    public RestData putManagerReset(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("PUT putManagerReset : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        Map<String, String> data = managerService.putManagerReset(systemId);
        if (null != data) {
            return new RestData(data);
        } else {
            return new RestData(1, ErrorMessage.MANAGER_NOT_EXIST);
        }
    }

    @RequestMapping(value = "/manager/change", method = RequestMethod.PUT)
    public RestData putManagerChange(@RequestBody ManagerVo managerVo, HttpServletRequest request) {
        logger.info("PUT putManagerChange : " + JsonUtil.getJsonString(managerVo));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        boolean data;
        try {
            data = managerService.putManagerChange(managerVo);
        } catch (XkpException e) {
            return new RestData(1, e.getMessage());
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/manager/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteManager(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteManager : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = managerService.deleteManager(systemId);
        return new RestData(data);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RestData postLogin(@RequestBody Manager manager) {
        logger.info("POST postLogin : " + JsonUtil.getJsonString(manager));
        try {
            Map<String, Object> data = managerService.postLogin(manager);
            return new RestData(data);
        } catch (XkpException e) {
            return new RestData(1, e.getMessage());
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.PUT)
    public RestData putLogin(@RequestBody ManagerVo managerVo, HttpServletRequest request) {
        logger.info("PUT putLogin : " + JsonUtil.getJsonString(managerVo));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean rtv = managerService.putLogin(managerVo);
        return new RestData(rtv);
    }

    @RequestMapping(value = "/grade", method = RequestMethod.POST)
    public RestData postGrade(@RequestBody Manager manager, HttpServletRequest request) {
        logger.info("POST postGrade : " + manager.getGrade());
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = managerService.postGrade(manager);
        return new RestData(data);
    }

    @RequestMapping(value = "/grade", method = RequestMethod.GET)
    public RestData getGrade(HttpServletRequest request) {
        logger.info("GET getGrade");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        List<String> data = managerService.getGrade();
        return new RestData(data);
    }

    @RequestMapping(value = "/grade/{grade}", method = RequestMethod.DELETE)
    public RestData deleteGrade(@PathVariable(value = "grade") String grade, HttpServletRequest request) {
        logger.info("GET getGrade");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = managerService.deleteGrade(grade);
        return new RestData(data);
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public RestData getData(HttpServletRequest request) {
        logger.info("GET getData");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = historyService.postHistory();
        return new RestData(data);
    }

    @RequestMapping(value = "/specialty", method = RequestMethod.POST)
    public RestData postSpecialty(@RequestBody Specialty specialty, HttpServletRequest request) {
        logger.info("POST postSpecialty : " + JsonUtil.getJsonString(specialty));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        int flag = specialtyMapper.insert(specialty);
        if (0 < flag) {
            return new RestData(specialty.getSystemId());
        } else {
            return new RestData(1, ErrorMessage.POST_SPECIALTY_FAILED);
        }
    }

    @RequestMapping(value = "/specialty/{academyId}", method = RequestMethod.GET)
    public RestData getSpecialty(@PathVariable(value = "academyId") int academyId, HttpServletRequest request) {
        logger.info("GET getSpecialty : academyId=" + academyId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        List<Specialty> data = specialtyMapper.selectByAcademyId(academyId);
        return new RestData(data);
    }

    @RequestMapping(value = "/specialty-name/{systemId}", method = RequestMethod.GET)
    public RestData getSpecialtyName(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("GET getSpecialtyName : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        String data = null;
        Specialty specialty = specialtyMapper.selectBySystemId(systemId);
        if (null != specialty) {
            data = specialty.getName();
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/specialty/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteSpecialty(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteSpecialty : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = false;
        if (0 < specialtyMapper.deleteBySystemId(systemId)) {
            data = true;
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/class", method = RequestMethod.POST)
    public RestData postClass(@RequestBody Class xkpClass, HttpServletRequest request) {
        logger.info("POST postClass : " + JsonUtil.getJsonString(xkpClass));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        if (null == xkpClass.getSpecialtyId() || null == xkpClass.getGrade()) {
            return new RestData(1, ErrorMessage.PARAMATER_ERROR);
        }
        int flag = classMapper.insert(xkpClass);
        if (0 < flag) {
            return new RestData(xkpClass.getSystemId());
        } else {
            return new RestData(1, ErrorMessage.POST_ACADEMY_FAILED);
        }
    }

    @RequestMapping(value = "/class", method = RequestMethod.GET)
    public RestData getClass(Class xkpClass, HttpServletRequest request) {
        logger.info("GET getClass : " + xkpClass);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        List<Map<String, Object>> data = new ArrayList<>();
        List<Class> classes = classMapper.selectByCondition(xkpClass);
        for (Class oneClass : classes) {
            Map<String, Object> map = new HashMap<>();
            map.put("systemId", oneClass.getSystemId());
            map.put("name", oneClass.getName());
            data.add(map);
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/class/{systemId}", method = RequestMethod.GET)
    public RestData getClassName(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("GET getClassName : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        String data = null;
        Class xkpClass = classMapper.selectBySystemId(systemId);
        if (null != xkpClass) {
            data = xkpClass.getName();
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/class/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteClass(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteClass : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"A".equals(currentUser.getType()) && !"B".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = false;
        if (0 < classMapper.deleteBySystemId(systemId)) {
            data = true;
        }
        return new RestData(data);
    }
}
