package pointeuse


import groovy.time.TimeDuration
import groovy.time.TimeCategory
class EmployeeService {

	def grailsApplication
	
	public int getTakenCA(Employee employee,Period period){
		def criteria
		def takenCA
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,31)
		startCalendar.set(Calendar.MONTH,4)
		startCalendar.set(Calendar.HOUR_OF_DAY,23)
		startCalendar.set(Calendar.MINUTE,59)
		startCalendar.set(Calendar.SECOND,59)
		
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
			
		startCalendar.set(Calendar.YEAR,period.year)
		endCalendar.set(Calendar.YEAR,period.year+1)
			
		//CA
		criteria = Absence.createCriteria()
		takenCA = criteria.list {
			and {
				eq('employee',employee)
				gt('date',startCalendar.time)
				lt('date',endCalendar.time)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		return takenCA!=null?takenCA.size():0
	}
	
	public int getInitialCA(Employee employee,Period period){
		def criteria
		def initialCA
		criteria = Vacation.createCriteria()
		initialCA = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',VacationType.CA)
			}
		}
		return initialCA!=null?initialCA.counter:0
	}
	
	
	public int getInitialRTT(Employee employee,Period period){
		def criteria
		def initialRTT
		criteria = Vacation.createCriteria()
		initialRTT = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',VacationType.RTT)
			}
		}
		return initialRTT!=null?initialRTT.counter:0
	}
	
	public int getRemainingCA(Employee employee,Period period){
		def remainingCA = 0
		def criteria
		def takenCA
		def initialCA
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,31)
		startCalendar.set(Calendar.MONTH,4)
		startCalendar.set(Calendar.HOUR_OF_DAY,23)
		startCalendar.set(Calendar.MINUTE,59)
		startCalendar.set(Calendar.SECOND,59)
		
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		startCalendar.set(Calendar.YEAR,period.year)
		endCalendar.set(Calendar.YEAR,period.year+1)
		
		criteria = Vacation.createCriteria()
		initialCA = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',VacationType.CA)
			}
		}		
		
		//CA
		criteria = Absence.createCriteria()
		takenCA = criteria.list {
			and {
				eq('employee',employee)
				gt('date',startCalendar.time)
				lt('date',endCalendar.time)
				eq('type',AbsenceType.VACANCE)
			}
		}		
		return remainingCA=takenCA!=null?(initialCA.counter - takenCA.size()):initialCA.counter
	}
	
	def getMonthlyPresenceBetweenDates(Date startDate,Date endDate){
		def employeeList
		def dayMap
		def criteria
		def startCalendar = Calendar.instance
		def endCalendar   = Calendar.instance
		startCalendar.time = startDate
		endCalendar.time   = endDate
		def employeeDailyMap = [:]
		
		//def site = Site.get(2)
		employeeList = Employee.list() //Employee.list()
		
		//employeeList = Employee.findAllBySite(site) //Employee.list()
		log.error('employeeList size: '+employeeList.size())
		def dayList
		def i = 1
			//log.error(startCalendar.time)
			for (Employee employee: employeeList){	
				
				//log.error('doing employee #:'+i+' : '+employee.lastName)
				startCalendar.time = startDate
				//log.error(startDate)
				dayList = []
				while( startCalendar.get(Calendar.DAY_OF_YEAR) <= endCalendar.get(Calendar.DAY_OF_YEAR) ){
					//log.error(startCalendar.time)
					//log.error(startCalendar.time)
					criteria = DailyTotal.createCriteria()
					def dailyTotal = criteria.get {
						and {
							eq('employee',employee)
							eq('day',startCalendar.get(Calendar.DAY_OF_MONTH))
							eq('month',startCalendar.get(Calendar.MONTH) + 1)
							eq('year',startCalendar.get(Calendar.YEAR) )
						}
					}
					if (dailyTotal != null){
						dayList.add('OK')
					}else{
						dayList.add('0')
					}
					if ( startCalendar.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR) )
						break
					startCalendar.roll(Calendar.DAY_OF_YEAR, 1)
				}
				employeeDailyMap.put(employee,dayList)
				i++
				
			}
		log.error("DONE")
		return [
			employeeDailyMap:employeeDailyMap,
			employeeList:employeeList
		]
	}
	
	def getMonthlyPresence(Site site,Date date){
		def employeeList
		def dayMap
		def absenceMap
		def criteria
		def calendar = Calendar.instance
		calendar.time = date
		def lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		def employeeDailyMap = [:]
		def employeeAbsenceMap = [:]
		def period = (calendar.get(Calendar.MONTH) >= 5) ? Period.findByYear(calendar.get(Calendar.YEAR)) : Period.findByYear(calendar.get(Calendar.YEAR) - 1)
		
		
		employeeList = Employee.findAllBySite(site)
		if (employeeList != null){
			for (Employee employee: employeeList){
				
				def conges = getYearToDateVacation(period, employee)
				conges.get('takenCA')
				conges.get('remainingCA')
				conges.get('takenRTT')
				conges.get('remainingRTT')
				
				dayMap = [:]
				absenceMap = [:]
				for (int i=1;i<lastDay+1;i++){
					criteria = DailyTotal.createCriteria()
					def dailyTotal = criteria.get {
						and {
							eq('employee',employee)
							eq('day',i)
							eq('month',calendar.get(Calendar.MONTH) + 1)
							eq('year',calendar.get(Calendar.YEAR) )
						}
					}
					if (dailyTotal != null){
						dayMap.put(i,'OK')
					}else{
						criteria = Absence.createCriteria()
						def absence = criteria.get {
							and {
								eq('employee',employee)
								eq('day',i)
								eq('month',calendar.get(Calendar.MONTH) + 1)
								eq('year',calendar.get(Calendar.YEAR) )
							}
						}
						if (absence != null){
							dayMap.put(i,absence.type)
							if (absenceMap.get(absence.type) != null){
								absenceMap.put(absence.type,absenceMap.get(absence.type)+1)
							}else{
								absenceMap.put(absence.type,1)
							}																	
						}else{
							criteria = BankHoliday.createCriteria()
							def holiday = criteria.get {
								and {
									eq('day',i)
									eq('month',calendar.get(Calendar.MONTH) + 1)
									eq('year',calendar.get(Calendar.YEAR) )
								}
							}
							if (holiday != null){
								dayMap.put(i,'F')
							}else{
								dayMap.put(i,'n/a')
							
							}						
						}
					}
				}
				employeeDailyMap.put(employee,dayMap)
				employeeAbsenceMap.put(employee,absenceMap)
			}
		}
		return [
			employeeDailyMap:employeeDailyMap,
			employeeAbsenceMap:employeeAbsenceMap,
			employeeList:employeeList
		]
	}	
	
	
	def getYearToDateVacation(Period period, Employee employee){
		def criteria
	
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def takenCA
		def initialCA
		def takenRTT
		def initialRTT
		
		startCalendar.set(Calendar.YEAR,period.year)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.clearTime()
			
		
	
			criteria = Vacation.createCriteria()
			initialCA = criteria.get{
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',VacationType.CA)
				}
			}
	
			criteria = Absence.createCriteria()
			takenCA = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.VACANCE)
				}
			}
		

			criteria = Vacation.createCriteria()
			initialRTT = criteria.get{
				and {
					eq('employee',employee)
					eq('period',period)
					eq('type',VacationType.RTT)
				}
			}
	
			criteria = Absence.createCriteria()
			takenRTT = criteria.list {
				and {
					eq('employee',employee)
					ge('date',startCalendar.time)
					lt('date',endCalendar.time)
					eq('type',AbsenceType.RTT)
				}
			}
			
		

		return [
			takenCA:takenCA,
			remainingCA: initialCA.counter - takenCA.size(),
			takenRTT:takenRTT,
			remainingRTT: initialRTT.counter - takenRTT.size()
		]
	}
}