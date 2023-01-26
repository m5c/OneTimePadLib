# One-Time-Pad Generator

![mavenci](https://github.com/m5c/TigerEncryptionOtpGenerator/actions/workflows/maven.yml/badge.svg)

This software is part of the Tiger Encryption suite and responsible for generation of random
One-Time-Pads.

## About

This is only the OTP generator. You still need an Encryptor / Decryptor software (e.g. the [TigerEncrytionDesktopApp](...) or [TigerEncryptionMobileApp](...)) to securely communicate.

## Usage

 1) Compile this software with:  
```mvn clean package```
 2) Generate a new One-Time-Pad (consisting of many shorter chunks) with either of:  
```java -jar target/OtpGenerator.jar```  
```mvn exec:java```
 3) Rename the created target dir to a **unique** 3-digit pad-index:  
```mv otp-XXX otp-042```
 4) Place the folder *within* the ```.otp``` folder in your home directory.

 > *Note: To prevent accidental overwrite of existing OTPs, the generator exit if the default output folder otp-XXX already exists.*

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
