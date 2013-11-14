import org.codehaus.groovy.grails.validation.AbstractConstraint
import org.springframework.validation.Errors

/**
 * Phone Number Constraint
 *
 * The phone number constraint is used to validate phone number formats
 *
 * Registering the Constraint.
 *
 * App Registration  Config.groovy
 * 
 * org.codehaus.groovy.grails.validation.ConstrainedProperty.registerNewConstraint(
 *  PhoneNumberConstraint.NAME, PhoneNumberConstraint.class)
 * 
 *
 * Plugin Registration   MyPlugin.groovy:
 * 
 * def doWithSpring = {
 *      ConstrainedProperty.registerNewConstraint(PhoneNumberConstraint.NAME, PhoneNumberConstraint.class);
 *  }
 * 
 *
 * This plugin is based upon the following posts:
 * 
 * http://www.zorched.net/2008/01/25/build-a-custom-validator-in-grails-with-a-plugin/
 * http://www.zorched.net/2008/01/26/custom-validators-in-grails-in-a-single-app/
 *
 * @author Jim Shingler ShinglerJim at gmail.com
 */
class PhoneNumberConstraint extends AbstractConstraint {

    private static final String DEFAULT_MESSAGE_CODE = "default.phoneNumber.invalid.message";
    public static final String NAME = "phoneNumber";

    private boolean validateConstraint

    public void setParameter(Object constraintParameter) {
        if (!(constraintParameter instanceof Boolean))
            throw new IllegalArgumentException("Parameter for constraint ["
                    + NAME + "] of property ["
                    + constraintPropertyName + "] of class ["
                    + constraintOwningClass + "] must be a boolean value");

        this.validateConstraint = ((Boolean) constraintParameter).booleanValue()
        super.setParameter(constraintParameter);
    }

    protected void processValidate(Object target, Object propertyValue, Errors errors) {
        if (validateConstraint && !validate(target, propertyValue)) {
            def args = (Object[]) [constraintPropertyName, constraintOwningClass,
                    propertyValue]
            super.rejectValue(target, errors, DEFAULT_MESSAGE_CODE,
                    "not." + NAME, args);
        }
    }

    boolean supports(Class type) {
        return type != null && String.class.isAssignableFrom(type);
    }

    String getName() {
        return NAME;
    }

    /**
     * This is where the real work is.  Use a regular expression to validate
     * the phone number.
     *
     * The core logic of the constraint is implemented as its own method to make the
     * constraint easier to test.
     */
    boolean validate(target, propertyValue) {
        propertyValue ==~ /^[01]?\s*[\(\.-]?(\d{3})[\)\.-]?\s*(\d{3})[\.-](\d{4})$/
    }
}
