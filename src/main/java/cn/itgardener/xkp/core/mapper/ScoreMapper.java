/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper;

import cn.itgardener.xkp.core.mapper.provider.ScoreProvider;
import cn.itgardener.xkp.core.model.Score;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Hunter-Yi on 17-9-11 下午10:06
 */
@Mapper
@Repository
public interface ScoreMapper {

    @Insert("INSERT INTO xkp_score (student_id,course_id,`type`,examination,inspection) VALUES (#{studentId},#{courseId},#{type},#{examination},#{inspection})")
    int insert(Score score);

    @SelectProvider(type = ScoreProvider.class, method = "selectByCondition")
    List<Score> selectByCondition(Score score);

    @UpdateProvider(type = ScoreProvider.class, method = "updateByCondition")
    int updateByCondition(Score score);

    @Delete("DELETE FROM xkp_score")
    int deleteAll();
}
