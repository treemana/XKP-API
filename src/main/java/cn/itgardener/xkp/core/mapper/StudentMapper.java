/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.mapper.provider.StudentProvider;
import cn.itgardener.xkp.core.model.Student;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Hunter-Yi on 17-8-24 下午10:19
 */
@Mapper
@Repository
public interface StudentMapper {

    @Insert("INSERT INTO xkp_student (class_id,student_number,name) VALUES(#{classId},#{studentNumber},#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Student student);

    @SelectProvider(type = StudentProvider.class, method = "selectByCondition")
    List<Student> selectByCondition(Student student);

    @UpdateProvider(type = StudentProvider.class, method = "updateBySystemId")
    int updateBySystemId(Student student);

    @UpdateProvider(type = StudentProvider.class, method = "updateInit")
    int updateInit();

    @Delete("DELETE FROM xkp_student WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);
}
