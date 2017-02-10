Loader.define('core/DOMExport', ['lib/Unicode'], function(Unicode) {
  /**
   * Provide export functionalities :
   *  - xpath generation/lookup
   *  - html exportation with filters and transformations
   * 
   * @module core
   * @class DOMExport
   * @static
   */
  var DOMExport = {};

  /**
   * Default function for text anonymisation
   * 
   * @method anonymiseDefault
   * @static
   * @private
   * @return {String}
   */
  function anonymiseDefault(text) {
    var i, len, c, str = "";

    for(i = 0, len = text.length; i < len; ++i) {
      c = text[i];
      if(Unicode.isLowercaseLetter(c))
        c = 'a';
      else if(Unicode.isUppercaseLetter(c))
        c = 'A';
      else if(Unicode.isNumber(c))
        c = '1';
      str += c;
    }

    return str;
  }

  /**
   * Retrieve the xpath of an element
   * Support Element and TextNode
   * 
   * @method xpathOf
   * @static
   * @public
   * @param {DOMElement} element
   * @return XPATH of element
   */
  DOMExport.xpathOf = function(element) {
    var xpath = [];
    for(; element; element = element.parentNode) 
    {
      if (element === document.body) {
        xpath.unshift("/html/body");
        break;
      }
      
      var pos = 1;
      var nodeName = element.nodeName.toLowerCase()
      for(var sib = element.previousSibling; sib; sib = sib.previousSibling) {
        if(sib.nodeName.toLowerCase() === nodeName) {
          ++pos;
        }
      }
      
      if(element.nodeType === 3) {
        nodeName = "text()";
      }
      
      xpath.unshift(nodeName + '[' + pos + ']');
    }
    return xpath.length > 0 ? xpath.join('/') : null;
  }

  /**
   * Lookup for an element from the xpath value
   * 
   * @method elementFromXpath
   * @static
   * @public
   * @param {String} xpath
   * @return The resolved element or null otherwise
   */
  DOMExport.elementFromXpath = function( xpath ) {
    try {
      return document.evaluate( xpath ,document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
    } catch(e) {
      console.log("Xpath exception thrown : ", e);
      return null;
    }
  }

  /**
   * Exports the HTML content of an element
   * 
   * @method export
   * @static
   * @public
   * @param {DOMElement} node
   * @param {Object} [options] Configuration object
   * @param {Function} [options.keepAttribute] 
   *    function(attrName : String, element : DOMElement) : Boolean
   *    Returns true if attribute must be kept, false otherwise.
   *    Defaults to always true
   * @param {Function} [options.keepElement] 
   *    function(element : DOMElement) : Boolean
   *    Returns true if element must be kept, false otherwise
   *    Defaults to always true
   * @param {Function} [options.anonymiseAttribute(attrName : String, element : DOMElement)] 
   *    function(attrName, element) : Boolean
   *    Returns true if element must be anonymised, false otherwise
   *    Defaults to always true
   * @param {Function} [options.anonymiseText] 
   *    function(element : DOMElement) : Boolean
   *    Returns true if text must be anonymised, false otherwise
   *    Defaults to always true
   * @param {Function} [options.anonymise] 
   *    function(text : String) : String
   *    Returns the text after anonymisation
   *    See anonymiseDefault for default implementation
   * @return {String} HTML code
   */
  DOMExport.export = function(node, options) {
    options = options || {dontModify : true};
    var keepAttr = options.keepAttribute || function(attrName, element) { return true; };
    var keepElement = options.keepElement || function(element) { return true; };
    var anonymiseAttr = options.anonymiseAttribute || function(attrName, element) { return true; };
    var anonymiseText = options.anonymiseText || function(element) { return true; };
    var anonymise = options.anonymise || anonymiseDefault;

    function visit(node) {
      var i, len, attribute;

      if(node.nodeType == 3 || node.nodeType == 8) { // TEXTElement || Comment
        if(anonymiseText(node)) {
          node.nodeValue = anonymise(node.nodeValue);
        }
      } else if(node.nodeType == 1 || node.nodeType == 10) { // Element | DocType
        if(node.attributes) {
          // Lookup attributes and
          for(i = 0, len = node.attributes.length; i < len; ++i) {
            attribute = node.attributes[i];
            if(!keepAttr(attribute.name, node)) {
              // This attribute must be removed
              node.removeAttribute(attribute.name);
            } else if(anonymiseAttr(attribute.name, node)) {
              attribute.value = anonymise(attribute.value);
            }
          }
        }

        // Recursive descent of nodes
        for(i = 0; i < node.childNodes.length; ++i) {
          if(keepElement(node.childNodes[i])) {
            visit(node.childNodes[i]);
          } else {
            node.removeChild(node.childNodes[i]);
            --i;
          }
        }
      } else {
        console.log("Unsupported element type ", node.nodeType, node);
      }
    }

    if(options.dontModify === true)
      return node.outerHTML;

    node = node.cloneNode(true);
    visit(node);

    return node.outerHTML;
  }

  return DOMExport;
});