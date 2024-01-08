package org.taktik.icure.exceptions

import org.springframework.security.authentication.BadCredentialsException

class InvalidJwtException(message: String) : BadCredentialsException(message)
