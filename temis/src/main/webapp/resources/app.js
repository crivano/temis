var app = angular.module('app', [ '720kb.datepicker', 'ngFileUpload',
		'angularjs-dropdown-multiselect', 'ngAnimate', 'ui.utils.masks',
		'ngMask', 'ngRoute', 'cgBusy', 'ngSanitize',
		'angularUtils.directives.dirPagination', 'angularModalService',
		'angucomplete-alt', 'webcam' ], function($compileProvider) {
	// configure new 'compile' directive by passing a directive
	// factory function. The factory function injects the '$compile'
	$compileProvider.directive('compile', function($compile) {
		// directive factory creates a link function
		return function(scope, element, attrs) {
			scope.$watch(function(scope) {
				// watch the 'compile' expression for changes
				return scope.$eval(attrs.compile);
			}, function(value) {
				// when the 'compile' expression changes
				// assign it into the current DOM
				element.html(value);

				// compile the new DOM and link it to the current
				// scope.
				// NOTE: we only compile .childNodes so that
				// we don't get into infinite loop compiling ourselves
				$compile(element.contents())(scope);
			});
		};
	});
});

app.constant('myModels', []);

app.factory('AuthInterceptor', function($rootScope, $q, $window, $location) {
	return {
		request : function(config) {
			return config;
		},

		responseError : function(response) {
			if (response.status === 401) {
				$rootScope.showError(response.data.errormsg, function() {
					$rootScope.loadUsuario();
					$location.path('/sobre');
				})
				return;
			}
			return $q.reject(response);
		}
	};
});

app.config([ '$routeProvider', '$provide', 'myModels', '$httpProvider',
		function($routeProvider, $provide, myModels, $httpProvider) {
			$httpProvider.interceptors.push('AuthInterceptor');

			var addCrud = function(path, editCtrl, showCtrl, listCtrl) {
				$routeProvider.when("/" + path + "/new", {
					templateUrl : "/temis/app/" + path + "/html/editar",
					controller : editCtrl || "crudEditCtrl"
				}).when("/" + path + "/new-duplicate/:keyOriginal", {
					templateUrl : "/temis/app/" + path + "/html/editar",
					controller : editCtrl || "crudEditCtrl"
				}).when("/" + path + "/edit/:key", {
					templateUrl : "/temis/app/" + path + "/html/editar",
					controller : editCtrl || "crudEditCtrl"
				}).when("/" + path + "/show/:key", {
					templateUrl : "/temis/app/" + path + "/html/exibir",
					controller : showCtrl || "crudShowCtrl"
				}).when("/" + path + "/list", {
					templateUrl : "/temis/app/" + path + "/html/listar",
					controller : listCtrl || "crudListCtrl"
				});
			}

			addCrud("inicial");
			addCrud("pessoa");

			$routeProvider.when('/home', {
				templateUrl : '/temis/app/resource/core/home.html',
				controller : 'ctrl'
			}).when('/sugestoes', {
				templateUrl : '/temis/app/resource/core/sugestoes.html',
				controller : 'ctrlSugerir'
			}).when('/sobre', {
				templateUrl : '/temis/app/resource/core/sobre.html',
				controller : 'ctrlSobre'
			}).when('/ajuda', {
				templateUrl : '/temis/app/resource/core/ajuda.html',
				controller : 'ctrlSobre'
			}).otherwise({
				redirectTo : '/home'
			});

			// This is used to find a input in the DOM from the model name
			window.myModels = myModels; // just for debugging
			$provide.decorator('ngModelDirective', function($delegate) {
				var directive = $delegate[0];
				var compile = directive.compile;
				directive.compile = function(tElement, tAttrs) {
					var link = compile.apply(this, arguments);
					tElement.append('<div>Added in the decorator</div>');
					return function(scope, elem, attrs) {
						link.post.apply(this, arguments);
						v = scope.$eval(tAttrs.ngModel);
						myModels.push({
							scope : scope,
							elem : elem,
							val : function() {
								return scope.$eval(tAttrs.ngModel);
							},
						});
					};
				};
				return $delegate;
			});

		} ]);

// This is used to find a input in the DOM from the model name
app
		.factory(
				'finder',
				function(myModels) {
					function findElem($scope, path) {
						var originalVal = $scope.$eval(path), possibleMatches = [], result = null;
						angular.forEach(myModels, function(model) {
							if (angular.equals(model.val(), originalVal))
								possibleMatches.push(model);
						});
						// temp change: the blah property is arbitrary
						try {
							var newVal = $scope.$eval(path + " = "
									+ JSON.stringify({
										val : path,
										blah : 'changed'
									}));
						} catch (e) {
							return null;
						}
						// find it: note: this could be made more efficient with
						// a breaking loop
						angular.forEach(possibleMatches, function(model) {
							if (angular.equals(model.val(), newVal))
								result = model;
						});
						// reset
						$scope
								.$eval(path + " = "
										+ JSON.stringify(originalVal));
						return result && result.elem;
					}
					return {
						findElem : findElem
					}
				})

app.directive('ngDebounce', function($timeout) {
	return {
		restrict : 'A',
		require : 'ngModel',
		priority : 99,
		link : function(scope, elm, attr, ngModelCtrl) {
			if (attr.type === 'radio' || attr.type === 'checkbox') {
				return;
			}

			var delay = parseInt(attr.ngDebounce, 10);
			if (isNaN(delay)) {
				delay = 1000;
			}

			elm.unbind('input');

			var debounce;
			elm.bind('input', function() {
				$timeout.cancel(debounce);
				debounce = $timeout(function() {
					scope.$apply(function() {
						ngModelCtrl.$setViewValue(elm.val());
					});
				}, delay);
			});
			elm.bind('blur', function() {
				scope.$apply(function() {
					ngModelCtrl.$setViewValue(elm.val());
				});
			});
		}
	};
});

app
		.controller(
				'routerCtrl',
				function($scope, $http, $templateCache, $location, $window,
						ModalService, $route, $timeout, $rootScope) {
					$scope.promise = undefined;

					$scope.addPromise = function(p) {
						// $scope.promise.length = 0;
						// $scope.promise.push(p);
						// console.log("addPromise", $scope.promise);
						// $scope.promise.unshift(p);
						$scope.promise = p;
					}

					$scope.pathFirstMember = function() {
						var regex = /\/([a-zA-Z0-9\-\_]+)\//;
						var str = $location.path();
						var m;

						if ((m = regex.exec(str)) !== null) {
							return m[1];
						}
					}

					$scope.navigate = function(url, target) {
						if (!target) {
							var ittrufusion = "https://ittrufusion.appspot.com";
							if ($window.location.href.substring(0, 16) == "http://localhost")
								ittrufusion = "http://localhost:8888";
							url = url.replace("__ittrufusion__", encodeURI(
									ittrufusion).replace('#', "%23"));
							url = url.replace("__current__", encodeURI(
									$window.location.href).replace('#', "%23"));
							$window.location.href = url;
						} else {
							$window.open(url, target);
						}
					}

					$rootScope.loadUsuario = function() {
						$scope
								.addPromise($http({
									url : 'app/usuario',
									method : "GET"
								})
										.then(
												function(response) {
													$scope.usuario = response.data.usuario;
													$scope.editing = false;
													$scope.settings = false;

													if (!$scope
															.alreadyLoggedIn())
														$location
																.path('/sobre');

													if ($scope.usuario.empresa.bizConfig.paypalAtivo
															&& !$scope.paypalScriptCarregado) {
														var $script = document
																.createElement('script');
														$script.src = 'https://www.paypalobjects.com/api/checkout.js';
														$script.id = 'paypal-script';
														document.body
																.appendChild($script);
														$scope.paypalScriptCarregado = true;
													}
													$rootScope.loadModulos();

													window.wootricSettings = {
														email : $scope.usuario.gmail,
														created_at : 1234567890,
														account_token : 'NPS-6edd38e3'
													};
													window.wootric('run');
												}, function(response) {
													$scope.showError(response);
												}));
					};

					$rootScope.loadModulos = function(cont) {
						$scope.modulo = {
							biz : {
								estatistica : {
									transacao : undefined,
									pagamento : undefined
								}
							}
						};

					}

					$scope.selecionarPapel = function(xPapel) {
						var fd = formdata({
							refEmpresa : xPapel.refEmpresa ? xPapel.refEmpresa.originalObject.key
									: undefined,
							refPessoa : xPapel.refPessoa ? xPapel.refPessoa.originalObject.key
									: undefined,
							refPapel : xPapel.refPapel ? xPapel.refPapel.originalObject.key
									: undefined,
						});

						$http(
								{
									url : 'app/usuario/papel',
									method : "POST",
									data : fd,
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									}
								}).then(function(response) {
							$scope.usuario = response.data.usuario;
							$route.reload();
						}, function(response) {
						});

					}

					$scope.alreadyLoggedIn = function() {
						if (!$scope.hasOwnProperty("usuario"))
							return false;
						return !$scope.usuario.hasOwnProperty("loginUrl");
					};

					$scope.needLogin = function() {
						if (!$scope.hasOwnProperty("usuario"))
							return false;
						return $scope.usuario.hasOwnProperty("loginUrl");
					};

					$rootScope.showError = function(result, cont) {
						var msg;
						if (result && result.data && result.data.errormsg)
							msg = result.data.errormsg;
						else
							msg = result;
						ModalService
								.showModal(
										{
											templateUrl : "app/resource/core/dialog-message.html",
											controller : "messageCtrl",
											inputs : {
												title : "Erro",
												message : msg
											}
										}).then(function(modal) {
									modal.element.modal();
									modal.close.then(function(result) {
										if (cont)
											cont();
									});
								});

					}

					$scope.hyperspace = function(obj) {
						// console.log(obj.originalObject.key);
						if (!obj || !obj.originalObject
								|| !obj.originalObject.key)
							return;
						$scope.jump(obj.originalObject.key);
						$timeout(function() {
							jQuery('.search-input').val("");
							jQuery('.search').click();
						});
					};

					$scope.jump = function(key) {
						if (!key)
							return;
						$scope.addPromise($http({
							url : 'app/locator?key=' + key,
							method : "GET"
						}).then(
								function(response) {
									var l = response.data.locator;
									$location
											.path('/'
													+ l.locator
													+ (l.skipShow ? '/edit/'
															: '/show/') + key);
								}, function(response) {
									$scope.showError(response);
								}));
					};

					$scope.locate = function(key, cont) {
						$scope.addPromise($http({
							url : 'app/locator?key=' + key,
							method : "GET"
						}).then(function(response) {
							cont(response.data.locator);
						}, function(response) {
							$scope.showError(response);
						}));

					}

					$scope.selectedObject = function(p1, p2) {
						p2.context[p2.variable] = p1;
						if (p2.full)
							$scope
									.loadRef(p2.variable,
											p2.context[p2.variable]);
					}

					$scope.loading = {};

					$scope.loadRef = function(name, variable, cont) {
						// console.log('detectou ' + name)
						if (!variable || !variable.originalObject
								|| !variable.originalObject.key) {
							// console.log('não vou atualizar ' + name)
							return;
						}
						var loadingVars = $scope.loading[variable.originalObject.key];
						// Verifica se já está carregando o conteúdo dessa
						// variável
						if (loadingVars)
							for (var i = 0; i < loadingVars.length; i++)
								if (loadingVars[i] === variable) {
									// console.log('já estou atualizando ' +
									// name)
									return;
								}
						if ($scope.loading[variable.originalObject.key] === undefined) {
							$scope.loading[variable.originalObject.key] = [];
							loadingVars = $scope.loading[variable.originalObject.key]
							// console.log('atualizando ' + name)
							var key = variable.originalObject.key;
							$scope
									.addPromise($http({
										url : 'app/locator?key=' + key,
										method : "GET"
									})
											.then(
													function(response) {
														var l = response.data.locator;
														$scope
																.addPromise($http(
																		{
																			url : 'app/'
																					+ l.locator
																					+ '/dados/'
																					+ key,
																			method : "GET"
																		})
																		.then(
																				function(
																						response) {
																					for (var i = 0; i < loadingVars.length; i++)
																						loadingVars[i].originalObject = response.data;
																					// console.log('atualizei
																					// ' +
																					// loadingVars.length
																					// + '
																					// ' +
																					// name);
																					delete $scope.loading[variable.originalObject.key];
																					if (cont)
																						cont();
																				},
																				function(
																						response) {
																					delete $scope.loading[variable.originalObject.key];
																					$scope
																							.showError(response);
																				}));
													},
													function(response) {
														delete $scope.loading[variable.originalObject.key];
														$scope
																.showError(response);
													}));
						}
						$scope.loading[variable.originalObject.key]
								.push(variable);
					}

					// Initialize
					$scope.loadUsuario();

					$timeout(function() {
						var buscarRef = $("#dropdownSearch");
						var buscarPopup = $("#dropdownSearchList");
						var popper = new Popper(buscarRef, buscarPopup, {
							placement : 'bottom-start',
							removeOnDestroy : false,
							modifiers : {
								preventOverflow : {
									order : 100000,
									enabled : true,
									boundariesElement : 'window'
								}
							},
							onCreate : function(data) {
							}
						})
						popper.enableEventListeners();
						popper.scheduleUpdate();
					}, 0);

					$scope.$on('$viewContentLoaded', function(event) {
						if ($window && $window.ga)
							$window.ga('send', 'pageview', {
								page : $location.path()
							});
					});

				});

app
		.controller(
				'crudEditCtrl',
				function($scope, $http, $window, $location, $routeParams,
						$timeout, finder, ModalService, Upload) {
					$scope.api = "app/" + $scope.pathFirstMember();
					$scope.base = "/" + $scope.pathFirstMember();

					$scope.begin = function(key) {
						$timeout(function() {
							$scope.$broadcast('before-loading');
						});
						$scope
								.addPromise($http(
										{
											url : $scope.api
													+ '/novo'
													+ (key ? '?keyOriginal='
															+ key : ''),
											method : "GET"
										}).then(function(response) {
									$scope.data = response.data;
									$scope.$broadcast('finished-loading');
									$timeout(function() {
										$scope.$broadcast('after-loading');
									});
								}, function(response) {
									$scope.showError(response);
								}));
					}

					$scope.edit = function(key) {
						$timeout(function() {
							$scope.$broadcast('before-loading');
						});
						$scope.addPromise($http({
							url : $scope.api + '/dados/' + key,
							method : "GET",
							data : $scope.data
						}).then(function(response) {
							$scope.data = response.data;
							$scope.$broadcast('finished-loading');
							$timeout(function() {
								$scope.$broadcast('after-loading');
							});
						}, function(response) {
							$scope.showError(response);
						}));
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
				});

app
		.controller(
				'crudShowCtrl',
				function($scope, $http, $window, $location, $routeParams,
						$timeout, ModalService) {
					$scope.api = "app/" + $scope.pathFirstMember();
					$scope.base = "/" + $scope.pathFirstMember();
					$scope.show = function() {
						$scope.addPromise($http(
								{
									url : $scope.api + '/dadoseacoes/'
											+ $routeParams.key,
									method : "GET"
								}).then(function(response) {
							var data = response.data;
							$scope.data = data.data;
							$scope.action = data.action;
							$scope.change = data.change;
							$scope.cda = data.cda;
							$scope.tag = response.data.tag;
							$scope.mentionedBy = response.data.mentionedBy;

							$scope.posLoad();
						}, function(response) {
							$scope.showError(response);
						}));

						// $scope.addPromise($http({
						// url : $scope.api + '/mencionado_por/' +
						// $routeParams.key,
						// method : "GET"
						// }).then(function(response) {
						// $scope.mentionedBy = response.data.list;
						// }, function(response) {
						// $scope.showError(response);
						// }));
					}

					$scope.actQuery = function(name) {
						$scope
								.addPromise($http(
										{
											url : $scope.api + '/acao/' + name
													+ "/" + $routeParams.key,
											method : "GET"
										})
										.then(
												function(response) {
													if (response.data.confirmation) {
														ModalService
																.showModal(
																		{
																			templateUrl : "app/resource/core/dialog-confirmation.html",
																			controller : "confirmationCtrl",
																			inputs : {
																				title : "Confirmação",
																				confirmation : response.data.confirmation
																			}
																		})
																.then(
																		function(
																				modal) {
																			modal.element
																					.modal();
																			modal.close
																					.then(function(
																							result) {
																						if (result.proceed)
																							$scope
																									.actRun(
																											name,
																											result.act);
																					});
																		});
													} else {
														if (response.data.html) {
															ModalService
																	.showModal(
																			{
																				template : '<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="actionTitle" aria-hidden="true"><div class="modal-dialog modal-dialog-bs4" role="document"><div class="modal-content"><div class="modal-header"><h5 class="modal-title" id="actionTitle">{{title}}</h5><button type="button" class="close" ng-click="cancel()" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button></div><div class="modal-body">'
																						+ response.data.html
																						+ '</div><div class="modal-footer"><button type="button" ng-click="cancel()" class="btn">Cancelar</button><button type="button" ng-click="close()" id="prosseguir" class="btn btn-primary" data-dismiss="modal">Prosseguir</button></div></div></div></div>',
																				controller : "actionCtrl",
																				inputs : {
																					title : name,
																					act : response.data.act,
																					parentScope : $scope
																				}
																			})
																	.then(
																			function(
																					modal) {
																				modal.element
																						.modal();
																				$timeout(
																						function() {
																							angular
																									.element(
																											'.dialog-form:first *:input[type!=hidden]:first')
																									.focus();
																						},
																						500);
																				modal.close
																						.then(function(
																								result) {
																							if (result.proceed)
																								$scope
																										.actRun(
																												name,
																												result.act);
																						});
																			});
														} else {
															$scope.actRun(name,
																	{});
														}
													}
												}, function(response) {
													$scope.showError(response);
												}));
					}

					$scope.actRun = function(name, act) {
						var fd = formdata(encodeKeys({
							act : act
						}));

						$scope
								.addPromise($http(
										{
											url : $scope.api + '/acao/' + name
													+ "/" + $routeParams.key,
											method : "POST",
											data : fd,
											headers : {
												'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
											}

										}).then(function(response) {
									if (response.data.next) {
										// $scope.$eval(response.data.next, {
										// $scope : $scope,
										// window : window
										// });
										eval(response.data.next);
									}
									$scope.show();
								}, function(response) {
									$scope.showError(response);
								}));
					}

					$scope.actMiniQuery = function(id, name, active) {
						if (!active)
							return;
						$scope
								.addPromise($http(
										{
											url : $scope.api + '/miniacao/'
													+ id + '/' + name + '/'
													+ $routeParams.key,
											method : "GET"
										})
										.then(
												function(response) {
													if (response.data.confirmation) {
														ModalService
																.showModal(
																		{
																			templateUrl : "app/resource/core/dialog-confirmation.html",
																			controller : "confirmationCtrl",
																			inputs : {
																				title : "Confirmação",
																				confirmation : response.data.confirmation
																			}
																		})
																.then(
																		function(
																				modal) {
																			modal.element
																					.modal();
																			modal.close
																					.then(function(
																							result) {
																						if (result.proceed)
																							$scope
																									.actMiniRun(
																											id,
																											name);
																					});
																		});
													} else {
														$scope.actMiniRun(id,
																name);
													}
												}, function(response) {
													$scope.showError(response);
												}));
					}

					$scope.actMiniTitle = function(name, active, explain) {
						if (explain)
							return name + " (" + explain + ")";
						return name;
					}

					$scope.actMiniRun = function(id, name) {

						$scope.addPromise($http(
								{
									url : $scope.api + '/miniacao/' + id + '/'
											+ name + '/' + $routeParams.key,
									method : "POST"
								}).then(function(response) {
							$scope.show();
						}, function(response) {
							$scope.showError(response);
						}));
					}

					$scope.edit = function() {
						$location.path($scope.base + '/edit/'
								+ $routeParams.key);
					}

					$scope.duplicate = function() {
						$location.path($scope.base + '/new-duplicate/'
								+ $routeParams.key);
					}

					$scope.cancel = function() {
						$window.history.back();
					}

					$scope.preLoad = function() {
					};

					$scope.posLoad = function() {
					};

					$scope.showAll = function() {
						$scope.showAllChanges = !$scope.showAllChanges;
						$(".fa-user-secret").parent().toggleClass(
								"xrp-action-active", $scope.showAllChanges);
					};

					$scope.filterActive = function(chg, index, array) {
						return !(!$scope.showAllChanges && (chg.tipo === 'Cancelamento'
								|| chg.idCanceladora !== undefined || chg.idDesabilitadora !== undefined));
					}

					if ($routeParams.key != undefined) {
						$timeout(function() {
							$scope.preLoad();
							$scope.show();
						})
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

					$scope.print = function() {
						$window.open('app' + $scope.base + '/stamped/'
								+ $scope.data.codigo + '.pdf?key='
								+ $routeParams.key, '_blank');
					}

					$scope.printLabel = function() {
						printJS('app' + $scope.base + '/label/'
								+ $scope.data.codigo + '.pdf?key='
								+ $routeParams.key);
					}

					$scope.wflProsseguir = function(desvio) {
						$scope
								.addPromise($http(
										{
											url : 'app/wfl-procedimento/'
													+ $scope.workflow.refProcedimento.originalObject.key
													+ '/evento/'
													+ $scope.workflow.evento
													+ '/prosseguir/'
													+ (desvio | '')
													+ "?parametro="
													+ encodeURI(JSON
															.stringify($scope.workflow.variavel)),
											method : "POST"
										}).then(function(response) {
									$scope.show();
								}, function(response) {
									$scope.showError(response);
								}));
					}
				});

app.controller('actionCtrl',
		function($scope, $http, $location, $routeParams, $element, $timeout,
				title, act, parentScope, close) {
			$scope.act = act;
			$scope.title = title;
			$scope.parentScope = parentScope;

			$scope.webcamChannel = {
				videoWidth : 960,
				videoHeight : 320,
				video : null
			};

			$scope.webcamChannel2 = {
				videoWidth : 320,
				videoHeight : 240,
				video : null
			};

			$scope.webcamMakeSnapshot = function(url) {
				var _video = $scope.webcamChannel.video;
				if (_video) {
					var patCanvas = document.querySelector('#snapshot');
					if (!patCanvas)
						return;

					patCanvas.width = _video.height;
					patCanvas.height = _video.height;
					var ctxPat = patCanvas.getContext('2d');
					var patOpts = {
						x : (_video.width - _video.height) / 2,
						y : 0,
						w : _video.height,
						h : _video.height
					};

					var getVideoData = function getVideoData(x, y, w, h) {
						var hiddenCanvas = document.createElement('canvas');
						hiddenCanvas.width = _video.width;
						hiddenCanvas.height = _video.height;
						var ctx = hiddenCanvas.getContext('2d');
						ctx
								.drawImage(_video, 0, 0, _video.width,
										_video.height);
						return ctx.getImageData(x, y, w, h);
					};

					var idata = getVideoData(patOpts.x, patOpts.y, patOpts.w,
							patOpts.h);
					ctxPat.putImageData(idata, 0, 0);

					$scope.act.webcamSnapshot = patCanvas.toDataURL(
							'image/jpeg', 9);
				}
			}

			$scope.webcamResetSnapshot = function() {
				$scope.act.webcamSnapshot = undefined;
			}

			$scope.selectedObject = function(p1, p2) {
				p2.context[p2.variable] = p1;
				if (p2.full)
					$scope.loadRef(p2.variable, p2.context[p2.variable]);
			}

			// This close function doesn't need to use jQuery or bootstrap,
			// because
			// the button has the 'data-dismiss' attribute.
			$scope.close = function() {
				// Manually hide the modal.
				$timeout(function() {
					$element.modal('hide');
				}, 200);

				close({
					act : $scope.act,
					proceed : true
				}, 500); // close, but give 500ms for bootstrap to animate
			};

			// This cancel function must use the bootstrap, 'modal' function
			// because
			// the doesn't have the 'data-dismiss' attribute.
			$scope.cancel = function() {
				// Manually hide the modal.
				$timeout(function() {
					$element.modal('hide');
				}, 200);

				// Now call close, returning control to the caller.
				close({
					proceed : false
				}, 500); // close, but give 500ms for bootstrap to animate
			};

		});

app.controller('confirmationCtrl', function($scope, $http, $location,
		$routeParams, $element, $timeout, title, confirmation, close) {
	$scope.title = title || "Confirmação";
	$scope.confirmation = confirmation || "Deseja prosseguir?";

	$scope.close = function() {
		// Manually hide the modal.
		$timeout(function() {
			$element.modal('hide');
		}, 200);

		close({
			proceed : true
		}, 500);
	};
	$scope.cancel = function() {
		// Manually hide the modal.
		$timeout(function() {
			$element.modal('hide');
		}, 200);

		// Now call close, returning control to the caller.
		close({
			proceed : false
		}, 500); // close, but give 500ms for bootstrap to animate
	};
});

app.controller('messageCtrl', function($scope, $http, $location, $routeParams,
		$element, $timeout, title, message, close) {
	$scope.title = title || "Atenção";
	$scope.message = message;

	$scope.close = function() {
		// Manually hide the modal.
		$timeout(function() {
			$element.modal('hide');
		}, 200);

		close({
			proceed : true
		}, 500);
	};
	$scope.cancel = function() {
		// Manually hide the modal.
		$timeout(function() {
			$element.modal('hide');
		}, 200);

		// Now call close, returning control to the caller.
		close({
			proceed : false
		}, 500); // close, but give 500ms for bootstrap to animate
	};
});

app
		.controller(
				'crudListCtrl',
				function($scope, $http, $location, $routeParams) {
					$scope.api = "app/" + $scope.pathFirstMember();
					$scope.base = "/" + $scope.pathFirstMember();
					$scope
							.addPromise($http(
									{
										url : $scope.api + '/todos',
										method : "GET",
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
										}
									}).then(function(response) {
								$scope.list = response.data.list;
							}, function(response) {
								$scope.showError(response);
							}));
					$scope.filter = "";

					$scope.create = function() {
						$location.path($scope.base + '/new');
					}

					$scope.edit = function(key) {
						$location.path($scope.base + '/edit/' + key);
					}

					$scope.show = function(key) {
						$location.path($scope.base + '/show/' + key);
					}
				});

app
		.controller(
				'documentoEditCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$routeParams',
						'ModalService',
						function($scope, $controller, Upload, $timeout, $http,
								$routeParams, ModalService) {
							// Initialize the super class and extend it.
							angular.extend(this, $controller('crudEditCtrl', {
								$scope : $scope
							}));
							var parent = angular.extend({}, $scope);

							$scope
									.$on(
											'finished-loading',
											function(event, args) {
												if ($routeParams.keyParent != undefined)
													$scope.data.refPai = {
														originalObject : {
															key : $routeParams.keyParent
														}
													};
												if ($routeParams.keyChild != undefined)
													$scope.data.refFilho = {
														originalObject : {
															key : $routeParams.keyChild
														}
													};

												if ($scope.data.markdown) {
													$scope._markdown = $scope.data.markdown;
												}
											});

							// Sempre que há alteração no destinatário,
							// recarregar o objeto completo
							$scope
									.$watch(
											'data',
											function() {
												if (!$scope.data)
													return;
												if (!$scope.data.refTipologia)
													delete $scope._markdown;
												// Ler a tipologia depois da
												// empresa, para que o
												// recompile funcione
												if ($scope.data.refEmpresa
														&& $scope.data.refEmpresa.originalObject
														&& $scope.data.refEmpresa.originalObject.id
														&& $scope.data.refTipologia
														&& $scope.data.refTipologia.originalObject
														&& !$scope.data.refTipologia.originalObject.id)
													$scope
															.loadRef(
																	'refTipologia',
																	$scope.data.refTipologia,
																	function() {
																		// console.log('chamei
																		// a
																		// cont');
																		if ($scope._markdown == undefined
																				|| $scope.data.id === undefined) {
																			// console.log('atualizei
																			// o
																			// modelo');
																			$scope._markdown = $scope.data.refTipologia.originalObject.modelo;
																		}
																		$scope
																				.$broadcast('recompile');
																	});

												if ($scope.data.refPai
														&& $scope.data.refPai.originalObject
														&& !$scope.data.refPai.originalObject.id)
													$scope.loadRef('refPai',
															$scope.data.refPai);

												if ($scope.data.refEmpresa
														&& $scope.data.refEmpresa.originalObject
														&& !$scope.data.refEmpresa.originalObject.id)
													$scope
															.loadRef(
																	'refEmpresa',
																	$scope.data.refEmpresa);
												if ($scope.data.refPapelSubscritor
														&& $scope.data.refPapelSubscritor.originalObject
														&& !$scope.data.refPapelSubscritor.originalObject.id)
													$scope
															.loadRef(
																	'refPapelSubscritor',
																	$scope.data.refPapelSubscritor);
												if ($scope.data.refPessoaSubscritor
														&& $scope.data.refPessoaSubscritor.originalObject
														&& !$scope.data.refPessoaSubscritor.originalObject.id)
													$scope
															.loadRef(
																	'refPessoaSubscritor',
																	$scope.data.refPessoaSubscritor);

												if ($scope.data.refUnidadeDestinatario
														&& $scope.data.refUnidadeDestinatario.originalObject
														&& !$scope.data.refUnidadeDestinatario.originalObject.id)
													$scope
															.loadRef(
																	'refUnidadeDestinatario',
																	$scope.data.refUnidadeDestinatario);
												if ($scope.data.refPapelDestinatario
														&& $scope.data.refPapelDestinatario.originalObject
														&& !$scope.data.refPapelDestinatario.originalObject.id)
													$scope
															.loadRef(
																	'refPapelDestinatario',
																	$scope.data.refPapelDestinatario);
												if ($scope.data.refPessoaDestinatario
														&& $scope.data.refPessoaDestinatario.originalObject
														&& !$scope.data.refPessoaDestinatario.originalObject.id)
													$scope
															.loadRef(
																	'refPessoaDestinatario',
																	$scope.data.refPessoaDestinatario);

												if ($scope.data.xCossignatario) {
													for (var i = 0; i < $scope.data.xCossignatario.length; i++) {
														var c = $scope.data.xCossignatario[i];
														if (c.tipo == 'PAPEL'
																&& c.refPapel
																&& c.refPapel.originalObject
																&& !c.refPapel.originalObject.id)
															$scope
																	.loadRef(
																			'refPapelCossignatario',
																			c.refPapel);
														if (c.tipo == 'PESSOA'
																&& c.refPessoa
																&& c.refPessoa.originalObject
																&& !c.refPessoa.originalObject.id)
															$scope
																	.loadRef(
																			'refPessoaCossignatario',
																			c.refPessoa);
													}
												}
											}, true);

							$scope.parentSave = $scope.save;

							$scope.save = function() {
								if (!$scope.data.refArquivoPdf
										&& (!$scope.data.refTipologia || $scope.data.refTipologia.originalObject.tipo == 'PDF')) {
									ModalService
											.showModal(
													{
														templateUrl : "app/resource/core/dialog-message.html",
														controller : "messageCtrl",
														inputs : {
															title : "Atenção",
															message : "Antes de salvar o documento é necessário especificar o PDF e aguardar que ele seja transferido para o XRP. Por favor, tente novamente."
														}
													}).then(function(modal) {
												modal.element.modal();
											});
									return;
								}
								$scope.$broadcast('will-save');
								parent.save();
							}
						} ]);

app
		.controller(
				'documentoShowCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$location',
						'$window',
						'$routeParams',
						function($scope, $controller, Upload, $timeout, $http,
								$location, $window, $routeParams) {
							// Initialize the super class and extend it.
							angular.extend(this, $controller('crudShowCtrl', {
								$scope : $scope
							}));

							$scope.preLoad = function() {
								if (document.getElementById('pdf-viewer'))
									document.getElementById('pdf-viewer').innerHTML = '<object data="app/ecm-documento/download?key='
											+ $routeParams.key
											+ '" type="application/pdf" class="pdf-viewer" style="height: 450px;"><p><b>Não foi possível apresentar o PDF</b>: Este browser não suporta exibição de PDFs. Por favor, faça o download do arquivo em: <a href="/pdf/sample-3pp.pdf">Download PDF</a>.</p></object>';
								else
									$timeout($scope.preLoad, 200);
							};

							$scope.posLoad = function() {
								if ($scope.data.dotTramitacao) {
									$scope
											.addPromise($http(
													{
														url : 'app/graphviz?graph='
																+ "digraph \"\" {"
																+ $scope.data.dotTramitacao
																+ "}",
														method : "GET"
													})
													.then(
															function(response) {
																document
																		.getElementById('graph-tramite').innerHTML = response.data;
															},
															function(response) {
																$scope
																		.showError(response);
															}));
								}
								$scope
										.addPromise($http(
												{
													url : 'app/wfl-procedimento/principal/'
															+ $routeParams.key
															+ '/aguardando',
													method : "GET"
												})
												.then(
														function(response) {
															$scope.workflow = response.data;
															if ($scope.workflow
																	&& $scope.workflow.dot) {
																$scope
																		.addPromise($http(
																				{
																					url : 'app/graphviz?graph='
																							+ "digraph \"\" {"
																							+ $scope.workflow.dot
																							+ "}",
																					method : "GET"
																				})
																				.then(
																						function(
																								response) {
																							document
																									.getElementById('graph-procedimento').innerHTML = response.data;
																						},
																						function(
																								response) {
																							$scope
																									.showError(response);
																						}));
															}
														},
														function(response) {
															$scope
																	.showError(response);
														}));
							};

							$scope.view = function() {
								$location.path($scope.base + '/view/'
										+ $routeParams.key);
							}

							$scope.include = function() {
								$location.path($scope.base + '/new/'
										+ $routeParams.key);
							}

							$scope.beIncluded = function() {
								$location.path($scope.base + '/new-parent/'
										+ $routeParams.key);
							}

							$scope.print = function() {
								$window.open('app' + $scope.base + '/stamped/'
										+ $scope.data.codigo + '.pdf?key='
										+ $routeParams.key, '_blank');
							}

						} ]);

app.controller('documentoViewCtrl', function($scope, $controller, Upload,
		$timeout, $http, $location, $routeParams) {
	$scope.api = "app/" + $scope.pathFirstMember();
	$scope.base = "/" + $scope.pathFirstMember();

	$scope.keySelectedDoc = undefined;

	$scope.setCurrent = function(key) {
		$scope.keySelectedDoc = key;
		angular.element('#pdf-viewer').attr('data',
				'app/ecm-documento/download?key=' + (key || $routeParams.key));
	}

	$scope.load = function() {
		$scope.addPromise($http({
			url : $scope.api + '/dadoseacoes/' + $routeParams.key,
			method : "GET"
		}).then(function(response) {
			var data = response.data;
			$scope.data = data.data;
			$scope.action = data.action;
			$scope.change = data.change;
			$scope.posLoad();
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.posLoad = function() {
		var indice = $scope.data.indice;
		var key = indice[indice.length - 1].keyDoc;
		for (var i = 0; i < indice.length; i++) {
			if (indice[i].nivel == 1)
				key = indice[i].keyDoc;
		}
		$scope.setCurrent(key);
	};

	if ($routeParams.key != undefined)
		$scope.load();
});

app
		.controller(
				'tipologiaEditCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$routeParams',
						'ModalService',
						function($scope, $controller, Upload, $timeout, $http,
								$routeParams, ModalService) {
							// Initialize the super class and extend it.
							angular.extend(this, $controller('crudEditCtrl', {
								$scope : $scope
							}));
							var parent = angular.extend({}, $scope);

							$scope
									.$watch(
											'data',
											function() {
												if (!$scope.data
														|| !$scope.data.workflow
														|| !$scope.data.workflow.tarefa) {
													if ($scope.data
															&& $scope.data.workflow)
														$scope.data.workflow.dot = undefined;
													document
															.getElementById('graph-workflow').innerHTML = "";
													$scope.tarefas = undefined;
													return;
												}
												var tarefas = [];

												for (var i = 0; i < $scope.data.workflow.tarefa.length; i++) {
													var t = $scope.data.workflow.tarefa[i];
													if (!t.id)
														t.id = uuidv4();
													t.nome = i
															+ 1
															+ ") "
															+ (t.titulo ? t.titulo
																	: t.tipo);
													tarefas.push({
														id : t.id,
														nome : t.nome
													})
												}
												tarefas.push({
													id : "fim",
													nome : "[Fim]"
												})

												$scope.tarefas = tarefas;

												$scope.data.workflow.dot = $scope
														.getDot($scope.data.workflow);
												$scope
														.addPromise($http(
																{
																	url : 'app/graphviz?graph='
																			+ $scope.data.workflow.dot,
																	method : "GET"
																})
																.then(
																		function(
																				response) {
																			document
																					.getElementById('graph-workflow').innerHTML = response.data;
																		},
																		function(
																				response) {
																			$scope
																					.showError(response);
																		}));

											}, true);

							function graphElement(shape, n, nextn) {
								var s = '"' + n.id + '"[shape="' + shape
										+ '"][label=<<font>' + n.titulo
										+ '</font>>];';
								if (n.tipo !== "FIM") {
									if (n.desvio && n.desvio.length > 0) {
										for ( var x in n.desvio) {
											var tr = n.desvio[x];
											if (tr.tarefa)
												s += '"' + n.id + '"->"'
														+ tr.tarefa + '"';
											else if (nextn)
												s += '"' + n.id + '"->"'
														+ nextn.id + '"';
											else
												s += '"' + n.id + '"->"fim"';
											if (tr.titulo && tr.titulo !== '')
												s += ' [label="' + tr.titulo
														+ '"]';
											s += ';';
										}
									} else if (n.depois) {
										s += '"' + n.id + '"->"' + n.depois
												+ '";';
									} else if (nextn) {
										s += '"' + n.id + '"->"' + nextn.id
												+ '";';
									} else {
										s += '"' + n.id + '"->"fim";';
									}
								}
								return s;
							}

							$scope.getDot = function(wf) {
								var tipos = {
									INCLUIR_DOCUMENTO : 'note',
									FORMULARIO : 'rectangle',
									DECISAO : 'diamond',
									EMAIL : 'folder',
								};
								var s = 'digraph G { graph[size="3,3"];';
								if (wf.tarefa.length > 0) {
									s += graphElement("oval", {
										id : "ini",
										titulo : "Início",
										desvio : [ {
											tarefa : wf.tarefa[0].id
										} ]
									});
									s += graphElement("oval", {
										id : "fim",
										titulo : "Fim",
										tipo : "FIM"
									});
									for (var i = 0; i < wf.tarefa.length; i++) {
										var n = wf.tarefa[i];
										s += graphElement(
												tipos[n.tipo],
												n,
												i < wf.tarefa.length - 1 ? wf.tarefa[i + 1]
														: undefined);
									}
								}
								s += '}';
								return s;
							}

						} ]);

app
		.controller(
				'transacaoEditCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$routeParams',
						'ModalService',
						function($scope, $controller, Upload, $timeout, $http,
								$routeParams, ModalService) {
							// Initialize the super class and extend it.
							angular.extend(this, $controller('crudEditCtrl', {
								$scope : $scope
							}));
							var parent = angular.extend({}, $scope);

							$scope.parentSelectedObject = $scope.selectedObject;

							$scope.$on('before-loading', function(event, args) {
								$scope.disableSelectedObject = true;
							});

							$scope.$on('after-loading', function(event, args) {
								$scope.disableSelectedObject = false;
							});

							$scope
									.$watch(
											'data.item',
											function() {
												if (!$scope.data)
													return;
												for (var i = 0; i < $scope.data.item.length; i++) {
													if ($scope.data.item[i].quantidade === undefined)
														$scope.data.item[i].quantidade = 1;
													$scope
															.atualizarTotalDoItem($scope.data.item[i]);
												}
												$scope.atualizarTotal();
											}, true);

							$scope.selectedObject = function(p1, p2) {
								if (!$scope.disableSelectedObject
										&& p2.variable == 'refProdutoVariacao') {
									p2.context[p2.variable] = p1;
									p2.context.preco = undefined;
									p2.context.total = undefined;
									$scope
											.loadRef(
													p2.variable,
													p2.context[p2.variable],
													function() {
														p2.context.preco = p2.context.refProdutoVariacao.originalObject.precoAtualizado
																|| p2.context.refProdutoVariacao.originalObject.preco
																|| p2.context.refProdutoVariacao.originalObject.refProduto.originalObject.preco;
														$scope
																.atualizarTotalDoItem(p2.context);
													});
								} else {
									$scope.parentSelectedObject(p1, p2);
								}
							}

							$scope.atualizarTotalDoItem = function(item) {
								item.total = item.quantidade * item.preco;
								if (!item.total)
									delete item.total;
								$scope.atualizarTotal();
							}

							$scope.calcTotal = function() {
								var total = 0;
								for (var i = 0; i < $scope.data.item.length; i++) {
									var item = $scope.data.item[i];
									total += item.total;
								}

								if ($scope.data.desconto)
									total -= $scope.data.desconto;

								if ($scope.data.despesas)
									total += $scope.data.despesas;

								if ($scope.data.frete)
									total += $scope.data.frete;

								return total;
							}

							$scope.atualizarTotal = function() {
								var total = $scope.calcTotal();
								if (total <= 0 || !total)
									delete $scope.data.total;
								else
									$scope.data.total = total;
								$scope.atualizarTroco();
							}

							$scope.atualizarDesconto = function() {
								var total = $scope.calcTotal();
								if ($scope.data.desconto)
									total += $scope.data.desconto;
								$scope.data.desconto = total
										- $scope.data.total;
								if (!$scope.data.desconto)
									delete $scope.data.desconto;
								$scope.atualizarTroco();
							}

							$scope.atualizarTroco = function() {
								if ($scope.data.trocar && $scope.data.total)
									$scope.data.troco = $scope.data.trocar
											- $scope.data.total;
								else {
									delete $scope.data.trocar;
									delete $scope.data.troco;
								}
							}
						} ]);

app
		.controller(
				'transacaoShowCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$location',
						'$window',
						'$routeParams',
						function($scope, $controller, Upload, $timeout, $http,
								$location, $window, $routeParams) {
							// Initialize the super class and extend it.
							angular.extend(this, $controller('crudShowCtrl', {
								$scope : $scope
							}));

							$scope.posLoad = function() {
								var total = (Math
										.round($scope.data.total * 100) / 100)
										.toFixed(2);
								if ($scope.usuario.empresa.bizConfig.paypalAtivo) {
									paypal.Button
											.render(
													{
														env : $scope.usuario.empresa.bizConfig.paypalProducao ? 'production'
																: 'sandbox',

														style : {
															label : 'paypal',
															size : 'small', // small
															// |
															// medium
															// |
															// large
															// |
															// responsive
															shape : 'rect', // pill
															// |
															// rect
															color : 'silver', // gold
															// |
															// blue
															// |
															// silver
															// |
															// black
															tagline : false
														},

														// PayPal Client IDs -
														// replace with your own
														// Create a PayPal app:
														// https://developer.paypal.com/developer/applications/create
														client : {
															sandbox : $scope.usuario.empresa.bizConfig.paypalCredencialSandbox,
															production : $scope.usuario.empresa.bizConfig.paypalCredencialLive
														},

														// Show the buyer a 'Pay
														// Now' button in the
														// checkout
														// flow
														commit : true,

														// payment() is called
														// when the button is
														// clicked
														payment : function(
																data, actions) {

															// Make a call to
															// the REST api to
															// create the
															// payment
															return actions.payment
																	.create({
																		payment : {
																			transactions : [ {
																				amount : {
																					total : total,
																					currency : 'BRL'
																				}
																			} ]
																		}
																	});
														},

														// onAuthorize() is
														// called when the buyer
														// approves the
														// payment
														onAuthorize : function(
																data, actions) {

															// Make a call to
															// the REST api to
															// execute the
															// payment
															return actions.payment
																	.execute()
																	.then(
																			function() {
																				window
																						.alert('Pagamento recebido!');
																			});
														}

													},
													'#paypal-button-container');
								}
							};
						} ]);

app
		.controller(
				'bizEstatisticasCtrl',
				[
						'$scope',
						'$controller',
						'Upload',
						'$timeout',
						'$http',
						'$location',
						'$window',
						'$routeParams',
						function($scope, $controller, Upload, $timeout, $http,
								$location, $window, $routeParams) {
							// $scope.data = [{teste: 1, teste2: 2}];
							$scope.editing = false;

							$scope.pivotCreate = function() {
								var weekday = [ "Dom", "2a", "3a", "4a", "5a",
										"6a", "Sáb" ];
								var dateFormat = $
										.extend($.pivotUtilities.derivers.dateFormat);
								var renderers = $.pivotUtilities.locales.pt.renderers;
								var gchart_renderers = $.pivotUtilities.locales.pt.gchart_renderers;

								// var conf = localStorage.getItem('pivot');
								// if (conf == null) {
								var conf;
								if ($scope.tipo == 'transacao')
									conf = {
										aggregatorName : 'Soma',
										vals : [ 'valor' ],
										rendererName : 'Gráfico de Barras Empilhadas',
										// rows : [ 'produtoId' ],
										rows : [],
										cols : [ 'ano', 'mês' ],
										inclusions : {
											"tipo" : [ "VENDA" ]
										},
									};
								else if ($scope.tipo == 'pagamento')
									conf = {
										aggregatorName : 'Soma',
										vals : [ 'valor' ],
										rendererName : 'Gráfico de Barras Empilhadas',
										// rows : [ 'produtoId' ],
										rows : [],
										cols : [ 'ano', 'mês' ],
									};

								if ($scope.config)
									conf = $scope.config;

								$scope.pivotConf = undefined;
								// }
								$scope.options = {
									showUI : false,
									renderers : $.extend(renderers,
											gchart_renderers),
									aggregatorName : conf.aggregatorName,
									vals : conf.vals,
									rendererName : conf.rendererName,
									cols : conf.cols,
									rows : conf.rows,
									inclusions : conf.inclusions,
									exclusions : conf.exclusions,
									derivedAttributes : {
										"ano" : function(record) {
											return record.dataHora.substring(0,
													4);
										},
										"mês" : function(record) {
											return record.dataHora.substring(5,
													7);
										},
										"dia" : function(record) {
											return record.dataHora.substring(8,
													10);
										},
										"dia da semana" : function(record) {
											var d = new Date(record.dataHora);
											return weekday[d.getDay()];
										},
										"horário" : function(record) {
											var hora = record.dataHora
													.substring(11, 13);
											var minuto = record.dataHora
													.substring(14, 16);
											if (hora < 6)
												return "00:00-06:00";
											if (hora >= 6 && hora < 12)
												return "06:00-12:00";
											if (hora >= 12 && hora < 18)
												return "12:00-18:00";
											return "18:00-23:59";
										}
									},
									hiddenAttributes : [ "dataHora" ],
									aggregators2 : {
										"Faturamento" : function() {
											return tpl.sum()([ "preco" ])
										},
										"Ticket Médio" : function() {
											return tpl.average()([ "preco" ])
										},
										"Pedidos" : function() {
											return tpl.distict()
													([ "pedidoId" ])
										}
									},
									unusedAttrsVertical : false,
									onRefresh : function(config) {
										var config_copy = JSON.parse(JSON
												.stringify(config));
										// delete some values which are
										// functions
										delete config_copy["aggregators"];
										delete config_copy["renderers"];
										delete config_copy["derivedAttributes"];
										// delete some bulky default values
										delete config_copy["rendererOptions"];
										delete config_copy["localeStrings"];
										$scope.pivotConf = config_copy;
										// localStorage.setItem('pivot',
										// config_copy)
										$scope.$apply();
									}
								};

								$("#pivot").pivotUI($scope.data,
										$scope.options, true, 'pt');
								$("#pivot table:first-child").css("width",
										"100%");
							}

							$scope.edit = function() {
								$scope.editing = true;
								$scope.options.showUI = $scope.editing;
								$("#pivot").pivotUI($scope.data,
										$scope.options, true, 'pt');
							}

							$scope.save = function() {
								$scope.configData.json = JSON
										.stringify($scope.pivotConf);
								var fd = formdata({
									data : encodeKeys($scope.configData)
								});

								$scope
										.addPromise($http(
												{
													url : 'app/biz-config-estatistica-'
															+ $scope.tipo
															+ '/dados',
													method : "POST",
													data : fd,
													headers : {
														'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
													}
												})
												.then(
														function(response) {
															$scope
																	.loadModulos(function() {
																		if ($scope.tipo === 'transacao'
																				&& response.data.configEstatisticaDeTransacao.key !== $scope.key) {
																			$location
																					.path('/biz-estatistica/'
																							+ response.data.configEstatisticaDeTransacao.key);
																			return;
																		}
																		if ($scope.tipo === 'pagamento'
																				&& response.data.configEstatisticaDePagamento.key !== $scope.key) {
																			$location
																					.path('/biz-estatistica-pagamento/'
																							+ response.data.configEstatisticaDePagamento.key);
																			return;
																		}
																		$scope.editing = false;
																		$scope.options.showUI = $scope.editing;
																		$scope.options.aggregatorName = $scope.pivotConf.aggregatorName;
																		$scope.options.vals = $scope.pivotConf.vals;
																		$scope.options.rendererName = $scope.pivotConf.rendererName;
																		$scope.options.cols = $scope.pivotConf.cols;
																		$scope.options.rows = $scope.pivotConf.rows;
																		$scope.options.inclusions = $scope.pivotConf.inclusions;
																		$scope.options.exclusions = $scope.pivotConf.exclusions;

																		$(
																				"#pivot")
																				.pivotUI(
																						$scope.data,
																						$scope.options,
																						true,
																						'pt');
																	});
														},
														function(response) {
															$scope
																	.showError(response);
														}));
							}

							$scope.saveAs = function() {
								delete $scope.configData.key;
								delete $scope.configData.id;
								delete $scope.configData.begin;
								delete $scope.configData.modify;
								$scope.save();
							}

							$scope.remove = function() {
								$scope.addPromise($http(
										{
											url : 'app/biz-config-estatistica-'
													+ $scope.tipo + '/dados/'
													+ $scope.configData.key,
											method : "DELETE"
										}).then(function(response) {
									$location.path('/home');
								}, function(response) {
									$scope.showError(response);
								}));
							}

							$scope.fetchEstatisticas = function(data) {
								if (data && data.json)
									$scope.config = JSON.parse(data.json);

								$scope.response = null;

								$scope
										.addPromise($http(
												{
													url : "app/biz-transacao/estatisticas-"
															+ $scope.tipo,
													method : "GET"
												})
												.then(
														function(response) {
															$scope.data = response.data.list;
															$scope
																	.pivotCreate();
														},
														function(response) {
															delete $scope.data;
															alert("erro buscando estatisticas.");
														}));
							};

							$scope.fetchConfig = function() {
								$scope.tipo = $routeParams.tipo;

								if ($routeParams.key === undefined) {
									$scope.fetchEstatisticas();
									return;
								}

								$scope.key = $routeParams.key;

								$scope.response = null;

								$scope
										.addPromise($http(
												{
													url : "app/biz-config-estatistica-"
															+ $scope.tipo
															+ "/dados/"
															+ $scope.key,
													method : "GET"
												})
												.then(
														function(response) {
															$scope.configData = response.data;
															$scope
																	.fetchEstatisticas($scope.configData);
														},
														function(response) {
															delete $scope.data;
															alert("erro buscando estatisticas.");
														}));
							};

							$scope.loadScript = function(url, callback) {
								jQuery.ajax({
									url : url,
									dataType : 'script',
									success : callback,
									async : true
								});
							}

							if (true)
								$scope
										.loadScript(
												'https://www.google.com/jsapi',
												function() {
													google
															.load(
																	"visualization",
																	"1",
																	{
																		packages : [
																				"corechart",
																				"charteditor" ],
																		callback : function() {
																			$scope
																					.loadScript(
																							'https://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js',
																							function() {
																								$scope
																										.loadScript(
																												'app/resource/core/javascripts/pivottable/pivot.min.js',
																												function() {
																													$scope
																															.loadScript(
																																	'app/resource/core/javascripts/pivottable/gchart_renderers.js',
																																	function() {
																																		$scope
																																				.loadScript(
																																						'app/resource/core/javascripts/pivottable/pivot.pt.min.js',
																																						function() {
																																							$scope
																																									.fetchConfig();
																																						});
																																	});
																												});
																							});
																		}
																	});
												});
						} ]);

app
		.controller(
				'ctrlSugerir',
				function($scope, $http, $templateCache, $interval, $window) {

					// Sugestoes

					$scope.fdSugerir = function() {
						var obj = {
							nome : $scope.sugestao.nome,
							email : $scope.sugestao.email,
							mensagem : $scope.sugestao.mensagem
						};
						return formdata(obj);
					}

					$scope.sugerir = function() {
						$http(
								{
									url : 'app/sugerir',
									method : "POST",
									data : $scope.fdSugerir(),
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									}
								}).then(function(response) {
							alert("Sua mensagem foi enviada. Muito obrigado!");
							$scope.sugestao = {};
						}, function(response) {
							$scope.showError(response);
						});
					}

				});

app
		.controller(
				'ctrlSobre',
				function($scope, $http, $templateCache, $interval, $window,
						$location, ModalService) {
					$scope.contato = {};

					// Sugestoes

					$scope.fdContato = function() {
						var obj = {
							nome : $scope.contato.nome,
							email : $scope.contato.email,
							telefone : $scope.contato.telefone,
							mensagem : $scope.contato.mensagem
						};
						return formdata(obj);
					}

					$scope.enviarContato = function() {
						$http(
								{
									url : 'app/contato',
									method : "POST",
									data : $scope.fdContato(),
									headers : {
										'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8'
									}
								})
								.then(
										function(response) {
											$window.ga('send', 'event',
													"MARKETING",
													"new-prospect", undefined,
													undefined);
											$scope.contato = {};
											ModalService
													.showModal(
															{
																templateUrl : "app/resource/core/dialog-message.html",
																controller : "messageCtrl",
																inputs : {
																	title : "Muito Obrigado!",
																	message : "Recebemos suas informações de contato. Criaremos um ambiente de testes e entraremos em contato assim que estiver pronto."
																}
															})
													.then(
															function(modal) {
																modal.element
																		.modal();
																modal.close
																		.then(function(
																				result) {
																			$location
																					.path('/sobre');
																		});
															});

										}, function(response) {
											$scope.showError(response);
										});
					}

					$scope.video = function() {
						window.ga('send', 'event', 'MARKETING',
								'video-gestao-documental');
						window.location = 'https://www.youtube.com/watch?v=mZwZgru7Byo';
					}

				});

app.controller('ctrl', function($scope, $http, $templateCache, $interval,
		$window, $location, $timeout) {
	$scope.tipo = ($scope.usuario && $scope.usuario.refPapel) ? "UNIDADE"
			: "PESSOAL";
	$scope.list = [];

	$scope.loadInbox = function() {
		if (!$scope.alreadyLoggedIn()) {
			$timeout($scope.loadInbox, 200);
			return;
		}
		$scope.addPromise($http({
			url : 'app/inbox?tipo=' + $scope.tipo,
			method : "GET"
		}).then(function(response) {
			var l = response.data.list;
			$scope.list = l;
			$scope.updateInbox();
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.updateInbox = function() {
		var l = filtrarPorSubstring($scope.list, $scope.filter);
		var a = [];
		var g = undefined;
		var i;
		for (i = 0; i < l.length; i++) {
			if (i === 0 || l[i].grupo !== l[i - 1].grupo)
				a.push({
					nome : l[i].grupoNome,
					tags : []
				});
			a[a.length - 1].tags.push(l[i]);
		}
		$scope.inbox = a;
	}

	$scope.$watch('filter', function() {
		$scope.updateInbox();
	});

	$scope.$watch('tipo', function() {
		$scope.loadInbox();
	});
});

app.controller('ctrlSearch', function($scope, $http, $templateCache, $interval,
		$window, $location, $timeout) {
	$scope.facetNames = [];
	$scope.facets = [];
	$scope.resultsPerPage = 10;
	$scope.lastfacetlength = 0;
	$scope.lastPage = 1;

	$scope.pagination = {
		current : 1
	};

	$scope.addFacet = function(name, f) {
		var index = $scope.facets.indexOf(f);
		if (index == -1) {
			$scope.facetNames.push(name);
			$scope.facets.push(f);
			$scope.getResultsPage(1);
		}
	};

	$scope.removeFacet = function(name, f) {
		var index = $scope.facets.indexOf(f);
		if (index !== -1) {
			$scope.facets.splice(index, 1);
			$scope.facetNames.splice(index, 1);
		}
		$scope.getResultsPage(1);
	};

	$scope.pageChanged = function(newPage) {
		$scope.getResultsPage(newPage);
	};

	$scope.getResultsPage = function(pageNumber) {
		console.log($scope.lastfilter === $scope.filter
				&& $scope.facets.length === $scope.lastfacetlength
				&& $scope.lastPage === pageNumber)
		if ($scope.lastfilter === $scope.filter
				&& $scope.facets.length === $scope.lastfacetlength
				&& $scope.lastPage === pageNumber)
			return;
		$scope.lastfilter = $scope.filter;
		$scope.lastfacetlength = $scope.facets.length;
		$scope.lastPage = pageNumber;
		$scope.pagination.current = pageNumber;

		var f
		if ($scope.facets.length > 0) {
			for (var i = 0; i < $scope.facets.length; i++) {
				if (f)
					f += ",";
				else
					f = ""
				f += $scope.facets[i];
			}
		}

		$scope
				.addPromise($http(
						{
							url : 'app/search?filter='
									+ ($scope.filter === undefined ? ""
											: $scope.filter) + '&page='
									+ $scope.pagination.current + '&perpage='
									+ $scope.resultsPerPage
									+ (f ? '&facets=' + f : ''),
							method : "GET"
						}).then(function(response) {
					if ($scope.filter === response.data.searchResults.filter)
						$scope.results = response.data.searchResults.result;
				}, function(response) {
					$scope.showError(response);
				}));
	}

	$scope.$watch('filter', function() {
		$scope.getResultsPage(1);
	});

	$timeout(function() {
		// $scope.getResultsPage(1);
		$scope.filter = "";
		angular.element('input#filter').focus();
	});

});

app.controller('ctrlSettings', function($scope, $http, $templateCache,
		$interval, $window, $location) {
	$scope.loadUsuario = function() {
		$scope.addPromise($http({
			url : 'app/usuario',
			method : "GET"
		}).then(function(response) {
			$scope.usuario = response.data.usuario;
			$scope.editing = false;
			$scope.settings = false;
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.cancel = function() {
		$location.path('/home')
	}

	$scope.save = function() {
		$scope.addPromise($http({
			url : 'app/usuario/settings',
			method : "POST",
			data : {
				settings : $scope.usuario.settings
			}
		}).then(function(response) {
			$location.path('/home')
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.loadUsuario();

	$('#date').datepicker({
		dateFormat : 'dd.mm.yy',
		prevText : '<i class="fa fa-angle-left"></i>',
		nextText : '<i class="fa fa-angle-right"></i>'
	});
});

app.controller('ctrlInfo', function($scope, $http, $templateCache, $interval,
		$window, $location) {
	$scope.loadUsuario = function() {
		$scope.addPromise($http({
			url : 'app/usuario',
			method : "GET"
		}).then(function(response) {
			$scope.usuario = response.data.usuario;
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.cancel = function() {
		$location.path('/home')
	}

	$scope.save = function() {
		$scope.addPromise($http({
			url : 'app/usuario/data',
			method : "POST",
			data : {
				data : $scope.usuario.data
			}
		}).then(function(response) {
			$location.path('/home')
		}, function(response) {
			$scope.showError(response);
		}));
	}

	$scope.loadUsuario();
});

app.directive('resize', function($window) {
	return function(scope, element) {
		var w = angular.element($window);
		var changeHeight = function() {
			window.setTimeout(function() {
				var top = document.getElementById(element.attr('id'))
						.getBoundingClientRect().top;
				element.css('height', (w.height() - top - 50) + 'px');
			})
		};
		w.bind('resize', function() {
			changeHeight(); // when window size gets changed
		});
		changeHeight(); // when page loads
	}
});
