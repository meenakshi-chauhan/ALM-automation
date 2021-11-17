var testNames = $("div.node-name:contains('$')");
 for(var i=0; i<testNames.length; i++){
    var text = testNames[i].innerHTML;
    testNames[i].innerHTML =text.substring(0,text.indexOf('$'))+'<br>' + '<span style="font-size:75%;color:red;">Bug id = '+text.substring(text.indexOf('$')+1)+'</span>'
    }