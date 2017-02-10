Loader.define('Zones', ['Zone', 'core/EventEmitter', 'core/CrossSiteCommunication'], function(Zone, EventEmitter, CrossSiteCommunication) {
  /**
   * Wrapper class to access/set properties and call functions on a zone that can be in the local window or in a remote one
   * Accessible properties are accessed via the info property of an instance of this object
   * Calling a function on a zone is done by calling the run function with the name of the function you want to call as first parameter.
   * Set a property is done by calling the 'set' function
   */
  var RemoteZone = function(remotewindow, info, zone) {
    this.remotewindow = remotewindow;
    this.info = info;
    this.zone = zone || null;
    zones.push(this);
  }
  RemoteZone.prototype = {
    /**
     * Access some public properties of a zone
     *
     * @property info
     * @type Object
     * @see Zone.info to know the available properties
     */
    /**
     * Identifier of the zone in the remotewindow context
     *
     * @property id
     * @readonly
     */
    get id() {
      return this.info.id;
    },

    /**
     * Type of the zone
     *
     * @property type
     * @readonly
     */
    get type() {
      return this.info.type;
    },

    /**
     * Information about the remote window (Provided by CrossSiteCommunication)
     *
     * @property windowInfo
     * @readonly
     */
    get windowInfo() {
      return CrossSiteCommunication.getInformations(this.remotewindow);
    },

    /**
     * Call functionName on the zone
     * Arguments after functionZone are automatically passed to the remoteZone function call
     * Be aware that those arguments are often serialized for the transmission.
     *
     * @method run
     * @param functionName
     * @param [args...]
     */
    run : function(functionName) {
      if(this.remotewindow == window) {
        if(typeof this.zone[functionName] == "function")
          this.zone[functionName].apply(this.zone, Array.prototype.slice.call(arguments, 1));
        else
          console.warn("Unable to call " + functionName + " for zone :", this.zone);
      } else {
        CrossSiteCommunication.emit(this.remotewindow, "zoneFunctionCall", this.id, null, functionName, Array.prototype.slice.call(arguments, 1));
      }
    },
    /**
     * Set propertyName to value on the zone
     *
     * @method set
     * @param propertyName
     * @param value
     */
    set : function(propertyName, value) {
      if(this.remotewindow == window) {
        this.zone[propertyName] = value;
      } else {
        CrossSiteCommunication.emit(this.remotewindow, "zoneSetProperty", this.id, null, propertyName, value);
      }
    },

    /**
     * Call functionName on the sub zone Arguments after functionZone are
     * automatically passed to the remoteZone function call Be aware that those
     * arguments are often serialized for the transmission.
     *
     * @method run
     * @param idx
     *            index of the sub zone. If not a number, run is used in place.
     *            So you can call this function in a more generic way and do
     *            call to non subzone function
     * @param functionName
     * @param [args...]
     */
    runSub : function(idx, functionName) {
      if(typeof idx !== "number") {
        idx = null;
      }

      if(this.remotewindow == window) {
        if(idx === null) {
          if(typeof this.zone[functionName] == "function")
            this.zone[functionName].apply(this.zone, Array.prototype.slice.call(arguments, 2));
          else
            console.warn("Unable to call " + functionName + " for zone :", this.zone);
        } else {
          var zone = this.zone && this.zone.subzones[idx];
          if(!zone)
            console.warn("Unable to reach subzone " + idx + " of zone :", this.zone);
          else if(typeof zone[functionName] != "function")
            console.warn("Unable to call " + functionName + " for zone :", zone);
          else
            zone[functionName].apply(zone, Array.prototype.slice.call(arguments, 2));
        }
      } else {
        CrossSiteCommunication.emit(this.remotewindow, "zoneFunctionCall", this.id, idx, functionName, Array.prototype.slice.call(arguments, 2));
      }
    },

    /**
     * Set propertyName to value on the sub zone
     *
     * @method set
     * @param idx
     *            index of the sub zone. If not a number, set is used in place.
     *            So you can call this function in a more generic way and do
     *            call to non subzone function
     * @param propertyName
     * @param value
     */
    setSub : function(idx, propertyName, value) {
      if(typeof idx !== "number") {
        this.set(propertyName, value);
      } else if(this.remotewindow == window) {
        var zone = this.zone && this.zone.subzones[idx];
        if(!zone)
          console.warn("Unable to reach subzone " + idx + " of zone :", this.zone);
        else
          zone[propertyName] = value;
      } else {
        CrossSiteCommunication.emit(this.remotewindow, "zoneSetProperty", this.id, idx, propertyName, value);
      }
    },

    sameAs : function(remotezone) {
      return remotezone instanceof RemoteZone && this.id == remotezone.id && this.remotewindow == remotezone.remotewindow;
    }
  }

// !!!!!!!!!!!!! Private variables !!!!!!!!!!!!!

  /**
   * Remote zones manager
   *
   * Provide a simple way to manage zones without having to take care of CrossSite Communications
   *
   * events
   *  - saved
   *  - removed
   *  - clicked
   *  - selectionStarted
   *  - selectionEnded
   *  - targetChanged
   */
  var Zones = function() {
  }
  EventEmitter.implementIn(Zones);

  /**
   * Zone used to make zone selection
   *
   * @property selectionZone
   * @type Zone
   * @private
   * @static
   */
  var selectionZone = null;

  /**
   * Current Zone managed by the interface.
   * null if no zone is currently managed.
   *
   * @property currentZone
   * @type RemoteZone
   * @private
   * @static
   */
  var currentZone = null;

  /**
   * True if a zone selection is in progress, false otherwise
   *
   * @property selectionInProgress
   * @type Boolean
   * @private
   * @static
   */
  var selectionInProgress = false;

  /**
   * List of remote zones under management
   */
  var zones = [];

// !!!!!!!!!!!!! Private functions !!!!!!!!!!!!!

  /**
   *
   */
  function getLocalZone(remotewindow, zoneinfo, zone)
  {
    for(var index = 0; index < zones.length; ++index) {
      if(zones[index].remotewindow == remotewindow && zones[index].id == zoneinfo.id) {
        zones[index].info = zoneinfo;
        return zones[index];
      }
    }

    return new RemoteZone(remotewindow, zoneinfo, zone);
  }

  /**
   *
   */
  function removeLocalZone(remotezone)
  {
    for(var index = 0; index < zones.length; ++index) {
      if(zones[index].remotewindow == remotezone.remotewindow && zones[index].id == remotezone.id) {
        zones.splice(index, 1);
        break;
      }
    }
  }

  function onTargetChanged(remotezone) {
    if(!selectionInProgress)
      return;
    if(currentZone != null) {
      if(remotezone.sameAs(currentZone))
        return;
      currentZone.set('target', null);
    }
    currentZone = remotezone;
  }

  function onSelectionEnded(remotezone) {
    if(!selectionInProgress)
      return;
    onTargetChanged(remotezone);
    Zones.stopSelection();
    if(!remotezone._newEventCalled) {
      remotezone._newEventCalled = true;
      Zones.emit('new', remotezone);
    }
    Zones.emit('focus', remotezone);
  }

// !!!!!!!!!!!!! Public API !!!!!!!!!!!!!

  Zones.getRemoteZone = function(remotezoneid, remotezonewindow) {
    for(var index = 0; index < zones.length; ++index) {
      if(zones[index].remotewindow == remotezonewindow && zones[index].id == remotezoneid) {
        return zones[index];
      }
    }
    return null;
  }

  Zones.runForAll = function(type, functionName, args) {
    args = Array.prototype.slice.call(arguments, 1);
    var list = Zones.list(type);
    for(var i = 0; i < list.length; ++i) {
      list[i].run.apply(list[i], args);
    }
  }

  Zones.run = function(functionName, type) {
    var ZoneClass = Zone.findType(type);
    if(typeof ZoneClass[functionName] == "function") {
      var args = Array.prototype.slice.call(arguments, 2);
      ZoneClass[functionName].apply(ZoneClass, args);
      CrossSiteCommunication.broadcast("zonesFunctionCall", functionName, args);
    }
  }

  Zones.startSelection = function(type) {
    type = type || null;
    selectionInProgress = true;
    if(selectionZone && selectionZone.type != type) {
      selectionZone.remove();
    }
    if(selectionZone == null || selectionZone.saved || selectionZone.removed) {
      selectionZone = Zone.create(type, {emitNew : false});
    }
    currentZone = null;
    selectionZone.label = "";
    selectionZone.startSelection();
    CrossSiteCommunication.broadcast("zoneStartSelection", type);
    Zones.on("targetChanged", onTargetChanged);
    Zones.on("selectionEnded", onSelectionEnded);
  }


  Zones.stopSelection = function() {
    selectionInProgress = false;
    if(selectionZone != null)
      selectionZone.stopSelection();
    CrossSiteCommunication.broadcast("zoneStopSelection");
    Zones.off("targetChanged", onTargetChanged);
    Zones.off("selectionEnded", onSelectionEnded);
  }

  Zones.toggleSelection = function(type) {
    if(selectionInProgress)
      Zones.stopSelection();
    else
      Zones.startSelection(type);
  }

  /**
   * List all available zones
   *
   * @method list
   * @public
   * @static
   * @param {String}
   *            [type] If defined, only return zones that are of type 'type'
   * @return {[RemoteZone]} A list of RemoteZone
   */
  Zones.list = function(type) {
    var list = [];
    for(var index = 0; index < zones.length; ++index) {
      if(typeof type == "undefined" || zones[index].info.type == type) {
        list.push(zones[index]);
      }
    }
    return list;
  }

  Zones.sort = function(list) {
    var windows = [window];

    function windowIdx(win) {
      var idx = windows.indexOf(win)
      if(idx == -1) {
        return windows.push(win) -1  ;
      }
      return idx;
    }

    list.sort(function(a, b) {
      return windowIdx(a.remotewindow) - windowIdx(b.remotewindow);
    });

    return list;
  }


// !!!!!!!!!!!!! Setup !!!!!!!!!!!!!
  CrossSiteCommunication.on("register", function(window) {
    if(selectionInProgress) {
      CrossSiteCommunication.emit(window, "zoneStartSelection");
    }
  });

  function onZoneEvent(event, remotezone) {
    if(event == "new") {
      remotezone._newEventCalled = true;
    }
    Zones.emit(event, remotezone);
    if(event == "removed") {
      removeLocalZone(remotezone);
    }
  }

  Zone.onEvent(function(event, zone) {
    var remotezone = getLocalZone(window, zone.info(), zone);
    onZoneEvent(event, remotezone);
  });

  CrossSiteCommunication.on("zoneEvent", function(window, zoneinfo, event) {
    var remotezone = getLocalZone(window, zoneinfo);
    onZoneEvent(event, remotezone);
  });

  return Zones;
});