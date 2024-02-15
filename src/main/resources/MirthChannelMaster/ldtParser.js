function convLDTtoXML(ldt) {

    const lines = ldt.split('\n');
    // var ldtXml = new XML('<LDT_Laborbefund xmlns="http://hl7.org/fhir/ldt"></LDT_Laborbefund>');
    var ldtXml = new XML('<LDT_Laborbefund></LDT_Laborbefund>');

    var tmpSatz = null;
    var tmpSatzCode = "";

    var tmpTest = null;

    var tmpGebuehren = null;

    var tmpAuftragsbezogenerHinweisText = "";
    var tmpErgebnisText = "";
    var tmpTestbezogenerHinweisText = "";
    var tmpNormalwertText = "";


    for( var i=0; i<lines.length; i++ ) {
        var field = lines[i].substring(3, 7);
        var value = lines[i].substring( 7, lines[i].length() ) ;


        //first check if open subfields have to be closed; from leaf to root
        if(field != '8460' && tmpNormalwertText != "") {
            tmpNormalwertText = tmpNormalwertText.trim()
            tmpTest['LDT_8460']['@value'] = tmpNormalwertText;
            tmpNormalwertText = "";
        }
        if(field != '8470' && tmpTestbezogenerHinweisText != "") {
            tmpTestbezogenerHinweisText = tmpTestbezogenerHinweisText.trim()
            tmpTest['LDT_8470']['@value'] = tmpTestbezogenerHinweisText;
            tmpTestbezogenerHinweisText = "";
        }
        if(field != '8480' && tmpErgebnisText != "") {
            tmpErgebnisText = tmpErgebnisText.trim()
            tmpTest['LDT_8480']['@value'] = tmpErgebnisText;
            tmpErgebnisText = "";
        }
        if( tmpGebuehren != null && !(field == 8406 || field == 5005 || field == 8614) ) {
            tmpTest.appendChild(tmpGebuehren);
            tmpGebuehren = null;
        }

        if(tmpTest != null
            && !(   field == '8411' || field == '5001' || field == '8406'
                || field == '5005' || field == '8614' || field == '8418'
                || field == '8428' || field == '8429' || field == '8430'
                || field == '8431' || field == '8432' || field == '8433'
                || field == '8420' || field == '8421' || field == '8480'
                || field == '8470' || field == '8460' || field == '8461'
                || field == '8462' || field == '8422' ) )
        {
            tmpSatz['LDT_Test'] += tmpTest;
            tmpTest = null;
        }

        if(field != '8490' && tmpAuftragsbezogenerHinweisText != "") {
            tmpAuftragsbezogenerHinweisText = tmpAuftragsbezogenerHinweisText.trim()
            tmpSatz['LDT_8490']['@value'] = tmpAuftragsbezogenerHinweisText;
            tmpAuftragsbezogenerHinweisText = "";
        }
        if(field == '8000' && tmpSatz != null) {
            ldtXml['LDT_'+ tmpSatzCode] += tmpSatz;
            tmpSatz = null;
        }


        // then check what to do with the current field and value; from root to leaf
        if(field == '8000') {
            tmpSatzCode = value;
            tmpSatz = createSegment('LDT_' + tmpSatzCode);

        } else if(field =='8410') {
            tmpTest = createSegment('LDT_Test');
            tmpTest['LDT_'+field]['@value'] = value;

        } else if(tmpTest != null) {
            if (    field == '8411' || field == '8418' || field == '8428'
                || field == '8429' || field == '8430' || field == '8431'
                || field == '8432' || field == '8433' || field == '8420'
                || field == '8421' || field == '8461' || field == '8462'
                || field == '8422'
            ) {
                value = value.replace('&', '&amp;').replace('>', '&gt;').replace('<', '&lt;').replace('\'', '&apos;');
                value = value.trim();
                tmpTest.appendChild( new XML('<LDT_' + field + ' value="' + value +'"/>') );

            } else if(field =='8460') {
                tmpNormalwertText =  tmpNormalwertText + value + ' ';
            } else if(field =='8470') {
                tmpTestbezogenerHinweisText =  tmpTestbezogenerHinweisText + value + ' ';
            } else if(field =='8480') {
                tmpErgebnisText =  tmpErgebnisText + value + ' ';
            } else if(field =='5001') {
                tmpGebuehren = createSegment('Gebueren');
                tmpGebuehren['LDT_5001']['@value'] = value;
            }
            if(tmpGebuehren != null && (field == '8406' || field == '5005' || field == '8614') ) {
                tmpGebuehren['LDT_' + field]['@value'] = value;
            }

        } else if(field =='8490') {
            tmpAuftragsbezogenerHinweisText =  tmpAuftragsbezogenerHinweisText + value + ' ';
        } else {
            //tmpSatz['LDT_'+field] = value;
            value = value.replace('&', '&amp;').replace('>', '&gt;').replace('<', '&lt;').replace('\'', '&apos;');
            value = value.trim();
            tmpSatz.appendChild( new XML('<LDT_' + field + ' value="' + value +'"/>') );
        }

    }


    // add last Satz that has not been added in for loop
    ldtXml['LDT_' + tmpSatzCode] += tmpSatz;
    return ldtXml;
}



//============================================== Main ==============================================

var ldtArr = convLDTtoXML(connectorMessage.getRawData());
msg = ldtArr;