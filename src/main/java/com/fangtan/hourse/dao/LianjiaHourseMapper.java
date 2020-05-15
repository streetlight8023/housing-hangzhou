package com.fangtan.hourse.dao;


import com.fangtan.hourse.domain.HzZoneStatic;
import com.fangtan.hourse.domain.LianjiaHourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author luoxiao @date 2019/1/29 11:25
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface LianjiaHourseMapper {

    void insert(LianjiaHourse LianjiaHourse);

    List<LianjiaHourse> findAll();

    List<LianjiaHourse> findByCode(@Param("housecode") String housecode);

    void updateNowPrice(LianjiaHourse LianjiaHourse);

    void offSale(@Param("housecode") String housecode);



}
