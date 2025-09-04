package com.lsm.utils.cache;

import java.util.Map;

/**
 * Catchable Map interface  
 * @author olgaso
 *
 * @param <K>
 * @param <V>
 */
public interface CacheMap<K, V> extends Map<K,V> {
    int power() ;
}