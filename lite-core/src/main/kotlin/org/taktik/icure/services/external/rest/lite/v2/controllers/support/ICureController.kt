package org.taktik.icure.services.external.rest.lite.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asyncservice.ICureLiteService

@RestController("iCureLiteControllerV2")
@Profile("app")
@RequestMapping("/rest/v2/icure")
@Tag(name = "icure")
class ICureController(
	private val iCureService: ICureLiteService,
) {

	@Operation(summary = "Retrieve a value in a section in the CouchDB config.")
	@GetMapping("/couchdb/config/{section}/{key}")
	fun getCouchDbConfigProperty(
		@PathVariable section: String,
		@PathVariable key: String
	) = mono {
		iCureService.getCouchDbConfigProperty(section, key)
	}

	@Operation(summary = "Sets a value in a section in the CouchDB config.")
	@PutMapping("/couchdb/config/{section}/{key}")
	fun setCouchDbConfigProperty(
		@PathVariable section: String,
		@PathVariable key: String,
		@RequestParam value: String,
	) = mono {
		iCureService.setCouchDbConfigProperty(section, key, value)
		"ok"
	}

	@Operation(summary = "Sets a configuration property for Kraken lite.")
	@PutMapping("/lite/config/{property}/{value}")
	fun setLiteConfigProperty(
		@PathVariable property: String,
		@PathVariable value: Boolean,
	) = mono {
		iCureService.setKrakenLiteProperty(property, value)
		"ok"
	}

}