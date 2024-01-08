package org.taktik.icure.annotations.entities

import java.text.SimpleDateFormat
import java.util.*

enum class ContentValues(val value: () -> Any) {
	UUID({ java.util.UUID.randomUUID().toString() }),
	CODE_ID({
		"${java.util.UUID.randomUUID().toString().substring(0,5)}|${java.util.UUID.randomUUID().toString().substring(0,5)}|${java.util.UUID.randomUUID().toString().substring(0,5)}"
	}),
	TARIFICATION_ID({
		"${java.util.UUID.randomUUID().toString().substring(0,5)}|${java.util.UUID.randomUUID().toString().substring(0,5)}|${java.util.UUID.randomUUID().toString().substring(0,5)}"
	}),
	TIMESTAMP({ System.currentTimeMillis() }),
	ANY_STRING({
		(('A'..'Z') + ('a'..'z') + ('0'..'9')).let { allowedChars ->
			(1..24)
				.map { allowedChars.random() }
				.joinToString("")
		}
	}),
	EMAIL({ "${java.util.UUID.randomUUID().toString().substring(0,6)}@icure.com" }),
	ANY_INT({ (Math.random() * 1000000).toInt() }),
	ANY_LONG({ (Math.random() * 1000000).toLong() }),
	ANY_DOUBLE({ Math.random() * 1000000 }),
	ANY_BOOLEAN({ Math.random() > 0.5 }),
	FUZZY_DATE({ SimpleDateFormat("yyyyMMddHHmmss").format(Date()).toLong() }),
	NESTED_ENTITY({}),
	NESTED_ENTITIES_SET({}),
	NESTED_ENTITIES_LIST({}),
	LOCALIZED_NESTED_ENTITIES({}),
}
