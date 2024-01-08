/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.http.websocket

import java.io.IOException

interface AsyncProgress {
	@Throws(IOException::class)
	suspend fun progress(progress: Double)
}
