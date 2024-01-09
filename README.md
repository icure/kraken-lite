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
```

After that, if the operation completes successfully, the repository and all its submodules will be correctly initialized. 