# One-Time-Pad Generator

![mavenci](https://github.com/m5c/TigerEncryptionOtpGenerator/actions/workflows/maven.yml/badge.svg)

This software is part of the Tiger Encryption suite and responsible for generation of random
One-Time-Pads.

## About

This is only the OTP generator. You still need an Encryptor / Decryptor software (e.g. the [TigerEncrytionDesktopApp](...) or [TigerEncryptionMobileApp](...)) to securely communicate.

### Terminology

One time pads are created for usage between a fixed set of communicating parties. All parties must hold the same *One Time Pad*.  
In the context a *One Time Pad* is definied as a 2D byte array.

 * Each entry on the outer array is defined as a *Chunk*.
    * All *Chunk*s have the same size.
    * The amount of *Chunk*s to create and the size per *Chunk* [can be customized](#customization).
 * Chunks are used to XOR encrypt individual messages.
    * Each chunk is reserved to a single encrypting party and must not be used by other parties for *encryption*. See []

## Usage

There are two ways to use this software:

 * As standalone program, to create *One Time Pads* and store them on disk for later use.
 * As library, to retrieve new *One Time Pads* as objects.

### Standalone Use

 1) Compile this software with:  
```mvn clean package```
 2) Generate a new One-Time-Pad (consisting of many shorter chunks) with either of:  
```java -jar target/OtpGenerator.jar```  
```mvn exec:java```
 3) Rename the created target dir to a **unique** 3-digit pad-index:  
```mv otp-XXX otp-042```
 4) Place the folder *within* the ```.otp``` folder in your home directory.

 > *Note: To prevent accidental overwrite of existing OTPs, the generator exit if the default output folder otp-XXX already exists.*

### Library Use

 1) Add this repository to your ```pom.xml```:  
```xml
...
```

 2) Add this dependency reference to your ```pom.xml```:  
```xml
...
```

 3) Invoke the library from your project code:  
```java
    import eu.kartoffelquadrat.tigerencryption.otpgenerator.OneTimePadGenerator;
    import eu.kartoffelquadrat.tigerencryption.otpgenerator.OneTimePadGenerator;
    
    [...]
    
    OneTimePad otp = OneTimePadGenerator.createOneTimePad(..., ...);
```


## Customization

You can change the amount of chunks per OTP and chunk length per OTP with these variables:

 * ```CHUNK_SIZE```: Length in bytes of message chunks. Shorter chunks have shown to be more robust against message tampering by communication relays. Notably email providers tend add linebreaks to longer lines.   
Default is 512
 * ```CHUNK_AMOUNT```: Total amount of chunks to create per OTP.  
Default is 4096

## Author

* Author: Maximilian Schiedermeier
* Github: [m5c](https://github.com/m5c/)
* Webpage: https://www.cs.mcgill.ca/~mschie3
* License: [MIT](https://opensource.org/licenses/MIT)
