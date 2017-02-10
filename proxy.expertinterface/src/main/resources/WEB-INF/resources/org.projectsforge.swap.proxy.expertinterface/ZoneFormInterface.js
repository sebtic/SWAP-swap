Loader.define('ZoneFormInterface', ['Zones', 'ZoneInterfaces', 'ZoneInterface', 'core/ManagedElement', 'ui/Action']
  , function (Zones, Manager, ZoneInterface, ManagedElement, Action) {
  var FormManager = {
      type : 'form',
      removable : false,

      /**
       * Called by _onnew when a zone got created. This is the function to
       * reimplement in subclasses to add zone custom ui
       *
       * @method _onnewcustom
       * @protected
       * @param {RemoteZone}
       *            remotezone Zone that just got created
       * @param {ManagedElement}
       *            row Table row to insert the ui in
       * @param {ManagedElement}
       *            table row to insert the custom ui in
       */
      _onnewcustom : function(remotezone, row, table) {
        var dom = remotezone.dom;
        dom.subs = [];

        remotezone.info.subzones.forEach(function(subzone, idx) {
          var sub = {};
          sub.row = dom.createZoneRow(remotezone, idx);

          // Empty cell
          dom.createZoneCell(sub.row);

          // Label
          sub.label = dom.createZoneCell(sub.row);
          sub.labelInput = ManagedElement('input').appendTo(sub.label);
          sub.labelInput.element.value = subzone.label;
          sub.labelInput.element.readOnly = !dom.lockAction.isUnlocked();
          sub.labelInput.onElement('keyup', function() {
            remotezone.setSub(idx, 'label', sub.labelInput.element.value);
          });
          dom.lockAction.on('stateChanged', function() {
            sub.labelInput.element.readOnly = !dom.lockAction.isUnlocked();
          });

          // Add ref
          sub.highlight = dom.createZoneCell(sub.row);
          sub.highlightButton = new Action('', function() {
            remotezone.runSub(idx, 'selectRef');
          }).appendTo(sub.label).setIcon('ref');
          sub.highlightButton.tooltip = "Ajouter une sélection comme ayant un rapport avec cet élément du formulaire."

          dom.subs.push(sub);
        });
      }
  }

  Manager.extendsInterface(FormManager, ZoneInterface);

  return FormManager;
})