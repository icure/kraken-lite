# Kraken Lite
This is a lite, open-source version of the kraken.  

## How to clone this repository
This repository depends on a git submodule, `kraken-common`, so it is important to correctly initialize it immediately after cloning, to avoid compilation errors.  
To do so, launch the following commands:

```bash
git clone git@github.com:icure/kraken-lite.git
cd kraken-lite
git submodule init
git submodule update
./gradlew :lite-core:build
```

After that, if the operation completes successfully, the repository and all its submodules will be correctly initialized. 

## Migration steps (for SDK version >=8)
If you are migrating from a version of the sdk that is less than 8 to the SDK v8 or greater, there are some properties that need to be added for the kraken-lite to work correctly.

### Enabling the new views
By default, the new CouchDB views required by the SDK v8 are not enabled. To enable them, add this property when running the kraken-lite:
```bash
-Dicure.dao.useDataOwnerPartition=true
```
You can set this property at any time by calling the following endpoint:
```bash
curl -v -X PUT http://localhost:16043/rest/v2/icure/lite/config/useDataOwnerPartition/false
```

:warning: If you set the property value using the HTTP call, this will NOT change the value stored in the property. 
This means that this value will return to the value set by the property at the next restart.

### Disable compatibility mode for views
Some views were moved to the Maurice partition, and so they will not be usable until the partitioned design Docs finish
indexing.
However, the old views are kept in the default design document for compatibility. If you don't want to force the indexation
of the Maurice views, you can enable the redirection to the compatibility views by setting the following property:
```bash
-Dicure.dao.useObsoleteViews=true
```
You can set this property at any time by calling the following endpoint:
```bash
curl -v -X PUT http://localhost:16043/rest/v2/icure/lite/config/useObsoleteViews/false
```

### Configuring background indexation workers
When a new view is created, CouchDB will start the indexation process in background. You can control the number of workers
allocated to indexation by setting the following property (default is 1):
```bash
-Dicure.dao.backgroundIndexationWorkers=<ANY_NUMBER>
```
The more workers, the faster the views will index but also more resources will be used.
You can change the number of workers for indexation at any time, by calling the following endpoint:
```bash
curl -v -X PUT http://localhost:16043/rest/v2/icure/couchdb/config/ken/batch_channels?value=<ANY_NUMBER>
```
:warning: Increasing the number of workers will immediately start more indexation processes but reducing it will not stop
them: the active process will have to complete before their number is actually reduced.

:warning: Querying the view before the indexation completes will make the indexation pass from a background state to a
foreground state (see below).

:warning: If you set the property value using the HTTP call, this will NOT change the value stored in the property.
This means that this value will return to the value set by the property at the next restart.

### Foreground view indexation
If you are using an old couchdb version, then the indexation of the views will not happen in background. In this case,
you can force the indexation of the views at startup by setting the following option to `true`:
```bash
-Dicure.dao.forceForegroundIndexation=true
```
:warning: While in the background indexation is possible to control the number of workers, this is not possible with the 
foreground indexation. The foreground indexation will try and use all the resources available on the system, and this can
be detrimental for the execution of the other processes on the machine.

You can also specify a limited set of Design Documents to index at startup. You can provide them as a comma-separated list
to the following property
```bash
-Dicure.dao.viewsToIndexAtStartup=Code_Maurice,Contact_DataOwner
```
The syntax for each element in the property is `{nameOfTheEntity}_{nameOfThePartition}`.

### How to trigger foreground view indexation for an Entity
To trigger the foreground view indexation for any entity, you have to make a POST request towards the following endpoint
`http://localhost:16043/rest/v2/icure/dd/<ENTITY_NAME>?warmup=true` where `<ENTITY_NAME>` is the name of the entity
which Design Documents you want to index.

For example, if you want to index the Design Documents for the `Contact` entity, you can do so by using the following call
```bash
curl -X POST http://localhost:16043/rest/v2/icure/dd/Contact?warmup=true
```
This will trigger the indexation and warmup (for older CouchDB versions) for all the Design Documents of the Contact 
entity of any partition.

### Kraken-lite authentication

All the endpoints on kraken-lite needs authentication. The only endpoints that can be accessed without authentication are:

```bash
/rest/*/auth/login
/rest/*/auth/refresh
/rest/*/auth/invalidate
/rest/*/user/forgottenPassword/*
/rest/*/icure/v
/rest/*/icure/p
/rest/*/icure/check
/rest/*/icure/ok
```

By default, only HCP users can use the authenticated endpoints. To allow also other types of users (e.g. patients, devices)
to authenticate, you have to set the following property to true:

```bash
-Dicure.security.allowOnlyHcp=true
```

## How to enable SAM and Kmehr modules
To include SAM and Kmehr module, two steps are needed:  
When building the `-Dicure.optional.regions=be` option should be set:
```bash
./gradlew -x test :lite-core:build -Dicure.optional.regions=be
```

When running the generated jar, the spring profiles `kmehr` (to include kmehr module) and `sam` (to include the SAM module) should be added:
```bash
-Dspring.profiles.active=app,kmehr,sam
```

### Running kraken-lite from IntelliJ including SAM and Kmehr modules
When running the kraken-lite through IntelliJ, it fails to rebuild it before running including the Kmehr and SAM libraries
even if the properties have been set. 
To circumvent this problem and run the kraken-lite from IntelliJ, navigate to the `lite-core` subproject and open the 
`build.gradle.kts` file.
Inside it, at the very end, you will find this function:

```kotlin
fun DependencyHandlerScope.injectOptionalJars() {
    val regions = System.getProperty("icure.optional.regions")?.lowercase()?.split(",") ?: emptyList()
    if (regions.contains("be")) {
        implementation(liteLibs.samModule)
        implementation(liteLibs.kmehrModule)
        implementation(liteLibs.bundles.kmehrDependencies)
    }
}
```

Update it to bypass the check on the region property to include the dependencies:

```kotlin
fun DependencyHandlerScope.injectOptionalJars() {
    val regions = System.getProperty("icure.optional.regions")?.lowercase()?.split(",") ?: emptyList()
    if (regions.contains("be") || true) {
        implementation(liteLibs.samModule)
        implementation(liteLibs.kmehrModule)
        implementation(liteLibs.bundles.kmehrDependencies)
    }
}
```

## How to add external design documents
In order to use external design documents, two steps are required:

### Set up the signing public key
All the external views must be signed, as specified in the [external views template repository](https://github.com/icure/external-design-doc-template). 
To add the public key to verify the signature, the following property must be set in the `application-app.properties` file:
```bash
-Dicure.couchdb.external.loading.publicSigningKey=<THE_PUBLIC_KEY>
```

### Set up the external views repositories
It is possible to set up multiple source as external repositories for views, as long as they follow the [external views template repository](https://github.com/icure/external-design-doc-template).
To set them up, you must define multiple properties with the prefix `icure.couchdb.external.repos` + the partition name, where
the value is the github URL of the repository (that must be public) up to the branch name:

```bash
-Dicure.couchdb.external.repos.PartitionOne=https://github.com/icure/external-design-doc-template/main -Dicure.couchdb.external.repos.AnotherPartition=https://github.com/icure/an-external-repo-for-views/main
```

## How to add plugins
To add plugin jars, put them into a single folder. Then, set the following property to that folder:
```bash
-Dicure.lite.plugins.sourceFolder=/path/to/plugin/folder
```
:warning:
Kraken-lite will try to load the plugins from all the JARs in the provided folder. To prevent errors, pass as parameter
a folder where no other JAR file is present (e.g. the kraken-lite jar itself).

## How to connect to kraken cloud CouchDB

To use kraken-lite with a kraken cloud CouchDB database, you have to start it with the following options:
```bash
-Dicure.couchdb.username=<YOUR_GROUP_USERNAME>
-Dicure.couchdb.password=<YOUR_GROUP_PASSWORD>
-Dicure.couchdb.prefix=icure-<YOUR_GROUP_NAME> # Notice the `icure-` prefix before the group name
-Dicure.couchdb.url=<REMOTE_COUCHDB_URL>
-Dicure.couchdb.populateDatabaseFromLocalXmls=false
-Dicure.objectstorage.icureCloudUrl=http://127.0.0.1:16043
```

## TroubleShooting

### Unresolved Reference: (X)FilterMapperImpl
If you are getting this issue, this means that kraken-lite is not able to find some classes that are auto-generated upon build.
To ensure that they exist, run the following commands in the project directory:
```bash
./gradlew clean
./gradlew :kraken-common:mapper:kspKotlin
```
If the commands complete successfully, the filter will be generated, and you will be able to start the kraken-lite.