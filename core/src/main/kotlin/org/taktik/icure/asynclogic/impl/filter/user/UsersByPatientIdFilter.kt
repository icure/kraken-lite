package org.taktik.icure.asynclogic.impl.filter.user

import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.domain.filter.user.UsersByPatientIdFilter
import org.taktik.icure.entities.User

interface UsersByPatientIdFilter : Filter<String, User, UsersByPatientIdFilter>