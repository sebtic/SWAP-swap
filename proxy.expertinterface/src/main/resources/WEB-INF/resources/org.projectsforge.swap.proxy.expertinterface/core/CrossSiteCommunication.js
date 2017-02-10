Loader.define('core/CrossSiteCommunication', ['core/EventEmitter', 'lib/CryptoJS-AES'], function(EventEmitter, CryptoJS) {
  /**
   * Provide simple and secure way to communicate between different websites.
   * @module core
   * @class CrossSiteCommunication
   */
  var CrossSiteCommunication = {};

  /**
   * List of sub window of the current page.
   * Sub windows are responsible of registering themselves using the CrossSiteCommunication API.
   * When a sub-window is loaded (iframe/frame/...), the ExpertInterfaceWorker emit a register event to the top window.
   * The top window is then responsible of registering the window and informing it about the current global status
   *
   * @property subwindows
   * @type Array<DOMWindow>
   * @private
   * @static
   */
  var subwindows = [];

  /**
   * Secret keyword for AES encryption, if secret is empty, no encryption is performed
   * The secrecy of this key is very important to keep communications secure.
   * Actually this system hasn't been proven to invulnerable. Further research would be required.
   * The goal of such encryption is to make the system safer, not easy to break.
   *
   * @property secret
   * @public
   * @static
   * @type String
   */
  CrossSiteCommunication.secret = "81a43f3b3a4ea86424052a147895cff56bdd896d";
  EventEmitter.implementIn(CrossSiteCommunication);
  var emit = EventEmitter.makeEmitPrivate(CrossSiteCommunication);

  /**
   * Listen for Cross Document messages
   *
   * @method on
   * @static
   * @param {String}
   *            event Name of event to listen
   * @param {Function}
   *            listener Function to call when event is captured. The
   *            first parameter is a reference to the window that sent
   *            the message.
   */

  /**
   * Listen for one Cross Document message
   *
   * @method once
   * @static
   * @param {String}
   *            event Name of event to listen for one time
   * @param {Function}
   *            listener Function to call when event is captured. The
   *            first parameter is a reference to the window that sent
   *            the message.
   */

  /**
   * Remove a listener to an event
   *
   * @method off
   * @static
   * @param {String}
   *            [event] Name of the event to remove, if not defined, remove all
   *            listeners
   * @param {Function}
   *            [listener] Function related to event to remove, if not defined,
   *            remove all listeners of the given event
   */

  /**
   * Emit a Cross Document message
   * @method emit
   * @static
   * @param {Window} windowtarget Reference to the window that will receive this event
   * @param {String} event Name of event to emit
   * @param  ... Parameters to use for calling listeners
   */
  CrossSiteCommunication.emit = function(windowtarget, event) {
    var args = Array.prototype.slice.call(arguments, 2);
    var messageObject = {
        "event" : event,
        "args" : args
    };
    var message = encode(messageObject);
    windowtarget.postMessage(message, "*");
  }

  /**
   * Broadcast to all registered documents a message
   * @method broadcast
   * @static
   * @param {String} event Name of event to emit
   * @param  ... Parameters to use for calling listeners
   * @return the number of event emitted
   */
  CrossSiteCommunication.broadcast = function(event) {
    var args = Array.prototype.slice.call(arguments, 2);
    var messageObject = {
        "event" : event,
        "args" : args
    };
    var message = encode(messageObject);
    for(var i = 0; i < subwindows.length; ++i) {
      subwindows[i].window.postMessage(message, "*");
    }
    return subwindows.length;
  }

  /**
   * Get informations about the requested window
   * @method getInformations
   * @static
   * @public
   * @param {Window} remotewindow Window to get the informations for
   * @return An information object about the window or null if not found
   */
  CrossSiteCommunication.getInformations = function(remotewindow) {
    if(remotewindow == window) {
      return info();
    }
    for(var i = 0; i < subwindows.length; ++i) {
      if(subwindows[i].window == remotewindow)
        return subwindows[i].info;
    }

    return null;
  }

  /**
   * Encode the a message according to the specification
   * @return An encode string, ready for message transfert
   */
  function encode(message)
  {
    var ObjectToJson = Object.prototype.toJSON;
    if(ObjectToJson) delete Object.prototype.toJSON;
    var ArrayToJson = Array.prototype.toJSON;
    if(ArrayToJson) delete Array.prototype.toJSON;

    message = JSON.stringify(message);

    if(ObjectToJson) Object.prototype.toJSON = ObjectToJson;
    if(ArrayToJson) Array.prototype.toJSON = ArrayToJson;

    if(typeof CrossSiteCommunication.secret == "string")
      return CryptoJS.AES.encrypt(message, CrossSiteCommunication.secret).toString();
    return message;
  }

  /**
   * Decode the a message according to the specification
   * @return The decoded message of null if an error occurred
   */
  function decrypt(message)
  {
    try {
      if(typeof CrossSiteCommunication.secret == "string") {
        message = CryptoJS.AES.decrypt(message, CrossSiteCommunication.secret).toString(CryptoJS.enc.Utf8);
        if(!message)
          return null; // Decryption failed
      }
      message = JSON.parse(message);
      return message;
    } catch(e) {
      return null;
    }
  }

  /**
   * Called by the navigator once a Cross Document message occurs
   */
  function receiveMessage(event)
  {
    var message = decrypt(event.data);
    if(!message || typeof message != "object")
      return;
    if(typeof message.event != "string")
      return;
    if(typeof message.args != "object" || typeof message.args.length != "number")
      return;
    message.args.splice(0,0, message.event, event.source);
    emit.apply(this, message.args);
  }

  /**
   * Return information about the current window
   */
  function info() {
    return {href : window.location.href };
  }

  window.addEventListener("message", receiveMessage, false);

  if(window.top != window)
  {
    var registerInterval;
    
    // Register to top window
    CrossSiteCommunication.on("registered", function() {
      window.clearInterval(registerInterval);
    });

    registerInterval = window.setInterval(function() {
      CrossSiteCommunication.emit(window.top, 'register', info());
    }, 2500);
    CrossSiteCommunication.emit(window.top, 'register', info());
  }
  else
  {
    // Listen for child windows registration requests.
    CrossSiteCommunication.on("register", function(remotewindow, info) {
      for(var i = 0; i < subwindows.length; ++i) {
        if(subwindows[i].window == remotewindow)
          return;
      }
      subwindows.push({ window : remotewindow, info : info });
      CrossSiteCommunication.emit(remotewindow, 'registered');
    });
  }

  return CrossSiteCommunication;
});