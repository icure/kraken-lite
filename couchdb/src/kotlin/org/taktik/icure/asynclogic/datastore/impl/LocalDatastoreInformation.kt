class LocalDatastoreInformation(
    val dbInstanceUrl: URI,
) : IDatastoreInformation {
    override fun getFullIdFor(entityId: String): String {
        return entityId
    }
}