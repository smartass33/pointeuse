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
			url = "jdbc:mariadb://localhost:3306/pointeuse"
			driverClassName = "org.mariadb.jdbc.Driver"
			username = "root"
			password = ""
			
			properties {
				maxActive = 50
				maxIdle = 25
				minIdle = 5
				initialSize = 5
				minEvictableIdleTimeMillis=60000
				timeBetweenEvictionRunsMillis=60000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=true
				validationQuery="SELECT 1"
			}
		}
    }
	
	demo_aws {
		dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mariadb://pointeuse.cjwt4qnapscg.eu-west-1.rds.amazonaws.com:3306/pointeuseLABM"
			driverClassName = "org.mariadb.jdbc.Driver"
			username = "pointeuse"
			password = "Wichita3*"
			properties {
				maxActive = 50
				maxIdle = 25
				minIdle = 5
				initialSize = 5
				minEvictableIdleTimeMillis=60000
				timeBetweenEvictionRunsMillis=60000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=true
				validationQuery="SELECT 1"
			}
		}
	}
	
	aws {
		dataSource {
			pooled = true
			dbCreate = "update"
			url = "jdbc:mariadb://pointeuse.cjwt4qnapscg.eu-west-1.rds.amazonaws.com:3306/pointeuse"
			driverClassName = "org.mariadb.jdbc.Driver"
			username = "pointeuse"
			password = "Wichita3*"
			properties {
				maxActive = 50
				maxIdle = 25
				minIdle = 5
				initialSize = 5
				minEvictableIdleTimeMillis=60000
				timeBetweenEvictionRunsMillis=60000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=true
				validationQuery="SELECT 1"
			}
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
}

