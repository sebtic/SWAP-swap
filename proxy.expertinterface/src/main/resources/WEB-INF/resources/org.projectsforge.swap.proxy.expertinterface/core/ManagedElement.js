Loader.define('core/ManagedElement', ['core/CSSStyle'], function(CSSStyle) {
  'use strict';
  var __arguments__ = {}

  var ManagedElement = function(element, className) {
    if(!(this instanceof ManagedElement)) {
      if(ManagedElement.isManagedElement(element))
        return element.ManagedElement;
      return new ManagedElement(__arguments__, arguments);
    }
    if(element === __arguments__) {
      CSSStyle.apply(this, className);
    } else {
      CSSStyle.apply(this, arguments);
    }
    this.element.ManagedElement = this;
  }

  ManagedElement.isManagedElement = function(element) {
    return element && element.ManagedElement instanceof ManagedElement;
  }
  
  ManagedElement.prototype = Object.create(CSSStyle.prototype);
  ManagedElement.prototype.constructor = ManagedElement;

  Object.defineProperty(ManagedElement.prototype, 'textContent', {
    get: function() {
      return this.element ? this.element.textContent : "";
    },
    set : function(value) {
      if(this.element)
        this.element.textContent = value;
    }
  });

  ManagedElement.prototype.onElement = function(event, callback) {
    if(this.element) {
      this.element.addEventListener(event, callback, false);
    }

    return this;
  }

  ManagedElement.prototype.appendTo = function(element) {
    if(element instanceof CSSStyle)
      element = element.element;

    if(element == this.element.parentNode)
      return;
    element.appendChild(this.element);
    this.refreshStyle();

    return this;
  }

  ManagedElement.prototype.removeChildrens = function(element) {
    while(this.element.firstChild) {
      this.element.removeChild(this.element.firstChild);
    }

    return this;
  }

  ManagedElement.prototype.remove = function() {
    if(this.element.parentNode) {
      this.element.parentNode.removeChild(this.element);
    }

    return this;
  }

  return ManagedElement;
});