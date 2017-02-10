Loader.define('ui/Action', ['core/EventEmitter', 'core/ManagedElement'], function(EventEmitter, ManagedElement) {
  'use strict';

  /**
   * An Action is a button
   *
   * @module ui
   * @class Action
   * @extends ManagedElement
   * @implements EventEmitter
   */
  var Action = function(label, defaultAction) {
    ManagedElement.call(this, 'div');
    EventEmitter.implementIn(this);

    this.dom = {
        text : document.createTextNode("")
    };
    this.element.appendChild(this.dom.text);

    this.addClass('action');
    this.label = "string" === typeof label ? label : "";
    this._enabled = true;

    var self = this;
    this.element.addEventListener('click', function(event) {
      event.preventDefault();
      event.stopPropagation();
      if(self.enabled)
        self.emit('run');
    }, false);

    if(typeof defaultAction == "function") {
      self.on('run', defaultAction);
    }
  }

  Action.prototype = Object.create(ManagedElement.prototype);
  Action.prototype.constructor = Action;

  Action.prototype.setIcon = function(icon) {
    if(icon == null) {
      if(this._icon) {
        // Get out of the icon mode
        this._icon.remove();
        delete this._icon;
      }
    } else {
      if(this._icon) {
        // Remove the previous icon
        this._icon.removeClass('action-icon-' + this._iconname);
      }
      if(!this._icon) {
        // Put the action in icon mode
        this._icon = ManagedElement('div').addClass('action-icon').appendTo(this);
      }
      this._iconname = icon;
      this._icon.addClass('action-icon-' + this._iconname);
    }

    return this;
  }

  /**
   * True if this action is enabled, false otherwise.
   *
   * @property enabled
   * @type Boolean
   */
  Object.defineProperty(Action.prototype, 'enabled', {
    get: function() {
      return this._enabled;
    },
    set : function(enable) {
      enable = !!enable;
      if(this._enabled !== enable) {
        if(enable)
          this.removeClassState('disabled');
        else
          this.addClassState('disabled');
        this._enabled = enable;
      }
    }
  });

  /**
   * Tooltip displayed when the user hold the cursor hover this action
   *
   * @property tooltip
   * @type String
   */
  Object.defineProperty(Action.prototype, 'tooltip', {
    get: function() {
      return this.element.getAttribute('title');
    },
    set : function(value) {
      this.element.setAttribute('title', value);
    }
  });

  /**
   * Label of this action
   *
   * @property label
   * @type String
   */
  Object.defineProperty(Action.prototype, 'label', {
    get: function() {
      return this.dom.text.textContent;
    },
    set : function(value) {
      this.dom.text.textContent = value;
    }
  });

  return Action;
});