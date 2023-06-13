import Utils from './utils.js'

export default {
    methods: {
        incluirNorma: function (o) {
            var act = {};
            if (o.normaRef) {
                act.siglaNorma = o.normaRef
            }
            if (o.normaTexto) {
                act.nomeNorma = o.normaTexto
            }
            if (o.artigoRef && o.artigoTexto) {
                act.numeroArtigo = o.artigoRef
                act.textoArtigo = o.artigoTexto
            }
            if (o.paragrafoRef && o.paragrafoTexto) {
                act.numeroParagrafo = o.paragrafoRef
                act.textoParagrafo = o.paragrafoTexto
            }
            if (o.incisoRef && o.incisoTexto) {
                act.numeroInciso = o.incisoRef
                act.textoInciso = o.incisoTexto
            }
            if (o.alineaRef && o.alineaTexto) {
                act.numeroAlinea = o.alineaRef
                act.textoAlinea = o.alineaTexto
            }
            this.$http.post('http://localhost:8080/temis/app/' + this.locator + '/acao/Incluir%20Norma%20Jur%C3%ADdica/' + this.$route.params.key,
                Utils.formdata({
                    act: act
                }), {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).then(
                () => {
                    this.load();
                },
                response => {
                    this.$bvModal.msgBoxOk(response.data.errormsg)
                });
            this.$refs.processoShow.hideSeeklex();
        },
        mockNorma: function (siglaNorma, nomeNorma, numeroArtigo, textoArtigo, numeroParagrafo, textoParagrafo) {
            window.setTimeout(function () {
                var act = this.action.act;
                act.siglaNorma = siglaNorma;
                act.nomeNorma = nomeNorma;
                act.numeroArtigo = numeroArtigo;
                act.textoArtigo = textoArtigo;
                act.numeroParagrafo = numeroParagrafo;
                act.textoParagrafo = textoParagrafo;
                this.action.proxify();
            }, 500)
        },
        mockJurisprudencia: function (
            siglaTribunal,
            nomeOrgaoJulgador,
            numeroProcesso,
            numeroAntigoDoProcesso,
            dataJulgamento,
            dataPublicacao,
            nomeRelator,
            textoEmenta,
            textoCertidaoDeJulgamento
        ) {
            window.setTimeout(() => {
                var act = this.action.act;
                act.siglaTribunal = siglaTribunal;
                act.nomeOrgaoJulgador = nomeOrgaoJulgador;
                act.numeroProcesso = numeroProcesso;
                act.numeroAntigoDoProcesso = numeroAntigoDoProcesso;
                act.dataJulgamento = dataJulgamento;
                act.dataPublicacao = dataPublicacao;
                act.nomeRelator = nomeRelator;
                act.textoEmenta = textoEmenta;
                act.textoCertidaoDeJulgamento = textoCertidaoDeJulgamento;
                this.action.proxify();
            }, 500)
        },
    }
}