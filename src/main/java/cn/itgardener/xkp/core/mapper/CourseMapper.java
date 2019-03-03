/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.mapper.provider.CourseProvider;
import cn.itgardener.xkp.core.model.Course;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Hunter-Yi on 17-8-30 下午10:14
 */
@Mapper
@Repository
public interface CourseMapper {

    @Insert("INSERT INTO xkp_course (class_id,credit,name,`type`) VALUES (#{classId},#{credit},#{name},#{type})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Course course);

    @SelectProvider(type = CourseProvider.class, method = "selectByCondition")
    List<Course> selectByCondition(Course course);

    @Delete("DELETE FROM xkp_course WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);

    /**
     * 删除所有课程
     *
     * @return 删除结果数量
     */
    @Delete("DELETE FROM xkp_course")
    int deleteAll();
}
