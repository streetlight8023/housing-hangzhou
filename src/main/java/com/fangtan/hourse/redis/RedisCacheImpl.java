package com.fangtan.hourse.redis;

import com.alibaba.fastjson.JSON;
import com.fangtan.hourse.enumConfig.RedisExpireEnum;
import com.fangtan.hourse.enumConfig.RedisIndexEnum;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @desc: 包装了对redis 6种数据结构使用的常见场景：
 * 1 字符串(strings), 2 散列(hashes), 3 列表(lists), 4 集合(sets), 5 有序集合(sorted sets), 6 地理空间(geospatial)
 * @date: 2019/1/11 14:59
 * @author: weiqingeng
 */
@Service("redisCache")
public class RedisCacheImpl<T> implements IRedisCache<T> {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheImpl.class);

    private boolean redisCacheSwitch =true;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public void set(RedisIndexEnum dbIndexEnum, String key, Object value, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || value == null) {
            return;
        }
        RedisConnection connection = null;
        try {
            if (null != dbIndexEnum) {
                connection = this.redisTemplate.getConnectionFactory().getConnection();
                connection.select(dbIndexEnum.getIndex());
                connection.set(this.redisTemplate.getStringSerializer().serialize(key),
                        this.redisTemplate.getValueSerializer().serialize(value),
                        Expiration.seconds(seconds.getSeconds()), RedisStringCommands.SetOption.UPSERT);
            } else {
                this.redisTemplate.opsForValue().set(key, value, seconds.getSeconds(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("set e:{}", e);
        } finally {
            connection.close();
        }

        return;
    }


    @Override
    public Object get(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            if (null != dbIndexEnum) {
                connection = this.redisTemplate.getConnectionFactory().getConnection();
                connection.select(dbIndexEnum.getIndex());
                Object object = redisTemplate.getValueSerializer().deserialize(connection.get(key.getBytes()));
                return object;
            } else {
                return this.redisTemplate.opsForValue().get(key);
            }
        } catch (Exception e) {
            logger.error("get e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public Set<String> keys(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            if (null != dbIndexEnum) {
                redisTemplate.setDefaultSerializer(new StringRedisSerializer());

                connection = this.redisTemplate.getConnectionFactory().getConnection();
                connection.select(dbIndexEnum.getIndex());
                key =key+"*";
                Set<byte[]> keys = connection.keys(key.getBytes());
                Set<String> result = Sets.newHashSet();
                for (byte[] bytes : keys) {
                    result.add(new String(bytes));
                }
                return  result;
            }
        } catch (Exception e) {
            logger.error("get e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public void hSet(RedisIndexEnum dbIndexEnum, String key, String hashKey, Object value, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key) {
            return;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            connection.hSet(this.redisTemplate.getStringSerializer().serialize(key), this.redisTemplate.getStringSerializer().serialize(hashKey),
                    this.redisTemplate.getValueSerializer().serialize(value));
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
        } catch (Exception e) {
            logger.error("hPut e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }


    @Override
    public Object hGet(RedisIndexEnum dbIndexEnum, String key, String hashKey) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            byte[] res = connection.hGet(this.redisTemplate.getStringSerializer().serialize(key), this.redisTemplate.getStringSerializer().serialize(hashKey));
            return redisTemplate.getValueSerializer().deserialize(res);
        } catch (Exception e) {
            logger.error("hGet e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public Long hSetAll(RedisIndexEnum dbIndexEnum, String key, Map<String, Object> hashMap, RedisExpireEnum seconds) {
        Long result = null;
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            //切换数据库
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());

            Map<byte[], byte[]> mapBytes = new HashMap<>(16);

            //事务开启
            connection.multi();
            this.del(dbIndexEnum, key);
            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                mapBytes.put(this.redisTemplate.getStringSerializer().serialize(entry.getKey()),
                        this.redisTemplate.getValueSerializer().serialize(entry.getValue()));
            }
            connection.hMSet(this.redisTemplate.getStringSerializer().serialize(key), mapBytes);
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            connection.exec();
        } catch (Exception e) {
            logger.error("hSetAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return result;
    }


    @Override
    public Map<Object, Object> hGetAll(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            Map<byte[], byte[]> mapBytes = connection.hGetAll(this.redisTemplate.getStringSerializer().serialize(key));
            Map<Object, Object> res = null;
            if (mapBytes != null && mapBytes.size() > 0) {
                res = new HashMap<>(16);
                for (Map.Entry<byte[], byte[]> entry : mapBytes.entrySet()) {
                    res.put(this.redisTemplate.getStringSerializer().deserialize(entry.getKey()),
                            this.redisTemplate.getValueSerializer().deserialize(entry.getValue()));
                }
            }
            return res;
        } catch (Exception e) {
            logger.error("hGetAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public <T> void listPushAll(RedisIndexEnum dbIndexEnum, String key, List<T> value, @NotNull RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || CollectionUtils.isEmpty(value)) {
            return;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            //事务开启
            connection.multi();
            this.del(dbIndexEnum, key);

            for (T t : value) {
                connection.rPush(this.redisTemplate.getStringSerializer().serialize(key),
                        this.redisTemplate.getValueSerializer().serialize(t));
            }
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            connection.exec();
        } catch (Exception e) {
            logger.error("listPushAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public boolean listPush(RedisIndexEnum dbIndexEnum, String key, Object value, @NotNull RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || null == value) {
            return false;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            Long res = connection.lPush(this.redisTemplate.getStringSerializer().serialize(key),
                    this.redisTemplate.getValueSerializer().serialize(value));
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            return res > 0;
        } catch (Exception e) {
            logger.error("listPush e:{}", e);
            return false;
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean isMember(RedisIndexEnum dbIndexEnum, String key, Object value) {
        if (!redisCacheSwitch || null == key || null == value) {
            return false;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            return connection.sIsMember(this.redisTemplate.getStringSerializer().serialize(key), this.redisTemplate.getValueSerializer().serialize(value));
        } catch (Exception e) {
            logger.error("listPush e:{}", e);
            return false;
        } finally {
            connection.close();
        }
    }

    @Override
    public List<T> listGetAll(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            logger.info("redisCacheSwitch or key is null");
            return null;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            List<T> list = null;
            List<byte[]> listByte = connection.lRange(key.getBytes(), 0, -1);
            if (!CollectionUtils.isEmpty(listByte)) {
                logger.info("list is not empty :" + listByte.size());
                list = new ArrayList<>();
                for (byte[] bytes : listByte) {
                    list.add((T) redisTemplate.getValueSerializer().deserialize(bytes));
                }
            }
            return list;
        } catch (Exception e) {
            logger.error("listGetAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public <T> void setAddAll(RedisIndexEnum dbIndexEnum, String key, Set<T> value, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || CollectionUtils.isEmpty(value)) {
            return;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            //事务开启
            connection.multi();
            this.del(dbIndexEnum, key);

            for (T t : value) {
                connection.sAdd(this.redisTemplate.getStringSerializer().serialize(key),
                        this.redisTemplate.getValueSerializer().serialize(t));
            }
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            connection.exec();
        } catch (Exception e) {
            logger.error("setAddAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public <T> void sadd(RedisIndexEnum dbIndexEnum, String key, String value, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || Strings.isNullOrEmpty(value)) {
            return;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            //事务开启
            connection.multi();
            connection.sAdd(this.redisTemplate.getStringSerializer().serialize(key),
                    this.redisTemplate.getValueSerializer().serialize(value));
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            connection.exec();
        } catch (Exception e) {
            logger.error("setAddAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Set<T> getAllSet(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            Set<byte[]> set = connection.sMembers(this.redisTemplate.getStringSerializer().serialize(key));
            Set<T> res = null;
            if (!CollectionUtils.isEmpty(set)) {
                res = new HashSet<>();
                for (byte[] bytes : set) {
                    res.add((T) redisTemplate.getValueSerializer().deserialize(bytes));
                }
            }
            return res;
        } catch (Exception e) {
            logger.error("getAllSet e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public void zSetAddAll(RedisIndexEnum dbIndexEnum, String key, Set<ZSetOperations.TypedTuple<Object>> value, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key || CollectionUtils.isEmpty(value)) {
            return;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            int score = 1;

            //事务开启
            connection.multi();
            this.del(dbIndexEnum, key);

            for (Object t : value) {
                connection.zAdd(this.redisTemplate.getStringSerializer().serialize(key), score,
                        this.redisTemplate.getValueSerializer().serialize(t));
                score++;
            }
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            connection.exec();
        } catch (Exception e) {
            logger.error("zSetAddAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }


    @Override
    public Set<T> zGetAll(RedisIndexEnum dbIndexEnum, String key) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            Set<byte[]> set = connection.zRange(this.redisTemplate.getStringSerializer().serialize(key), 0, -1);
            Set<T> res = null;
            if (!CollectionUtils.isEmpty(set)) {
                res = new HashSet<>();
                for (byte[] bytes : set) {
                    res.add((T) redisTemplate.getValueSerializer().deserialize(bytes));
                }
                connection.close();
                return res;
            }
        } catch (Exception e) {
            logger.error("zGetAll e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public Long incrBy(RedisIndexEnum dbIndexEnum, String key, Long incr, RedisExpireEnum seconds) {
        if (!redisCacheSwitch || null == key) {
            return null;
        }
        RedisConnection connection = null;
        try {
            if (null == incr) {
                incr = 1L;
            }
            // 切换redis仓库
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            Long result = connection.incrBy(this.redisTemplate.getStringSerializer().serialize(key), incr);
            connection.expire(this.redisTemplate.getStringSerializer().serialize(key), seconds.getSeconds());
            return result;
        } catch (Exception e) {
            logger.error("incrBy e:{}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    @Override
    public void del(RedisIndexEnum dbIndexEnum, String key) {
        RedisConnection connection = null;
        try {
            // 切换redis仓库
            connection = this.redisTemplate.getConnectionFactory().getConnection();
            connection.select(dbIndexEnum.getIndex());
            connection.del(this.redisTemplate.getStringSerializer().serialize(key));
        } catch (Exception e) {
            logger.error("del e={}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public <T> T hGet(RedisIndexEnum dbIndexEnum, String key,String field, Class<T> clazz) {
        try {
            Object object = this.hGet(dbIndexEnum, key,field);
            return JSON.parseObject(String.valueOf(object), clazz);
        } catch (Exception var6) {
            logger.error("ns:{},key:{}", new Object[]{key, var6});
            return null;
        }
    }

}
