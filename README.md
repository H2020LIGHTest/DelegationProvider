# LIGHTest Delegation Publisher (DP)

![LIGHTest](https://www.lightest.eu/static/LIGHTestLogo.png)


### Disclaimer 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
This software is the output of a research project. 
It was developed as proof-of-concept to explore, test & verify various components
created in the LIGHTest project. It can thus be used to show the concepts
of LIGHTest, and as a reference implementation. 


# LIGHTest

Lightweight Infrastructure for Global Heterogeneous Trust management in support of an open Ecosystem of Stakeholders and Trust schemes.

For more information please visit the LIGHTest website: https://www.lightest.eu/


# Documentation

See the [LIGHTest Deliverables](https://www.lightest.eu/downloads/pub_deliverables/index.html).


# Requirements

* Java 1.8 or newer
* Maven ~3.6.0
* IAIK libraries (see [pom.xml](./pom.xml#L182-211))
  * ([Library overview](https://jce.iaik.tugraz.at/sic/Products), [free opensource license available](https://jce.iaik.tugraz.at/sic/Sales/Licences/License_for_Open_Source_Projects)) 
* Internet access 
* LIGHTest Trust Infrastructure
  * Trust Scheme Publication Authority
  * Delegation Publisher
  * Trust Translation Authority


# How to use it

* Clients:
  * [LocalATVClient](src/main/java/eu/lightest/verifier/client/LocalATVClient.java) (recommended)
  * [RemoteATVClient](src/main/java/eu/lightest/verifier/client/RemoteATVClient.java)
  * or use [VerificationProcess](src/main/java/eu/lightest/verifier/controller/VerificationProcess.java) directly
* Configuration: [atv.properties](src/main/resources/atv.properties)
* Log Config:  [log4j.properties](src/main/resources/log4j.properties)
* DSS Validation Policy: [dss-validation-policy.xml](src/main/resources/eu/europa/esig/dss/asic/validation/dss-validation-policy.xml)


## Example Usage

```java
```


## Maven

You can use the ATV library as a Maven dependency

```xml
<dependency>
    <groupId>eu.lightest</groupId>
    <artifactId>dp</artifactId>
    <version>1.x.x-SNAPSHOT</version>
</dependency>
```


# Licence
* Apache License 2.0 (see [LICENSE](./LICENSE))
* © LIGHTest Consortium
