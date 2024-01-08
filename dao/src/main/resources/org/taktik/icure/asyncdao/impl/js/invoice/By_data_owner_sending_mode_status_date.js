function hasInvoicingCodes(doc) {
  return doc.invoicingCodes.length > 0;
}function onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
}function mapCodeStatus(code){
  if(!code.pending && !code.canceled && !code.accepted && !code.resent && !code.archived) return "TOBESENT";
  if(code.pending && !code.canceled && !code.accepted && !code.resent && !code.archived) return "PENDING";
  if(code.pending && !code.canceled && !code.accepted && code.resent && !code.archived) return "TOBECORRECTED";
  if(!code.pending && (code.canceled || code.accepted) && !code.resent && !code.archived) return "TREATED";
  if(code.archived) return "ARCHIVED";
  return "UNKNOWN";
}function mapStatuses(doc){
  var codesStatus = doc.invoicingCodes.map(mapCodeStatus);
  var uniqueCodes = codesStatus.filter(onlyUnique);
  return uniqueCodes;
}function mapSendingMode(doc){
  if(doc.invoiceType === "payingagency") {
    return "OP";
  } else if(doc.invoiceType === "mutualfund") {
    return "EFACT"
  } else if(doc.sentMediumType === "eattest") {
    return "EATTEST";
  } else if(doc.sentMediumType === "paper" && doc.invoiceType === "patient") {
    return "PATIENT";
  } else {
    return "UNKNOWN";
  }
}function(doc) {
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

  if (doc.java_type === 'org.taktik.icure.entities.Invoice' && hasInvoicingCodes(doc)) {
    var statuses = mapStatuses(doc)
    var sendingMode = mapSendingMode(doc)
    emit_for_delegates(doc, function (dataOwnerId, doc) {
        statuses.forEach(function (s) {
            emit([dataOwnerId, sendingMode, s, doc.invoiceDate], null)
        });
    })
  }
}
