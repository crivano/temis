import Vue from "vue";
import Router from "vue-router";
import ConsultaSimples from "@/components/ConsultaSimples";
import Processo from "@/components/Processo";
import ProcessoLista from "@/components/ProcessoLista";
import PeticaoInicial from "@/components/PeticaoInicial";
import PeticaoIntercorrente from "@/components/PeticaoIntercorrente";
import ConsultaCertidao from "@/components/ConsultaCertidao";
import Certidao from "@/components/Certidao";
import AvisoLista from "@/components/AvisoLista";
import AvisoConfirmadoRecentes from "@/components/AvisoConfirmadoRecentes";
import AvisoConfirmadoLista from "@/components/AvisoConfirmadoLista";
import EtiquetaLista from "@/components/EtiquetaLista";
import Mesa from "@/components/Mesa";
import Documento from "@/components/Documento";
import Login from "@/components/Login";
import Sugestoes from "@/components/Sugestoes";
import Sobre from "@/components/Sobre";
import Status from "@/components/Status";
import ProcessoBL from "../bl/processo.js";

Vue.use(Router);

var r = new Router({
  routes: [{
      path: "/login",
      name: "Login",
      component: Login
    },
    {
      path: "/",
      name: "Consulta Simples",
      component: ConsultaSimples
    },
    {
      path: "/consultar/:numero",
      name: "Consultar",
      component: ConsultaSimples
    },
    {
      path: "/processo/:numero",
      name: "Processo",
      component: Processo,
      meta: {
        title: route => {
          return (
            "Processo " +
            ProcessoBL.formatarProcesso(
              ProcessoBL.somenteNumeros(route.params.numero)
            ) +
            ".."
          );
        }
      }
    },
    {
      path: "/processo-lista",
      name: "Lista de Processos",
      component: ProcessoLista
    },
    {
      path: "/consultar-certidao",
      name: "Consultar Certidão",
      component: ConsultaCertidao
    },
    {
      path: "/emitir-certidao/:requisitante/:cpfcnpj",
      name: "Emitir Certidão",
      component: Certidao
    },
    {
      path: "/autenticar-certidao/:numero/:cpfcnpj",
      name: "Autenticar Certidão",
      component: Certidao
    },
    {
      path: "/reimprimir-certidao/:numero/:cpfcnpj",
      name: "Reimprimir Certidão",
      component: Certidao
    },
    {
      path: "/etiqueta-lista",
      name: "Lista de Etiquetas",
      component: EtiquetaLista
    },
    {
      path: "/peticao-inicial",
      name: "Petição Inicial",
      component: PeticaoInicial
    },
    {
      path: "/peticao-intercorrente",
      name: "Petição Intercorrente",
      component: PeticaoIntercorrente
    },
    {
      path: "/aviso-lista",
      name: "Lista de Avisos",
      component: AvisoLista
    },
    {
      path: "/mesa",
      name: "Mesa",
      component: Mesa
    },
    {
      path: "/documento/:numero",
      name: "Documento",
      component: Documento,
      meta: {
        title: route => {
          return "Documento " + route.params.numero + "..";
        }
      }
    },
    {
      path: "/aviso-confirmado-recentes",
      name: "Avisos Confirmados Recentemente",
      component: AvisoConfirmadoRecentes
    },
    {
      path: "/aviso-confirmado-lista/:dataInicial/:dataFinal/:porConfirmacao/:porOmissao/:doGrupo",
      name: "Lista de Avisos Confirmados",
      component: AvisoConfirmadoLista
    },
    {
      path: "/sugestoes",
      name: "Sugestões",
      component: Sugestoes
    },
    {
      path: "/status",
      name: "Status",
      component: Status
    },
    {
      path: "/sobre",
      name: "Sobre",
      component: Sobre
    },
    {
      path: "*",
      redirect: "/"
    }
  ],

  scrollBehavior(to, from, savedPosition) {
    if (to.name === "Documento") return {
      x: 0,
      y: 0
    };
  }
});

var addCrud = function (name, path, editCtrl, showCtrl, listCtrl) {
  r.addRoutes([{
      path: "/" + path + "/new",
      name: name + "New",
      component: import("/temis/app/" + path + "/html/editar")
    },
    {
      path: "/" + path + "/new-duplicate/:keyOriginal",
      name: name + "New",
      component: import("/temis/app/" + path + "/html/editar")
    },
    {
      path: "/" + path + "/edit/:key",
      name: name + "Edit",
      component: import("/temis/app/" + path + "/html/editar")
    },
    {
      path: "/" + path + "/show/:key",
      name: name + "Show",
      component: import("/temis/app/" + path + "/html/exibir")
    },
    {
      path: "/" + path + "/list",
      name: name + "List",
      component: import("/temis/app/" + path + "/html/listar")
    }
  ]);
}

addCrud("PeticaoInicial", "inicial");
addCrud("Pessoa", "pessoa");

// Lazy-loads view components, but with better UX. A loading view
// will be used if the component takes a while to load, falling
// back to a timeout view in case the page fails to load. You can
// use this component to lazy-load a route with:
//
// component: () => lazyLoadView(import('@views/my-view'))
//
// NOTE: Components loaded with this strategy DO NOT have access
// to in-component guards, such as beforeRouteEnter,
// beforeRouteUpdate, and beforeRouteLeave. You must either use
// route-level guards instead or lazy-load the component directly:
//
// component: () => import('@views/my-view')
//
function lazyLoadView(AsyncView) {
  const AsyncHandler = () => ({
    component: AsyncView,
    // A component to use while the component is loading.
    loading: require('@views/_loading.vue').default,
    // Delay before showing the loading component.
    // Default: 200 (milliseconds).
    delay: 400,
    // A fallback component in case the timeout is exceeded
    // when loading the component.
    error: require('@views/_timeout.vue').default,
    // Time before giving up trying to load the component.
    // Default: Infinity (milliseconds).
    timeout: 10000,
  })

  return Promise.resolve({
    functional: true,
    render(h, {
      data,
      children
    }) {
      // Transparently pass any props or children
      // to the view component.
      return h(AsyncHandler, data, children)
    },
  })
};

export default r;