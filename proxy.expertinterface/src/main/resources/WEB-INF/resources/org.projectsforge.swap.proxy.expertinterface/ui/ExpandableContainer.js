Loader.define('ui/ExpandableContainer', ['core/EventEmitter', 'core/ManagedElement'], function(EventEmitter, ManagedElement) {
  'use strict';

  /**
   * An ExpandableContainer is a container with a title and body.
   * The body can be collapsed.
   *
   * @module ui
   * @class ExpandableContainer
   * @extends ManagedElement
   * @implements EventEmitter
   */
  var ExpandableContainer = function() {
    ManagedElement.call(this, 'div');
    EventEmitter.implementIn(this);

    this.addClass('expandablecontainer');

    // head ---------+
    // | title ----+ |
    // | |         | |
    // | +---------+ |
    // content ------+
    // |             |
    // +-------------+
    /**
     * Container of the title. The content of this element is managed by this class.
     * If you want to change the header, see the title property
     *
     * @property head
     * @protected
     * @type ManagedElement
     */
    this._head = ManagedElement('div').addClass('expandablecontainer-head').appendTo(this);

    /**
     * Title of the container
     *
     * @property title
     * @public
     * @type ManagedElement
     */
    this.title = ManagedElement('div').addClass('expandablecontainer-title').appendTo(this._head);

    /**
     * Content of the container
     *
     * @property content
     * @public
     * @type ManagedElement
     */
    this.content = ManagedElement('div').addClass('expandablecontainer-content').appendTo(this);

    this.collapsed = true;
  }

  ExpandableContainer.prototype = Object.create(ManagedElement.prototype);
  ExpandableContainer.prototype.constructor = ExpandableContainer;

  /**
   * Hold the current state of the expandable container.
   * Setting this property will automatically change the state of the container.
   *
   * @property collapsed
   * @type Boolean
   */
  Object.defineProperty(ExpandableContainer.prototype, 'collapsed', {
    get: function() {
      return this._collapsed;
    },
    set : function(collapse) {
      collapse = !!collapse;
      if(this._collapsed !== collapse) {
        if(collapse)
          this.content.addClassState('collapsed');
        else
          this.content.removeClassState('collapsed');
        this._collapsed = collapse;
      }
    }
  });

  return ExpandableContainer;
});