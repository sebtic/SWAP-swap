Loader.define('ui/Draggable', ['lib/jQuery'], function($) {
  /**
   * Simple class to give dragging capabilities to an element
   *
   * @module ui
   * @class Draggable
   * @constructor
   * @param {DOMElement}
   *            element The element to drag, if container is also given,
   *            this element define the area that define where dragging
   *            the element is possible
   * @param {DOMElement}
   *            [container] The element to move while dragging, if not
   *            set, element is used
   */
  var Draggable = function(element, container) {
    container = container || element;
    var $container = $(container);
    var offsetTop, offsetLeft;
    var width, height;
    var minTop, minLeft, maxTop, maxLeft;
    var padding = {
      top : 0, right : 0, bottom : 0, left : 0
    };

    element.addEventListener("mousedown", onMousedown, false);

    /**
     * Simple event handler that prevent default execution of an event
     * @param {Event} event
     */
    function stopDefault(event){
        if(event && event.preventDefault){
          event.preventDefault();
        }
        return false;
    }

    /**
     * Mouse down event handler.
     * Start dragging the element.
     * @param {Event} event
     */
    function onMousedown(event) {
      var position = $container.position();

      window.addEventListener("mousemove", onMouseMove, false);
      window.addEventListener("mouseup", onMouseUp, false);

      width = $container.outerWidth();
      height = $container.outerHeight();
      minTop = padding.top;
      minLeft = padding.left;
      maxTop = $(window).height() - height - padding.bottom;
      maxLeft = $(window).width() - width - padding.right;
      offsetTop = position.top - event.pageY;
      offsetLeft = position.left - event.pageX;

      return stopDefault(event);
    }

    /**
     * Global Mouse move event handler.
     * Move the element as the mouse move.
     * @param {Event} event
     */
    function onMouseMove(event) {
      var top, left;

      top = Math.max(minTop, Math.min(maxTop, event.pageY+offsetTop));
      left = Math.max(minLeft, Math.min(maxLeft, event.pageX+offsetLeft));

      container.style.setProperty("top", top+"px" , "important");
      container.style.setProperty("left", left+"px" , "important");
    }

    /**
     * Global Mouse up event handler.
     * End the current dragging.
     * @param {Event} event
     */
    function onMouseUp(event) {
      onMouseMove(event);

      window.removeEventListener("mousemove", onMouseMove, false);
      window.removeEventListener("mouseup", onMouseUp, false);
    }

    /**
     * Remove dragging capabilities of the element
     * @method remove
     * @public
     */
    this.remove = function() {
      element.removeEventListener("mousedown", onMousedown, false);
    }

    /**
     * Define the space between window borders and element sides.
     * @method setPadding
     * @param {Number} top
     * @param {Number} [right] if not set, padding is the same for all sides
     * @param {Number} [bottom] if not set, bottom is the same as top
     * @param {Number} [left] if not set, left is the same as right
     */
    this.setPadding = function(top, right, bottom, left) {
      padding.top = top;
      padding.right = right || padding.top;
      padding.bottom = bottom || padding.top;
      padding.left = left || padding.right;
    }
  }

  return Draggable;
});