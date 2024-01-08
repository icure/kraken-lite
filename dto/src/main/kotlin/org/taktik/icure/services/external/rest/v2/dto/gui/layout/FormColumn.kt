/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v2.dto.gui.layout

import java.io.Serializable

/**
 * Created by aduchate on 07/02/13, 17:10
 */
class FormColumn : Serializable {
	private var formDataList: MutableList<FormLayoutData> = ArrayList()

	/**
	 * Determines the columns span of the object
	 *
	 * @param columns: 1=column 1, 1-2=column 1 and 2. Null means all columns.
	 */
	var columns: String? = null
	var shouldDisplay: Boolean? = null
	fun getFormDataList(): List<FormLayoutData> {
		return formDataList
	}

	fun setFormDataList(formDataList: MutableList<FormLayoutData>) {
		this.formDataList = formDataList
	}

	fun addFormData(fd: FormLayoutData) {
		formDataList.add(fd)
	}
}
