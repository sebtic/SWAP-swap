Loader.define('ZoneSelection', ['core/DOMExport', 'core/ManagedElement'],
    function(DOMExport, ManagedElement) {

  //https://developer.mozilla.org/en-US/docs/Web/API/Range.getClientRects
  var ZoneSelection = function(parent) {
    var self = this;

    this.dom = [];
    this.domContainer = ManagedElement('div');
    this.startContainer = null;
    this.endContainer = null;
    this.startOffset = -1;
    this.endOffset = -1;
    this.displayedRange = null;
    this.parent = parent;
    this._selectionHandler = null;

    this.domContainer.onElement('click', function() {
      self.parent.focus();
    });

    window.addEventListener('resize', this._onresize = function() {
      self._replace();
    }, false);
  }

  ZoneSelection.prototype = {
      setDisplayedRange : function(range) {
        if(range === null) {
          this.displayedRange = null;
          this.domContainer.remove();
          return;
        }
        var parent = range.commonAncestorContainer;
        if(parent.nodeType == 3)
          parent = parent.parentNode;

        while(parent && parent.nodeName.toLowerCase() != "html" && window.getComputedStyle(parent)['position'] === 'static') {
          parent = parent.offsetParent;
        }

        var parentRect = null;
        if(!parent || parent.nodeName.toLowerCase() == "html") {
          parent = document.body;

          if(window.getComputedStyle(parent)['position'] === 'static')
            parentRect = { top : -window.scrollY, left : -window.scrollX }
        }

        if(parentRect === null)
          parentRect = parent.getBoundingClientRect();

        var rects = range.getClientRects();
        var i, rect, div, left, top;

        this.domContainer.appendTo(parent);
        for(i = 0; i < rects.length; ++i) {
          rect = rects[i];
          if(i >= this.dom.length)
            this.dom[i] = ManagedElement('div').addClass('zoneselection');
          div = this.dom[i];

          top = rect.top - parentRect.top;
          left = rect.left - parentRect.left;
          div.css('left', left + 'px');
          div.css('top', top + 'px');
          div.css('width', (rect.right - rect.left) + 'px');
          div.css('height', (rect.bottom - rect.top) + 'px');

          div.appendTo(this.domContainer);
        }

        for(; i < this.dom.length; ++i) {
          this.dom[i].remove();
        }
        this.dom.length = rects.length;

        this.displayedRange = range;
      },
      _replace : function() {
        if(this.displayedRange)
          this.setDisplayedRange(this.displayedRange);
      },
      hide : function() {
        this.domContainer.css('display', 'none');
      },
      show : function() {
        this.domContainer.css('display', 'block');
      },

      /**
       * Highlight this selection
       *
       * @method highlight
       * @public
       * @param {Boolean}
       *            [keep] If true, the highlight is permanent, if false the
       *            highlight is removed if present, otherwise highlight for 1s
       */
      highlight : function(keep) {
        var self = this;
          
        if(this._highlight_timeout) {
          window.clearTimeout(this._highlight_timeout);
          delete this._highlight_timeout;
        }
        
        if(keep === false) {
          for(var i = 0; i < this.dom.length; ++i) {
            this.dom[i].removeClassState('highlight');
          }
          return;
        }


        for(var i = 0; i < this.dom.length; ++i) {
          this.dom[i].addClassState('highlight');
        }

        if(keep === true) {
          return;
        }

        this._highlight_timeout = window.setTimeout(function() {
          delete self._highlight_timeout;
          for(var i = 0; i < self.dom.length; ++i) {
            self.dom[i].removeClassState('highlight');
          }
        }, 1000);
      },

      startSelection : function() {
        if(this._selectionHandler == null) {
          var self = this;
          this.parent.tmpHideOthers(true);
          window.addEventListener('mouseup', this._selectionHandler = function() {
            var sel = window.getSelection();
            self.setRange(sel.getRangeAt(0));
            self.parent.tmpHideOthers(false);
            if(self.displayedRange && !self.displayedRange.collapsed) {
              window.removeEventListener('mouseup', self._selectionHandler, false);
              self._selectionHandler = null;
            }
          }, false);
        }
      },

      setRange : function(range) {
        this.startContainer = range.startContainer;
        this.endContainer = range.endContainer;
        this.startOffset = range.startOffset;
        this.endOffset = range.endOffset;
        try {
          if(!(range instanceof Range)) {
            range = document.createRange();
            range.setStart(this.startContainer, this.startOffset);
            range.setEnd(this.endContainer, this.endOffset);
          }
          this.setDisplayedRange(range);
        } catch(e) {
          this.setDisplayedRange(null);
        }
      },

      data : function () {
        return this.info();
      },

      info : function () {
        return {
          startContainerXpath : DOMExport.xpathOf(this.startContainer),
          endContainerXpath : DOMExport.xpathOf(this.endContainer),
          startOffset : this.startOffset,
          endOffset : this.endOffset
        };
      },

      loadData : function(data) {
        this.startContainer = DOMExport.elementFromXpath(data.startContainerXpath);
        this.endContainer = DOMExport.elementFromXpath(data.endContainerXpath);
        this.startOffset = data.startOffset;
        this.endOffset = data.endOffset;

        try {
          var range = document.createRange();
          range.setStart(this.startContainer, this.startOffset);
          range.setEnd(this.endContainer, this.endOffset);
          this.setDisplayedRange(range);
        } catch(e) {
          this.setDisplayedRange(null);
        }
      },

      /**
       * Remove this zone
       *
       * @method remove
       */
      remove : function() {
        Zone.removeExclusion(this.domContainer.element);
        window.removeEventListener('resize', this._onresize, false);
      }
  }
  
  var xpathOptions = {
     keepElement : function(element) {
       return !ManagedElement.isManagedElement(element);
     }
   };

  return ZoneSelection;
});