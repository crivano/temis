module.exports = {
	mounted() {
		this.$http.get('http://localhost:8080/temis/app/__LOCATOR__/todos').then(
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
		create: function() {
			this.$router.push({name: '__CLASS__New'});
		},
		edit: function(key) {
			this.$router.push({name: '__CLASS__Edit', params: {key: key}});
		},
		show: function(key) {
			this.$router.push({name: '__CLASS__Show', params: {key: key}});
		}
	}
}