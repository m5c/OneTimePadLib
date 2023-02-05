# One-Time-Pad Generator

![mavenci](https://github.com/m5c/TigerEncryptionOtpGenerator/actions/workflows/maven.yml/badge.svg)

An all-purpose symmetric crypto library.

## About

This repository hosts the sources of an open source symmetric crypto library. It comes without
graphical or command line interface, but offers simple and convenient methods for one-time-pad based
message encryption.

### Safety

The library sets on one-time-pads, that is to say key material that is used symmmetrically for
encryption and decryption. It is considered the only unbreakable encryption, since the key material
is as long as the protected content. Unlike with other, assymmetric methods, breaking a message is
not a matter of computational power, since any message of *approximately equal* target length is a
potential valid outcome.

> (*) This library conceals the exact length of individual messages by whitespace padding.

#### Restrictions

A secure use of one-time-pads is subject to several conditions. If not respected, the secrecy of
transmitted messages may be compromised.

* All communicating parties must use the same one-time-pad for the entire communication.  
However, the protection of encrypted messages is only as good as the way the one time pads have been exchanged.
* A crypto chunk (random byte series withing a pad) must be never used for more than one message. Multi encryption of different messages, using the same one time pad chunk [opens gate to statistic attacks](https://www.douglas.stebila.ca/teaching/visual-one-time-pad/).  
The library comes with functionality that tells client software which chunk should be used for the next encryption. See [usage](#usage) section.

#### Recommendations for a secure use

 * The one-time-pad must be exchanged in a secure manner, ideally the parties gather physically at the
  moment of pad creation, for a secure transfer (Airdrop, Usb-Drive, LAN, etc...)
 * Use unique chunks for every message encryption  
Every one time pad is associated to a fixed set of communicating parties. Internally it and reserves a subset of the available chunks per party.  
    * Use only the chunks belonging to your party for encryption. See [usage](#usage) section.
    * Avoid double use of a chunk, by requesting follow-up indexes from the encrypted message object. See [usage](#usage) section.
 * Register multiple end-clients as individual parties, to associate individual chunks to each of them.  
This is why on pad creation, parties are always specified as a ```username``` + ```device``` bundle, e.g. ```max@desktop```.


The library provides method signatures that forward a secure usage. For details see
the [Usage](#usage) section

## Usage

...

## Installation

There are two ways to use this library in client software:

* As standalone program, to create *One Time Pads* and store them on disk for later use.
* As library, to retrieve new *One Time Pads* as objects.

### Jar

This option is for developpers who want to manually add the library to their ```classpath```.

* Build the library with ```mvn clean package```
* The standalone JAR is generated to the ```target``` folder.

### Maven

This option is for developpers who want to access the library from a maven project.   
The library can be added using this dependency block:

```xml
...
```

The library is not in the official maven repositories. It must be either installed locally, or be
referenced from the developpers repository.

#### Local Installation

```bash
mvn clean package
```


#### Reference to Developpers Repo

To use a provided binary, pulled from the developpers, use this trusted reposiotory:

```xml
...
```
## Misc

You can change the amount of chunks per OTP and chunk length per OTP with these variables:

* ```CHUNK_SIZE```: Length in bytes of message chunks. Shorter chunks have shown to be more robust
  against message tampering by communication relays. Notably email providers tend add linebreaks to
  longer lines.   
  Default is 64
* ```CHUNK_AMOUNT```: Total amount of chunks to create per OTP.  
  Default is 16*1024

## Author

* Author: Maximilian Schiedermeier
* Github: [m5c](https://github.com/m5c/)
* Webpage: https://www.cs.mcgill.ca/~mschie3
* License: [MIT](https://opensource.org/licenses/MIT)
