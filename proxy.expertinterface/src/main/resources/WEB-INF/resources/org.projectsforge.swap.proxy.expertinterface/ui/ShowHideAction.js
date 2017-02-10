Loader.define('ui/ShowHideAction', ['ui/Action', 'core/ManagedElement'], function(Action, ManagedElement) {
  'use strict';

  /**
   * An Action with a special show/hide button that emit show/hide events
   *
   * @module ui
   * @class ShowHideAction
   * @extends Action
   */
  var ShowHideAction = function(label, defaultAction, hidden) {
    Action.apply(this, arguments);

    this.dom.showhide = ManagedElement('div').addClass('action-showhide').appendTo(this);

    this._hidden = !!hidden;
    if(this._hidden)
      this.dom.showhide.addClassState('hidden');

    var self = this;
    this.dom.showhide.element.addEventListener('click', function(event) {
      self.toggleHidden();
      event.stopPropagation();
    }, false);
  }

  ShowHideAction.prototype = Object.create(Action.prototype);
  ShowHideAction.prototype.constructor = ShowHideAction;

  /**
   * True if the action show/hide state is hidden, false otherwise
   *
   * @property hidden
   * @type Boolean
   */
  Object.defineProperty(ShowHideAction.prototype, 'hidden', {
    get: function() {
      return this._hidden;
    },
    set : function(value) {
      if(value)
        this.hide();
      else
        this.show();
    }
  });

  /**
   * Put this action in hidden state :
   *  - change the button state
   *  - emit the show event
   *
   * @method show
   */
  ShowHideAction.prototype.show = function() {
    this.dom.showhide.removeClassState('hidden');
    this._hidden = false;
    this.emit('show');
  }

  /**
   * Put this action in hidden state :
   *  - change the button state
   *  - emit the hide event
   *
   * @method hide
   */
  ShowHideAction.prototype.hide = function() {
    this._hidden = true;
    this.dom.showhide.addClassState('hidden');
    this.emit('hide');
  }

  /**
   * Toggle this action hidden state
   *
   * @method toggleHidden
   */
  ShowHideAction.prototype.toggleHidden = function() {
    this.hidden = !this.hidden;
  }

  return ShowHideAction;
});