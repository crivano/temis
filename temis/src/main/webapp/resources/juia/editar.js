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
		begin: function(key) {
			this.$http.get('http://localhost:8080/temis/app/__LOCATOR__/novo'
								+ (key ? '?keyOriginal='
										+ key : '')).then(
				response => {
					this.data = response.data;
				}, 
				response => {
					console.log(response)
				});
		},
		
		edit: function(key) {
			this.$http.get('http://localhost:8080/temis/app/__LOCATOR__/dados/' + key).then(
				response => {
					this.data = response.data;
				}, 
				response => {
					console.log(response)
				});
		},
		
		save: function() {
			delete $scope.data.change;
			delete $scope.data.title;
			var fd = formdata({
				data : encodeKeys($scope.data)
			});
			
			this.$http.post('http://localhost:8080/temis/app/__LOCATOR__/dados', fd, {
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
				}
			}).then(
					
			
			// delete $scope.data.begin;
			// delete $scope.data.modify;


			$scope
					.addPromise($http(
							{
								url : $scope.api + '/dados',
								method : "POST",
								data : fd,
							})
							.then(
									function(response) {
										// $location.path('/home');

										for ( var index in response.data) {
											var key = response.data[index].key;
										}

										$scope
												.addPromise($http(
														{
															url : 'app/locator?key='
																	+ key,
															method : "GET"
														})
														.then(
																function(
																		response) {
																	var l = response.data.locator;
																	$location
																			.path('/'
																					+ l.locator
																					+ (l.skipShow ? '/list'
																							: '/show/'
																									+ key));
																},
																function(
																		response) {
																	$scope
																			.showError(response);
																}));
										$window
												.ga(
														'send',
														'event',
														$scope.usuario.empresa.identificador,
														($scope.data.key ? "edit-"
																: "insert-")
																+ $scope
																		.pathFirstMember(),
														$scope.usuario.gmail,
														undefined);
									},
									function(response) {
										if (response.status == 400
												&& response.data.errorkind == 'validation') {
											var el = finder
													.findElem(
															$scope,
															'data.'
																	+ response.data.errors[0].category);
											if (el) {
												var flash = function(
														el) {
													angular
															.element(
																	el)
															.addClass(
																	'validation-error');
													$timeout(
															function() {
																angular
																		.element(
																				el)
																		.removeClass(
																				'validation-error');
															}, 600);
												}
												el.focus();
												flash(el);
											} else
												console
														.log('Não foi possível localizar o controle para atribuir o foco.');

											$scope
													.showError(response);
											return;
										}
										$scope.showError(response);
									}));		}
		
		
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







$scope.save = function() {
	delete $scope.data.change;

	delete $scope.data.title;
	// delete $scope.data.begin;
	// delete $scope.data.modify;

	var fd = formdata({
		data : encodeKeys($scope.data)
	});

	$scope
			.addPromise($http(
					{
						url : $scope.api + '/dados',
						method : "POST",
						data : fd,
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
						}
					})
					.then(
							function(response) {
								// $location.path('/home');

								for ( var index in response.data) {
									var key = response.data[index].key;
								}

								$scope
										.addPromise($http(
												{
													url : 'app/locator?key='
															+ key,
													method : "GET"
												})
												.then(
														function(
																response) {
															var l = response.data.locator;
															$location
																	.path('/'
																			+ l.locator
																			+ (l.skipShow ? '/list'
																					: '/show/'
																							+ key));
														},
														function(
																response) {
															$scope
																	.showError(response);
														}));
								$window
										.ga(
												'send',
												'event',
												$scope.usuario.empresa.identificador,
												($scope.data.key ? "edit-"
														: "insert-")
														+ $scope
																.pathFirstMember(),
												$scope.usuario.gmail,
												undefined);
							},
							function(response) {
								if (response.status == 400
										&& response.data.errorkind == 'validation') {
									var el = finder
											.findElem(
													$scope,
													'data.'
															+ response.data.errors[0].category);
									if (el) {
										var flash = function(
												el) {
											angular
													.element(
															el)
													.addClass(
															'validation-error');
											$timeout(
													function() {
														angular
																.element(
																		el)
																.removeClass(
																		'validation-error');
													}, 600);
										}
										el.focus();
										flash(el);
									} else
										console
												.log('Não foi possível localizar o controle para atribuir o foco.');

									$scope
											.showError(response);
									return;
								}
								$scope.showError(response);
							}));
}

$scope.remove = function() {
	if ($routeParams.key === undefined)
		return;

	$scope
			.locate(
					$routeParams.key,
					function(l) {
						ModalService
								.showModal(
										{
											templateUrl : "app/resource/core/dialog-confirmation.html",
											controller : "confirmationCtrl",
											inputs : {
												title : "Confirmação",
												confirmation : "Esta operação é irreversível. Tem certeza que deseja excluir est"
														+ (l.gender === 'SHE' ? 'a'
																: 'e')
														+ " "
														+ l.singular
																.toLowerCase()
														+ "?"
											}
										})
								.then(
										function(modal) {
											modal.element
													.modal();
											modal.close
													.then(function(
															result) {
														if (!result.proceed)
															return;
														$scope
																.removeQuiet();
													});
										});
					})
}

$scope.removeQuiet = function() {
	if ($routeParams.key === undefined)
		return;
	$scope.addPromise($http({
		url : $scope.api + '/dados/' + $routeParams.key,
		method : "DELETE"
	}).then(function(response) {
		$location.path($scope.base + '/list');
	}, function(response) {
		$scope.showError(response);
	}));
}

$scope.cancel = function() {
	$window.history.back();
}

$scope.insert = function() {
	$scope.data.item = ($scope.data.item || []);
	$scope.data.item.push({});
}

$scope.load = function(varName, url) {
	$scope.addPromise($http({
		url : url,
		method : "GET"
	}).then(function(response) {
		$scope[varName] = response.data;
	}, function(response) {
		$scope.showError(response);
	}));
}

$scope.uploadFiles = function(model, files) {
	$scope.files = files;
	if (files && files.length) {
		Upload
				.upload({
					url : 'app/iam-empresa/upload',
					data : {
						files : files
					}
				})
				.then(
						function(response) {
							$timeout(function() {
								eval('$scope.'
										+ model
										+ " = {originalObject: {key: '"
										+ response.data.key
										+ "', description: '"
										+ response.data.contentType
										+ "'}}");
								// $scope.data.ecmConfig[model]
								// = response.data.key;
								eval('$scope.'
										+ model
										+ "_uploadMessage = 'OK, transferência concluída.'");
								// $scope.uploadMessage =
								// "OK, transferência
								// concluída.";
								// $scope.$apply();
							});
						},
						function(response) {
							if (response.status > 0) {
								eval('$scope.'
										+ model
										+ "_uploadMessage = '"
										+ response.status
										+ ': '
										+ response.data
										+ "'");
								// $scope.uploadMessage =
								// response.status + ': ' +
								// response.data;
								eval('delete $scope.'
										+ model);
								// delete
								// $scope.data.ecmConfig[model];
								// $scope.$apply();
							}
						},
						function(evt) {
							eval('$scope.'
									+ model
									+ "_uploadProgress = "
									+ Math
											.min(
													100,
													parseInt(100.0
															* evt.loaded
															/ evt.total)));
							// $scope.progress =
							// Math.min(100, parseInt(100.0
							// * evt.loaded /
							// evt.total));
							// $scope.uploadMessage =
							// $scope.progress + '%';
							eval('$scope.'
									+ model
									+ "_uploadMessage = 'Transferência em progresso, aguarde...'");
							// $scope.uploadMessage =
							// "Transferência em progresso,
							// aguarde...";
							// $scope.$apply();
						});
	}
};

if ($routeParams.key != undefined)
	$scope.edit($routeParams.key);
else if ($routeParams.keyOriginal != undefined)
	$scope.begin($routeParams.keyOriginal);
else
	$scope.begin();