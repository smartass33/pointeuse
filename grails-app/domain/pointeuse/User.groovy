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
	
	//static belongsTo = Site
	static hasMany = [userRoles:UserRole]//,sites:Site]

	static constraints = {
		username blank: false, unique: true
		password blank: false,display: false
		firstName blank: false
		lastName blank: false
		//sites (blank: true,nullable:true)
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
