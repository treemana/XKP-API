package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.model.History;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HistoryMapper {

    /**
     * 在历史记录表中插入数据
     * @param history
     * @return
     */
    @Insert("INSERT INTO xkp_history (academy_id,academy_name,specialty_id,specialty_name,class_id,class_name,grade,title_date,courses,benchmark_data) VALUES (#{academyId},#{academyName},#{specialtyId},#{specialtyName},#{classId},#{className},#{grade},#{titleDate},#{courses},#{benchmarkData})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(History history);

    /**
     * 查询历史表中年级
     * @return
     */
    @Select("SELECT DISTINCT grade FROM xkp_history WHERE grade IS NOT NULL")
    List<String> selectGrade();

    /**
     * 查询历史大表
     * @return
     */
    @Select("SELECT benchmark_data AS benchmarkData,courses FROM xkp_history WHERE class_id=#{classId}")
    History selectByClassId(int classId);

    /**
     * 查询大表表头
     * @return
     */
    @Select("SELECT DISTINCT title_date FROM xkp_history WHERE title_date IS NOT NULL")
    List<String> selectTitle();
}
