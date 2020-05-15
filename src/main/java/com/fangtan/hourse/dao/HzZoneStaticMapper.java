package com.fangtan.hourse.dao;


import com.fangtan.hourse.domain.HzZoneStatic;

import java.util.List;

/**
 * @author luoxiao @date 2019/1/29 11:25
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface HzZoneStaticMapper {

    void insert(HzZoneStatic hzZoneStatic);

    List<HzZoneStatic> findAll();

}
