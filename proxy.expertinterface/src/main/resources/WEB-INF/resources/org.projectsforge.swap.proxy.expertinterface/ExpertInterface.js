Loader.define('ExpertInterface', ['core/Overlay', 'core/ProxyCommunication', 'ui/Draggable'
                                  , 'core/ManagedElement', 'ui/Action', 'ui/ShowHideAction', 'ui/CheckableAction'
                                  , 'Zones', 'Zone', 'ZoneInterfaces'
                                  , 'ZoneInterface', 'ZoneLinkInterface', 'ZoneImageInterface', 'ZoneTableInterface', 'ZoneFormInterface'
                                  , 'CSSDefaultTheme', 'ZoneLink', 'ZoneImage', 'ZoneTable', 'ZoneForm'],
    function (Overlay, ProxyCommunication, Draggable
        , ManagedElement, Action, ShowHideAction, CheckableAction
        , Zones, Zone, ZoneInterfaces
        , ZoneInterface, LinkManager, ImageManager, TableManager, FormManager) {
  'use strict';
  ProxyCommunication.baseUrl = 'http://proxyapi.swap.projectsforge.org/api/org.projectsforge.swap.expertinterface.http.api/';

  var interfaceActions = {};
  var ManagerAction = function(name, tooltip, manager) {
    ShowHideAction.call(this, name, undefined, true);
    var self = this;
    this.addClass("interfaceexpert-action");
    this.tooltip = tooltip;
    this.activated = false;
    this.ui = manager;
    this.ui.action = this;

    this.on('run', function() {
      if(self.activated) {
        self.desactivate();
      } else {
        self.activate();
      }
    });
    this.on('show', function() {
      manager.show();
    });
    this.on('hide', function() {
      manager.hide();
    });

    this.element.addEventListener('mousedown', function(event) {
      event.stopPropagation();
    }, false);

    interfaceActions[manager.type] = this;
  }
  ManagerAction.prototype = Object.create(ShowHideAction.prototype);
  ManagerAction.prototype.constructor = ManagerAction;

  ManagerAction.prototype.activate = function() {
    if(!this.activated) {
      for(var i in interfaceActions) {
        if(i != this.ui.type) {
          // Desactivate others
          interfaceActions[i].desactivate()
        }
      }
      this.ui.activate();
      this.addClassState("activated");
      this.activated = true;
    }
  }

  ManagerAction.prototype.desactivate = function() {
    if(this.activated) {
      this.ui.desactivate();
      this.removeClassState("activated");
      this.activated = false;
    }
  }

  Overlay.ready(function(overlay) {

    /**
     * CSSStyles of the interface DOMElements
     *
     * @property css
     * @private
     * @static
     */
    var dom = {
        root : new ManagedElement('div', "toolbar"),
        titleItems : new ManagedElement('div', "toolbar-titleitems"),
        title : new ManagedElement('div', "toolbar-title"),
        items : new ManagedElement('div', "toolbar-actions")
    };

    /**
     * Draggable object responsible of the Drag feature of the interface
     *
     * @property draggable
     * @private
     * @static$(dom.label).on('keyup', function() {
      if(currentZone != null) {
        currentZone.set('label', dom.label.value);
      }
    });
     */
    var draggable = new Draggable(dom.titleItems.element, dom.root.element);

    draggable.setPadding(10);

    dom.root.appendTo(overlay);
    dom.titleItems.appendTo(dom.root);
    dom.title.appendTo(dom.titleItems);
    dom.items.appendTo(dom.root);

    Zone.exclude(dom.root.element);

    //Dispatch zone events
    var events = ['focus', 'targetChanged', 'labelChanged', 'removed', 'saved', 'saveFailed'];
    events.forEach(function(event) {
      var managerFunction = "on" + event.toLowerCase();
      Zones.on(event, function(remotezone) {
        var manager = ZoneInterfaces.getFor(remotezone);
        if(typeof manager[managerFunction] === "function") {
          manager[managerFunction](remotezone);
        }
      });
    })

    Zones.on('new', function(remotezone) {
      if(remotezone.type == null) {
        remotezone.run('show');
      }
      ZoneInterfaces.getFor(remotezone).onnew(remotezone);
    });

    // Load zones of the page
    Zone.load();

    dom.title.textContent = "Annotations";

    ZoneInterfaces.defaultInterface = ZoneInterface;
    ZoneInterfaces.register(LinkManager);
    ZoneInterfaces.register(ImageManager);
    ZoneInterfaces.register(TableManager);
    ZoneInterfaces.register(FormManager);
    ZoneInterfaces.container = dom.items;

    var markZone = new ManagerAction("Zones", "Permet de marquer une zone de la page", ZoneInterface);
    markZone.appendTo(dom.titleItems);
    markZone.show();

    var markLink = new ManagerAction("Links", "Permet de marquer un lien de la page", LinkManager);
    markLink.appendTo(dom.titleItems);
    markLink.on('run', function() {
      markLink.show();
      markImage.hide();
      markTable.hide();
      markForm.hide();
    });

    var markImage = new ManagerAction("Images", "Permet de marquer un image de la page", ImageManager);
    markImage.appendTo(dom.titleItems);
    markImage.on('run', function() {
      markLink.hide();
      markImage.show();
      markTable.hide();
      markForm.hide();
    });

    var markTable = new ManagerAction("Table", "Permet de marquer les tableaux présent dans la page", TableManager);
    markTable.appendTo(dom.titleItems);
    markTable.on('run', function() {
      markLink.hide();
      markImage.hide();
      markTable.show();
      markForm.hide();
    });

    var markForm = new ManagerAction("Form", "Permet de marquer les formulaires présent dans la page", FormManager);
    markForm.appendTo(dom.titleItems);
    markForm.on('run', function() {
      markLink.hide();
      markImage.hide();
      markTable.hide();
      markForm.show();
    });

    var SettingsInterface = {
      type : "_settings_",
      /**
       * Interface just got activated. Update the ui.
       */
      activate : function() {
        var self = this;

        ZoneInterfaces.currentZone = null;

        if (!this.dom) {
          // It's the first time this interface is activated -> create the elements
          this.dom = {};

          this.dom.root = ManagedElement('div').appendTo(ZoneInterfaces.container);
          this.dom.pageConfidentiality = ManagedElement('div').appendTo(this.dom.root);
          this.dom.pageConfidentialityCheckbox = new CheckableAction().appendTo(this.dom.pageConfidentiality);
          this.dom.pageConfidentialityLabel = ManagedElement('span').appendTo(this.dom.pageConfidentiality);
          this.dom.pageConfidentialityLabel.textContent = "Exporter la page sous forme anonymisé ?";
          this.dom.pageConfidentialityCheckbox.checked = ZoneInterfaces.anonymisePage;
          this.dom.pageConfidentialityCheckbox.on("stateChanged", function(checked) {
            ZoneInterfaces.anonymisePage = checked;
          });

        } else {
          // Set the root element as Interfaces container content
          this.dom.root.appendTo(ZoneInterfaces.container);
        }
      },

      /**
       * Interface just got desactivated. Update the ui.
       */
      desactivate : function() {
        ZoneInterfaces.container.removeChildrens();
      }
    };

    var settings = new ManagerAction("", "Configuration des annotations", SettingsInterface);
    settings.appendTo(dom.titleItems);
    settings.dom.showhide.remove();
    settings.setIcon('settings');

  });

  return null;
});