package org.taktik.icure.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NotBlankValidator : ConstraintValidator<NotBlank?, Any?> {
    override fun initialize(parameters: NotBlank?) {}
    override fun isValid(`object`: Any?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return `object` != null &&
                (`object` as? String)?.isNotBlank() == true
    }
}
