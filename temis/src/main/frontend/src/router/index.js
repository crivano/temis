import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Painel from '../views/Painel.vue'
import Login from '../views/Login.vue'
import httpVueLoader from 'http-vue-loader';


Vue.use(VueRouter)

httpVueLoader.httpRequest = function (url) {

    return Vue.http.get(url)
        .then(function (res) {

            return res.data;
        })
        .catch(function (err) {

            return Promise.reject(err.status);
        });
}

const routes = [{
    path: '/',
    name: 'Painel',
    component: Painel
}, {
    path: '/login',
    name: 'Login',
    component: Login
}, {
    path: '/home',
    name: 'Home',
    component: Home
},
{
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
        import( /* webpackChunkName: "about" */ '../views/About.vue')
}
]

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return {
                x: 0,
                y: 0
            }
        }
    }
})

export const buildRoutes = function (name, path) {
    const api = "http://localhost:8080/temis";
    const editar = httpVueLoader(`${api}/app/${path}/juia/editar.vue`)
    const exibir = httpVueLoader(`${api}/app/${path}/juia/exibir.vue`)
    const listar = httpVueLoader(`${api}/app/${path}/juia/listar.vue`)
    return [{
        path: "/" + path + "/new",
        name: name + "New",
        component: editar
    },
    {
        path: "/" + path + "/new-duplicate/:keyOriginal",
        name: name + "Duplicate",
        component: editar
    },
    {
        path: "/" + path + "/edit/:key",
        name: name + "Edit",
        component: editar
    }, {
        path: "/" + path + "/show/:key",
        name: name + "Show",
        component: exibir
    }, {
        path: "/" + path + "/list",
        name: name + "List",
        component: listar
    }]
}

export default router;