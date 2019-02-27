package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.model.History;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface HistoryMapper {

    @Insert("INSERT INTO xkp_history (academy_id,academy_name,specialty_id,specialty_name,class_id,class_name,grade,title_date,courses,benchmark_data) VALUES (#{academyId},#{academyName},#{specialtyId},#{specialtyName},#{classId},#{className},#{grade},#{titleDate},#{courses},#{benchmarkData})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(History history);
}
