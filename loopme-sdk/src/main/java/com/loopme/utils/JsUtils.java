package com.loopme.utils;

import java.util.List;

public class JsUtils {
    private static int sIdCounter;
    private static final String JS_RESOURCE = "[JS_RESOURCE]";
    private static final String SCRIPT_ID = "[SCRIPT_ID]";
    private static final String START_HTML_TAG = " <html><head></head><body>\n";
    private static final String END_HTML_TAG = "</body></html>";

    private static final String START_SCRIPT_TAG = "<script type = \"text/javascript\">\n";
    private static final String END_SCRIPT_TAG = "</script>\n";

    private static final String HEAD_VAR = "    \tvar head = document.getElementsByTagName('head').item(0);\n";

    private static final String SUCCESS_FUNCTION =
            "   var onSuccessFun = function (message) {\n" +
                    "            return function() {\n" +
                    "                console.log('vast4 ad verification script loaded successfully ' + message); \n" +
                    "                window.location = 'vast4://jsLoadSuccess/' + message;\n" +
                    "            }\n" +
                    "        }\n";

    private static final String ERROR_FUNCTION =
            "    var onErrorFun = function (message) {\n" +
                    "            return function() {\n" +
                    "                console.log('vast4 ad verification script failed to load ' + message); \n" +
                    "                window.location = 'vast4://jsLoadFail/' + message;\n" +
                    "            }  \n" +
                    "        }\n";

    private static final String VERIFICATION_SCRIPT_PATTERN =
            "\tvar script[SCRIPT_ID] = document.createElement('script');\n" +
                    "   \t\tscript[SCRIPT_ID].setAttribute('type', 'text/javascript');\n" +
                    "    \tscript[SCRIPT_ID].setAttribute('src', '[JS_RESOURCE]');\n" +
                    "    \tscript[SCRIPT_ID].addEventListener('load', onSuccessFun(script[SCRIPT_ID].src), false);\n" +
                    "    \tscript[SCRIPT_ID].addEventListener('error', onErrorFun(script[SCRIPT_ID].src), false);\n" +
                    "    \thead.appendChild(script[SCRIPT_ID]);\n";

    public static String buildHtml(List<String> jsScriptList) {
        StringBuilder html = new StringBuilder();
        html.append(START_HTML_TAG);
        html.append(buildScript(jsScriptList));
        html.append(END_HTML_TAG);
        return html.toString();
    }

    public static StringBuilder buildScript(List<String> jsScriptList) {
        StringBuilder script = new StringBuilder();
        script.append(START_SCRIPT_TAG);
        script.append(HEAD_VAR);
        script.append(SUCCESS_FUNCTION);
        script.append(ERROR_FUNCTION);
        for (String jsLink : jsScriptList) {
            script.append(getScript(jsLink));
        }
        script.append(END_SCRIPT_TAG);
        return script;
    }

    private static String getScript(String jsLink) {
        String scriptId = String.valueOf(generateScriptId());
        String script = StringUtils.replace(VERIFICATION_SCRIPT_PATTERN, SCRIPT_ID, scriptId);
        script = StringUtils.replace(script, JS_RESOURCE, jsLink);
        return script;
    }

    private static int generateScriptId() {
        return sIdCounter++;
    }
}
