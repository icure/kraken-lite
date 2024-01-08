/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto.data

import org.taktik.icure.services.external.rest.v1.dto.CodeDto

/**
 * Created by aduchate on 01/02/13, 12:20
 */
class FormContent(
	override val id: String? = null,
	override val entityClass: String? = null,
	override val entityId: String? = null,
	label: String? = null,
	index: Int? = null,
	guid: String? = null,
	tags: List<CodeDto>? = null,
	val formTemplateGuid: String? = null,
	val dashboardGuid: String? = null,
	val dataJXPath: String? = null,
	val descr: String? = null,
	val isAllowMultiple: Boolean = false,
	val isDeleted: Boolean = false,
	val items: MutableList<FormItem> = ArrayList()
) : FormItem(label, index, guid, tags), DisplayableContent
