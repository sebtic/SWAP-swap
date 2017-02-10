Loader.define('ZoneTable', ['Zone', 'lib/jQuery'],
    function(Zone, $) {
  'use strict';

  var ZoneHeadTable = function(parent) {
    Zone.call(this);
    this.type = 'table-head';
    this.parent = parent;
    this.setLabelHidden(true);
    this._created();
  }
  ZoneHeadTable.prototype = Object.create(Zone.prototype);
  ZoneHeadTable.prototype.constructor = ZoneHeadTable;
  ZoneHeadTable.prototype._emit = function(what) {
    if(what == "targetChanged")
      this.parent._emit("targetChanged");
  }
  ZoneHeadTable.prototype.info = function () {
    var info = Zone.prototype.info.call(this);
    info.head = true;
    return info;
  }

  ZoneHeadTable.prototype.data = function (options) {
    var data = Zone.prototype.data.apply(this, arguments);
    data.head = true;
    return data;
  }

  var ZoneTable = function() {
    Zone.apply(this, arguments);
    this.type = 'table';
    this.filter = 'table';
    
    if(typeof this.subtype !== 'string') {
      this.subtype = 'data';
    }
    
    if(!this.subzones[0]) {
      this.subzones[0] = new ZoneHeadTable(this);
    }
    
    this._created();
  };
  ZoneTable.prototype = Object.create(Zone.prototype);
  ZoneTable.prototype.constructor = ZoneTable;

  ZoneTable.prototype.headSelect = function() {
    this.subzones[0].startSelection();
  }

  ZoneTable.prototype.headMoveTo = function() {
    Zone.prototype.moveTo.apply(this.subzones[0], arguments);
  }

  ZoneTable.prototype.headClearTarget = function() {
    this.subzones[0].target = null;
  }

  ZoneTable.prototype.info = function () {
    var info = Zone.prototype.info.call(this);
    info.subtype = this.subtype;
    return info;
  }

  ZoneTable.prototype.loadData = function(zoneData) {
    Zone.prototype.loadData.apply(this, arguments);
    this.subtype = zoneData.subtype || "data";
    if(zoneData.subzones && zoneData.subzones[0]) {
      if(!this.subzones[0])
        this.subzones[0] = new ZoneHeadTable(this);
      this.subzones[0].loadData(zoneData.subzones[0]);
    } 
  },

  ZoneTable.prototype.data = function (options) {
    var data = Zone.prototype.data.apply(this, arguments);
    data.subtype = this.subtype;
    if(!this.subzones[0].target)
      data.subzones.length = 0;
    return data;
  }

  Zone.registerType('table', ZoneTable);
  ZoneTable.load = function() {
    $('table').each(function() {
      for(var i = 0, len = Zone.zones.length; i < len; ++i) {
        if(Zone.zones[i].target == this)
          return;
      }
      var zone = new ZoneTable({hidden : true});
      zone.target = this;
    })
  }

  Zone.registerType('table', ZoneTable);

  return ZoneTable;
});