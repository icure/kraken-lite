function(doc) {
  var emit_for_delegates = function (doc, emitWithDelegateAndDoc) {
    let emittedDataOwners
    if (doc.delegations) {
      const delegates = Object.keys(doc.delegations)
      for (const dataOwnerId of delegates) {
        emitWithDelegateAndDoc(dataOwnerId, doc)
      }
      emittedDataOwners = new Set(delegates)
    } else {
      emittedDataOwners = new Set()
    }
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

  var emit_services_by_code = function(hcparty, doc) {
    doc.services.forEach(function (service) {
      var d = service.valueDate ? service.valueDate : service.openingDate;
      if (service.codes && service.codes.length) {
        service.codes.forEach(function (code) {
          emit([hcparty, code.type, code.code,  d<99999999?d*1000000:d], service._id);
        });
      }
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
    emit_for_delegates(doc, function (dataOwnerId, doc) {
      emit_services_by_code(dataOwnerId, doc);
    })
  }
}
