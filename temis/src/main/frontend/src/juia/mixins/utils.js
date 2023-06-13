export default {
    formdata: function (obj) {
        var s = "";
        var f = function (prefix, jsonObj) {
            if (typeof jsonObj == "object" && jsonObj !== null) {
                for (const [k, v] of Object.entries(jsonObj)) {
                    if (k == "$$hashKey" || v === null)
                        return;
                    if (typeof v == "object" && v !== null) {
                        var nextprefix = prefix;
                        if (Array.isArray(jsonObj)) {
                            if (prefix.endsWith(".")) {
                                nextprefix = nextprefix.substring(0, nextprefix.length - 1);
                            }
                            nextprefix += "[" + k + "].";
                        } else
                            nextprefix += k + ".";
                        f(nextprefix, v);
                    } else if (Array.isArray(jsonObj)) {
                        if (prefix.endsWith(".")) {
                            nextprefix = prefix.substring(0, prefix.length - 1);
                        }
                        if (s != "")
                            s = s + "&";
                        s += nextprefix + "[" + k + "]=" + v;
                    } else {
                        if (s != "")
                            s = s + "&";
                        s = s + prefix + k + "=" + encodeURIComponent(v);
                    }
                }
            }
        }

        f("", obj);

        console.log(s)

        return s;
    },
}