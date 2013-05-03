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

	static hasMany = [userRoles:UserRole]

	static constraints = {
		username blank: false, unique: true
		password blank: false,display: false
		firstName blank: false
		lastName blank: false
	}
	


	static mapping = {
		password column: '`password`'
		userRoles cascade: 'all-delete-orphan'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
