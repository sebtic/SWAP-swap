Loader.define('core/Overlay', ['lib/jQuery', 'core/ManagedElement'], function ($, ManagedElement) {
  /**
   * Create a special overlay for a page.
   * Allow other modules to insert elements as child to bypass page CSS.
   * @module Overlay
   */
    var waiting = [];
    var overlay = null;

    var Overlay = {
      /**
       * Main function of this module, it's where another module can retrieve a reference to the overlay.
       * @method ready
       * @static
       * @param {Function} callback Function to call once overlay is ready.
       *                 The overlay is passed as first parameter.
       */
      ready : function(callback) {
        if(overlay !== null) {
          callback(overlay);
        } else {
          waiting.push(callback);
        }
      }
    };

    $(function() {
      overlay = ManagedElement('div').appendTo(document.body).element;

      for(var i = 0; i < waiting.length; ++i) {
        waiting[i](overlay);
      }
      waiting = null;
    });

    return Overlay;
});