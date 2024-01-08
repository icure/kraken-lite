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

  var emit_contacts = function (k, doc) {
    var formIdsMap = {};
    doc.services.forEach(function (s) {
      if (s.formId) {
        if (!formIdsMap[s.formId]) {
          formIdsMap[s.formId] = 1;
          emit([k, s.formId], null);
        }
      }
    });
    doc.subContacts.forEach(function (sc) {
      if (sc.formId) {
        if (!formIdsMap[sc.formId]) {
          formIdsMap[sc.formId] = 1;
          emit([k, sc.formId], null);
        }
      }
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length) {
    emit_for_delegates(doc, function (dataOwnerId, doc) {
      emit_contacts(dataOwnerId, doc);
    })
  }
}
