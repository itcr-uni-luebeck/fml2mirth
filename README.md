[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10678100.svg)](https://doi.org/10.5281/zenodo.10678100)

# FML2Mirth

*FML2Mirth* is a command line tool that can generate [*Mirth*](https://www.nextgen.com/solutions/interoperability/mirth-integration-engine/mirth-connect-downloads)-Channels from [FHIR-*StructureMaps*](http://hl7.org/fhir/R4B/structuremap.html), that transform an input structure (messages) to a output structure defined by the *StructureMap*.

## Building the tool

The tool is implemented in *Java*. So for building you need a JDK. Then you can clone the repo with [*git*](https://git-scm.com). The easiest way to build it, is with *Maven*. In a terminal simply `cd` into the folder and enter:

```mvn clean install```

 When the build process is successful you find the tool named `FML2Mirth-1.0-SNAPSHOT.jar` in the `target` directory.

## Using the Tool
You can use the tool via a terminal and if you have *Java* installed on the system. (In the examples the part '-1.0-SNAPSHOT' is removed from the tools name)

```java -jar FML2Mirth.jar```

FML2Mirth has a bunch of input parameters to provide the necessary data.
| Parameter | Description |
| --------- | ----------- |
|`--map`| Path to the StructureMap the transformation should be based on|
|`--out`| Path to write the output to. If a channel is successfully fetched the whole channel xml is written. If there is no Mirth-Server or channel specified, than only the generated transformer JavaScript is written to that path.
|`--cID` | The channelID in which the transformation should be injected
|`--tS`| Protocol, IP-address and port of the terminology server that provides the translation service
|`--isLDT`| If isLDT is set the LDT parser is also injected into the channel
|`--mS`| Protocol, IP-address and port of the Mirth server
|`--mUser`| Username on the Mirth server
|`--mPw`| Password of that user on the Mirth server
|`--help`| Show the instructions

The tool always needs the path to a FHIR-*StructureMap* as an input (`--map`).

The tool can operate in two modes. The first and very simple one is used if you just provide a *StructureMap* and an output path.

```java -jar FML2Mirth.jar --map "path/to/structureMap" --out "path/to/output/transformer.js"```

In this case only the *JavaScript* code for the transformation is generated and written to the output path. You can then copy and paste the code by hand to a transformer step in a *Mirth* channel.

If you provide an address for the REST-API of a *Mirth* server (by Default on port 8443) and the credentials of a user for that Mirth channel, as well as the Id of a channel the tool can fetch the channel definition as XML and injects the transformation into that channel. Afterwards it also deploys it again and the channel with the new transformer can be used right away. 

```java -jar FML2Mirth.jar --map path/toStructureMap --mUser user --mPw pw --cID <Mirth channel UUID> --out path/to/output/generated/channel --mS https://<MirthServerIP>:8443```

## Translation Service
If you use the ```translate``` operation you need to specify the IP-address and the port of a server that provides a FHIR-translation service like the [HAPI-JPA-Server](https://github.com/hapifhir/hapi-fhir-jpaserver-starter). This server needs to have access to the *ConceptMaps* you use in your *StructureMap*. Than the *JavaScript* code in the *Mirth* transformers can call this server to translate given codes.

**NOTE:** There seems to be a problem in *Mirth* with the switch to *Java* 17 that prevents HTTP-calls from the *JavaScript* code. So if you want to use the translate function you need to use *Java* 11 at the moment. If you use a *docker*-image make sure to use one with *Java* 11 (At the time of writing only provided up to *Mirth* version 4.1.1).

## Additional Remarks
Editing *StructureMaps* directly will get unfeasible for more complex structures. Defining a transformation can be done in the [FHIR-Mapping-Language (FML)](http://hl7.org/fhir/R4B/mapping-language.html) and there are parsers that parse the FML-script into a *StructureMap*. An example parser is in the official [FHIR-Validator](http://hl7.org/fhir/R4B/downloads.html) up to FHIR-Version R4B. To parse an FML-map you can use the validator like this:

```java -jar validator_cli.jar -ig path/to/map/file -compile url/given/by/the/map -version <FHIR-version> -output output/path/to/StructureMap.xml```

If you are are using *Visual Studio Code* there is [Plugin](https://marketplace.visualstudio.com/items?itemName=HealexSystems.fhir-mapping-language-support) by *HEALEX Systems* that provides syntax highlighting for the FML. 

A small [tutorial](http://hl7.org/fhir/R4B/mapping-tutorial.html) for the FML is included int the FHIR-Specification.

Also very helpful for getting started is the [FHIR mapping tutorial](https://github.com/ahdis/fhir-mapping-tutorial) by *ahdis*.


