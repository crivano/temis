import Utils from './utils.js'

export default {
    mounted() {
        if (this.$route.params.key) {
            this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/dados/' + this.$route.params.key).then(
                response => {
                    this.$set(this, "data", response.data);
                },
                response => {
                    console.log(response)
                });
        } else {
            this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/juia/info').then(
                response => {
                    if (!response.data.edit)
                        this.save(true);
                },
                response => {
                    console.log(response)
                });
        }
    },
    data() {
        return {
            data: {},
            dataProxies: {}
        }
    },
    computed: {
        formdata: function() {
            var obj = {
                data: this.data
            };
            return Utils.formdata(obj);
        }
    },
    methods: {
        cancel: function() {
            this.$router.push({
                name: this.clazz + 'List'
            });
        },
        remove: function() {
            this.$http.delete(`http://localhost:8080/temis/app/${this.locator}/dados/${this.$route.params.key}`).then(
                response => {
                    this.$router.push({
                        name: this.clazz + 'List'
                    });
                    response;
                },
                response => {
                    console.log(response)
                });
        },
        save: function(toShow) {
            this.$http.post('http://localhost:8080/temis/app/' + this.locator + '/dados', this.formdata, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(
                response => {
                    this.$set(this, "data", response.data.data);
                    if (toShow)
                        this.$router.push({
                            name: this.clazz + 'Show',
                            params: {
                                key: this.data.key
                            }
                        });
                    else
                        this.$router.push({
                            name: this.clazz + 'List'
                        });
                },
                response => {
                    console.log(response)
                });
        },
        addProxy: function(prefix, o, k, v) {
            if (this.dataProxies[prefix] === undefined) {
                this.dataProxies[prefix] = v;
                delete o[k];
                this.$set(o, k, v);
            }
        },
        proxifyAll: function() {
            this.dataProxies = {};
            this.proxify();
        },
        proxify: function(variable) {
            var obj = (variable !== undefined) ? variable : this.data;
            var s = "";
            var f = (prefix, jsonObj) => {
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
                            this.addProxy(prefix, jsonObj, k, v);
                        } else if (Array.isArray(jsonObj)) {
                            if (prefix.endsWith(".")) {
                                nextprefix = prefix.substring(0, prefix.length - 1);
                            }
                            if (s != "")
                                s = s + "&";
                            s += nextprefix + "[" + k + "]=" + v;
                            this.addProxy(nextprefix + "[" + k + "]", jsonObj, k, v);
                        } else {
                            if (s != "")
                                s = s + "&";
                            s = s + prefix + k + "=" + encodeURIComponent(v);
                            this.addProxy(prefix + k, jsonObj, k, v);
                        }
                    }
                }
            }

            f("", obj);
            console.log(this.dataProxies)
        },
        reproxyold: function(v) {
            if (v === undefined) v = this.data;
            for (var k in v) {
                if (Object.prototype.hasOwnProperty.call(v, k)) {
                    var val = v[k]
                    delete v[k];
                    this.$set(v, k, val);
                }
            }
        },
        // atInput: function (variable, event) {
        // this.$set(this.data, variable, event.target.value)
        // this.data[variable] = event.target.value;
        // console.log("alterei", variable)
        // this.reproxy();
        // },
        atChange: function(variable, event) {
            if (event.target.checked !== undefined)
                this.$set(this.data, variable, event.target.checked)
        },
        atValues: function(variable, event) {
            console.log("values")
            console.log(event)
            this.$set(this.data, variable, event[0])
        },
        entityAdd: function(variable) {
            var v = variable.substring(5);
            this.$set(this.data, v, this.data[v] || []);
            this.data[v].push({});
        }
    }
}