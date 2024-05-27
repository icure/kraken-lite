# Kraken Lite
This is a lite, open-source version of the kraken.  

## How to clone this repository
This repository depends on a git submodule, `kraken-common`, so it is important to correctly initialize it immediately after cloning, to avoid compilation errors.  
To do so, launch the following commands:

```
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
```
./gradlew -x test :lite-core:build -Dicure.optional.regions=be
```

When running the generated jar, the spring profiles `kmehr` (to include kmehr module) and `sam` (to include the SAM module) should be added:
``` 
-Dspring.profiles.active=app,kmehr,sam
```