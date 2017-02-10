Loader.define('ZoneForm', ['Zone', 'lib/jQuery', 'core/DOMExport'],
    function(Zone, $, DOMExport) {
  'use strict';

  var ZoneFormInput = function(parent, element) {
    Zone.call(this, {hidden : true});
    this.type = 'form-input';
    this.filter = 'input, button, textarea';
    this.setLabelHidden(true);
    this.parent = parent;
    this.target = element;
    this._created();
  }
  ZoneFormInput.prototype = Object.create(Zone.prototype);
  ZoneFormInput.prototype.constructor = ZoneFormInput;
  ZoneFormInput.prototype._emit = function(what) {
    if(what === "focus")
      this.parent.focus();
  }

  ZoneFormInput.prototype.info = function () {
    var info = Zone.prototype.info.call(this);
    info.head = true;
    return info;
  }

  ZoneFormInput.prototype.data = function (options) {
    var data = Zone.prototype.data.apply(this, arguments);
    data.head = true;
    return data;
  }

  var ZoneForm = function() {
    Zone.apply(this, arguments);
    this.type = 'form';
    this.filter = 'form';
    if(this.target !== null && this.target.elements && this.target.elements.length) {
      for(var i = 0, len = this.target.elements.length; i < len; ++i) {
        this.getSubZone(this.target.elements[i]);
      }
    }
    this._created();
  };
  ZoneForm.prototype = Object.create(Zone.prototype);
  ZoneForm.prototype.constructor = ZoneForm;
  
  ZoneForm.prototype.getSubZone = function(target) {
    for(var i = 0, len = this.subzones.length; i < len; ++i) {
      if(this.subzones[i].target === target)
        return this.subzones[i];
    }
    var subzone = new ZoneFormInput(this, target);
    this.subzones.push(subzone);
    return subzone;
  }
  
  ZoneForm.prototype.subZoneFromData = function(subZoneData) {
    var target = DOMExport.elementFromXpath(subZoneData.xpath);
    if(target !== null) {
      var subzone = this.getSubZone(target);
      subzone.loadData(subZoneData);
    }
  }
  
  ZoneForm.prototype.loadData = function(zoneData) {
    Zone.prototype.loadData.apply(this, arguments);
    if(zoneData.subzones && zoneData.subzones.length > 0) {
      for(var i = 0; i < zoneData.subzones.length; ++i) {
        this.subZoneFromData(zoneData.subzones[i]);
      }
    }
  }

  ZoneForm.load = function() {
    $('form').each(function() {
      for(var i = 0, len = Zone.zones.length; i < len; ++i) {
        if(Zone.zones[i].target === this)
          return;
      }
      new ZoneForm({hidden : true, target : this});
    })
  }

  Zone.registerType('form', ZoneForm);
  Zone.registerType('form-input', ZoneFormInput);

  return ZoneForm;
});