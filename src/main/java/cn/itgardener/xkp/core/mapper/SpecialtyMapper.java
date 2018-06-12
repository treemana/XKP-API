/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.model.Specialty;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhengyi on 17-7-29.
 */
@Mapper
@Repository
public interface SpecialtyMapper {

    @Insert("INSERT INTO xkp_specialty (academy_id,name) VALUES(#{academyId},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Specialty specialty);

    @Select("SELECT system_id AS systemId,academy_id AS academyId,name FROM xkp_specialty WHERE academy_id=#{academyId}")
    List<Specialty> selectByAcademyId(int academyId);

    @Select("SELECT system_id AS systemId,academy_id AS academyId,name FROM xkp_specialty WHERE system_id=#{systemId}")
    Specialty selectBySystemId(int systemId);

    @Delete("DELETE FROM xkp_specialty WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);
}
