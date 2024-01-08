function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted ) {
        doc.services.forEach(function (s) { if (s.qualifiedLinks && Object.keys(s.qualifiedLinks).length) {
            Object.keys(s.qualifiedLinks).forEach(function(k) {
            var links = s.qualifiedLinks[k]
            Object.keys(links).forEach( function(l) { emit(links[l], [k, s._id]) } ) } )
        } });
    }
}
