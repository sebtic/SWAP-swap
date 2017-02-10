Loader.define('ZoneInterfaces', [], function() {

  var ZoneInterfaces = {
    zoneinterfaces : {},
    defaultInterface : null,
    /**
     * Current Zone managed by the interface. null if no zone is currently
     * managed.
     *
     * @property currentZone
     * @type Zone
     * @public
     * @static
     */
    currentZone : null,
    container : null,
    anonymisePage : false,
    register : function(zoneinterface) {
      this.zoneinterfaces[zoneinterface.type] = zoneinterface;
    },
    getFor : function(remotezone) {
      if (remotezone.info.type in this.zoneinterfaces)
        return this.zoneinterfaces[remotezone.info.type];
      if (!this.defaultInterface)
        throw "No interface found for " + remotezone.info.type
            + " and no default interface has been setup";
      return this.defaultInterface;
    },
    extendsInterface : function(zoneinterface, basezoneinterface) {
      for ( var i in basezoneinterface) {
        if (!(i in zoneinterface)) {
          zoneinterface[i] = basezoneinterface[i];
        }
      }
    }
  }

  return ZoneInterfaces;
})