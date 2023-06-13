<template>
  <div id="app">
    <b-navbar type="dark" variant="dark">
      <b-navbar-brand href="#/"
        ><img class="brand" src="./assets/logo.png" />
      </b-navbar-brand>

      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

      <b-collapse id="nav-collapse" is-nav>
        <b-navbar-nav>
          <b-nav-item text="Painel" href="#/">Painel</b-nav-item>
          <b-nav-item-dropdown text="Criar">
            <b-dropdown-item v-for="m in $store.state.model.filter(a => a.menuCreate)" :key="m.className" :href="'#/' + m.locator + '/new'">{{ m.singular }}</b-dropdown-item>
          </b-nav-item-dropdown>
          <b-nav-item-dropdown text="Cadastros">
            <b-dropdown-item v-for="m in $store.state.model.filter(a => a.menuList)" :key="m.className" :href="'#/' + m.locator + '/list'">{{ m.singular }}</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div class="container-fluid content">
      <router-view />
    </div>
  </div>
</template>

<script>
import AuthBL from "./utils/auth.js";
import {buildRoutes} from "./router"

export default {
  name: "App",
  async mounted() {
    await this.$store.dispatch('inicializar');
    let routes = []
    this.$store.state.model.forEach(i => routes = routes.concat(buildRoutes(i.classSimpleName, i.locator)))
    this.$router.addRoutes(routes)
    this.$on("updateLogged", (token) => {
      if (token) {
        AuthBL.setIdToken(token);
        this.jwt = AuthBL.decodeToken(token);
        // $rootScope.updateLogged();
        // $state.go('consulta-processual');
        this.$router.push({
          name: "Mesa",
          params: { exibirAcessoAnterior: true },
        });
      }
    });
  },
};
</script>

<style>
.carrinho-icone {
  color: white;
  width: 2em !important;
  height: 2em;
}
.carrinho-badge {
  margin-left: 0.5em;
}

/* JUIA */
header.juia {
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  list-style: none;
  background-color: #e2e3e5 !important;
  color: #383d41;
  opacity: 1;
  border-radius: 0.25rem;
}

/* JUIA */
header.juia-strong {
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  list-style: none;
  background-color: var(--dark) !important;
  color: #fff;
  opacity: 1;
  border-radius: 0.25rem;
}

.juia-title {
  padding: 0.75rem 1rem;
  margin-left: -15px;
  margin-right: -15px;
  margin-bottom: 1rem;
  list-style: none;
  background-color: var(--primary) !important;
  opacity: 1;
  color: #fff;
  border-radius: 0;
}

.juia-title-old {
  padding: 0.75rem 1rem;
  margin-left: -15px;
  margin-right: -15px;
  margin-top: -15px;
  margin-bottom: 1rem;
  list-style: none;
  background-color: #e9ecef !important;
  border-radius: 0.25rem;
}

.juia-form .label {
  display: block;
  margin-bottom: 0px;
  font-weight: 400;
}

big.juia {
  font-size: 150%;
}

li.blog-tags-enabled-true:hover {
  background: var(--success) !important;
}

li.blog-tags-enabled-true:hover i {
  color: #fff !important;
}

li.blog-tags-enabled-false:hover {
  background: var(--danger) !important;
}

li.blog-tags-enabled-false:hover i {
  color: #fff !important;
}

li.blog-tags-enabled-false {
  color: #a7a7a7 !important;
}

li.blog-tags-enabled-false i {
  color: #a7a7a7 !important;
}

.blog-tags-required i {
  color: var(--warning) !important;
}

li.blog-tags-on i {
  color: var(--primary) !important;
}

.i-required {
  color: var(--warning) !important;
}

ul.list-required li i {
  color: var(--warning) !important;
}

ul.blog-tags li {
  padding: 2px 7px;
  background: #f7f7f7;
  margin: 0 3px 6px 0;
  display: inline-block;
}

ul.blog-tags li i {
  color: #666;
}

ul.blog-tags li:hover,
ul.blog-tags l1:hover i {
  color: #fff !important;
  text-decoration: none;
  transition: all 0.2s ease-in-out;
}

#footer:after {
  clear: both;
  content: "";
  display: table;
}

.xrp-label {
  margin-bottom: 0.5em;
}

.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.label-clue {
  margin-left: 0.25em;
  color: #ccc;
  color: #ccc;
  font-size: 60%;
  vertical-align: super;
  /*	float: right;
	margin-top: 4px; */
}

.form-control.is-invalid {
  background-image: none !important;
}

.invalid-feedback {
  margin-top: 0 !important;
}
</style>
