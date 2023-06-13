var Base64 = {

	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	// public method for encoding
	encode : function(input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		input = Base64._utf8_encode(input);

		while (i < input.length) {

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output + this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

		}

		return output;
	},

	// public method for decoding
	decode : function(input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}

		}

		output = Base64._utf8_decode(output);

		return output;

	},

	// private method for UTF-8 encoding
	_utf8_encode : function(string) {
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if ((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// private method for UTF-8 decoding
	_utf8_decode : function(utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if ((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}

}

if (typeof String.prototype.endsWith !== 'function') {
	String.prototype.endsWith = function(suffix) {
		return this.indexOf(suffix, this.length - suffix.length) !== -1;
	};
}

if (!Array.prototype.filter) {
	Array.prototype.filter = function(fun /* , thisp */) {
		var len = this.length >>> 0;
		if (typeof fun != "function")
			throw new TypeError();

		var res = [];
		var thisp = arguments[1];
		for (var i = 0; i < len; i++) {
			if (i in this) {
				var val = this[i]; // in case fun mutates
				// this
				if (fun.call(thisp, val, i, this))
					res.push(val);
			}
		}
		return res;
	};
}

String.prototype.jsonParse = function() {
	return JSON.parse(this);
};

var selectmembers = function(obj) {
	var ret = {};
	for ( var prop in obj) {
		// skip loop if the property is from prototype
		if (!obj.hasOwnProperty(prop))
			continue;

		if (prop.startsWith("_") || prop.startsWith("$"))
			continue;

		ret[prop] = obj[prop];
	}
	return ret;
}

var encodeKeys = function(obj) {
	var f = function(o) {
		if (typeof o == "object") {
			$.each(o, function(k, v) {
				if (k == "$$hashKey" || v === null)
					return;
				if (typeof v == "object" && v != null) {
					if (v.hasOwnProperty('originalObject')) {
						o[k] = v.originalObject.key;
					} else {
						o[k] = f(v);
					}
				}
			});
		}
		return o;
	}
	return f(JSON.parse(JSON.stringify(obj)));
}

var formdata = function(obj) {
	var s = "";
	var f = function(prefix, jsonObj) {
		if (typeof jsonObj == "object" && jsonObj !== null) {
			$.each(jsonObj, function(k, v) {
				if (k == "$$hashKey" || v === null)
					return;
				if (typeof v == "object" && v !== null) {
					var nextprefix = prefix;
					if (typeof k == "number") {
						if (prefix.endsWith(".")) {
							nextprefix = nextprefix.substring(0, nextprefix.length - 1);
						}
						nextprefix += "[" + k + "].";
					} else
						nextprefix += k + ".";
					f(nextprefix, v);
				} else if (typeof k == "number") {
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
			});
		}
	}

	f("", obj);

	// alert(s);

	return s;
}

var filtrarPorSubstring = function(a, s) {
	var re = new RegExp(s, 'i')
	return a.filter(function filterItem(item) {
		for ( var key in item) {
			if (!item.hasOwnProperty(key))
				continue
			if (typeof item[key] === 'string' && re.test(item[key])) {
				return true
			}
			if (typeof item[key] === 'object' && filterItem(item[key])) {
				return true
			}
		}
		return false
	})
}

var uuidv4 = function() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}