package org.taktik.icure.services.external.rest.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asyncservice.ExchangeDataMapService
import org.taktik.icure.services.external.rest.v2.dto.ExchangeDataMapCreationBatch
import org.taktik.icure.services.external.rest.v2.dto.ExchangeDataMapDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.ExchangeDataMapV2Mapper
import org.taktik.icure.utils.injectReactorContext

@RestController("exchangeDataMapControllerV2")
@Profile("app")
@RequestMapping("/rest/v2/exchangedatamap")
@Tag(name = "exchangeDataMap")
class ExchangeDataMapController(
    private val exchangeDataMapService: ExchangeDataMapService,
    private val exchangeDataMapV2Mapper: ExchangeDataMapV2Mapper
) {

    @Operation(description = "Retrieves an existing Exchange Data Map")
    @GetMapping("/{exchangeDataMapId}")
    fun getExchangeDataMap(
        @PathVariable exchangeDataMapId: String
    ) = mono {
        exchangeDataMapService.getExchangeDataMap(exchangeDataMapId)?.let {
            exchangeDataMapV2Mapper.map(it)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot retrieve the Exchange Data Map with id $exchangeDataMapId")
    }

    @Operation(description = "Creates a new Exchange Data Map")
    @PostMapping
    fun createExchangeDataMap(
        @RequestBody dataMap: ExchangeDataMapDto
    ) = mono {
        exchangeDataMapService.createExchangeDataMap(exchangeDataMapV2Mapper.map(dataMap))?.let {
            exchangeDataMapV2Mapper.map(it)
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Exchange Data Map creation failed")
    }

    @Operation(description = "Modifies an existing Exchange Data Map")
    @PutMapping("/forKey/{accessControlKey}")
    fun modifyExchangeDataMap(
        @PathVariable accessControlKey: String,
        @RequestBody dataMap: ExchangeDataMapDto
    ) = mono {
        exchangeDataMapService.modifyExchangeDataMap(
            accessControlKey,
            exchangeDataMapV2Mapper.map(dataMap)
        )?.let {
            exchangeDataMapV2Mapper.map(it)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot modify the Exchange Data Map for key $accessControlKey")
    }

    @Operation(description = "Creates a new Exchange Data Map batch, updating the ones that already exist")
    @PutMapping("/batch")
    fun createOrUpdateExchangeDataMapBatch(
        @RequestBody batch: ExchangeDataMapCreationBatch
    ) = mono {
        exchangeDataMapService.createOrUpdateExchangeDataMapBatch(
            batch.batch
        ).collect()
        ResponseEntity.ok().body("ok")
    }

    @Operation(description = "Gets an Exchange Data Map batch by ids")
    @PostMapping("/batch")
    fun getExchangeDataMapBatch(
        @RequestBody ids: ListOfIdsDto
    ) = exchangeDataMapService.getExchangeDataMapBatch(ids.ids)
        .map(exchangeDataMapV2Mapper::map)
        .injectReactorContext()

}