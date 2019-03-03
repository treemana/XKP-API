/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

import cn.itgardener.xkp.common.RestData;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.vo.ScoreVo;

import java.util.List;
import java.util.Map;

/**
 * Created by Hunter-Yi on 17-9-12 上午9:40
 */
public interface ScoreService {

    List<Map<String, Object>> getScoreByClassId(int classId);

    boolean postScore(ScoreVo scoreVo);

    boolean putScore(ScoreVo scoreVo);

    void deleteAll();

    RestData postScoreTable(String fileName, Manager currentUser);

}
