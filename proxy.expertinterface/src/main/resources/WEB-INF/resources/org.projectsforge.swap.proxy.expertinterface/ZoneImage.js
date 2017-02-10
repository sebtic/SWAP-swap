Loader.define('ZoneImage', ['Zone', 'lib/jQuery'],
    function(Zone, $) {
  'use strict';

  var ZoneImage = function() {
    Zone.apply(this, arguments);
    this.type = 'image';
    this.filter = 'img';
    this._created();
  };
  ZoneImage.prototype = Object.create(Zone.prototype);
  ZoneImage.prototype.constructor = ZoneImage;

  ZoneImage.load = function() {
    $('img').each(function() {
      for(var i = 0, len = Zone.zones.length; i < len; ++i) {
        if(Zone.zones[i].target == this)
          return;
      }
      var zone = new ZoneImage({hidden : true});
      zone.target = this;
    })
  }

  ZoneImage.prototype.info = function () {
    var info = Zone.prototype._info.call(this);
    info.src = this.target ? this.target.src : "";
    return info;
  }

  ZoneImage.prototype.data = function (options) {
    var data = Zone.prototype.data.apply(this, arguments);
    data.src = this.target ? this.target.src : ""
    return data;
  }

  Zone.registerType('image', ZoneImage);

  return ZoneImage;
});