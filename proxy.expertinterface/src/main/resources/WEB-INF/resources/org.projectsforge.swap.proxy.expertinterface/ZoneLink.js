Loader.define('ZoneLink', ['Zone', 'lib/jQuery', 'ZoneSelection'],
    function(Zone, $, ZoneSelection) {

  var ZoneLink = function() {
    Zone.apply(this, arguments);
    this.type = 'link';
    this.filter = 'a';
    this.setLabelHidden(true);
    this._created();
  };
  ZoneLink.prototype = Object.create(Zone.prototype);
  ZoneLink.prototype.constructor = ZoneLink;

  ZoneLink.load = function() {
    $('a').each(function() {
      for(var i = 0, len = Zone.zones.length; i < len; ++i) {
        if(Zone.zones[i].target == this)
          return;
      }
      new ZoneLink({hidden : true, target : this, label : this.textContent});
    })
  }

  ZoneLink.prototype.info = function () {
    var info = Zone.prototype._info.call(this);
    info.url = this.target ? this.target.href : "";
    return info;
  }

  ZoneLink.prototype.data = function (options) {
    var data = Zone.prototype.data.apply(this, arguments);
    data.href = this.target ? this.target.href : ""
    return data;
  }

  Zone.registerType('link', ZoneLink);

  return ZoneLink;
});