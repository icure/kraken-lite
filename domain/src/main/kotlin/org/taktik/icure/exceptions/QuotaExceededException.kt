/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.exceptions

import org.springframework.security.access.AccessDeniedException

class QuotaExceededException(msg: String) : AccessDeniedException(msg)
