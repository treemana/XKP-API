/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

import cn.itgardener.xkp.core.model.Benchmark;
import cn.itgardener.xkp.core.model.History;

import java.util.List;

/**
 * @author : zhengyi9
 * @date : 2018-06-27 16:39
 * @since : Java 8
 */
public interface HistoryService {

    /**
     * 将前一个学期数据转到历史数据并初始化当前学期
     *
     * @return 是否执行成功
     */
    boolean postHistory();

    /**
     * 获取历史大表
     * @param titleId
     * @return
     */
    List<Object> getHistory(int titleId);

    /**
     * 获取历史大表表头
     * @return
     */
    List<Object> getTitle(int classId);

    /**
     * 获取历史年级
     * @return
     */
    List<String> getGrade();

    /**
     * 获取历史课程
     * @param titleId
     * @return
     */
    List<Object> getCourses(int titleId);

}
