package pointeuse


class User {
	transient springSecurityService
	String firstName
	String lastName
	String username
	String password
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	String phoneNumber
	String email
	boolean hasMail
	int reportSendDay
	/*
	static transients = ['beforeInsertRunOnce','beforeUpdateRunOnce']
	boolean beforeInsertRunOnce
	boolean beforeUpdateRunOnce
	*/
	static hasMany = [userRoles:UserRole]

	static constraints = {
		username blank: false, unique: true
		password blank: false,display: false
		firstName blank: false
		lastName blank: false
		phoneNumber (blank: true,nullable:true)
		email (blank: true,nullable:true)
	}
	
	static mapping = {
		password column: '`password`'
		userRoles cascade: 'all-delete-orphan'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

/*

	def beforeInsert() {
		if (! beforeInsertRunOnce) {
			beforeInsertRunOnce = true
			encodePassword()
		}
	}

	def afterInsert() {
		beforeInsertRunOnce = false
	}

	def beforeUpdate() {
		if (isDirty('password') && ! beforeUpdateRunOnce ) {
			beforeUpdateRunOnce = true
			encodePassword()
		}
	}

	def afterUpdate() {
		beforeUpdateRunOnce = false
	}
	*/
	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
