Loader.define('ZoneTableInterface', [ 'Zones', 'ZoneInterfaces',
    'ZoneInterface', 'core/ManagedElement', 'ui/Action' ], function(Zones,
    Manager, ZoneInterface, ManagedElement, Action) {

  /**
   * Static class that handle the interface of table zones
   *
   * @module expertinterface
   * @class ZoneTableInterface
   * @static
   */
  var ZoneTableInterface = {
      type: 'table',
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
        ManagedElement('th').appendTo(row).textContent = "Type";
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
        dom.type = dom.createZoneCell(dom.row);
        dom.typeAction = new Action(remotezone.info.subtype == "deco" ? "Décoration" : "Données", function() {
          this.subtype = this.subtype == "deco" ? "data" : "deco";
          remotezone.set('subtype', this.subtype);
          this.label = this.subtype == "deco" ? "Décoration" : "Données";
        }).appendTo(dom.type);
        dom.typeAction.subtype = remotezone.info.subtype;
        dom.typeAction.enabled = dom.lockAction.isUnlocked();

        ////////////////////////////////////////////////////
        ///// Table head special row

        dom.row_head = dom.createZoneRow();

        // Empty cell
        dom.createZoneCell(dom.row_head);

        // Entête label
        dom.head_label = dom.createZoneCell(dom.row_head);
        dom.head_label.textContent = "En-tête";

        // Selection tools
        dom.head_tools = dom.createZoneCell(dom.row_head);

        // Parent
        dom.head_moveToParentAction = new Action('', function() {
          remotezone.run('headMoveTo', 'parentElement');
        }).appendTo(dom.head_tools).setIcon('moveto-parent');

        // Previous sibling
        dom.head_moveToPrevAction = new Action('', function() {
          remotezone.run('headMoveTo', 'previousElement');
        }).appendTo(dom.head_tools).setIcon('moveto-prev');

        // Next sibling
        dom.head_moveToNextAction = new Action('', function() {
          remotezone.run('headMoveTo', 'nextElement');
        }).appendTo(dom.head_tools).setIcon('moveto-next');

        // First child
        dom.head_moveToFirstAction = new Action('', function() {
          remotezone.run('headMoveTo', 'firstChildElement');
        }).appendTo(dom.head_tools).setIcon('moveto-firstchild');

        // Select
        dom.head_select = new Action('', function() {
          remotezone.run('headSelect');
        }).appendTo(dom.head_tools).setIcon('select');

        dom.head_clear = new Action("", function() {
          remotezone.run('headClearTarget');
        }).appendTo(dom.head_tools).setIcon('delete');

        this.ontargetchanged(remotezone);

        ////////////////////////////////////////////////////

        var self = this;
        dom.lockAction.on('stateChanged', function() {
          dom.typeAction.enabled = dom.lockAction.isUnlocked();
          self.ontargetchanged(remotezone);
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
        if (remotezone && remotezone.dom) {
          var enabled = remotezone.dom.lockAction.isUnlocked();
          var zone = remotezone.info.subzones ? remotezone.info.subzones[0] : null;
          remotezone.dom.head_moveToParentAction.enabled = enabled && zone && zone.hasParent;
          remotezone.dom.head_moveToNextAction.enabled = enabled && zone && zone.hasNext;
          remotezone.dom.head_moveToPrevAction.enabled = enabled && zone && zone.hasPrevious;
          remotezone.dom.head_moveToFirstAction.enabled = enabled && zone && zone.hasFirstChild;
        }
      }
  }

  Manager.extendsInterface(ZoneTableInterface, ZoneInterface);

  return ZoneTableInterface;
})