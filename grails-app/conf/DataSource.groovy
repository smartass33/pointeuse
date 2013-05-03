dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings

environments {
    development {
        dataSource {
		    pooled = true
		    dbCreate = "update"
		    //url = "jdbc:mysql://192.168.1.16:3306/pointeuse"
			url = "jdbc:mysql://localhost:3306/pointeuse"
		    driverClassName = "com.mysql.jdbc.Driver"
		    //username = "pointeuse"
		    username = "root"
			password = ""
			
			properties {
				maxActive = 50
				maxIdle = 25
				minIdle = 5
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000			}
		}
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mysql://10.33.6.10:3306/pointeuse"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "pointeuse"
			password = "pointeuse"
			
			
			properties {
				maxActive = 50
				maxIdle = 25
				minIdle = 5
				initialSize = 5
				minEvictableIdleTimeMillis = 60000
				timeBetweenEvictionRunsMillis = 60000
				maxWait = 10000
			}
		}
    
    }

}

