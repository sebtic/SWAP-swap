Loader.define('core/EventEmitter', [], function() {
  /**
   * Simple class that can emit events to listeners.
   * Inspired by nodejs and Web standards.
   * @module core
   * @class EventEmitter
   */
  var EventEmitter = function(objectToExtends) {
    this._listenersMap = {};
    this._globalListeners = [];
  }

  /**
   * Make an object capable of sending events.
   * Map on, once, off and emit functions to the object.
   * @method implementIn
   * @static
   * @param {Object} obj The obj to add capabilities to
   */
  EventEmitter.implementIn = function (obj) {
    obj._listenersMap = {};
    obj._globalListeners = [];
    for(var i in EventEmitter.prototype) {
      obj[i] = EventEmitter.prototype[i];
    }
  }

  /**
   * Remove the emit function from the given object
   * @method makeEmitPrivate
   * @static
   * @param {Object} obj The obj to modify (implementIn(obj) SHOULD have happen before)
   * @return a private like function witch mimic the initial emit functin behavior
   */
  EventEmitter.makeEmitPrivate = function (obj) {;
    var emit = obj.emit;
    delete obj.emit;
    return function() {
      emit.apply(obj, arguments);
    }
  }

  EventEmitter.prototype = {
      /**
       * Add a listener to an event
       * @method on
       * @param {String} event Name of the event
       * @param {Function} listener Function to call when event is emitted
       */
      on : function(event, listener) {
        checkEventValidity(event);
        checkListenerValidity(listener);

        if(!(event in this._listenersMap)) {
          this._listenersMap[event] = listener;
        } else {
          var listeners = this._listenersMap[event];
          if("function" === typeof listeners) {
            this._listenersMap[event] = [listeners, listener];
          } else {
            this._listenersMap[event].push(listener);
          }
        }
      },
      /**
       * Add a one time listener to an event
       * @method on
       * @param {String} event Name of the event
       * @param {Function} listener Function to call when event is emitted
       */
      once : function(event, listener) {
        function handleEvent() {
          this.removeListener(event, handleEvent);
          listener.apply(this, arguments);
        }
        this.on(event, handleEvent);
      },
      /**
       * Remove a listener to an event
       *
       * @method off
       * @param {String} [event] Name of the event to remove,
       *                         if not defined, remove all listeners
       * @param {Function} [listener] Function related to event to remove,
       *                         if not defined, remove all listeners of the given event
       */
      off : function(event, listener) {
        if(event) {
          // Delete only listener of event
          checkEventValidity(event);
          if(event in this._listenersMap) {
            if(listener) {
              // Delete only matching listener of event
              checkListenerValidity(listener);
              var listeners = this._listenersMap[event];
              if("function" === typeof listeners) {
                // Only one listener of event
                if(listeners === listener) {
                  delete this._listenersMap[event];
                  return true;
                }
              } else {
                // Multiple listeners of event
                for(var i = 0, j = listeners.length; i < j; ++i) {
                  if(listeners[i] === listener) {
                    listeners.splice(i, 1);
                    if(listeners.length == 0)
                      delete this._listenersMap[event];
                    return true;
                  }
                }
              }
            } else {
              // Delete all listeners of event
              delete this._listenersMap[event];
              return true;
            }
          }

          return false;
        }

        // Delete all listeners
        this._listenersMap = {};

        return true;

      },
      /**
       * Add a listener to all events
       * @method onEvent
       * @param {Function} listener Function to call when an event is emitted
       */
      onEvent : function(listener) {
        this._globalListeners.push(listener);
      },
      /**
       * Remove a listener to all events
       * @method onEvent
       * @param {Function} listener Function to remove that was called when any event was emitted
       */
      offEvent : function(listener) {
        checkListenerValidity(listener);
        for(var i = 0, j = this._globalListeners.length; i < j; ++i) {
          if(this._globalListeners[i] === listener) {
            this._globalListeners.splice(i, 1);
            return true;
          }
        }
        return false;
      },
      /**
       * Emit an event
       * @method emit
       * @param {String} event Name of event to emit
       * @param  ... Parameters to use for calling listeners
       */
      emit: function(event) {
        var i, j;

        checkEventValidity(event);

        // Forward to global listeners
        for(i = 0, j = this._globalListeners.length; i < j; ++i) {
          this._globalListeners[i].apply(this, arguments);
        }

        // Forward to per event listeners
        if(event in this._listenersMap) {
          var listeners = this._listenersMap[event];
          // Creating an array of arguments to pass (all arguments but the first one)
          var args = Array.prototype.slice.call(arguments, 1);
          if("function" === typeof listeners) {
            listeners.apply(this, args);
          } else {
            // Temporary clone of the listeners array to prevent bugs, if the list is modified elsewhere.
            listeners = listeners.slice(0);
            for(i = 0, j = listeners.length; i < j; ++i) {
              listeners[i].apply(this, args);
            }
          }
        }
      }
  }

  function checkEventValidity(event) {
    if("string" !== typeof event) {
      throw "event is not a string, it's a " + typeof event;
    }
  }

  function checkListenerValidity(listener) {
    if("function" !== typeof listener) {
      throw "listener is not a function, it's a " + typeof listener;
    }
  }

  return EventEmitter;
});