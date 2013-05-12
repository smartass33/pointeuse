package pointeuse

import net.sf.ehcache.*
import net.sf.ehcache.store.*
import org.apache.commons.logging.LogFactory

import org.codehaus.groovy.grails.commons.ConfigurationHolder


/**

The plan here is to have a simple caching service so I don't have to 
hit the db every time. Not sure if it's worth developing, but I'm
leaving it here as a placeholder.

*/

class CacheService {

    boolean transactional = false

    Map nameToTimeout = [ : ]

    private CacheManager manager = CacheManager.create();

    private Cache getCache(cname, int timeout) {

        Cache cache
        if (manager.cacheExists(cname)) {
            cache = manager.getCache(cname)
        } else {
            cache = new Cache(cname, 1000,
                    MemoryStoreEvictionPolicy.LFU, false, '', false,
                    timeout, timeout, false, 0, null)
            manager.addCache(cache)
        }
        return cache
    }


    def synchronized getFromCache(cname, timeout, key) {

        if (ConfigurationHolder.config.cache.enabled) {
            Cache cache = getCache(cname, timeout)
            Element e = cache.get(key)
            return e ? e.objectValue : e
        } else {
            return null
        }

    }

    def synchronized getCacheMap(cname) {

         if (manager.cacheExists(cname)) {
            def cache = manager.getCache(cname)
            def keys = cache.getKeys()
            def timeToInfo = [ : ]
            keys.each { key ->
                timeToInfo[key] = cache.get(key)
            }
            return timeToInfo
         } else {
             return [:]
         }

    }

    def synchronized putToCache(cname, timeout, key, value) {

        if (ConfigurationHolder.config.cache.enabled) {
            Cache cache = getCache(cname, timeout)
            Element e = new Element(key, value)
            cache.put(e)
        }

    }

}