// import Vue from 'vue'

// import {
//     TableSimplePlugin
// } from "bootstrap-vue";

import Utils from './utils.js'

export default {
    mounted() {
        this.load();
        this.$on('validation-changed', (invalid) => {
            this.actInvalid = invalid
        });
    },
    data() {
        return {
            data: {
                change: []
            },
            tag: [],
            action: [],
            event: [],
            act: undefined,
            actTitle: undefined,
            actEventId: undefined,
            actTemplate: undefined,
            actInvalid: undefined,
            actShow: false,
            audit: false
        }
    },
    computed: {
        filteredEvents: function () {
            if (!this.event) return [];
            return this.event.filter(e => this.audit ? e.visibilidade !== 'EXIBIR_NUNCA' : e.visibilidade === 'EXIBIR_SEMPRE');
        },
        action: function () {
            return this.$refs.action;
        },
        required: function () {
            var r = [];
            if (this.event) {
                this.event.forEach(e => {
                    if (!e.action) return;
                    e.action.forEach(a => {
                        if (a.required)
                            r.push({
                                event: e,
                                action: a
                            });
                    })
                });
            }
            if (this.action) {
                this.action.forEach(a => {
                    if (a.required)
                        r.push({
                            action: a
                        });
                })
            }
            return r;
        }
    },
    methods: {
        load() {
            this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/dadoseacoes/' + this.$route.params.key).then(
                response => {
                    this.data = response.data.data;
                    this.action = response.data.action;
                    this.tag = response.data.tag;
                    this.event = response.data.event;
                    if (this.event) {
                        var set = {};
                        this.data.evento.forEach(e => {
                            set[e.id] = e;
                        })
                        this.event.forEach(e => {
                            e.evento = set[e.id];
                        })
                    }
                },
                response => {
                    console.log(response)
                });
        },
        '$eval'(expr) {
            return eval(expr)
        },
        edit: function () {
            this.$router.push({
                name: this.clazz + 'Edit',
                params: {
                    key: this.data.key
                }
            });
        },
        showPdf: function (key) {
            var win = window.open('http://localhost:8080/temis/app/download/' + key, '_blank');
            win.focus();
        },
        actQuery: function (name, eventId) {
            this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/acao/' + name + '/' + this.$route.params.key + (eventId ? "?idEvento=" + eventId : "")).then(
                response => {
                    if (response.data.confirmation) {
                        this.boxOne = ''
                        this.$bvModal.msgBoxConfirm(response.data.confirmation, {
                                title: 'Confirmação',
                                size: 'sm',
                                okTitle: 'OK',
                                cancelVariant: 'light',
                                cancelTitle: 'Cancelar',
                                hideHeaderClose: false,
                                centered: true
                            })
                            .then(value => {
                                if (value) {
                                    this.actRun(name, eventId, {});
                                }
                            })
                    } else {
                        if (response.data.html) {
                            const html = response.data.html.replace('v-if="data"', '')
                            console.log(html)
                            this.$set(this, 'act', response.data.act);
                            this.actTemplate = '<validation-observer ref="observer" v-slot="obs">' + html + '<watcher ref="watcher" :value="obs.invalid"/>' +
                                '<div class="modal-footer pl-0 pr-0 pb-0"><b-button name="cancelar" variant="secondary" @click.prevent="$bvModal.hide(\'action\')">Cancelar</b-button>' +
                                '<b-button name="ok" :disabled="obs.invalid" variant="primary" @click.prevent="$parent.$parent.$parent.actEnter()">Ok</b-button></div></validation-observer>';
                            // this.actTemplate = response.data.html;
                            this.actTitle = name;
                            this.actEventId = eventId;
                            this.$bvModal.show('action');
                            this.$nextTick(() => {
                                var setFocus = (children) => {
                                    for (var i = 0; i < children.length; i++) {
                                        var child = children[i];
                                        if (child.focus) {
                                            child.focus();
                                            return;
                                        }
                                        if (child.$children) {
                                            setFocus(child.$children);
                                        }
                                    }
                                }
                                setFocus(this.$refs.action.$children)
                            })
                        } else {
                            this.actRun(name, eventId, {});
                        }
                    }
                },
                response => {
                    console.log(response)
                });
        },
        actOK: function () {
            if (this.actInvalid)
                return;
            this.actRun(this.actTitle, this.actEventId, this.$refs.action.act);
        },
        actEnter: function () {
            console.log('enter')
            if (this.actInvalid)
                return;
            this.actOK();
            this.$bvModal.hide('action');
        },
        actRun: function (name, eventId, act) {
            this.$set(this, 'act', act);
            this.$http.post('http://localhost:8080/temis/app/' + this.locator + '/acao/' + name + '/' + this.$route.params.key + (eventId ? "?idEvento=" + eventId : ""), Utils.formdata({
                act: this.act
            }), {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            }).then(
                response => {
                    if (response.data.next) {
                        // $scope.$eval(response.data.next, {
                        // $scope : $scope,
                        // window : window
                        // });
                        //eval(response.data.next);
                    }

                    // this.$set(this, "data", response.data);
                    // this.$router.push({
                    //     name: this.clazz + 'List'
                    // });

                    this.load();
                },
                response => {
                    this.$bvModal.msgBoxOk(response.data.errormsg)
                });
        },
        actMiniTitle: function (name, active, explanation) {
            return name + ": " + explanation;
        },
        toggleAudit: function () {
            this.audit = !this.audit;
        },
        //     actMiniQuery: function (id, name) {
        //         this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/miniacao/' + id + '/' + name + '/' + this.$route.params.key).then(
        //             response => {
        //                 if (response.data.confirmation) {
        //                     this.boxOne = ''
        //                     this.$bvModal.msgBoxConfirm(response.data.confirmation, {
        //                             title: 'Confirmação',
        //                             size: 'sm',
        //                             okTitle: 'OK',
        //                             cancelVariant: 'light',
        //                             cancelTitle: 'Cancelar',
        //                             hideHeaderClose: false,
        //                             centered: true
        //                         })
        //                         .then(value => {
        //                             if (value) {
        //                                 this.actMiniRun(id, name, {});
        //                             }
        //                         })
        //                 } else {
        //                     if (response.data.html) {
        //                         this.$set(this, 'act', response.data.act);
        //                         this.actTemplate = response.data.html;
        //                         this.actTitle = name;
        //                         this.$bvModal.show('action');
        //                     } else {
        //                         this.actMiniRun(id, name, {});
        //                     }
        //                 }
        //             },
        //             response => {
        //                 console.log(response)
        //             });
        //     },
        //     actMiniRun: function (id, name, act) {
        //         this.$set(this, 'act', act);
        //         this.$http.post('http://localhost:8080/temis/app/' + this.locator + '/miniacao/' + id + '/' + name + '/' + this.$route.params.key, Utils.formdata({
        //             act: this.act
        //         }), {
        //             headers: {
        //                 'Content-Type': 'application/x-www-form-urlencoded'
        //             }
        //         }).then(
        //             response => {
        //                 if (response.data.next) {
        //                     // $scope.$eval(response.data.next, {
        //                     // $scope : $scope,
        //                     // window : window
        //                     // });
        //                     //eval(response.data.next);
        //                 }

        //                 // this.$set(this, "data", response.data);
        //                 // this.$router.push({
        //                 //     name: this.clazz + 'List'
        //                 // });

        //                 this.load();
        //             },
        //             response => {
        //                 this.$bvModal.msgBoxOk(response.data.errormsg)
        //                 // .then(value => {})
        //                 // .catch(err => {})
        //             });
        //     },
    }
}