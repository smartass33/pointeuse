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
	
	dell {
		dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mysql://localhost:3306/pointeuse?autoReconnect=true"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "root"
			password = "root"
			properties {
				maxActive = 100
				maxIdle = 25
				minIdle = 20
				initialSize = 20
				maxWait = 10000
				validationQuery = "select 1"
				testOnBorrow = true
				testWhileIdle = true
				testOnReturn = true
			}
		}
	
	}
	
	dell_test {
		dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mysql://localhost:3306/pointeuse_test?autoReconnect=true"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "root"
			password = "root"
			properties {
				maxActive = 100
				maxIdle = 25
				minIdle = 20
				initialSize = 20
				maxWait = 10000
				validationQuery = "select 1"
				testOnBorrow = true
				testWhileIdle = true
				testOnReturn = true
			}
		}
	
	}
	

}

