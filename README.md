# Kraken Lite
This is a lite, open-source version of the kraken.  

## How to clone this repo
This repository depends on a git submodule, `kraken-common`, so it is important to correctly initialize it imemdiately after cloning, to avoid compilation errors.  
To do so:
- Clone the repo
- Open a terminal in the repository folder
- Run the following command `git submodule init && git submodule update`

If the operation completes succesfully, the `kraken-common` submodule will be cloned.
