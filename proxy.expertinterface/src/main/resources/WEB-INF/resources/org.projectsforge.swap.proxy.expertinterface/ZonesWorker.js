Loader.define('ZonesWorker', ['Zone', 'core/CrossSiteCommunication'], function(Zone, CrossSiteCommunication) {
  /**
   * List of local zones under management
   */
  var zones = [];

  /**
   * Zone used to make zone selection
   *
   * @property selectionZone
   * @type Zone
   * @private
   * @static
   */
  var selectionZone = null;

  function findLocalZone(zoneid) {
    for(var index = 0; index < zones.length; ++index) {
      if(zones[index].idInstance == zoneid) {
        return zones[index];
      }
    }

    return null;
  }

  CrossSiteCommunication.on("zoneFunctionCall", function(window, zoneid, idx, functionName, args) {
    var zone = findLocalZone(zoneid);
    if(zone == null) {
      console.warn("Unable to find zone " + zoneid);
    } else if(typeof functionName !== "string") {
      console.warn("functionName isn't a string : ", functionName);
    } else if(idx === null) {
      // Simple zone call
      if(typeof zone[functionName] != "function")
        console.warn("Unable to call " + functionName + " for zone :", zone);
      else
        zone[functionName].apply(zone, args);
    } else {
      // Subzone call
      if(!zone.subzones[idx])
        console.warn("Unable to reach subzone " + idx + " of zone :", zone);
      else if(typeof zone.subzones[idx][functionName] != "function")
        console.warn("Unable to call " + functionName + " for subzone :", zone.subzones[idx]);
      else
        zone.subzones[idx][functionName].apply(zone.subzones[idx], args);
    }
  });

  CrossSiteCommunication.on("zoneSetProperty", function(window, zoneid, idx, propertyName, value) {
    var zone = findLocalZone(zoneid);
    if(zone == null) {
      console.warn("Unable to find zone " + zoneid);
    } else if(idx === null) {
      // Simple zone set
      zone[propertyName] = value;
    }else {
      // Subzone set
      if(!zone.subzones[idx])
        console.warn("Unable to reach subzone " + idx + " of zone :", zone);
      else
        zone.subzones[idx][propertyName] = value;
    }
  });

  CrossSiteCommunication.on("zonesFunctionCall", function(window, functionName, args) {
    if(typeof functionName == "string" && typeof Zone[functionName] == "function") {
      Zone[functionName].apply(Zone, args);
    }
  });

  Zone.onEvent(function(event, zone) {
    var info = zone.info();
    if(findLocalZone(info.id) == null)
      zones.push(zone);
    CrossSiteCommunication.emit(window.top, "zoneEvent", info, event);
  });

  CrossSiteCommunication.on("zoneStartSelection", function(window, type) {
    if(selectionZone && selectionZone.type != type) {
      selectionZone.remove();
    }
    if(selectionZone == null || selectionZone.saved || selectionZone.removed) {
      selectionZone = Zone.create(type, {emitNew : false});
    }
    selectionZone.label = "";
    selectionZone.startSelection();
  });

  CrossSiteCommunication.on("zoneStopSelection", function(window) {
    if(selectionZone != null)
      selectionZone.stopSelection();
  });

});