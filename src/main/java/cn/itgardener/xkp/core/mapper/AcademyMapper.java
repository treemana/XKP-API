/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.mapper.provider.AcademyProvider;
import cn.itgardener.xkp.core.model.Academy;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhengyi on 17-7-18.
 * <p>
 * Described by You-Bian on 17-10-30.
 * <p>
 * Repository   注解方式开发并且没有配置XML来包扫描的时候需要加这个注解
 * <p>
 * Mapper       表明该接口是一个mapper
 * <p>
 * Options      设置缓存时间,能够为对象生成自增的ID
 * <p>
 * useGeneratedKeys:启用生成主键
 * <p>
 * keyProperty:选择键
 * <p>
 * ResultType   设置返回的数据类型
 * <p>
 * CURD:可直接利用下边的注解进行简单的SQL语句的构建,而不是用XML的MAPPER文件
 * <p>
 * Provider     选择语句提供者,可以利用下边的注解来进行比较复杂的SQL语句的构建,也就是充满IF等条件的动态SQL的构建
 * 使用的是Mybatis的语法来构造sql语句
 * type:为选择指定的类作为sql语句的提供者
 * method:选择制定的方法来构造sql
 * 使用的Provider不需要进行额外的声明,直接import就可以了
 * Provider的方法的参数和返回值都在mapper的接口中进行定义,Provider中对应的方法的名字、参数、返回值和mapper中完全相同
 */
@Mapper
@Repository
public interface AcademyMapper {

    @Insert("INSERT INTO xkp_academy(name) VALUES(#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "systemId")
    int insert(Academy academy);

    @SelectProvider(type = AcademyProvider.class, method = "getAllAcademy")
    @ResultType(Academy.class)
    List<Academy> selectAll();

    @Select("SELECT * FROM xkp_academy WHERE system_id=#{systemId}")
    Academy selectAcademyById(int systemId);

    @Delete("DELETE FROM xkp_academy WHERE system_id=#{systemId}")
    int deleteBySystemId(int systemId);
}
