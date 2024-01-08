package org.taktik.icure.exceptions

import org.springframework.security.authentication.BadCredentialsException

class Invalid2FAException(message: String = "Invalid 2FA") : BadCredentialsException(message)
