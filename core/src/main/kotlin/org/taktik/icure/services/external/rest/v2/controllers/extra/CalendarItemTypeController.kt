/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.exception.DocumentNotFoundException
import org.taktik.icure.asyncservice.CalendarItemTypeService
import org.taktik.icure.services.external.rest.v2.dto.CalendarItemTypeDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.CalendarItemTypeV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController("calendarItemTypeControllerV2")
@Profile("app")
@RequestMapping("/rest/v2/calendarItemType")
@Tag(name = "calendarItemType")
class CalendarItemTypeController(
	private val calendarItemTypeService: CalendarItemTypeService,
	private val calendarItemTypeV2Mapper: CalendarItemTypeV2Mapper
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Operation(summary = "Gets all calendarItemTypes")
	@GetMapping
	fun getCalendarItemTypes(): Flux<CalendarItemTypeDto> =
		calendarItemTypeService.getAllCalendarItemTypes().map { calendarItemTypeV2Mapper.map(it) }.injectReactorContext()

	@Operation(summary = "Gets all calendarItemTypes include deleted")
	@GetMapping("/includeDeleted")
	fun getCalendarItemTypesIncludeDeleted(): Flux<CalendarItemTypeDto> =
		calendarItemTypeService.getAllEntitiesIncludeDelete().map { calendarItemTypeV2Mapper.map(it) }.injectReactorContext()

	@Operation(summary = "Creates a calendarItemType")
	@PostMapping
	fun createCalendarItemType(@RequestBody calendarItemTypeDto: CalendarItemTypeDto) = mono {
		calendarItemTypeService.createCalendarItemType(calendarItemTypeV2Mapper.map(calendarItemTypeDto))?.let { calendarItemTypeV2Mapper.map(it) }
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CalendarItemType creation failed")
	}

	@Operation(summary = "Deletes a batch of calendarItemTypes")
	@PostMapping("/delete/batch")
	fun deleteCalendarItemTypes(@RequestBody calendarItemTypeIds: ListOfIdsDto): Flux<DocIdentifier> =
		calendarItemTypeIds.ids.takeIf { it.isNotEmpty() }?.let { ids ->
			calendarItemTypeService.deleteCalendarItemTypes(ids).injectReactorContext()
		} ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }

	@Operation(summary = "Gets a calendarItemType")
	@GetMapping("/{calendarItemTypeId}")
	fun getCalendarItemType(@PathVariable calendarItemTypeId: String) = mono {
		calendarItemTypeService.getCalendarItemType(calendarItemTypeId)?.let { calendarItemTypeV2Mapper.map(it) }
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "CalendarItemType fetching failed")
	}

	@Operation(summary = "Modifies an calendarItemType")
	@PutMapping
	fun modifyCalendarItemType(@RequestBody calendarItemTypeDto: CalendarItemTypeDto) = mono {
		calendarItemTypeService.modifyCalendarItemType(calendarItemTypeV2Mapper.map(calendarItemTypeDto))?.let { calendarItemTypeV2Mapper.map(it) }
			?: throw DocumentNotFoundException("CalendarItemType modification failed")
	}
}