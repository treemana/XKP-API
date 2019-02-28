/*
 * Copyright (c) 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.model.Class;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhengyi on 17-7-29.
 */
@Mapper
@Repository
public interface ClassMapper {

    @Insert("INSERT INTO xkp_class(specialty_id,grade,name) VALUES(#{specialtyId},#{grade},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Class xkpClass);

    @Select("SELECT system_id AS systemId,specialty_id AS specialtyId,grade,name FROM xkp_class")
    List<Class> selectAll();

    @Select("SELECT system_id AS systemId,specialty_id AS specialtyId,grade,name FROM xkp_class WHERE specialty_id=#{specialtyId} AND grade=#{grade}")
    List<Class> selectByCondition(Class xkpClass);

    @Select("SELECT system_id AS systemId,specialty_id AS specialtyId,grade,name FROM xkp_class WHERE system_id=#{systemId}")
    Class selectBySystemId(int systemId);

    @Delete("DELETE FROM xkp_class WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);

    @Delete("DELETE FROM xkp_class WHERE grade=#{grade}")
    int deleteByGrade(String grade);
}
