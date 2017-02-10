Loader.define('core/ProxyCommunication', ['lib/jQuery', 'core/EventEmitter'], function ($, EventEmitter) {
  /**
   * Object returned by the get/post method of ProxyCommunication
   * @module core
   * @class Deferred
   */
  /**
   * Construct a success callback
   *
   * @method done
   * @param {Function} callback
   * @param {Object} callback.data Data returned by the request
   */
  /**
   * Construct an error callback
   *
   * @method fail
   * @param {Function} callback
   */
  /**
   * Construct a callback for both success and error
   *
   * @method always
   * @param {Function} callback
   */

  /**
   * @module core
   * @class ProxyCommunication
   * @static
   */
  var ProxyCommunication = {
    baseUrl : "",

    /**
     * Send a GET request to the specified API part
     *
     * @method get
     * @static
     * @public
     * @param {Function} [callback] If given, this callback is set as a success callback
     * @param {Object} callback.data Data returned by the request
     * @return {Deferred}
     */
    get : function(api, callback) {
      var request = {
        type: "GET",
        url: ProxyCommunication.baseUrl + api,
        dataType: "json"
      };

      return doRequest(request, callback);
    },

    /**
     * Send a POST request to the specified API part
     *
     * @method get
     * @static
     * @public
     * @param {Function} [callback] If given, this callback is set as a success callback
     * @param {Object} callback.data Data returned by the request
     * @return {Deferred}
     */
    post : function(api, data, callback) {
      var request = {
        type: "POST",
        url: ProxyCommunication.baseUrl + api,
        data: data,
        dataType: "json"
      };

      return doRequest(request, callback);
    }
  };

  function doRequest(request, callback) {
    var xhr = $.ajax(request);

    if(callback) {
      xhr.done(function(msg) {
        callback(msg);
      });
    }

    xhr.fail(function(jqXHR, textStatus) {
      ProxyCommunication.emit('fail', textStatus, request);
    });

    return xhr;
  }

  EventEmitter.implementIn(ProxyCommunication);

  ProxyCommunication.on('fail', function(textStatus, request) {
    console.log("Request failed : "+textStatus, request);
  });

  return ProxyCommunication;
});