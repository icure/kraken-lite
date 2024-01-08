/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.services.external.rest.v1.dto

import java.io.Serializable
import org.taktik.icure.services.external.rest.v1.dto.data.FormContent

class GetSubFormEditionReply(val formContent: FormContent? = null, val templates: List<FormTemplateDto>? = null) : Serializable
