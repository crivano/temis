import Vue from 'vue'
import VueResource from 'vue-resource';
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import {
  BootstrapVue,
  BootstrapVueIcons
} from 'bootstrap-vue'

import {
  library
} from '@fortawesome/fontawesome-svg-core'
import {
  faShoppingCart,
  faPlusSquare,
  faMinusSquare,
  faTrashAlt,
  faCheck,
  faTimes,
  faArrowLeft,
  faHome,
  faKey,
  faCalendarPlus,
  faCalendarTimes,
  faBan
} from '@fortawesome/free-solid-svg-icons'
import {
  faWhatsapp
} from '@fortawesome/free-brands-svg-icons'
import {
  FontAwesomeIcon
} from '@fortawesome/vue-fontawesome'

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import VueTheMask from 'vue-the-mask'

import {
  ValidationProvider,
  ValidationObserver,
  extend
} from 'vee-validate';
import {
  // required,
  email
} from 'vee-validate/dist/rules';
import ptBR from "vee-validate/dist/locale/pt_BR";
import ValidacaoBL from './utils/validate';

extend('required', {
  message: "Preenchimento obrigatório",
  validate: function (s) {
    return !!s;
  },
  computesRequired: true

});

extend('email', {
  ...email,
  message: "Preencher com um email válido"
});

extend("cpf", {
  message: () => "CPF inválido",
  validate: ValidacaoBL.validarCPF
});

extend("cnpj", {
  message: () => "CNPJ inválido",
  validate: ValidacaoBL.validarCNPJ
});

extend("cpfcnpj", {
  message: () => "CPF/CNPJ inválido",
  validate: ValidacaoBL.validarCPFCNPJ
});

extend("oab", {
  message: () => "OAB inválido",
  validate: ValidacaoBL.validarOAB
});

extend("cert", {
  message: () => "Número de certidão inválido",
  validate: ValidacaoBL.validarCertidao
});

ptBR.messages.cpf = field => "CPF " + field + " inválido";
ptBR.messages.cnpj = field => "CNPJ " + field + " inválido";
ptBR.messages.oab = field => "OAB " + field + " inválido";
// Validator.localize('pt_BR', ptBR)



import vSelectPage from 'v-selectpage'

import JuiaList from './juia/mixins/list.js'
import JuiaShow from './juia/mixins/show.js'
import JuiaEdit from './juia/mixins/edit.js'
import JuiaText from './juia/components/JuiaText.vue'
import JuiaTextArea from './juia/components/JuiaTextArea.vue'
import JuiaHidden from './juia/components/JuiaHidden.vue'
import JuiaSelect from './juia/components/JuiaSelect.vue'
import JuiaCheck from './juia/components/JuiaCheck.vue'
import JuiaRef from './juia/components/JuiaRef.vue'
import JuiaDate from './juia/components/JuiaDate.vue'
import JuiaStringSelect from './juia/components/JuiaStringSelect.vue'
import JuiaNumeric from './juia/components/JuiaNumeric.vue'
import JuiaInteger from './juia/components/JuiaInteger.vue'
import JuiaMoney from './juia/components/JuiaMoney.vue'
import JuiaFile from './juia/components/JuiaFile.vue'

Vue.component('JuiaTextArea', JuiaTextArea);
Vue.component('JuiaText', JuiaText);
Vue.component('JuiaSelect', JuiaSelect);
Vue.component('JuiaCheck', JuiaCheck);
Vue.component('JuiaRef', JuiaRef);
Vue.component('JuiaHidden', JuiaHidden);
Vue.component('JuiaDate', JuiaDate);
Vue.component('JuiaStringSelect', JuiaStringSelect);
Vue.component('JuiaNumeric', JuiaNumeric);
Vue.component('JuiaInteger', JuiaInteger);
Vue.component('JuiaMoney', JuiaMoney);
Vue.component('JuiaFile', JuiaFile);

import MixinProcessoShow from './juia/mixins/pro_processo_show'
window.juia = {
  list: JuiaList,
  show: JuiaShow,
  edit: JuiaEdit,
  pro_processo_show: MixinProcessoShow,
}


library.add(faShoppingCart)
library.add(faPlusSquare)
library.add(faMinusSquare)
library.add(faWhatsapp)
library.add(faTrashAlt)
library.add(faCheck)
library.add(faTimes)
library.add(faArrowLeft)
library.add(faHome)
library.add(faKey)
library.add(faCalendarPlus)
library.add(faCalendarTimes)
library.add(faBan)

Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.use(VueTheMask)

Vue.use(VueResource);

console.log(process.env.VUE_APP_API_URL)
Vue.http.options.root = process.env.VUE_APP_API_URL
Vue.http.options.credentials = true


Vue.use(vSelectPage, {
  language: 'pt_br',
  dataLoad: function (vue, data, params) {
    return new Promise((resolve, reject) => {
      Vue.http.get(data + "?pageSize=" + params.pageSize + "&pageNumber=" + params.pageNumber +
        (params.searchKey ? "&searchKey=" + params.searchKey : "") +
        (params.searchValue ? "&searchValue=" + params.searchValue : "") +
        (params.queryKey ? "&queryKey=" + params.queryKey : "") +
        (params.queryValue ? "&queryValue=" + params.queryValue : "")
      ).then(resp => resolve(resp), resp => reject(resp))
    })
  }
})

Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

Vue.component('ValidationProvider', ValidationProvider);
Vue.component('ValidationObserver', ValidationObserver);

import VCalendar from 'v-calendar';

// Use v-calendar & v-date-picker components
Vue.use(VCalendar, {
  // input: ["DD/MM/YYYY"],
  // data: ["DD/MM/YYYY"]
});

import VueNumeric from './juia/resources/vue-numeric'

// import VueNumeric from 'vue-numeric'

Vue.use(VueNumeric)

Vue.config.productionTip = false

import ProcessoShow from './components/ProcessoShow.vue'
Vue.component('ProcessoShow', ProcessoShow);

import NormaShow from './components/NormaShow.vue'
Vue.component('NormaShow', NormaShow);

import Watcher from './components/Watcher.vue'
Vue.component('Watcher', Watcher);

import VueLodash from 'vue-lodash'
import lodash from 'lodash'
Vue.use(VueLodash, {
  name: '$lodash',
  lodash: lodash
})

import VueFileAgent from 'vue-file-agent';
import 'vue-file-agent/dist/vue-file-agent.css';
Vue.use(VueFileAgent);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')