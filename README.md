# One-Time-Pad Generator

![mavenci](https://github.com/m5c/TigerEncryptionOtpGenerator/actions/workflows/maven.yml/badge.svg)

An all-purpose symmetric crypto library.

## About

This repository hosts the sources of an open source symmetric crypto library. It comes without
graphical or command line interface, but offers simple and convenient methods for one-time-pad based
message encryption.

### Safety

The library sets on one-time-pads, that is to say key material that is used symmetrically for
encryption and decryption. It is considered the only unbreakable encryption, since the key material
is as long as the protected content. Unlike with other, asymmetric methods, breaking a message is
not a matter of computational power, since any message of *approximately equal* target length is a
potential valid outcome.

> (*) This library conceals the exact length of individual messages by white space padding.

#### Restrictions

A secure use of one-time-pads is subject to several conditions. If not respected, the secrecy of
transmitted messages may be compromised.

* All communicating parties must use the same one-time-pad for the entire communication.  
  However, the protection of encrypted messages is only as good as the way the one time pads have
  been exchanged.
* A crypto chunk (random byte series withing a pad) must be never used for more than one message.
  Multi encryption of different messages, using the same one time pad
  chunk [opens gate to statistic attacks](https://www.douglas.stebila.ca/teaching/visual-one-time-pad/).  
  The library comes with functionality that tells client software which chunk should be used for the
  next encryption. See [usage](#usage) section.

#### Recommendations for a secure use

* The one-time-pad must be exchanged in a secure manner, ideally the parties gather physically at
  the
  moment of pad creation, for a secure transfer (Airdrop, USB-Drive, LAN, etc...)
* Use unique chunks for every message encryption  
  Every one time pad is associated to a fixed set of communicating parties. Internally it and
  reserves a subset of the available chunks per party.
    * Use only the chunks belonging to your party for encryption. See [usage](#usage) section.
    * Avoid double use of a chunk, by requesting follow-up indexes from the encrypted message
      object. See [usage](#usage) section.
* Register multiple end-clients as individual parties, to associate individual chunks to each of
  them.  
  This is why on pad creation, parties are always specified as a ```username``` + ```device```
  bundle, e.g. ```max@desktop```.

The library provides method signatures that forward a secure usage. For details see
the [Usage](#usage) section

## Usage

This section explains how to access to library provisioned functionality for the purpose of a secure
communication. Further code examples are in the [ConversationTest](src/test/java/eu/kartoffelquadrat/otplib/ConversationTest.java) class.

### Pad creation

Before any form of communication, one of the participating parties must create a
new ```OneTimePad``` instance. This can be done conveniently, using the
corresponding ```OneTimePadGenerator``` factory class. Note that all parties must be known at the
moment of pad creation. A party is defined by a string of format ```[username]@[machinename]```.

Sample code:

```java
    String[] parties = new String[]{"max@laptop","max@phone","moritz@dektop"};
    OneTimePad pad = OneTimePadGenerator.generatePad(parties);
```

The pad can then be serialized, e.g. for storage to file and subsequent sharing with other parties:

```java
  String serializedPad=SerializationTools
    .getGsonPadConverter().toJson(pad);
```

### Conversations

The key component for secure message handling is
the [```Conversation``` class](https://m5c.github.io/OneTimePadLib). A conversation is based on a
existing ```OneTimePad```. It does not matter whether the pad was newly created or loaded from disk.

Sample code for creating a new empty conversation:

```java
  Conversation maxLaptopConversation
    =new Conversation(pad,"max@laptop");
```

Conversations offer two main methods:

* A method for **adding new plain messages**: This one allows the conversation owner (in this
  case ```max```) to provide a new, unprotected message, for secure encryption and adding to the
  conversation:

```java
  String payload="This is a very secret message!";
    PlainMessage secretMessage=new PlainMessage("max","laptop",payload.getBytes());
    EnctypedMessage enc=maxLaptopConversation.encryptAndAddMessage(secretMessage);
```

* A method for **adding existing encrypted messages**: This one allows adding messages that have
  already been encrypted by another party. Adding implicitly returns the corresponding plain
  message:

```java
  EncryptedMessage receivedEncMessage=...;
  PlainMessage decryptedMessage=maxLaptopConversion.addEncryptedMessage(receivedEncMessage);
  System.out.println(decryptedMessage.getPayloadAsString());
```

> Note: In both cases the effect is that an encrypted message is added to the conversation. The
> difference is only whether the calling client is the author of the message or just the receiver.

### Save and Load, Serialization

The library comes with further features for convenient saving and loading of pads and conversations.
For details, see the [java doc of public library methods](https://m5c.github.io/OneTimePadLib).

## Installation

There are two ways to install this library:

* Reference as standalone Jar on classpath

### Library as Jar file

This option is for developers who want to manually add the library to their ```classpath```.

* Clone the library: ```git clone https://github.com/m5c/OneTimePadLib```
* Build the library with ```cd OneTimePadLib; mvn clean package```
* The standalone JAR is generated to the ```target``` folder.

### Reference as Maven dependency

This option is for developers who want to access the library from a maven project.   
The library can be referenced using this dependency block:

```xml

<dependency>
    <groupId>eu.kartoffelquadrat</groupId>
    <artifactId>otplib</artifactId>
    <version>1.1</version>
</dependency>
```

Note that the library is not in the official maven repositories. Therefore, it must be either
installed manually, or pulled from a third party repository.

#### Local Installation

You can directly install this library into your local maven repository for further referencing.
Using this option you build the library right from its sources.

```bash
git clone https://github.com/m5c/OneTimePadLib
cd OneTimePadLib
mvn clean install
```

#### Reference to Developers Repo

To use a provided binary, pulled from the developers, use this trusted repository:

```xml

<repositories>
    <repository>
        <id>otplibrepo</id>
        <url>https://www.cs.mcgill.ca/~mschie3/otplibrepo/</url>
    </repository>
</repositories>
```

## Author

* Author: Maximilian Schiedermeier
* Github: [m5c](https://github.com/m5c/)
* Webpage: https://www.cs.mcgill.ca/~mschie3
* License: [MIT](https://opensource.org/licenses/MIT)
