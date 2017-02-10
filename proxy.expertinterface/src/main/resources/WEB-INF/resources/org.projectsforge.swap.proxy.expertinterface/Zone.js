Loader.define('Zone', ['core/ManagedElement', 'lib/jQuery', 'core/ProxyCommunication', 'core/EventEmitter', 'core/DOMExport', 'ZoneSelection'],
    function(ManagedElement, $, ProxyCommunication, EventEmitter, DOMExport, ZoneSelection) {
  var excluded = [];
  var types = {};
  var instanceCounter = 0;

  /**
   * Provide zone selection and loading capabilities
   * @class Zone
   */

  /**
   * Events :
   *  - selectionStart
   *  - selectionEnd
   *  - focus
   *  - remove
   */
  var Zone = function(options, zoneData) {
    options = options || {}
    var self = this;

    this.dom = {
        zone : ManagedElement('div', 'zone'),
        label : ManagedElement('span', 'zone-overlay-label')
    };

    /**
     * Id of this zone in the database.
     * Set to null by default.
     *
     * @property id
     * @type {String}
     * @public
     */
    this.id = null;

    /**
     * Current window instance id for fast zone comparison
     *
     * @property idInstance
     * @type {Number}
     * @public
     * @readOnly
     */
    this.idInstance = ++instanceCounter;

    /**
     * Type of the zone
     *
     * @property type
     * @type {String}
     * @public
     */
    this.type = null;

    /**
     * Filter of the zone for target element restrictions
     * A filter is css like rule declaration.
     *
     * @see jQuery.is()
     * @property filter
     * @type {String}
     * @public
     */
    this.filter = null;

    /**
     * Hold the current label value.
     * Used by label get & set
     * Maintained to always be a valid string
     *
     * @property _label
     * @private
     * @type {String}
     */
    this._label = options.label || "";

    /**
     * Hold the current target of this zone.
     * Used by target get & set.
     * null is no valid target is set.
     *
     * @property _target
     * @private
     * @type {DOMElement}
     */
    this._target = options.target || null;

    /**
     * Current offset of the zone overlay for fast updating
     *
     * @see _replace
     * @property _offset
     * @private
     * @type {Object}
     */
    this._offset = {left:null, top:null, width:null, height:null};

    /**
     * Offset to apply to each side of the overlay for better placement control.
     * It contains 4 properties : left, top, width, height
     *
     * @property cssOffset
     * @public
     * @type {Object}
     */
    this.cssOffset = {left:-2, top:-2, width:2*2-4, height:2*2-4};

    /**
     * Hold the current save state of this zone.
     *
     * @property _saved
     * @private
     * @type {Boolean}
     */
    this._saved = false;

    /**
     * In page selection linked to this zone
     * The current implementation allow only one selection to be linked to a zone
     *
     * @property ref
     * @public
     * @type ZoneSelection
     */
    this.ref = new ZoneSelection(this);

    /**
     * List of subzone of this zone.
     * A subzone MUST at least provide two methods -
     * data() - info() Those methods are required to construct the objects
     * returned by the data() and the info() methods of this Zone
     *
     * @property subzones
     * @public
     * @type Array
     */
    this.subzones = [];

    /**
     * Simple boolean to determine if a selection is in progress
     *
     * @property _selectionInProgress
     * @private
     * @type Boolean
     */
    this._selectionInProgress = false;

    // Prevent this zone from being selected
    Zone.exclude(this.container);
    Zone.exclude(this.ref.domContainer.element);

    // Add this zone to the list
    Zone.zones.push(this);

    // Register events

    this.dom.zone.onElement("click", function() {
      self.focus();
    });

    // Ensure current target placement when the browser is resized
    $(window).on('resize', this._onresize = function() {
      self._replace();
    });

    if(options.hidden) {
      this.hide();
    }

    if(zoneData) {
      // Data has been provided at construction time, use it
      this._saved = true;
      this.loadData(zoneData);
    }

    // Subclasse are responsible of calling this._created() at they constructor ends
    if(this.constructor === Zone && options.emitNew !== false) {
      // see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/constructor
      this._created();
    }
  }

  EventEmitter.implementIn(Zone);

  /**
   * List of all created zone
   * Most of all zone implementation maintains themselves in the list
   * Normally you should only use this in readonly ways.
   *
   * @property zones
   * @public
   * @type Array<Zone>
   */
  Zone.zones = [];

  /**
   * Register a zone class with a given type
   *
   * @method registerType
   * @static
   * @public
   * @param {String} type
   * @param {ZoneClass} typeClass
   */
  Zone.registerType = function(type, typeClass) {
    if(typeof typeClass !== "function")
      throw "Typeof typeClass is not a function"
    types[type] = typeClass;
  }

  /**
   * Find the zone class of the given registered type
   *
   * @method findType
   * @static
   * @public
   * @param {String} type
   * @return {ZoneClass} the found ZoneClass or Zone if type resolution failed
   */
  Zone.findType = function(type) {
    if(type) {
      if(typeof types[type] === "function") {
        return types[type];
      }
      console.error("Unable to find zone of type " + type+", fallback to default zone", types);
    }
    return Zone;
  }

  /**
   * Create a new zone from the given zone type
   * Zone types are registered by the registerType method
   * @method create
   * @static
   * @public
   * @param {String} type
   * @return {Zone}
   */
  Zone.create = function(type, option) {
    return new (Zone.findType(type))(option);
  }

  /**
   * Load zones from proxy for the current page
   *
   * @method load
   * @static
   * @return {Deferred}
   */
  Zone.load = function() {
    var deferred = ProxyCommunication.post('zones/listactives', {
      url : document.location.href
    }).done(function(zones) {
      for(var i = 0; i < zones.length; i++) {
        new (Zone.findType(zones[i].type))({hidden : true}, zones[i]);
      }
    }).always(function() {
      for(var i in types) {
        if(typeof types[i].load === "function") {
          types[i].load();
        }
      }
    });

    return deferred;
  }

  /**
   * Exclude some part of the page for better zone selection
   * @method exclude
   * @public
   * @static
   * @param {DOMElement} element Element to exclude
   */
  Zone.exclude = function(element) {
    excluded.push(element);
  };

  /**
   * Remove a previously excluded element from exclusion
   * @method removeExclusion
   * @public
   * @static
   * @param {DOMElement} element Element to exclude
   */
  Zone.removeExclusion = function(element) {
    var index = excluded.indexOf(element);
    if(index > -1) {
      excluded.splice(index, 1);
    }
  };

  Zone.prototype = {
      constructor : Zone,

      /**
       * True if this zone is hidden, false otherwise
       *
       * @property hidden
       * @readonly
       * @type Boolean
       */
      get hidden() {
        return this._hidden;
      },
      /**
       * True if this zone is saved, false otherwise
       *
       * @property saved
       * @readonly
       * @type Boolean
       */
      get saved() {
        return this._saved;
      },

      /**
       * True if this zone has been removed, false otherwise
       *
       * @property removed
       * @readonly
       * @type Boolean
       */
      get removed() {
        return !!this._removed;
      },

      /**
       * The container of this zone
       *
       * @property container
       * @readonly
       * @type DOMElement
       */
      get container() {
        return this.dom.zone.element;
      },

      /**
       * Returns true if this zone can be moved to parent element, false otherwise
       *
       * @property hasParent
       * @readonly
       * @type Boolean
       */
      get hasParent() {
        return this.parentElement != null;
      },

      /**
       * Return a valid parent element of this zone, or null if none can be found
       *
       * @property parentElement
       * @type DOMElement
       * @readonly
       */
      get parentElement() {
        if(this.target && this.isValidZoneTarget(this.target.parentNode)) {
          return this.target.parentNode;
        }
        return null;
      },

      /**is.hide();
       * Returns true if this zone can be moved to next sibling element, false otherwise
       *
       * @property hasNext
       * @type Boolean
       * @readonly
       */
      get hasNext() {
        return this.nextElement != null;
      },

      /**
       * Return a valid next sibling element of this zone, or null if none can be found
       *
       * @property parentElement
       * @type DOMElement
       * @readonly
       */
      get nextElement() {
        var node = this.target && this.target.nextElementSibling;
        while(node) {
          if(this.isValidZoneTarget(node)) {
            return node;
          }
          node = node.nextElementSibling;
        }
        return null;
      },

      /**
       * Returns true if this zone can be moved to previous sibling element, false otherwise
       *
       * @method hasPrevious
       * @type Boolean
       * @readonly
       */
      get hasPrevious() {
        return this.previousElement != null;
      },

      /**
       * Return a valid previous sibling element of this zone, or null if none can be found
       *
       * @property parentElement
       * @type DOMElement
       * @readonly
       */
      get previousElement() {
        var node = this.target && this.target.previousElementSibling;
        while(node) {
          if(this.isValidZoneTarget(node)) {
            return node;
          }
          node = node.previousElementSibling;
        }
        return null;
      },

      /**
       * Returns true if this zone can be moved to first child element, false otherwise
       *
       * @property hasFirstChild
       * @type Boolean
       * @readonly
       */
      get hasFirstChild() {
        return this.firstChildElement != null;
      },

      /**
       * Return a valid first child element of this zone, or null if none can be found
       *
       * @property parentElement
       * @type DOMElement
       * @readonly
       */
      get firstChildElement() {
        var node = this.target && this.target.firstElementChild;
        while(node) {
          if(this.isValidZoneTarget(node)) {
            return node;
          }
          node = node.firstElementChild;
        }
        return null;
      },

      /**
       * The name of this zone
       *
       * @property label
       * @type String
       */
      get label() {
        return this._label;
      },
      set label(value) {
        value = ''+value;

        if(value === this._label)
          return;

        this._label = value;

        if(!this._hideLabel) {
          if(value !== '') {
            if(!this.dom.label.parentNode)
              this.dom.label.appendTo(this.dom.zone);
          } else if(this.dom.label.parentNode === this.dom.zone) {
            this.dom.label.remove();
          }
          this.dom.label.textContent = value;
        }

        this._emit("labelChanged");
      },

      /**
       * Change the label visibility of this zone
       *
       * @method setLabelHidden
       * @public
       * @param {Boolean} bHide
       */
      setLabelHidden : function(bHide) {
        bHide = !!bHide;
        if(bHide !== this._hideLabel) {
          if(bHide) {
            this.dom.label.remove();
          } else {
            this.dom.label.appendTo(this.dom.zone);
            this.dom.label.textContent = this._label;
          }

          this._hideLabel = bHide;
        }
      },

      /**
       * Hold the current xpath of the target element of this zone
       *
       * @property xpath
       * @type String
       */
      get xpath() {
        return DOMExport.xpathOf(this._target);
      },
      set xpath(value) {
        this.target = DOMExport.elementFromXpath(value);
      },

      /**
       * Hold the current target element of this zone
       *
       * @property target
       * @type DOMElement
       */
      get target() {
        return this._target;
      },
      set target(element) {
        if(!this.isValidZoneTarget(element)) {
          element = null;
        }
        if(this._target === element)
          return;
        this._target = element || null;
        if(!element) {
          this.dom.zone.css('display', 'none');
          this._emit("targetChanged");
          return;
        }

        if(!this.hidden)
          this.dom.zone.css('display', 'block'); // Show
        this._replace();

        this._emit("targetChanged");
      },

      /**
       * Check if the given element can be a valid target for the zone
       *
       * @method isValidZoneTarget
       * @public
       * @param {DOMElement} element to check
       * @return true if the element can be a valid zone, false otherwise
       */
      isValidZoneTarget : function(element)
      {
        return (element
          && element.nodeName.toLowerCase() !== "html"
          && element.nodeName.toLowerCase() !== "body"
          && contains(excluded, element) === null
          && (!this.filter || $(element).is(this.filter))
        );
      },

      /**
       * Called by the constructor once creation is done
       *
       * @method _created
       * @protected
       */
      _created : function() {
        this._emit('new');
      },


      /**
       * Make sure this zone is displayed at the right place
       *
       * @method _replace
       * @protected
       */
      _replace : function() {
        if(this.target != null && !this.hidden) {
          var element = this.target;

          var offsetParent = element.offsetParent;
          while(offsetParent && offsetParent.nodeName.toLowerCase() !== "html" && window.getComputedStyle(offsetParent)['position'] === 'static') {
            offsetParent = offsetParent.offsetParent;
          }

          if(!offsetParent || offsetParent.nodeName.toLowerCase() === "html")
            offsetParent = document.body;

          this.dom.zone.appendTo(offsetParent);

          var offset = {
            top : element.offsetTop + this.cssOffset.top,
            left : element.offsetLeft + this.cssOffset.left,
            width : element.offsetWidth + this.cssOffset.width,
            height : element.offsetHeight + this.cssOffset.height
          }

          element = element.offsetParent;
          while(element && element != offsetParent && offsetParent.nodeName.toLowerCase() !== "html") {
            offset.top += element.offsetTop;
            offset.left += element.offsetLeft;
            element = element.offsetParent;
          }

          if(this._offset.left !== offset.left)
            this.dom.zone.css('left', offset.left+'px');

          if(this._offset.top !== offset.top)
            this.dom.zone.css('top', offset.top+'px');

          if(this._offset.width !== offset.width)
            this.dom.zone.css('width', offset.width+'px');

          if(this._offset.height !== offset.height)
            this.dom.zone.css('height', offset.height+'px');

          this._offset = offset;
        }
      },

      /**
       * Give the focus to this zone
       *
       * @method focus
       * @public
       */
      focus : function() {
        this._emit('focus');
      },

      /**
       * Highlight this zone
       *
       * @method highlight
       * @public
       * @param {Boolean}
       *            [keep] If true, the highlight is permanent, if false the
       *            highlight is removed if present, otherwise highlight for 1s
       */
      highlight : function(keep) {
        var self = this;

        if(this.ref) {
          this.ref.highlight(keep);
        }
          
        if(this._highlight_timeout) {
          window.clearTimeout(this._highlight_timeout);
          delete this._highlight_timeout;
        }
        
        if(keep === false) {
          this.dom.zone.removeClassState('highlight');
          return;
        }

        this.dom.zone.addClassState('highlight');

        if(keep === true) {
          return;
        }

        this._highlight_timeout = window.setTimeout(function() {
          delete self._highlight_timeout;
          self.dom.zone.removeClassState('highlight');
        }, 1000);
      },

      /**
       * Temporary hide other zones.
       *
       * @method tmpHideOthers
       * @public
       * @param {Boolean} bHide If true, temporary hide other zones, otherwise restore other zones to previous state
       */
      tmpHideOthers : function(bHide) {
        var self = this;
        Zone.zones.forEach(function(zone) {
          if(zone !== self) {
            if(bHide) {
              zone._tmpHiddenPreviousState = zone._tmpHiddenPreviousState || zone.hidden;
              zone.hide();
            } else {
              if(zone._tmpHiddenPreviousState === false)
                zone.show();
              delete zone._tmpHiddenPreviousState;
            }
          }
        });
      },

      /**
       * Move the target of this zone to the given one if the it's a valid zone target
       *
       * @method moveToParent
       * @param {DOMElement} newTarget
       */
      moveTo : function(newTarget) {
        if(typeof newTarget === "string") {
          newTarget = this[newTarget];
        }
        if(this.isValidZoneTarget(newTarget))
          this.target = newTarget;
      },

      /**
       * Hide this zone
       *
       * @method hide
       */
      hide : function() {
        if(this._hidden)
          return;

        this.dom.zone.css('display', 'none');
        this._hidden = true;
        this.subzones.forEach(function(subzone) {
          subzone.hide();
        });
        this.ref.hide();
      },

      /**
       * Show this zone
       *
       * @method show
       */
      show : function() {
        if(!this._hidden)
          return;

        if(this.target != null)
          this.dom.zone.css('display', 'block');
        this._hidden = false;
        if(this.target != null)
          this._replace();
        this.subzones.forEach(function(subzone) {
          subzone.show();
        });
        this.ref.show();
      },

      /**
       * Toggle the selection of the zone
       * @method toggleSelect
       * @public
       * @return {Boolean} True if selection has been turned on, false otherwise
       */
      toggleSelection : function() {
        if(!this.startSelection()) {
          this.stopSelection();
          return false;
        }
        return true;
      },

      /**
       * @property selectionInProgress
       * @public
       * @readOnly
       * Test if a selection is in progress
       */
      get selectionInProgress() {
        return this._selectionInProgress;
      },

      /**
       * Cancel selection if one is in progress
       *
       * @method stopSelection
       * @return {Boolean} True if selection has been cancelled, false otherwise
       */
      stopSelection : function() {
        if("function" === typeof this._stopSelect) {
          this._stopSelect();
          return true;
        }
        return false;
      },

      /**
       * Start selection if no one is already in progress
       *
       * @methd startSelection
       * @return {Boolean} True if selection has begun, false otherwise
       */
      startSelection : function() {
        if(this._selectionInProgress) {
          return false;
        }

        var lastTargetCSS = null;
        var lastTarget = null;
        var self = this;

        this.target = null;
        this._selectionInProgress = true;
        this.show();

        // Called when selection stops by the onClick method just below or stopSelection of this zone
        this._stopSelect = function() {
          $(window).off("mousemove", onMouseMove).off("mousedown", onClick);
          if(lastTarget != null && lastTargetCSS != null) {
            lastTarget.style.setProperty('cursor', lastTargetCSS.value, lastTargetCSS.priority);
            lastTargetCSS = null;
          }
          self._selectionInProgress = false;
          delete self._stopSelect;
          this._emit('selectionEnded');
        }

        // When the mouse move
        function onMouseMove(event) {
          var target = event.target;
          function findTarget() {
            var container = contains(excluded, target);
            if(container != null) {
              var display = { value : container.style.getPropertyValue("display"), priority : container.style.getPropertyPriority("display") };
              container.style.setProperty("display", "none", "important");
              target = document.elementFromPoint(event.clientX,event.clientY);
              findTarget();
              container.style.setProperty('display', display.value, display.priority);
            }
          }
          findTarget();
          if(self.target != target) {
            if(lastTarget != null && lastTargetCSS != null) {
              lastTarget.style.setProperty('cursor', lastTargetCSS.value, lastTargetCSS.priority);
              lastTargetCSS = null;
            }
            if(target != null) {
              lastTargetCSS = { value : target.style.getPropertyValue("cursor"), priority : target.style.getPropertyPriority("cursor") };
              target.style.setProperty("cursor", "pointer", "important");
            }
            lastTarget = target;
            self.target = target;
          }
          event.preventDefault();
        }

        // When the mouse button is clicked
        function onClick(event) {
          onMouseMove(event);
          self._stopSelect();
        }

        $(window).on("mousemove", onMouseMove).on("mousedown", onClick);

        this._emit('selectionStarted');

        return true;
      },

      /**
       * Start the live text selection of this zone reference
       *
       * @method selectRef
       * @public
       */
      selectRef : function() {
        this.ref.startSelection();
      },

      /**
       * Info object that describe this zone
       * The returned informations are useful
       * for informing the user about the current state of this zone
       *
       * @method info
       * @return {Object}
       */
      info : function () {
        var info = this._info();
        
        info.hasPrevious = this.hasPrevious;
        info.hasNext = this.hasNext;
        info.hasParent = this.hasParent;
        info.hasFirstChild = this.hasFirstChild;

        return info;
      },


      /**
       * Default info object that describe this zone
       *
       * @method _info
       * @protected
       * @return {Object}
       */
      _info : function () {
        var info = {
          id : this.idInstance,
          type : this.type,
          label : this.label,
          saved : this.saved,
          anonymous : this.anonymous,
          hasTarget : this.target !== null,
          allowSave : this.saved || (this.target !== null && this.label.length > 0),
          subzones : [],
          ref : this.ref.info()
        }
        
        this.subzones.forEach(function(subzone) {
          info.subzones.push(subzone.info());
        });

        return info;
      },

      /**
       * Load data previously made by the data() function.
       * This should restore this zone in the previously saved state.
       *
       * @method loadData
       * @public
       * @param {Object} zoneData A value previously returned by data()
       * @see data()
       */
      loadData : function(zoneData) {
        this.id = zoneData.id;
        this.label = zoneData.label;
        this.xpath = zoneData.xpath;
        this.type = zoneData.type;
        this.anonymous = zoneData.anonymous;
        this.timeRemoved = zoneData.timeRemoved ? new Date(zoneData.timeRemoved) : null;
        this.timeCreated = zoneData.timeCreated ? new Date(zoneData.timeCreated) : null;
        if(zoneData.ref) {
          this.ref.loadData(zoneData.ref);
        }
        
        // Zone subtype are responsible of loading subzones
      },

      /**
       * Data object that describe this zone.
       * The returned information can be loaded back via loadData
       * to recreate a zone in the same state after a page change
       *
       * @see loadData
       * @method data
       * @return {Object}
       */
      data : function (options) {
        var content = "";
        var anonymiseContent = !!options.anonymiseContent;
        var exportOptions = {
          keepElement : function(element) {
            return !ManagedElement.isManagedElement(element);
          },
          anonymiseAttribute : function() { 
            return anonymiseContent; 
          },
          anonymiseText : function() { 
            return anonymiseContent; 
          }
        };
        
        content = DOMExport.export(this.target, exportOptions);
        
        var data = {
          id : this.id,
          content : content,
          anonymous : anonymiseContent,
          url : document.location.href,
          label : this.label,
          xpath : this.xpath,
          subzones : [],
          ref : this.ref.data()
        };

        this.subzones.forEach(function(subzone) {
          data.subzones.push(subzone.data(options));
        })

        return data;
      },

      /**
       * Save this zone
       *
       * @method save
       */
      save : function(options) {
        var self = this;
        if(!this._saving && this.target !== null && this.label.length > 0) {
          this._saving = true;
          var data = this.data(options || {});
          // Export the page only once
          var anonymisePage = !!options.anonymisePage;
          var page = DOMExport.export(document.documentElement, {
            keepElement : function(element) {
              return !ManagedElement.isManagedElement(element);
            },
            anonymiseAttribute : function() { 
              return anonymisePage; 
            },
            anonymiseText : function() { 
              return anonymisePage; 
            }
          });
          data.page = {
            url : document.location.href,
            html : page,
            anonymous : anonymisePage
          };
          ProxyCommunication.post('zones/set', {
            zone : JSON.stringify(data), 
            type: (this.type || "default") 
          })
          .done(function(json) {
            self._saving = false;
            
            if(json && typeof json.zoneid === "number") {
              self.id = json.zoneid;
              self._saved = true;
              self._emit('saved');
            } else {
              self._emit('saveFailed');
            }
          })
          .fail(function() {
            self._saving = false;
            self._emit('saveFailed');
          });
        } else {
          self._emit('saveFailed');
        }
      },

      /**
       * Remove this zone from Zone.zones list
       *
       * @method removeFromGlobalList
       */
      removeFromGlobalList : function() {
        var index = Zone.zones.indexOf(this);
        if(index > -1) {
          Zone.zones.splice(index, 1);
        }
      },

      /**
       * Remove this zone
       *
       * @method remove
       */
      remove : function() {
        Zone.removeExclusion(this.container);
        this.removeFromGlobalList();

        $(this.container).remove();
        if(this._saved) {
          ProxyCommunication.post('zones/remove', {
            zoneid : this.id
          }, function(json) {
            console.log("Zone removed", json);
          });
          this._saved = false;
        }
        this._target = null;
        this._removed = true;
        $(window).off('resize', this._onresize);
        this._emit('removed');
      },

      /**
       * Emit an event
       * @method _emit
       * @protected
       */
      _emit : function(what) {
        Zone.emit(what, this);
      }
  }

  /**
   * Test if the element is contained by one of the containers
   * @method contains
   * @private
   * @property {[DOMElement]} List of DOMElement to treat as possible containers
   * @property {DOMElement} Element to inspect
   * @return {DOMElement} The container that contains element, or null if none has been found
   */
  function contains(containers, element) {
    var idx;
    while(element) {
      idx = containers.indexOf(element);
      if(idx != -1) {
        return containers[idx];
      }
      element = element.parentNode;
    }
    return null;
  }

  return Zone;
});