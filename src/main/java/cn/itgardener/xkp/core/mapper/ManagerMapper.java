/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.mapper.provider.ManagerProvider;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.vo.ManagerVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhengyi on 17-7-25.
 */
@Mapper
@Repository
public interface ManagerMapper {

    @InsertProvider(type = ManagerProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Manager manager);

    @Select("SELECT * FROM xkp_manager WHERE system_id=#{systemId};")
    @Results({
            @Result(property = "systemId", column = "system_id"),
            @Result(property = "academyId", column = "academy_id"),
            @Result(property = "specialty_id", column = "specialtyId"),
            @Result(property = "classId", column = "class_id"),
            @Result(property = "grade", column = "grade"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "type", column = "type"),
            @Result(property = "status", column = "status"),
            @Result(property = "token", column = "token")
    })
    Manager selectBySystemId(int systemId);

    @SelectProvider(type = ManagerProvider.class, method = "selectByCondition")
    List<Manager> selectByCondition(Manager manager);

    @Select("SELECT DISTINCT grade FROM xkp_manager WHERE grade IS NOT NULL")
    List<String> selectGrade();

    @Update("UPDATE xkp_manager SET password=#{password} WHERE system_id=#{systemId}")
    int updatePasswordBySystemId(Manager manager);

    @Update("UPDATE xkp_manager SET token=#{token} WHERE system_id=#{systemId}")
    int updateTokenBySystemId(Manager manager);

    @UpdateProvider(type = ManagerProvider.class, method = "updateByCondition")
    int updateByCondition(ManagerVo managerVo);

    @Delete("DELETE FROM xkp_manager WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);

    @Delete("DELETE FROM xkp_manager WHERE grade=#{grade}")
    int deleteByGrade(String grade);
}
