let testList = msg['LDT_8201']['LDT_Test'];
delete(msg['LDT_8201']['LDT_Test']);

let lastParent = null;

for(i=0; i<testList.length(); i++) {

    let tmpTest = testList[i];

    if(tmpTest['LDT_8428']['@value'] == '0') {
        if(lastParent != null) {
            msg['LDT_8201'].appendChild(lastParent);
        }

        lastParent = tmpTest;

        if(lastParent['LDT_8411']['@value'] == 'KLEINES BLUTBILD'
            ||lastParent['LDT_8411']['@value'] == 'GROSSES BLUTBILD'){
            while(true) {
                if(i<testList.length()-1) {
                    i++;
                    tmpTest = testList[i];
                } else {
                    break;
                }
                if(tmpTest ['LDT_8410']['@value'] == 'BLEU'
                    || tmpTest ['LDT_8410']['@value'] == 'BERY'
                    || tmpTest ['LDT_8410']['@value'] == 'HBGG'
                    || tmpTest ['LDT_8410']['@value'] == 'HAET'
                    || tmpTest ['LDT_8410']['@value'] == 'HBE'
                    || tmpTest ['LDT_8410']['@value'] == 'HBGP'
                    || tmpTest ['LDT_8410']['@value'] == 'MCV'
                    || tmpTest ['LDT_8410']['@value'] == 'TROM'
                    || tmpTest ['LDT_8410']['@value'] == 'MCH') {
                    lastParent.appendChild(tmpTest);
                } else {
                    msg['LDT_8201'].appendChild(lastParent);
                    lastParent = null;
                    i--;
                    break;
                }
            }
        }

        else if( lastParent['LDT_8411']['@value'] == 'DIFF BLUTBILD')  {
            while(true) {
                if(i<testList.length()-1) {
                    i++;
                    tmpTest = testList[i];
                } else {
                    break;
                }
                if(tmpTest ['LDT_8410']['@value'] == 'NNEU'
                    || tmpTest ['LDT_8410']['@value'] == 'LYM'
                    || tmpTest ['LDT_8410']['@value'] == 'MONO'
                    || tmpTest ['LDT_8410']['@value'] == 'EOS'
                    || tmpTest ['LDT_8410']['@value'] == 'BASO'
                    || tmpTest ['LDT_8410']['@value'] == 'GRAN'
                    || tmpTest ['LDT_8410']['@value'] == 'NEUA'
                    || tmpTest ['LDT_8410']['@value'] == 'LYMA'
                    || tmpTest ['LDT_8410']['@value'] == 'MONOA'
                    || tmpTest ['LDT_8410']['@value'] == 'EOA'
                    || tmpTest ['LDT_8410']['@value'] == 'BASOA'
                    || tmpTest ['LDT_8410']['@value'] == 'GRANA') {
                    lastParent.appendChild(tmpTest);
                } else {
                    msg['LDT_8201'].appendChild(lastParent);
                    lastParent = null;
                    i--;
                    break;
                }
            }
        }

        else if(lastParent['LDT_8411']['@value'] == 'DIFF BLUTBILD MANUEL') {
            while(true) {
                if(i<testList.length()-1) {
                    i++;
                    tmpTest = testList[i];
                } else {
                    break;
                }
                if(tmpTest ['LDT_8410']['@value'] == 'NSEG'
                    || tmpTest ['LDT_8410']['@value'] == 'NSTA'
                    || tmpTest ['LDT_8410']['@value'] == 'MMYE'
                    || tmpTest ['LDT_8410']['@value'] == 'MYE'
                    || tmpTest ['LDT_8410']['@value'] == 'PROM'
                    || tmpTest ['LDT_8410']['@value'] == 'MYBL'
                    || tmpTest ['LDT_8410']['@value'] == 'LYMNA'
                    || tmpTest ['LDT_8410']['@value'] == 'ATYP'
                    || tmpTest ['LDT_8410']['@value'] == 'MONONA'
                    || tmpTest ['LDT_8410']['@value'] == 'EONA'
                    || tmpTest ['LDT_8410']['@value'] == 'BASONA'
                    || tmpTest ['LDT_8410']['@value'] == 'NORMNA'
                    || tmpTest ['LDT_8410']['@value'] == 'SONS'
                    || tmpTest ['LDT_8410']['@value'] == 'KERN'
                    || tmpTest ['LDT_8410']['@value'] == 'NEUA'
                    || tmpTest ['LDT_8410']['@value'] == 'LYMA'
                    || tmpTest ['LDT_8410']['@value'] == 'MONOA'
                    || tmpTest ['LDT_8410']['@value'] == 'EOA'
                    || tmpTest ['LDT_8410']['@value'] == 'BASOA'
                    || tmpTest ['LDT_8410']['@value'] == 'GRANA') {
                    lastParent.appendChild(tmpTest);
                } else {
                    msg['LDT_8201'].appendChild(lastParent);
                    lastParent = null;
                    i--
                    break;
                }
            }
        }

    } else if(tmpTest.localName() == 'LDT_Test' && tmpTest['LDT_8428']['@value'] != '0' && lastParent != null) {
        lastParent.appendChild(tmpTest);
    } else if(tmpTest.localName() == 'LDT_Test' && tmpTest['LDT_8428']['@value'] != '0' && lastParent == null) {
        msg['LDT_8201'].appendChild(tmpTest);
    }
}

if(lastParent != null) {
    msg['LDT_8201'].appendChild(lastParent);
}