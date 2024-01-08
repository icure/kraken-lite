function(doc) {
    var emit_for_delegates = function (doc, emitWithDelegateAndDoc) {
        let emittedDataOwners
        emittedDataOwners = new Set()
        if (doc.securityMetadata) {
            const metadata = doc.securityMetadata
            let equivalencesByCanonical = {}
            if (metadata.keysEquivalences) {
                for (const [equivalentKey, canonicalKey] of Object.entries(metadata.keysEquivalences)) {
                    const prev = equivalencesByCanonical[canonicalKey]
                    if (prev) {
                        prev.push(equivalentKey)
                    } else {
                        equivalencesByCanonical[canonicalKey] = [equivalentKey]
                    }
                }
            }
            if (metadata.secureDelegations) {
                for (const [delegationKey, secureDelegation] of Object.entries(metadata.secureDelegations)) {
                    if (secureDelegation.delegate) {
                        if (!emittedDataOwners.has(secureDelegation.delegate)) {
                            emittedDataOwners.add(secureDelegation.delegate)
                            emitWithDelegateAndDoc(secureDelegation.delegate, doc)
                        }
                    }
                    if (!secureDelegation.delegate || !secureDelegation.delegator) {
                        emitWithDelegateAndDoc(delegationKey, doc)
                        const equivalences = equivalencesByCanonical[delegationKey]
                        if (equivalences) {
                            equivalences.forEach(function (equivalence) { emitWithDelegateAndDoc(equivalence, doc) })
                        }
                    }
                }
            }
        }
    }

  if (doc.java_type === 'org.taktik.icure.entities.Message' && !doc.deleted) {
    var addresses = (doc.toAddresses && doc.toAddresses.length) || (doc.invoiceIds && doc.invoiceIds.length) ? {} : {'INBOX': 1};
    if (doc.toAddresses) {
      doc.toAddresses.forEach(function (a) {
        addresses[a] = 1;
      });
    }
    emit_for_delegates(doc, function (dataOwnerId, doc) {
      Object.keys(addresses).forEach(function (a) {
        emit([dataOwnerId, a, -doc.received], null);
      });
    })
  }
}
