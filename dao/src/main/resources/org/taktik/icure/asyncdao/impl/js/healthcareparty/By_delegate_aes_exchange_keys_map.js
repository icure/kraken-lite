map = function(doc) {
	var emitted = []
	if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted && ((doc.hcPartyKeys && doc.publicKey) || doc.aesExchangeKeys)) {
		var aesPubKeys = Object.keys(doc.aesExchangeKeys || {});
		aesPubKeys.forEach(function (pk) {
			var ks = doc.aesExchangeKeys[pk]
			Object.keys(ks).forEach(function (hcpId) {
				var delegateKeys = ks[hcpId];
				Object.keys(delegateKeys).forEach(function (delPub) {
					var delK = delegateKeys[delPub]
					if (pk === doc.publicKey) { emitted.push(hcpId) }
					emit(hcpId, [doc._id, pk.slice(-32), delPub.slice(-32), delK]);
				})
			});
		});

		Object.keys(doc.hcPartyKeys).forEach(function (k) {
		  if (!emitted.includes(k)) {
				emit(k, [doc._id, doc.publicKey.slice(-32), '', doc.hcPartyKeys[k][1]]);
		  }
		});
	}
}
