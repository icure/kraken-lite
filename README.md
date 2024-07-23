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

## How to add External design documents
In order to use external design documents, two steps are required:

### Set up the signing public key
All the external views must be signed, as specified in the [external views template repository](https://github.com/icure/external-design-doc-template). 
To add the public key to verify the signature, the following property must be set in the `application-app.properties` file:
```bash
icure.couchdb.external.loading.publicSigningKey=<THE_PUBLIC_KEY>
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