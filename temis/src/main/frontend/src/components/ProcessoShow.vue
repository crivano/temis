<template>
  <div>
    <div class="row">
      <div class="col">
        <h2>
          Pólo Ativo
          <processo-action :act="mapAction['Incluir Parte']"></processo-action>
        </h2>
        <ol v-if="poloAtivo && poloAtivo.length">
          <li v-for="e in poloAtivo" :key="'parte' + e.id">
            <processo-actions
              :show-component="showComponent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-parte
              :show-component="showComponent"
              :event="e"
              :events="event"
              prefixo="Pólo Ativo"
            ></processo-parte>
          </li>
        </ol>
      </div>
      <div class="col">
        <h2>
          Pólo Passivo
          <processo-action :act="mapAction['Incluir Parte']"></processo-action>
        </h2>
        <ol v-if="poloPassivo && poloPassivo.length">
          <li v-for="e in poloPassivo" :key="'parte' + e.id">
            <processo-actions
              :show-component="showComponent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-parte
              :show-component="showComponent"
              :event="e"
              :events="event"
              prefixo="Pólo Passivo"
            ></processo-parte>
          </li>
        </ol>
      </div>
      <div class="col" v-if="poloOutro && poloOutro.length">
        <h2>
          Pólo Outro
          <processo-action :act="mapAction['Incluir Parte']"></processo-action>
        </h2>
        <ol>
          <li v-for="e in poloPassivo" :key="'parte' + e.id">
            <processo-actions
              :show-component="showComponent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-parte
              :show-component="showComponent"
              :event="e"
              :events="event"
              prefixo="Outros"
            ></processo-parte>
          </li>
        </ol>
      </div>
    </div>

    <div class="row">
      <div class="col col-12">
        <h2>
          Dos Pedidos
          <processo-action :act="mapAction['Incluir Pedido']"></processo-action>
        </h2>
        <ol type="a" v-if="pedidos && pedidos.length">
          <li v-for="e in pedidos" :key="'pedido' + e.id">
            <processo-actions
              :show-component="$parent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-pedido :e="e"></processo-pedido>
          </li>
        </ol>
      </div>
    </div>

    <div class="row">
      <div class="col col-12">
        <h2>
          Dos Fatos
          <processo-action :act="mapAction['Incluir Fato']"></processo-action>
        </h2>
        <ol v-if="fatos && fatos.length">
          <li v-for="e in fatos" :key="'fato' + e.id">
            <processo-actions
              :show-component="$parent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-fato :e="e" :event="event"></processo-fato>
          </li>
        </ol>
      </div>
    </div>

    <div class="row">
      <div class="col col-12">
        <h2>
          Do Direito
          <processo-action
            :act="mapAction['Incluir Norma Jurídica']"
          ></processo-action>
          <a
            id="show-seeklex"
            class="btn btn-sm btn-light ml-2"
            title="Pesquisar"
            @click.prevent="showSeeklex()"
          >
            <i class="fas fa-search"></i>
          </a>
          <processo-seeklex v-model="openSeeklex"></processo-seeklex>
        </h2>
        <p
          v-if="fatos && fatos.length && normas.length <= 1"
          class="alert alert-success"
        >
          Norma jurídicas sugeridas:
          <span v-if="!normas || normas.length == 0"
            ><a
              href=""
              @click.prevent="
                $parent.actQuery('Incluir Norma Jurídica');
                $parent.mockNorma(
                  'CDC',
                  'Código do Consumidor',
                  '84',
                  'Na ação que tenha por objeto o cumprimento da obrigação de fazer ou não fazer, o juiz concederá a tutela especifica da obrigação ou determinará providencias que assegurem o resultado prático equivalente ao do adimplemento.',
                  '3',
                  'Sendo relevante o fundamento da demanda e havendo justificado receio de ineficácia do provimento final, é lícito ao juiz conceder a tutela liminarmente ou após justificação prévia, citado o réu.'
                );
              "
            >
              §3º, Art 84, CDC</a
            >
            (98%),</span
          >
          <span v-if="!normas || normas.length <= 1"
            ><a
              href=""
              @click.prevent="
                $parent.actQuery('Incluir Norma Jurídica');
                $parent.mockNorma(
                  'CDC',
                  'Código do Consumidor',
                  '31',
                  'A oferta e apresentação de produtos ou serviços devem assegurar informações corretas, claras, precisas, ostensivas e em língua portuguesa sobre suas características, qualidades, quantidade, composição, preço, garantia, prazos de validade e origem, entre outros dados, bem como sobre os riscos que apresentam à saúde e segurança dos consumidores.'
                );
              "
            >
              Art 31, CDC</a
            >
            (87%).</span
          >
        </p>

        <ol type="i" v-if="normas && normas.length">
          <li v-for="e in normas" :key="'norma' + e.id">
            <processo-actions
              :show-component="$parent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <processo-norma :norma="e.norma"></processo-norma>
          </li>
        </ol>
      </div>
    </div>

    <div class="row">
      <div class="col col-12">
        <h2>
          Da Jurisprudência
          <processo-action
            :act="mapAction['Incluir Jurisprudência']"
          ></processo-action>
        </h2>
        <p
          v-if="fatos && fatos.length > 0 && (!jurisprudencias || jurisprudencias.length == 0)"
          class="alert alert-success"
        >
          Jurisprudências sugeridas:
          <span v-if="!jurisprudencias || jurisprudencias.length == 0"
            ><a
              href=""
              @click.prevent="
                $parent.actQuery('Incluir Jurisprudência');
                $parent.mockJurisprudencia(
                  'TRF2',
                  'Tribunal Regional Federal da 2&ordf; Região',
                  '0033747-44.2015.4.02.5101',
                  '',
                  '04/05/2020',
                  '06/05/2020',
                  'ALUISIO GONÇALVES DE CASTRO MENDES',
                  'EMBARGOS DE DECLARAÇÃO. AUSÊNCIA DE OMISSÃO, CONTRADIÇÃO, OBSCURIDADE OU ERRO MATERIAL. MATÉRIA DEVIDAMENTE APRECIADA PELO ACÓRDÃO RECORRIDO. DESPROVIMENTO DO RECURSO. 1. O artigo 1.022 do Código de Processo Civil elenca, como hipóteses de cabimento dos embargos de declaração, a omissão, a obscuridade, a contradição e o erro material. 2. No caso em questão, inexiste omissão, contradição ou obscuridade, uma vez que, pela leitura do inteiro teor do acórdão embargado, depreende-se que este apreciou devidamente a matéria em debate, analisando de forma exaustiva, clara e objetiva as questões relevantes para o deslinde da controvérsia. 3. Depreende-se, pois, que a parte embargante pretende, na verdade, modificar o julgado, coma rediscussão da matéria, e não sanar qualquer dos mencionados vícios. Note-se que somente em hipóteses excepcionais pode-se emprestar efeitos infringentes aos embargos de declaração, não sendo este o caso dos presentes embargos de declaração. 4. Embargos de declaração desprovidos.',
                  'Vistos e relatados os presentes autos em que são partes as acima indicadas, decide a Quinta Turma Especializada do Tribunal Regional Federal da 2ª Região, por unanimidade, negar provimento aos embargos de declaração, na forma do Relatório e do Voto, que ficam fazendo parte do presente julgado.'
                );
              "
            >
              TRF2 - 0033747-44.2015.4.02.5101</a
            >
            (79%).</span
          >
        </p>
        <ol type="I" v-if="jurisprudencias && jurisprudencias.length">
          <li v-for="e in jurisprudencias" :key="'jurisprudencia' + e.id">
            <processo-actions
              :show-component="$parent"
              :event="e"
              :actions="map[e.id].action"
            ></processo-actions>
            <h5 :key="'jurisprudencia-titulo' + e.id">
              {{ e.jurisprudencia.siglaTribunal }} -
              {{ e.jurisprudencia.numeroProcesso }}
            </h5>
            <p
              v-if="e.jurisprudencia.textoEmenta"
              :key="'jurisprudencia-ementa' + e.id"
            >
              {{ e.jurisprudencia.textoEmenta }}
            </p>
            <p
              v-if="e.jurisprudencia.textoCertidaoDeJulgamento"
              :key="'jurisprudencia-certidao' + e.id"
            >
              {{ e.jurisprudencia.textoCertidaoDeJulgamento }}
            </p>
            <p
              v-if="
                e.jurisprudencia.dataJulgamento ||
                  e.jurisprudencia.dataPublicacao ||
                  e.jurisprudencia.nomeOrgaoJulgador ||
                  e.jurisprudencia.nomeRelator
              "
              :key="'jurisprudencia-datas' + e.id"
            >
              <span v-if="e.jurisprudencia.dataJulgamento"
                >Data do Julgamento: {{ e.jurisprudencia.dataJulgamento }}</span
              ><span v-if="e.jurisprudencia.dataJulgamento"
                >, Data da Publicação:
                {{ e.jurisprudencia.dataPublicacao }}</span
              ><span v-if="e.jurisprudencia.nomeOrgaoJulgador"
                >, Órgão Julgador:
                {{ e.jurisprudencia.nomeOrgaoJulgador }}</span
              ><span v-if="e.jurisprudencia.nomeRelator"
                >, Relator: {{ e.jurisprudencia.nomeRelator }}</span
              >.
            </p>
          </li>
        </ol>
      </div>
    </div>
  </div>
</template>

<script>
import ProcessoParte from "./ProcessoParte";
import ProcessoPedido from "./ProcessoPedido";
import ProcessoFato from "./ProcessoFato";
import ProcessoActions from "./ProcessoActions";
import ProcessoAction from "./ProcessoAction";
import ProcessoSeeklex from "./ProcessoSeeklex";
import ProcessoNorma from "./ProcessoNorma";

export default {
  props: {
    data: { type: Object },
    event: { type: Array },
    action: { type: Array },
  },
  data() {
    return {
      showComponent: this.$parent,
      openSeeklex: false,
    };
  },
  computed: {
    map: function() {
      const map = {};
      for (var i = 0; i < this.data.evento.length; i++) {
        var e = this.data.evento[i];
        map[e.id] = {
          action: this.event[i].action,
        };
      }
      return map;
    },
    mapAction: function() {
      const map = {};
      for (var i = 0; i < this.action.length; i++) {
        var a = this.action[i];
        map[a.name] = a;
      }
      return map;
    },
    poloAtivo: function() {
      if (!this.data || !this.data.evento) return;
      return this.partes.filter(
        (item) => item.tipoDeParte && item.tipoDeParte.startsWith("ATIVO")
      );
    },
    poloPassivo: function() {
      if (!this.data || !this.data.evento) return;
      return this.partes.filter(
        (item) => item.tipoDeParte && item.tipoDeParte.startsWith("PASSIVO")
      );
    },
    poloOutro: function() {
      if (!this.data || !this.data.evento) return;
      return this.partes.filter(
        (item) => item.tipoDeParte && item.tipoDeParte.startsWith("OUTRO")
      );
    },
    partes: function() {
      if (!this.data || !this.data.evento) return;
      return this.data.evento.filter(
        (item) => item.kind == "EventoProcessualInclusaoDeParte"
      );
    },
    fatos: function() {
      if (!this.data || !this.data.evento) return;
      return this.data.evento.filter(
        (item) => item.kind == "EventoProcessualInclusaoDeFato"
      );
    },
    normas: function() {
      if (!this.data || !this.data.evento) return;
      return this.data.evento.filter(
        (item) => item.kind == "EventoProcessualInclusaoDeNorma"
      );
    },
    jurisprudencias: function() {
      if (!this.data || !this.data.evento) return;
      return this.data.evento.filter(
        (item) => item.kind == "EventoProcessualInclusaoDeJurisprudencia"
      );
    },
    pedidos: function() {
      if (!this.data || !this.data.evento) return;
      return this.data.evento
        .filter((item) => item.kind == "EventoProcessualInclusaoDePedido")
        .sort((a, b) => {
          if (a.liminar === "SIM" && b.liminar === "NAO") {
            return -1;
          }
          if (a.liminar === "NAO" && b.liminar === "SIM") {
            return 1;
          }
          if (a.dtIni > b.dtIni) {
            return 1;
          }
          if (a.dtIni < b.dtIni) {
            return -1;
          }
          return a.id - b.id;
        });
    },
  },
  methods: {
    showSeeklex: function() {
      this.openSeeklex = true;
    },
    hideSeeklex: function() {
      this.openSeeklex = false;
    },
  },
  components: {
    "processo-norma": ProcessoNorma,
    "processo-actions": ProcessoActions,
    "processo-action": ProcessoAction,
    "processo-parte": ProcessoParte,
    "processo-pedido": ProcessoPedido,
    "processo-fato": ProcessoFato,
    "processo-seeklex": ProcessoSeeklex,
  },
};
</script>
