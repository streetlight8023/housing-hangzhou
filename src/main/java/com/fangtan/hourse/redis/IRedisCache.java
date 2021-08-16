package com.fangtan.hourse.redis;

import com.fangtan.hourse.enumConfig.RedisExpireEnum;
import com.fangtan.hourse.enumConfig.RedisIndexEnum;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @desc: 操作redis 封装的方法
 * 数据6种类型：1 字符串(strings), 2 散列(hashes), 3 列表(lists), 4 集合(sets)  5 有序集合(sorted sets) 6 地理空间(geospatial)
 * @date: 2019/1/11 14:28
 * @author: weiqingeng
 */
public interface IRedisCache<T> {

    /**
     * 操作redis的String数据结构,添加数据
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @param value       值
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     * @return
     */
    void set(RedisIndexEnum dbIndexEnum, String key, Object value, RedisExpireEnum seconds);


    /**
     * 操作redis的String数据结构，获取数据
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @return
     */
    Object get(RedisIndexEnum dbIndexEnum, String key);

    Set<String> keys(RedisIndexEnum dbIndexEnum, String key);

    /**
     * 操作redis的Hash数据结构, key-vale设置数据, 只能单个key-value插入
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         整个hash的名
     * @param hashKey     hashmap中的某个key
     * @param value       hashKey对应的值
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     */
    void hSet(RedisIndexEnum dbIndexEnum, String key, String hashKey, Object value, RedisExpireEnum seconds);


    /**
     * 操作 redis的hash数据结构,获取单个key的值
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key
     * @param hashKey
     * @return
     */
    Object hGet(RedisIndexEnum dbIndexEnum, String key, String hashKey);

    <T> T hGet(RedisIndexEnum dbIndexEnum, String key, String field, Class<T> clazz);


    /**
     * 操作 redis的hash数据结构,存放在java中构建好的hashMap
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         整个hash的名
     * @param hashMap     java中形成的key，value键值对
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     */
    Long hSetAll(RedisIndexEnum dbIndexEnum, String key, Map<String, Object> hashMap, RedisExpireEnum seconds);


    /**
     * 操作redis的hash数据结构, 获取某个key的整个hashMap值
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         整个hash的名
     * @return 整个hash值
     */
    Map<Object, Object> hGetAll(RedisIndexEnum dbIndexEnum, String key);


    /**
     * 操作redis的list数据结构，存放java中构建好的list<T>对象
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @param value       值
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     */
    <T> void listPushAll(RedisIndexEnum dbIndexEnum, String key, List<T> value, RedisExpireEnum seconds);

    /**
     * 操作redis的list数据结构，构造列表
     *
     * @param dbIndexEnum
     * @param key
     * @param value
     * @param seconds
     */
    boolean listPush(RedisIndexEnum dbIndexEnum, String key, Object value, RedisExpireEnum seconds);


    /**
     * 查看value是否在list中已经存在
     *
     * @param dbIndexEnum
     * @param key
     * @param value
     * @param seconds
     */
    boolean isMember(RedisIndexEnum dbIndexEnum, String key, Object value);

    /**
     * 操作redis的list数据结构，获取整个list的值
     * <T>自定义泛型是为了 取出来的类型和存进去的类型一致
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @return
     */
    List<T> listGetAll(RedisIndexEnum dbIndexEnum, String key);

    /**
     * 操作redis的set不重复集合数据结构，存放java中构建好的Set<T>对象
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @param value       值
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     */
    <T> void setAddAll(RedisIndexEnum dbIndexEnum, String key, Set<T> value, RedisExpireEnum seconds);

    <T> void sadd(RedisIndexEnum dbIndexEnum, String key, String value, RedisExpireEnum seconds);

    /**
     * 操作redis的set不重复集合数据结构，获取整个set的值
     * <T>自定义泛型是为了 取出来的类型和存进去的类型一致
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @return
     */
    Set<T> getAllSet(RedisIndexEnum dbIndexEnum, String key);

    /**
     * 操作redis的zset有序不重复集合数据结构，存放java中构建好的Set<T>对象
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @param value       值
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     */
    void zSetAddAll(RedisIndexEnum dbIndexEnum, String key, Set<ZSetOperations.TypedTuple<Object>> value, RedisExpireEnum seconds);


    /**
     * 操作redis的zset有序不重复集合数据结构，获取整个set的值
     * <T>自定义泛型是为了 取出来的类型和存进去的类型一致
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @return
     */
    Set<T> zGetAll(RedisIndexEnum dbIndexEnum, String key);

    /**
     * 按照incr步阶自增长并返回增长后的数值
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         键
     * @param incr        步阶
     * @param seconds     key 失效时间 单位：秒,null代表永久保存
     * @return 增长后的数值
     */
    Long incrBy(RedisIndexEnum dbIndexEnum, String key, Long incr, RedisExpireEnum seconds);


    /**
     * 删除key
     *
     * @param dbIndexEnum redis的仓库  0-15 共16个仓库，请注意，存数据和取数据要选择一致的index
     * @param key         需要被删除的key
     */
    void del(RedisIndexEnum dbIndexEnum, String key);


}
