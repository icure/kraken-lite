package org.taktik.icure.exceptions

import org.springframework.security.authentication.BadCredentialsException

class Missing2FAException(message: String = "Missing 2FA") : BadCredentialsException(message)
