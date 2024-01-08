map = function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.MaintenanceTask' && !doc.deleted && doc.created) {
		if (doc.delegations) {
			Object.keys(doc.delegations).forEach(function (d) {
				emit([d, doc.created])
			});
		}
	}
}
