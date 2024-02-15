package mainpackage;

/**
 * Class that is responsible to generate all the needed functions that are more or less equal in all transformer
 * scripts. To hava only the relevant ones in the script they are only added if they are encountered in the
 * StructureMap.
 */
public class StaticCode {

    private String code;
    private String translateUrl = "";
    private boolean hasUuid = false;
    private boolean hasCopy = false;
    private boolean hasCreate = false;
    private boolean hasC = false;
    private boolean hasTranslate = false;
    private boolean hasDateOp = false;
    private boolean hasAppend = false;
    private boolean hasPointer = false;
    private boolean hasEvaluate = false;


    /**
     * Constructor without terminology server URL.
     * The create function is always added because it is also used to create implicitly created sub elements.
     */
    public StaticCode() {
        this.code = "// ======================================= FHIR - Functions =======================================\n";
        //addParentByCondition();
        addCreateFunc();
    }
    /**
     * Constructor with terminology server URL.
     * The create function is always added because it is also used to create implicitly created sub elements.
     */
    public StaticCode(String terminologyServerUrl) {
        this.code = "// ======================================= FHIR - Functions =======================================\n";
        //addParentByCondition();
        addCreateFunc();
        this.translateUrl = terminologyServerUrl;
    }

    /**
     * Functions are alway added via this Method. It checks whether a function is already in the script and adds it only
     * if that is not the case.
     * @param funcName
     */
    public void addFunc(String funcName) {
        switch (funcName) {
            case "uuid":
                addUuidFunc();
                break;
            case "copy":
                addCopyFunc();
                break;
            case "create":
                addCreateFunc();
                break;
            case "c":
                addCFunc();
                break;
            case "translate":
                addTranslateFunc(this.translateUrl);
                break;
            case "dateop":
                addDateOpFunc();
                break;
            case "append":
                addAppendFunc();
            case "pointer":
                addPointerFunc();
            case "evaluate":
                addEvaluateFunc();
        }
    }


    private void addUuidFunc() {
        if(!this.hasUuid) {
            String uuid = "function uuid() {\n" +
                    "    var u='',i=0;\n" +
                    "    while(i++<36) {\n" +
                    "        var c='xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'[i-1],r=Math.random()*16|0,v=c=='x'?r:(r&0x3|0x8);\n" +
                    "        u+=(c=='-'||c=='4')?c:v.toString(16)\n" +
                    "    }\n" +
                    "    return u;\n" +
                    "}";
            this.code = this.code + "\n" + uuid + "\n";
            this.hasUuid = true;
        }
    }

    private void addCopyFunc() {
        if(!this.hasCopy) {
            String copy = "function copy(param) {\n" +
                    "   if( typeof(param) == 'xml' && String(param['@value']) !== \"\" ) {\n" +
                    "       return param['@value'];\n" +
                    "   } else {\n" +
                    "       return param;\n" +
                    "   }\n" +
                    "}";
            this.code = this.code + "\n" + copy + "\n";
            this.hasCopy = true;
        }
    }

    private void addCreateFunc() {
        if(!this.hasCreate) {
            String create = "function create(resource) {\n" +
                    "   return new XML('<' + resource + '></'+ resource + '>');\n" +
                    "}";
            this.code = this.code + "\n" + create + "\n";
            this.hasCreate = true;
        }
    }

    private void addDateOpFunc() {
        if(!this.hasDateOp) {
            String funcCode = "function dateop(arg) {\n" +
                    "    if(typeof(arg) == 'xml' && String(arg['@value']) !== \"\") {\n" +
                    "       arg = arg['@value'];\n" +
                    "    };\n" +
                    "   let funcCode = null;\n" +
                    "   if(String(arg) == \"timestamp\") {\n" +
                    "       tS = new Date();\n" +
                    "       dateOut = tS.toISOString();\n" +
                    "       dateOut = dateOut.slice(0, 19) + \"+01:00\"; //German Time Zone Offset\n" +
                    "   } else {\n" +
                    "       date = String(arg);\n" +
                    "       dateOut = date;\n" +
                    "       if(date.length == 8) {\n" +
                    "           dateOut = date.slice(0,4) + \"-\" + date.slice(4,6) + \"-\" + date.slice(6,8);\n" +
                    "       }\n" +
                    "   }\n" +
                    "   if(arguments.length == 2) {\n" +
                    "       let time = arguments[1];\n" +
                    "       if(typeof(time) == 'xml') {\n" +
                    "           time = time['@value'];\n" +
                    "       };\n" +
                    "       if((String(time)).length == 4) {\n" +
                    "           dateOut = dateOut + \"T\" + time.slice(0,2) + \":\" + time.slice(2,4) + \":00+01:00\";\n" +
                    "       }" +
                    "   }\n" +
                    "   return dateOut;\n" +
                    "};";
            this.code = this.code + "\n" + funcCode + "\n";
            this.hasDateOp = true;
        }
    }

    private void addCFunc() {
        if(!this.hasC) {
            String funcCode = "function c(system, code, display) {\n" +
                    "    if(typeof(system) == 'xml' && String(system['@value']) !== \"\") {\n" +
                    "       system = system['@value'];\n" +
                    "    };\n" +
                    "    if(typeof(code) == 'xml' && String(code['@value']) !== \"\") {\n" +
                    "       code = code['@value'];\n" +
                    "    };\n" +
                    "    if(typeof(display) == 'xml' && String(display['@value']) !== \"\") {\n" +
                    "       display = display['@value'];\n" +
                    "    };\n" +
                    "    let coding = new XML('<coding></coding>');\n" +
                    "    if(system != null) {\n" +
                    "        coding.appendChild( new XML('<system value=\"' + system + '\"/>') );\n" +
                    "    }\n" +
                    "    if(code != null) {\n" +
                    "        coding.appendChild( new XML('<code value=\"' + code + '\"/>') );\n" +
                    "    }\n" +
                    "    if(display != null) {\n" +
                    "        coding.appendChild( new XML('<display value=\"' + display + '\"/>') );\n" +
                    "    }\n" +
                    "    return coding;" +
                    "}";
            this.code = this.code + "\n" + funcCode + "\n";
            this.hasC = true;
        }
    }

    private void addTranslateFunc(String serverUrl) {
        if(!this.hasTranslate) {
            String translate =  "function translate(code, map, output) {\n" +
                    "    if(typeof(code) == 'xml' && String(code['@value']) !== \"\") {\n" +
                    "       code = code['@value'];\n" +
                    "    };\n" +
                    "    if(typeof(map) == 'xml' && String(map['@value']) !== \"\") {\n" +
                    "       map = map['@value'];\n" +
                    "    };\n" +
                    "    if(typeof(output) == 'xml' && String(output['@value']) !== \"\") {\n" +
                    "       output = output['@value'];\n" +
                    "    };\n" +
                    "\n" +
                    "\tcode = code.trim();\n" +
                    "\tcode = code.split(' ').join('+');\n" +
                    "     if(code == '%' || code == '+' || code == '++' || code == '+++' || code == String.fromCharCode(181, 103, 47, 108)) { // 'µg/l' \n" +
                    "    \t\tcode = encodeURIComponent(code);\n" +
                    "    \t}\n" +
                    "    \n" +
                    "    let url = \"\"\n" +
                    "    if( map.startsWith(\"urn:uuid:\") || map.startsWith(\"http://\") ) {\n" +
                    "        url = new java.net.URL('"+ this.translateUrl + "/fhir/ConceptMap/$translate?code=' + code + '&url=' + map);\n" +
                    "    } else {\n" +
                    "        url = new java.net.URL('"+ this.translateUrl + "/fhir/ConceptMap/$translate?code=' + code);\n" +
                    "    }\n" +
                    "    let conn = url.openConnection();\n" +
                    "    conn.setRequestMethod('GET');\n" +
                    "    conn.setRequestProperty('accept', 'application/xml');\n" +
                    "\n" +
                    "    let inputStream = conn.getInputStream();\n" +
                    "    let streamReader = new java.io.InputStreamReader(inputStream);\n" +
                    "    let respStream = new java.io.BufferedReader(streamReader);\n" +
                    "    let buffer = new java.lang.StringBuffer();\n" +
                    "    let line = null;\n" +
                    "    while ((line = respStream.readLine()) != null) {\n" +
                    "        buffer.append(line + '\\n');\n" +
                    "    }\n" +
                    "    respStream.close();\n" +
                    "\n" +
                    "    let responseObject = new XML(buffer.toString());\n" +
                    "    let returnVal = null;\n" +
                    "\n" +
                    "\tvar selectedVal = \"NOT FOUND\";\n" +
                    "\t\n" +
                    "\tfor(let i=0; i<responseObject.children().length(); i++) {\n" +
                    "\t\tif(responseObject.child(i).child(0).attribute('value') == \"match\") {\n" +
                    "\t\t\tlet matchChildren = responseObject.child(i);\n" +
                    "\t\t\tfor(let j=0; j<matchChildren.children().length(); j++) {\n" +
                    "\t\t\t\tif(matchChildren.child(j).child(0).attribute('value') == \"concept\") {\n" +
                    "\t\t\t\t\tlet valueCodingChildList = matchChildren.child(j).child(1).children();\n" +
                    "\t\t\t\t\tif(output == \"coding\") {\n" +
                    "\t\t\t\t\t\tselectedVal = new XML('<coding></coding>');\n" +
                    "\t\t\t\t\t\tfor(let k=0; k<valueCodingChildList.length(); k++) {\n" +
                    "\t\t\t\t\t\t\tlet tmpTag = valueCodingChildList[k].localName();\n" +
                    "\t\t\t\t\t          let tmpVal = valueCodingChildList[k].attribute('value');\n" +
                    "\t\t\t\t\t          selectedVal[tmpTag]['@value'] = tmpVal;\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t} else if (output == \"CodeableConcept\") {\n" +
                    "\t\t\t\t        throw \"ERROR: Return CodeableConcept of Translate() not jet implemented!\"\n" +
                    "\t\t\t\t    \t} else { \n" +
                    "\t\t\t\t\t\tfor(let k=0; k<valueCodingChildList.length(); k++) {\n" +
                    "\t\t\t\t\t\t\tlet tagName = valueCodingChildList[k].localName();\n" +
                    "\t\t\t\t\t\t\tif(output == tagName ) {\n" +
                    "\t\t\t\t\t\t\t\tselectedVal = valueCodingChildList[k].attribute('value');\n" +
                    "\t\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t\t}\n" +
                    "\t\t\t\t\t}\n" +
                    "\t\t\t\t\t\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\tif(selectedVal == \"NOT FOUND\") {\n" +
                    "\t\tthrow \"ERROR: The '\" + output + \"' for '\"+ code + \"' is not provided by the ConceptMap\";\n" +
                    "\t}\n" +
                    "\treturn selectedVal;\n" +
                    "};";
            this.code = this.code + "\n" + translate + "\n";
            this.hasTranslate = true;
        }
    }

    private void addAppendFunc() {
        if(!this.hasAppend) {
            String funcCode = "function append() {\n" +
                    "   let joinedStr = \"\";\n" +
                    "   for(i in arguments) {\n" +
                    "       if(typeof(arguments[i]) == 'xml' && String(arguments[i]['@value']) !== \"\") {\n" +
                    "           joinedStr = joinedStr + arguments[i]['@value'];\n" +
                    "       } else {\n" +
                    "           joinedStr = joinedStr + arguments[i]\n" +
                    "       }\n" +
                    "   }\n" +
                    "   return joinedStr;\n" +
                    "}";
            this.code = this.code + "\n" + funcCode + "\n";
            this.hasAppend = true;
        }
    }

    private void addPointerFunc() {
        if(!this.hasPointer) {
            String funcCode = "function pointer(resource) {\n" +
                    "   return resource['id'];\n" +
                    "}";
            this.code = this.code + "\n" + funcCode + "\n";
            this.hasPointer = true;
        }
    }

    private void addEvaluateFunc() {
        if(!this.hasEvaluate) {
            String funcCode = "function evaluate(convFhirPathExp) {\n" +
                    "   return convFhirPathExp;\n" +
                    "}\n";
            this.code = this.code + "\n" + funcCode + "\n";
            this.hasEvaluate = true;
        }
    }


    private void addFhirPathNodeCheck() {
        String funcCode = "// function to check whether to return value or node\n" +
                "function fhirPathNodeCheck( xmlNode ) {\n" +
                "\tlogger.info('xmlNode: ' + xmlNode.children().length());\n" +
                "\tif(xmlNode.children().length() > 0) {\n" +
                "\t\tlogger.info('return xmlNode');\n" +
                "\t\treturn xmlNode;\n" +
                "\t} else if(xmlNode.children().length() == 0){\n" +
                "\t\tlogger.info('return value');\n" +
                "\t\treturn xmlNode['@value'];\n" +
                "\t}\n" +
                "}\n";
        this.code = this.code + "\n" + funcCode + "\n";
    }

    private void addParentByCondition() {
        /*
        CAVE: Not part of any specification. Just needed to rework structure to enable Processing of
        LDT-test-hierarchies that are not reflected by the structure itself.

        @condition: the condition has to be provided by a lambda function. If true for a sub-node this sub-node will be
        a parent node in the output. All following nodes with the same Name will be child nodes until a node conforms to
        the condition again.
        The condition has to be formulated in the context of the sub-node that is the focus of the restructuring.
        @ctx: an XML node that should be restructured.
        @parentName: name of the Tags that should be restructured.
         */

        String funcCode = "//CAVE: Not part of any specification! Just here to enable LDTv2-Processing\n" +
                "function parentByCondition(condition, ctx, parentName) {\n" +
                "\tlet out = new XML('<'+ctx.localName()+'></'+ctx.localName()+'>');\n" +
                "\tel = ctx.elements();\n" +
                "\tlet last_parent = null;\n" +
                "\t\n" +
                "\tfor(i=0; i<el.length(); i++) {\n" +
                "\t\tif(el[i].localName() != parentName) {\n" +
                "\t\t\tout.appendChild(el[i]);\n" +
                "\t\t} else if(condition(el[i])) {\n" +
                "\t\t\tout.appendChild(el[i]);\n" +
                "\t\t\tlast_parent = out.elements()[out.elements().length()-1];\n" +
                "\t\t} else if(el[i].localName() == parentName && !condition(el[i]) && last_parent != null) {\n" +
                "\t\t\tlast_parent.appendChild(el[i]);\n" +
                "\t\t} else {\n" +
                "\t\t\tout.appendChild(el[i]);\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\treturn out;\n" +
                "}\n";
        this.code = this.code + "\n" + funcCode + "\n";
    }


    public String getCode() {
        return this.code;
    }
}
