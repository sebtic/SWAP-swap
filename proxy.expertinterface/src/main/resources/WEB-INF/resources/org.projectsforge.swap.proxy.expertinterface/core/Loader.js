/**
 * Copyright 2013 Vincent Rouillé <vincent.rouille@etu.univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */

/**
 * @module core
 * @class Loader Simple AMD like JS loader
 */
(function() {
  /**
   * Loader object, shared with module namespace
   */
  var Loader = {};
  (function(scope, loaderEval) {

    /**
     * Url to load modules/ressources
     * The url ends by / and is automatically set
     *
     * @property url
     * @type String
     * @public
     */
    scope.url = (function() {
      var script = document.getElementsByTagName('script');
      if(script && script[0]) {
        script = script[0].src;
        // The autodetection could be more flexible
        return script.replace(/core\/Loader.js$/, '');
      }
      return null;
    })();JSON

    /**
     * List of managed modules
     */
    var modules = {};

    /**
     * Determine if the debug mode has been activated.
     * You can activate the debug mode by calling Loader.debugMode()
     */
    var _debugmode = false;

    var _verbose = 0;

    /**
     * Number of seconds to wait for a module before printing an error
	 * into the console about a failed loading if the module isn't
	 * loaded after that amount of time.
	 */
    var _timeout = 10;

    /**
     * Mark a module as loaded and apply notifications
     *
     * @method setModuleReady
     * @param {Module}
     *            module
     */
    function setModuleReady(module) {
      if(_verbose > 0)
        console.log("Module Ready " + module.name + " calling " + module.waiting.length + " listeners");
      module.state = 'ready';

      // Callback listener of the ready event of this module
      for (var i = 0, len = module.waiting.length; i < len; ++i) {
        module.waiting[i].call(scope, module);
      }
      module.waiting = null;
    }

    /**
     * Get the url of a module.
     */
    function getUrl(name) {
      if(scope.url === null) {
        throw "Loader.url is not set, and can't continue"
      }
      // TODO Allow mapping name to specific urls
      return scope.url + name;
    }

    /**
     * Get a module definition or create it, if the module hasn't been created yet.
     * @method getOrCreateModule
     * @param {String} name The name of the module
     * @return {Module}
     */
    function getOrCreateModule(name) {
      if (name in modules) {
        return modules[name];
      }

      /**
       * @class Module
       * @property {String} name Name of the module
       * @property {String} state Current state of the module, can be : creation |
       *           loading | ready
       * @property {[Function]} waiting List of listener waiting for this module
       *           to be ready
       * @property {Object} content An object that represent the module public
       *           Api.
       */
      return modules[name] = {
        'name' : name,
        'state' : 'creation',
        'waiting' : [],
        'content' : null
      };
    }

    /**
     * Load a module script using the DOM
     *
     * @param name
     */
    function loadScript(name) {
      var url = getUrl(name + ".js");
      if (url !== null) {
        if(_debugmode) {
          var node = document.createElement('script');

          node.type = 'text/javascript';
          node.async = true;
          node.src = url;

          document.head.appendChild(node);
        } else {
          var xhr = new XMLHttpRequest();
          xhr.timeout = 5000; // 5sec
          xhr.open("GET", url, true);
          xhr.ontimeout = function () {
            console.error("The request for " + url + " timed out.");
          };
          xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
              if (xhr.status === 200) {
                loaderEval(xhr.responseText);
              } else {
                console.error(name, xhr.status, xhr.statusText);
              }
            }
          };
          xhr.send(null);
        }
      }
    }

    /**
     * @method require
     * @public
     * @param {String}
     *            name Name of a module
     * @param {Function}
     *            onsuccess Function called once the module is ready
     */
    var require = scope.require = function(name, onsuccess) {
      var module = getOrCreateModule(name);

      if (module.state === 'ready') {
        onsuccess(module);
        return;
      } else {
        if ("function" === typeof onsuccess) {
          module.waiting.push(onsuccess);
        }
        if (module.state === 'creation') {
          module.state = 'loading';
          loadScript(name);
          window.setTimeout(function() {
        	  if(module.state === 'loading') {
        		  console.error("Unable to load module " + module.name + ", timeout");
        	  }
          }, _timeout * 1000);
        }
      }
    }

    /**
     * Define a module, but dependencies can be loaded async.
     * TODO Maybe it's possible to find a simpler way to handle, async module loading.
     * @define
     * @public
     * @param {String}
     *            moduleName Name of the module
     * @param {[String]}
     *            List of required class/module. Circular dependencies are
     *            allowed, thanks to the async dependency resolution system
     * @param {[Function]}
     *            definition A function that return the module definition, or
     *            null. It's where you can execute the code you want. The first
     *            parameter given is an object that give you the current
     *            dependency states. It has an onready property that allow you do
     *            define a special callback to handle dependencies are all ready
     *            event.
     */
    scope.defineAsync = function(moduleName, dependencies,
        definition) {
      var args = [];
      var notifier = {};
      var len = dependencies.length;
      var module = getOrCreateModule(moduleName);
      var loaded = 0;

      var asyncLoader = function(index, name) {
        require(name, function(depModule) {
          ++loaded;
          args[index] = depModule.content;
          if (loaded == len) {
            // Dependencies are fully loaded, notify the module about it
            if("function" === typeof notifier.onready) {
              notifier.onready.apply(scope, args);
            }
            setModuleReady(module);
          }
        });
      }

      if (dependencies.length > 0) {
        for (var i = 0; i < len; ++i) {
          // Async loading
          asyncLoader(i, dependencies[i]);
        }
      }
      module.content = definition.call(scope, notifier);
      if (dependencies.length == 0) {
        if("function" === typeof notifier.onready) {
          notifier.onready.apply(scope, []);
        }
        setModuleReady(moduleName);
      }
    };

    /**
     * Define a module.
     *
     * @define
     * @public
     * @param {String}
     *            moduleName Name of the module
     * @param {[String]}
     *            List of required class/module. Be sure to not do circular
     *            dependency of modules because this is not supported.
     * @param {[Function]}
     *            definition A function that return the module definition, or
     *            null. It's where you can execute the code you want, using
     *            dependencies as parameters.
     */
    scope.define = function(moduleName, dependencies, definition) {
      if(typeof moduleName != "string")
        throw "Module name isn't a string";
      if(typeof dependencies != "object" || typeof dependencies.length != "number")
        throw "Dependencies isn't an array";
      if(typeof definition != "function")
        throw "Definition isn't a function";
      var args = [];
      var len = dependencies.length;
      var loaded = 0;
      var module = getOrCreateModule(moduleName);
      var asyncLoader = function(index, name) {
        require(name, function(depModule) {
          ++loaded;
          args[index] = depModule.content;
          if (loaded == len) {
            // Dependencies are fully loaded, we can execute it
            module.content = definition.apply(scope, args);
            setModuleReady(module);
          }
          if(_verbose > 1)
            console.log('Module ' + moduleName + ' dependency loaded ' + depModule.name + ' (' + loaded + '/' + len + ')');
        });
      }

      if (dependencies.length > 0) {
        for (var i = 0; i < len; ++i) {
          // Async loading
          asyncLoader(i, dependencies[i]);
        }
      } else {
        module.content = definition.apply(scope, args);
        setModuleReady(module);
      }
    };

    /**
     * Activate the debug mode
     * The debug mode MUST NOT be used in production environment has it make the Loader accessible globally
     * The main advantage of the debug mode is that module aren't loaded by the eval isolated method.
     * The eval isolated method is often incompatible with debuggers.
     */
    scope.debugMode = function() {
      if(!_debugmode) {
        _debugmode = true;
        _verbose = 0;
        if(window.Loader)
          console.log("Global Loader object was already defined", window.Loader);
        window.Loader = Loader;
        console.log("Loader is now in debug mode");
      }
    };

    /**
     * The verbosity level of the Loader class
     * - 0 errors/warnings
     * - 1 infos
     * - 2 debug
     *
     * @property verbosity
     * @type Integer
     * @public
     * @static
     */
    Object.defineProperty(scope, 'verbosity', {
	  get: function() {
	    return _verbose;
	  },
	  set : function(value) {
	    _verbose = value;
    	console.log("Verbosity set to value", value);
	  }
    });
  })(Loader, function(jsContent) {
    // Special function to eval module content inside a very special context
    // Only the local variable Loader is accessible
    eval(jsContent);
  });

  Loader.require('main');
})();