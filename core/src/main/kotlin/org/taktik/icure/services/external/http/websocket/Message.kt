/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.http.websocket

import java.io.Serializable

class Message<K : List<Serializable?>?>(var command: String, var type: String, var uuid: String, var body: K)
