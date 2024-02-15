package mainpackage;

import channelgenerator.*;

import java.util.HashMap;

public class App {
    public static void main( String[] args ) {
        System.out.println("========== Mirth-channel-generator ==========");

        HashMap<String, String> argMap = processParams(args);

        if(argMap.get("help") == null) {

            String structMap = Utils.readFile(argMap.get("pathToStructMap"));
            String termServerURL = argMap.get("termServURL");

            // JavaScript-generation is invoked here. script hold the JavaScript codes as a String
            JavaScriptGenerator jsGen = new JavaScriptGenerator(structMap);
            String script = jsGen.genJsCode(termServerURL);

            // In the following the channel is fetched from the mirth-server and the script is injected in the channel.
            // If there is no channel or mirth server provided the script is simply written to the --out path.
            // If also the --out path is not provided, the help dialog is printed to std-out.
            if (argMap.get("mirthServerAddress") != null && argMap.get("mirthUserName") != null && argMap.get("mirthPassword") != null) {
                MirthConnector connector = new MirthConnector(argMap.get("mirthServerAddress"), argMap.get("mirthUserName"), argMap.get("mirthPassword"));
                String channelTemplate = null;
                if (argMap.get("channelID") != null) {
                    System.out.println("Fetching channel with ID: " + argMap.get("channelID"));
                    channelTemplate = connector.getChannel(argMap.get("channelID"));
                }

                ChannelGenerator channelGenerator = new ChannelGenerator(argMap.get("channelID"));
                String channelStr = channelGenerator.generateChannelFromScript(script, argMap.get("isLDT"), channelTemplate);

                System.out.println("Sending generated channel to Mirth...");
                connector.postChannel(channelStr);
                System.out.println("Deploying generated channel...");
                connector.deployChannel(channelGenerator.getChannelID());

                if (argMap.get("outputPath") != null) {
                    System.out.println("Writing channel file to: " + argMap.get("outputPath"));
                    Utils.writeFile(channelStr, argMap.get("outputPath"));
                }

                System.out.println("Mirth-channel generation finished...");
                System.out.println("=============================================");

            } else if (argMap.get("outputPath") != null) {
                System.out.println("Writing transformer script to: " + argMap.get("outputPath"));
                Utils.writeFile(script, argMap.get("outputPath"));
            }
        } else {
            printHelp();
        }
    }

    /***
     * Method that processes the input parameters given by the user. Also includes error handling if inputs are
     * insufficient.
     * @param  args: Input array red from command line
     * @return HashMap<String, String> with keys and values for the given parameters. If some parameters are not given,
     * they might be filled with defaults.
     */
    private static HashMap<String, String> processParams(String[] args) {
        HashMap<String, String> map = new HashMap<>();

        for(int i=0; i<args.length; i++) {
            String key = args[i].toLowerCase();
            if(map.get("help") == null) {
                switch (key) {
                    case "--map": {
                        map.put("pathToStructMap", args[++i]);
                        break;
                    }
                    case "--ts": {
                        map.put("termServURL", args[++i]);
                        break;
                    }
                    case "--out": {
                        map.put("outputPath", args[++i]);
                        break;
                    }
                    case "--isldt": {
                        map.put("isLDT", "true");
                        break;
                    }
                    case "--muser": {
                        map.put("mirthUserName", args[++i]);
                        break;
                    }
                    case "--mpw": {
                        map.put("mirthPassword", args[++i]);
                        break;
                    }
                    case "--ms": {
                        map.put("mirthServerAddress", args[++i] + "/api");
                        break;
                    }
                    case "--cid": {
                        map.put("channelID", args[++i]);
                        break;
                    }
                    case "--help": {
                        map.put("help", "true");
                        break;
                    }
                    default:
                        map.put("help", "true");
                        System.out.println("The parameter: '" + args[i] + "' is unknown.");
                        break;
                }
            }
        }

        if(map.get("help") == null) {
            if (map.get("channelID") == null) {
                System.out.println("WARNING: No channelID specified! Generating just transformer script. (--help for info)");
            }
            if (map.get("pathToStructMap") == null) {
                throw new RuntimeException("ERROR: No StructureMap specified. (--help for info)");
            }
            if ((map.get("mirthUserName") == null ^ map.get("mirthPassword") == null) && map.get("channelID") != null) {
                throw new RuntimeException("ERROR: You have to provide username and password for mirth (--help for info)");
            }
            if ((map.get("mirthUserName") != null && map.get("mirthPassword") != null) && map.get("mirthServerAddress") == null) {
                System.out.println("Username and password for Mirth server provided, but no address --> using localhost:8443");
                map.put("mirthServerAddress", "https://localhost:8443" + "/api");
            }
            if (map.get("termServURL") == null) {
                System.out.println("No terminologie server provided --> using localhost:8080/fhir");
                map.put("termServURL", "http://localhost:8080/fhir");
            }
            if (map.get("outputPath") == null && map.get("mirthServerAddress") == null) {
                throw new RuntimeException("ERROR: At least an output path or a mirth server address has to be provided.");
            }
        }

        return map;
    }

    private static void printHelp() {
        System.out.println("=== Channel generator help ===");
        System.out.println("Parameters:");
        System.out.println("    Upper or lower case does not matter.");
        System.out.println("    --map:    Path to the StructureMap the transformation should be based on.");
        System.out.println("    --out:    Path to write the output to. If a channel is successfully generates\n" +
                           "              the whole channel xml is written. If there is no Mirth-Server or\n" +
                           "              channel specified, than only the generated transformer JavaScript is\n" +
                           "              written to that path.");
        System.out.println("    --cID:    The channelID in which the transformation should injected.");
        System.out.println("    --tS:     Protocol, IP-address and port of the terminology server that provides the translation\n" +
                           "              service.");
        System.out.println("    --isLDT:  if isLDT is set the LDT parser is also injected in the channel.");
        System.out.println("    --mS:     Protocol, IP-address and port of the Mirth server.");
        System.out.println("    --mUser:  Username on the Mirth server.");
        System.out.println("    --mPw:    Password of that User on the Mirth server.");
        System.out.println("    --help:   Show the instructions.");
    }
}
