/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

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

}
