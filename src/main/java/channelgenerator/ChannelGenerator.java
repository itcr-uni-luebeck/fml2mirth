package channelgenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.UUID;

/**
 * Class responsible for injecting the necessary JavaScript code into the channel XML fetched from mirth
 */
public class ChannelGenerator {
    private final String channelID;

    public ChannelGenerator(String channelID) {
        if(channelID != null) {
            this.channelID = channelID;
        } else {
            this.channelID = UUID.randomUUID().toString();
        }
    }

    /**
     * Method that injects the JavaScript Code into a channel
     * @param javaScriptCode
     * @param isLDTStr Boolean; if true the LDT-Parser is also injected as a Transformer in the source-Connector of
     *                 the Channel.
     * @param template Channel XML as a String. Will get parsed by this method
     * @return a String hilding the generated Chanel in an XML-format
     */
    public String generateChannelFromScript(String javaScriptCode, String isLDTStr, String template) {
        // Some lines are commented out at the moment, because there was the plan to provide a minimal default channel,
        // if no mirth instance is provided, but this adds a fair amount of complexity and introduced bugs, so for the
        // moment this functionality is not provided.

        boolean isLDT = false;
        if (isLDTStr != null) {
            isLDT = Boolean.parseBoolean(isLDTStr);
        }

        String channelStr = "";

        if(template == null) {
            //channelStr = readFileToString("/basicChannel.xml");
            throw new RuntimeException("No Channel specified. Please provide a channel id or template.");
        } else {
            channelStr = template;
        }

        String channelStrOut = null;

        try {
            DocumentBuilder builder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream( new StringReader(channelStr) );
            Document channelXml = builder.parse( inputSource );

            Element root = channelXml.getDocumentElement();

            if(isLDT) {

                Element sourceCon = (Element) root.getElementsByTagName("sourceConnector").item(0);
                Element transformer = (Element) sourceCon.getElementsByTagName("transformer").item(0);
                Element elements = (Element) transformer.getElementsByTagName("elements").item(0);

                NodeList JSTransformers = transformer.getElementsByTagName("com.mirth.connect.plugins.javascriptstep.JavaScriptStep");
                if(JSTransformers.getLength() > 0) {
                    for(int i=0; i<JSTransformers.getLength(); i++) {
                        Element JSTransformer = (Element) JSTransformers.item(i);
                        Node name = JSTransformer.getElementsByTagName("name").item(0);
                        if(name != null && name.getTextContent().equals("ldtParser")) {
                            Node script = JSTransformer.getElementsByTagName("script").item(0);
                            script.setTextContent(readFileToString("/ldtParser.js"));
//                        } else if(name != null && name.getTextContent().equals("restructureLDT")) {
//                            Node script = JSTransformer.getElementsByTagName("script").item(0);
//                            script.setTextContent(readFileToString("/restructureLDT.js"));
                        }
                    }
                } else {
                    Element ldtTransformer = buildTransformerStep(channelXml, "/ldtParser.js", null, "ldtParser", 0);
                    elements.appendChild(ldtTransformer);

                    // If restructuring should be injected !!! alters input with firm specific business logic !!!
//                    Element restructureLdtTransformer = buildTransformerStep(channelXml, "/restructureLDT.js", null, "restructureLDT", 1);
//                    elements.appendChild(restructureLdtTransformer);
                }
            }

            // Alter JavaScript Code in transformer.
            Element destCon = (Element) root.getElementsByTagName("destinationConnectors").item(0);
            //Element connector = (Element) destCon.getElementsByTagName("connector").item(0);
            Element transformer = (Element) destCon.getElementsByTagName("transformer").item(0);
            Element elements = (Element) transformer.getElementsByTagName("elements").item(0);

            NodeList JSTransformers = transformer.getElementsByTagName("com.mirth.connect.plugins.javascriptstep.JavaScriptStep");
            if(JSTransformers.getLength() > 0) {
                for(int i=0; i<JSTransformers.getLength(); i++) {
                    Element JSTransformer = (Element) JSTransformers.item(i);
                    Node name = JSTransformer.getElementsByTagName("name").item(0);
                    if(name != null && name.getTextContent().equals("StructureMapTransformer")) {
                        Node script = JSTransformer.getElementsByTagName("script").item(0);
                        script.setTextContent(javaScriptCode);
                    // TODO: replace in production. Just for easier debugging.
//                    } else if(name != null && name.getTextContent().equals("returnResponseTransformer")) {
//                        Node script = JSTransformer.getElementsByTagName("script").item(0);
//                        script.setTextContent("channelMap.put('message', XmlUtil.prettyPrint(msg));");
                    }
                }
            } else {
                Element structMapCodeTransformer = buildTransformerStep(channelXml, null, javaScriptCode, "StructureMapTransformer", 0);
                elements.appendChild(structMapCodeTransformer);

//                // TODO: replace in production. Just for easier debugging.
//                Element returnResponseTransformer = buildTransformerStep(channelXml, null, "channelMap.put('message', XmlUtil.prettyPrint(msg));", "returnResponseTransformer", 1);
//                elements.appendChild(returnResponseTransformer);
            }

            // write file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer docTransformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(channelXml);

            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            docTransformer.transform(domSource, result);
            writer.close();

            channelStrOut = writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelStrOut;
    }

    /**
     * if the transformer step is not present in the given channel a new one is generated
     * @param channel
     * @param pathToScript
     * @param transformScript
     * @param transformerName
     * @param seqNum
     * @return XML node for a transformer
     * @throws ParserConfigurationException
     */
    private Element buildTransformerStep(Document channel, String pathToScript, String transformScript, String transformerName, int seqNum) throws ParserConfigurationException {
        String transformerScript = "";
        if(pathToScript != null) {
            transformerScript = readFileToString(pathToScript);
        } else {
            transformerScript = transformScript;
        }

        Element transformerRoot = channel.createElement("com.mirth.connect.plugins.javascriptstep.JavaScriptStep");
        transformerRoot.setAttribute("version", "4.1.1");

        Node name = (Node) channel.createElement("name");
        name.setTextContent(transformerName);
        transformerRoot.appendChild(name);

        Node sequenceNumber = (Node) channel.createElement("sequenceNumber");
        sequenceNumber.setTextContent(String.valueOf(seqNum));
        transformerRoot.appendChild(sequenceNumber);

        Node enabled = (Node) channel.createElement("enabled");
        enabled.setTextContent("true");
        transformerRoot.appendChild(enabled);

        Node script = (Node) channel.createElement("script");
        script.setTextContent(transformerScript);
        transformerRoot.appendChild(script);

        return transformerRoot;
    }


    private String readFileToString(String pathToFile) {
        String outStr = "";
        InputStream stream = getClass().getResourceAsStream(pathToFile);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    outStr = outStr + "\n" + line;
                } else {
                    break;
                }
            };
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStr;
    }

    public String getChannelID() {
        return this.channelID;
    }
}
