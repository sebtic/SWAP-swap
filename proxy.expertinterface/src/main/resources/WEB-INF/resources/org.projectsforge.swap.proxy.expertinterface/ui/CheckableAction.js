Loader.define('ui/CheckableAction', ['ui/Action', 'core/ManagedElement'], function(Action, ManagedElement) {
  'use strict';

  /**
   * A checkable action
   *
   * @module ui
   * @class CheckableAction
   * @extends Action
   */
  var CheckableAction = function(checked) {
    Action.apply(this, arguments);

    this.checked = checked;

    var self = this;
    this.on('run', function() {
      self.checked = !self.checked; // Toggle
    });
  }

  CheckableAction.prototype = Object.create(Action.prototype);
  CheckableAction.prototype.constructor = CheckableAction;

  /**
   * True if the action show/hide state is hidden, false otherwise
   *
   * @property hidden
   * @type Boolean
   */
  Object.defineProperty(CheckableAction.prototype, 'checked', {
    get: function() {
      return this._checked;
    },
    set : function(value) {
      value = !!value;
      if(this._checked === value)
        return;
      this._checked = value;
      if(this._checked) {
        this.addClassState('checked');
        this.setIcon('checked');
        this.emit('checked');
        this.emit('stateChanged', true);
      } else {
        this.removeClassState('checked');
        this.setIcon('unchecked');
        this.emit('unchecked');
        this.emit('stateChanged', false);
      }
    }
  });

  return CheckableAction;
});