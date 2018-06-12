/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

import cn.itgardener.xkp.core.model.Benchmark;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Hunter-Yi on 17-9-13 上午11:56
 */
public interface BenchmarkService {

    List<Benchmark> getBenchmarkByClassId(int classId);

    void downLoadBenchmarkDocxByClassId(int classId, HttpServletResponse response);

    void downLoadBenchmarkXlsxByClassId(int classId, HttpServletResponse response);
}
