# MacProtectionCui
A console environment for MacProtection

## How to use ?
Two way to use this library :

### -java -jar
You can use this library directly with `java -jar`

Example :

    # -java -jar ../../dist/MacProtectionCui.jar <command> [<arguments]>
    
    -java -jar ../../dist/MacProtectionCui.jar algos
    -java -jar ../../dist/MacProtectionCui.jar export -p password -a HmacMD5 -f check.save -s directoryToScan


### cryo
MacProtectionCui comes with a _bash_ executable into `cui/examples/`

Example :

    # cryo
    
    cryo algos
    cryo export -p password -a HmacMD5 -f check.save -s directoryToScan

## List of commands
* __algos__ List all algorithms
* __show__	Show informations about the folder (state, checksum, ...)
* __diff__	Show changes between source directory and checkfile
* __export__	Export checkfile of source folder
* __help__ Get help on a specific <command> or list all commands
