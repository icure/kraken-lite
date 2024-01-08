/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.security

import java.io.Serializable
import org.springframework.http.HttpMethod
import org.springframework.security.core.Authentication

data class SecurityToken(val method: HttpMethod, val path: String, val authentication: Authentication) : Serializable
