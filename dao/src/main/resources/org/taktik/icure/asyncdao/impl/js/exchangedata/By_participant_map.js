map = function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.ExchangeData' && !doc.deleted && (doc.delegator && doc.delegate)) {
		emit(doc.delegator, null)
		if (doc.delegate !== doc.delegator) {
			emit(doc.delegate, null)
		}
	}
}
