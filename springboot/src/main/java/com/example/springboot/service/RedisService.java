package com.example.springboot.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis服务接口
 */
public interface RedisService {

    /**
     * 保存键值对
     * @param key 键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 保存键值对并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取值
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 获取值并转换为指定类型
     * @param key 键
     * @param clazz 类型
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除键
     * @param key 键
     * @return 是否删除成功
     */
    boolean delete(String key);

    /**
     * 批量删除键
     * @param keys 键集合
     * @return 删除的键数量
     */
    long delete(List<String> keys);

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取过期时间
     * @param key 键
     * @param unit 时间单位
     * @return 过期时间
     */
    long getExpire(String key, TimeUnit unit);

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    boolean hasKey(String key);

    /**
     * 递增
     * @param key 键
     * @param delta 递增因子
     * @return 递增后的值
     */
    Long increment(String key, long delta);

    /**
     * 递减
     * @param key 键
     * @param delta 递减因子
     * @return 递减后的值
     */
    Long decrement(String key, long delta);

    /**
     * 获取指定前缀的键
     * @param pattern 前缀
     * @return 键集合
     */
    Set<String> keys(String pattern);

    /**
     * 存储Hash结构
     * @param key 键
     * @param hashKey hash键
     * @param value 值
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 获取Hash结构中的值
     * @param key 键
     * @param hashKey hash键
     * @return 值
     */
    Object hGet(String key, String hashKey);

    /**
     * 删除Hash结构中的值
     * @param key 键
     * @param hashKeys hash键
     * @return 删除的数量
     */
    Long hDelete(String key, Object... hashKeys);

    /**
     * 判断Hash结构中是否存在该键
     * @param key 键
     * @param hashKey hash键
     * @return 是否存在
     */
    boolean hHasKey(String key, String hashKey);

    /**
     * 获取Hash结构中的所有键值对
     * @param key 键
     * @return 键值对
     */
    Map<Object, Object> hGetAll(String key);
} 