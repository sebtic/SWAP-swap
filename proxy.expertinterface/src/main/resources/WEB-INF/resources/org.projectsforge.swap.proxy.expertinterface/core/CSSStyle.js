Loader.define('core/CSSStyle', [], function() {
  'use strict';

  /**
   * StyleSheet node create at runtime that will be used to create CSS rules at runtime.
   *
   * @property stylesheet
   * @private
   * @type StyleSheet
   * @øtatic
   */
  var stylesheet = (function() {
    var style = document.createElement("style");
    style.appendChild(document.createTextNode("")); //< Webkit hack
    document.head.appendChild(style);
    return style.sheet;
  })();

  /**
   * Prefix added before rule name to ensure no conflict happens with the original webpage.
   *
   * @property ruleprefix
   * @type String
   * @private
   * @øtatic
   */
  var ruleprefix = "swap-";

  (function() {
    // Build initial global rule that force any CSSStyle element to be at initial W3C style state.
    // The initial style state doesn't mean the default browser state but a sightly different state defined by W3C
	// See : https://developer.mozilla.org/en-US/docs/Web/CSS/initial
    var rule = "." + ruleprefix + "initial {";
    var styles = window.getComputedStyle(document.createElement('div'));
    for(var i = 0; i < styles.length; ++i) {
      rule += styles[i] + ":initial !important;"
    }
    rule += "}";
    stylesheet.insertRule(rule, 0);
  })();

  /**
   * Provide a simple interface for managing element style properties
   *
   * If className is given, the element CSS is :
   *  - reseted to browser initial values
   *  - the class "global" is applied
   *  - the class "global.nodeName" is applied (example : global.DIV)
   *  - the class classNamthis.dom.roote is applied
   *
   * @module core
   * @class CSSStyle
   * @constructor
   * @param {DOMElement|String}
   *            element A DOMELement or a valid nodeName
   * @param {String}
   *            [className] Name of the class for default setup
   */
  var CSSStyle = function(element, className) {
    if(typeof element == "string") {
      // Element is a nodeName, create a DOMElement according to it
      element = document.createElement(element);
    }

    /**
     * The element managed by this object
     *
     * @property element
     * @type DOMElement
     * @public
     */
    this.element = element;
    this.element.className = ruleprefix + "initial";
    this.addClass("global");
    this.addClass("global-"+element.nodeName.toLowerCase());
    if(typeof className == "string") {
      this.addClass(className);
    }
  }
  CSSStyle.prototype = {};

  /**
   * Return the create rule name from the internal naming convention
   * @method getRuleName
   * @private
   * @static
   * @param {String}
   *            name Name of the rule. Similar to className in CSS.
   * @param {String}
   *            pseudoEl When should this rule be applied (default, hover)
   */
  function getRuleName(name, pseudoEl) {
    var rulename = ruleprefix + name;
    if(pseudoEl && pseudoEl != "default") {
      rulename += "." + getRuleStateName(pseudoEl);
    }
    if(pseudoEl == "hover" || pseudoEl == "focus" || pseudoEl == "active" || pseudoEl == "before" || pseudoEl == "after") {
      // Native pseudo elements
      rulename += ", ." + ruleprefix + name + ":" + pseudoEl;
    }
    return rulename;
  }

  /**
   * Return the create rule name from the internal naming convention
   * @method getRuleName
   * @private
   * @static
   * @param {String}
   *            pseudoEl When should this rule be applied (default, hover)
   */
  function getRuleStateName(pseudoEl) {
    return ruleprefix + "--" + pseudoEl;
  }

  /**
   * Add className to an element class list
   *
   * @method addClass
   * @private
   * @static
   * @param element The element to change the classes
   * @param className The className to add
   */
  function addClass(element, className) {
    if(element.className.length == 0) {
      element.className = className;
    } else {
      var rx = new RegExp("(^| )"+className+"(?= |$)", "g");
      if(!rx.test(element.className)) {
        element.className += " " + className;
      }
    }
  }

  /**
   * Remove className to an element class list
   *
   * @method removeClass
   * @private
   * @static
   * @param element The element to change the classes
   * @param className The className to remove
   */
  function removeClass(element, className) {
    element.className = element.className.replace(new RegExp("(^| )"+className+"((:|--)[^ ]+)*(?= |$)", "g"), "");
  }

  /**
   * Create a css rule
   *
   * @method buildRule
   * @private
   * @static
   * @param {String}
   *            name Complete name of the css rule
   * @param {Map<String, String>}
   *            properties Map of css properties. The key is the css property
   *            		   name and the value is the css property value.
   */
  function buildRule(name, properties) {
    var rule = name;
    rule += " { ";
    for(var propertyName in properties) {
      // Here we could add a special layer that would handle not yet supported propertyName
      // So things like prefixes -moz, -ms, -o, -webkit, could be automatically added if the browser requires it
      // Maybe a static public property that allow the user to define custom property handler
      // and with the next line as default handler.
      // Or an automatic system based on the list of rule supported by the browser
      // and prefix detection ('-[a-z]+-' pattern)
      rule += propertyName + ":"+properties[propertyName]+" !important;"
    }
    rule += "}";
    return rule;
  }

  /**
   * Add a CSS rule
   *
   * @method addRule
   * @static
   * @param {String}
   *            name Name of the rule. Similar to className in CSS.
   * @param {String}
   *            pseudoEl When should this rule be applied (default, hover)
   * @param {Object}
   *            properties Map of propertyName to value as an object
   */
  CSSStyle.addRule = function(name, pseudoEl, properties) {
    if(typeof pseudoEl === "object") {
      properties = pseudoEl;
      pseudoEl = "default";
    }
    stylesheet.insertRule("." + buildRule(getRuleName(name, pseudoEl), properties), stylesheet.cssRules.length);
  }

  /**
   * Returns a relative css url for the given href
   *
   * @method url
   * @static
   * @param {String}
   *            href The url to resolve
   */
  CSSStyle.url = function(href) {
    return "url('" + Loader.url + href + "')";
  }

  /**
   * Get the computed style of the requested property of the managed element
   *
   * @method css
   * @public
   * @param {String} propertyName
   * @return {String}
   */
  /**
   * Get the computed style of the requested properties for the managed element
   *
   * @method css
   * @public
   * @param {Array<String>} propertyNames
   * @return {Object} A map of properties to values
   */
  /**
   * Set the style of one property for the managed element
   *
   * @method css
   * @public
   * @param {String} propertyName
   * @param {String|Number} value
   * @return this for chained calls
   */
  /**
   * Set the style of multiple properties for the managed element
   *
   * @method css
   * @public
   * @param {Object} properties A map of properties to values
   * @return this for chained calls
   */
  CSSStyle.prototype.css = function(arg1, arg2) {
    if(typeof arg1 === "string") { // arg1 is propertyName
      if(arguments.length == 1) {
        return window.getComputedStyle(this.element).getPropertyValue(arg1);
      }

      this.element.style.setProperty(arg1, arg2, 'important');
    } else if(arg1 instanceof Array) {
      var styles = window.getComputedStyle(this.element);
      var values = {};
      arg1.forEach(function(propertyName) {
        values[propertyName] = styles.getPropertyValue(propertyName);
      });
      return values;
    } else if(typeof arg1 === "object") {
      for(var propertyName in arg1) {
        this.element.style.setProperty(propertyName, arg1[propertyName], 'important');
      }
    } else {
      console.warn("Unable to matches parameters to definition, nothing has been done")
    }

    return this; // Chained call
  }

  /**
   * Apply a custom CSS rule to the managed element
   *
   * @method addClass
   * @param {String}
   *            className Name of the class to apply
   * @return this for chained calls
   */
  CSSStyle.prototype.addClass = function(className) {
    addClass(this.element, getRuleName(className));

    return this; // Chained call
  }

  /**
   * Remove a custom CSS rule of the managed element
   *
   * @method removeClass
   * @param {Object}
   *            className Name of the rule to remove
   * @return this for chained calls
   */
  CSSStyle.prototype.removeClass = function(className) {
    removeClass(this.element, getRuleName(className));

    return this;
  }

  /**
   * Apply CSS state for a rule
   *
   * @method addClassState
   * @param {String}
   *            state Name of the state to add
   * @return this for chained calls
   */
  CSSStyle.prototype.addClassState = function(state) {
    addClass(this.element, getRuleStateName(state));

    return this;
  }

  /**
   * Remove CSS state properties for an applied rule
   *
   * @method removeClassState
   * @param {String}
   *            state Name of the state to remove
   * @return this for chained calls
   */
  CSSStyle.prototype.removeClassState = function(state) {
    removeClass(this.element, getRuleStateName(state));

    return this;
  }

  CSSStyle.prototype.refreshStyle = function() {

    return this;
  }

  /**
   * Reset the style of the element to the navigator default style
   * Remove all custom classes/properties
   *
   * @method resetStyle
   * @return this CSSStyle object for call chaining
   */
  CSSStyle.prototype.resetStyle = function() {
    this.element.className = "";
    this.addClass("global");
    this.addClass("global-"+element.nodeName.toLowerCase());

    return this;
  }

  /**
   * Gives the values of all the CSS properties of an element after applying
   * the active stylesheets and resolving any basic computation those values
   * may contain.
   *
   * @method getComputedStyle
   * @see https://developer.mozilla.org/fr/docs/DOM/window.getComputedStyle
   * @return {CSSStyleDeclaration}
   */
  CSSStyle.prototype.getComputedStyle = function() {
    return window.getComputedStyle(this.element);
  }

  return CSSStyle;
});