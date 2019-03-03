
/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.model.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HistoryMapper {

    /**
     * 在历史记录表中插入数据
     *
     * @param history
     * @return
     */
    @Insert("INSERT INTO xkp_history (academy_id,academy_name,specialty_id,specialty_name,class_id,class_name,grade,title_date,courses,benchmark_data) VALUES (#{academyId},#{academyName},#{specialtyId},#{specialtyName},#{classId},#{className},#{grade},#{titleDate},#{courses},#{benchmarkData})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(History history);

    /**
     * 查询历史表中年级
     *
     * @return
     */
    @Select("SELECT DISTINCT grade FROM xkp_history WHERE grade IS NOT NULL")
    List<String> selectGrade();

    /**
     * 查询历史大表
     *
     * @return
     */
    @Select("SELECT benchmark_data AS benchmarkData,courses FROM xkp_history WHERE system_id=#{titleId}")
    History selectByCondition(int titleId);

    /**
     * 查询大表表头
     *
     * @return
     */
    @Select("SELECT system_id AS systemId, title_date AS titleDate FROM xkp_history WHERE title_date IS NOT NULL and class_id=#{classId}")
    List<History> selectTitle(int classId);
}
