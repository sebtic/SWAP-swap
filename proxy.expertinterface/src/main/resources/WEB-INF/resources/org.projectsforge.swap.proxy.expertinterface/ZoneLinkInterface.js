Loader.define('ZoneLinkInterface', [ 'Zones', 'ZoneInterfaces',
    'ZoneInterface', 'core/ManagedElement', 'ui/Action' ], function(Zones,
        Interfaces, ZoneInterface, ManagedElement, Action) {

  /**
   * Static class that handle the interface of link zones
   *
   * @module expertinterface
   * @class ZoneLinkInterface
   * @static
   */
  var ZoneLinkInterface = {
    type : 'link',
    removable : false,

    /**
     * Called by onnew when a page of zones got created
     *
     * @method _onnewpage
     * @protected
     * @param {ManagedElement}
     *            row Table row to create the header
     */
    _onnewpage : function(row) {
      ZoneInterface._onnewpage.apply(this, arguments);
      ManagedElement('th').appendTo(row).textContent = "Url";
    },

    /**
     * Called by _onnew when a zone got created. This is the function to
     * reimplement in subclasses to add zone custom ui
     *
     * @method _onnewcustom
     * @protected
     * @param {RemoteZone}
     *            remotezone Zone that just got created
     */
    _onnewcustom : function(remotezone) {
      var dom = remotezone.dom;

      dom.url = dom.createZoneCell(dom.row);
      dom.url.textContent = remotezone.info.url;

      dom.ref = dom.createZoneCell(dom.row);
      dom.refButton = new Action('', function() {
        remotezone.run('selectRef');
      }).appendTo(dom.ref).setIcon('ref');
      dom.refButton.tooltip = "Ajouter une sélection comme ayant un rapport avec ce lien."
      dom.refButton.enabled = dom.lockAction.isUnlocked();
      dom.lockAction.on('stateChanged', function() {
        dom.refButton.enabled = dom.lockAction.isUnlocked();
      });
    },

    /**
     * Called when a zone managed by this interface got its target changed
     *
     * @method ontargetchanged
     * @public
     * @param {RemoteZone}
     *            remotezone
     */
    ontargetchanged : function(remotezone) {

    }
  };

  Interfaces.extendsInterface(ZoneLinkInterface, ZoneInterface);

  return ZoneLinkInterface;
})