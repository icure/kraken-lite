/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.*
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.SecurityMetadataMapper

@Mapper(componentModel = "spring", uses = [DelegationMapper::class, CodeStubMapper::class, SecurityMetadataMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StubMapper {
	fun mapToStub(contact: Contact): IcureStubDto
	fun mapToStub(calendarItem: CalendarItem): IcureStubDto
	fun mapToStub(message: Message): IcureStubDto
	fun mapToStub(healthElement: HealthElement): IcureStubDto
	fun mapToStub(form: Form): IcureStubDto
	fun mapToStub(document: Document): IcureStubDto
	fun mapToStub(classification: Classification): IcureStubDto
	fun mapToStub(invoice: Invoice): IcureStubDto
}
