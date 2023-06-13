export default {
    mounted() {
        this.$http.get('http://localhost:8080/temis/app/' + this.locator + '/todos').then(
            response => {
                this.list = response.data.list;
            },
            response => {
                console.log(response)
            });
    },
    data() {
        return {
            list: []
        }
    },
    methods: {
        create: function () {
            this.$router.push({
                name: this.clazz + 'New'
            });
        },
        click: function (o) {
            this.show(o);
        },
        edit: function (o) {
            this.$router.push({
                name: this.clazz + 'Edit',
                params: {
                    key: o.key
                }
            });
        },
        show: function (o) {
            this.$router.push({
                name: this.clazz + 'Show',
                params: {
                    key: o.key
                }
            });
        }
    }
}