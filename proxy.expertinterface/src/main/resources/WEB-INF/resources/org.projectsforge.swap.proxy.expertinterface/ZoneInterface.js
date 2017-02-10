Loader.define('ZoneInterface', [ 'Zones', 'ZoneInterfaces',
    'core/ManagedElement', 'ui/Action', 'ui/ExpandableContainer',
    'ui/LockAction', 'ui/CheckableAction' ], function(Zones, Interfaces, ManagedElement, Action,
    ExpandableContainer, LockAction, CheckableAction) {

  /**
   * Static class that handle the interface of zones
   */
  var ZoneInterface = {
    /**
     * Type of zone managed by this interface
     *
     * @property type
     * @public
     * @type String
     */
    type : null,

    /**
     * True if zones can be removed, false otherwise
     *
     * @property removable
     * @public
     * @type Boolean
     */
    removable : true,

    /**
     * True if zones can be edited once saved
     *
     * @property editable
     * @public
     * @type Boolean
     */
    editable : true,

    /**
     * Show managed zones
     *
     * @method show
     * @public
     */
    show : function() {
      Zones.runForAll(this.type, 'show');
    },

    /**
     * Hide managed zones
     *
     * @method hide
     * @public
     */
    hide : function() {
      Zones.runForAll(this.type, 'hide');
    },

    /**
     * Interface just got activated. Update the ui.
     */
    activate : function() {
      var self = this;

      Interfaces.currentZone = null;

      if (!this.dom) {
        // It's the first time this interface is activated -> create the
        // elements
        this.dom = {};

        // root element subtree :
        // +- top ---------------------+
        // | [selection] |
        // +- pages[x] ----------------+
        // |+- table -----------------+|
        // || zones ||
        // |+-------------------------+|
        // +---------------------------+

        this.dom.root = ManagedElement('div').appendTo(Interfaces.container);
        this.dom.top = ManagedElement('div').appendTo(this.dom.root);

        if (this.removable) {
          this.dom.top_selection = new Action(
              "Selectionner une zone à annoter", function() {
                Zones.toggleSelection(self.type);
              }).appendTo(this.dom.top);
        }

        this.dom.pages = [];
        this.dom.pages.windows = [];

        var zones = Zones.sort(Zones.list(this.type));
        zones.forEach(function(remotezone) {
          self.onnew(remotezone);
        });
      } else {
        // Set the root element as Interfaces container content
        this.dom.root.appendTo(Interfaces.container);
      }
    },

    /**
     * Interface just got desactivated. Update the ui.
     */
    desactivate : function() {
      Interfaces.container.removeChildrens();
    },

    /**
     * Called when a zone got created
     *
     * @param {RemoteZone}
     *            remotezone Zone that just got created
     */
    onnew : function(remotezone) {
      if (this.dom && !remotezone.dom) {
        var page;
        var currentWindow = this.dom.pages.windows
            .indexOf(remotezone.remotewindow);
        if (currentWindow == -1) {
          // Current page hasn't been created yet

          // Add the window to the list :
          currentWindow = this.dom.pages.windows.length;
          this.dom.pages.windows.push(remotezone.remotewindow);

          // Create the page :
          page = new ExpandableContainer();
          page.appendTo(this.dom.root);
          page.title.textContent = remotezone.windowInfo.href;
          page.table = ManagedElement('table').addClass('zoneinterface-table')
              .appendTo(page.content);
          page.createZoneRow = function(remotezone, idx) {
            var row = ManagedElement('tr').addClass('zoneinterface-zonerow').appendTo(page.table);
            if(remotezone) {
              row.onElement('mouseover', function() {
                remotezone.runSub(idx, 'highlight', true);
              });
              row.onElement('mouseout', function() {
                remotezone.runSub(idx, 'highlight', false);
              });
            }
            return row;
          }
          this._onnewpage(ManagedElement('tr').appendTo(page.table));

          // Add the page to the list
          this.dom.pages.push(page);
        } else {
          page = this.dom.pages[currentWindow];
        }

        remotezone.dom = {};
        remotezone.dom.createZoneRow = page.createZoneRow;
        remotezone.dom.createZoneCell = function(row) {
          return new ManagedElement('td').appendTo(row || remotezone.dom.rom);
        };
        remotezone.dom.row = remotezone.dom.createZoneRow(remotezone);

        this._onnew(remotezone);
      }
    },

    /**
     * Called by onnew when a page of zones got created
     *
     * @method _onnewpage
     * @protected
     * @param {ManagedElement}
     *            row Table row to create the header
     */
    _onnewpage : function(row) {
      ManagedElement('th').appendTo(row); // Lock
      ManagedElement('th').appendTo(row).textContent = "Label";
      ManagedElement('th').appendTo(row); // Anon
    },

    /**
     * Called by onnew when a zone got created
     *
     * @method _onnew
     * @private
     * @param {RemoteZone}
     *            remotezone Zone that just got created
     */
    _onnew : function(remotezone) {
      var self = this;
      var dom = remotezone.dom;
      var row = dom.row

      // Lock
      dom.lock = dom.createZoneCell(row);
      dom.lockAction = new LockAction(remotezone.info.saved, self.editable)
          .appendTo(dom.lock);
      dom.lockAction.on('stateChanged', function() {
        dom.labelInput.readOnly = !dom.lockAction.isUnlocked();
      });
      dom.lockAction.on('locking', function() {
        remotezone.run('save',  {
          anonymiseContent :dom.anonCheckbox.checked,
          anonymisePage : Interfaces.anonymisePage
        });
      });
      dom.lockAction.enabled = remotezone.info.allowSave;
      dom.lockAction.on('stateChanged', function() {
        dom.labelInput.element.readOnly = !dom.lockAction.isUnlocked();
        dom.anonCheckbox.enabled = dom.lockAction.isUnlocked();
      });

      // Label
      dom.label = dom.createZoneCell(row);
      dom.labelInput = new ManagedElement('input').appendTo(dom.label);
      dom.labelInput.element.value = remotezone.info.label;
      dom.labelInput.element.readOnly = !dom.lockAction.isUnlocked();
      dom.labelInput.onElement('keyup', function() {
        remotezone.set('label', dom.labelInput.element.value);
      });
      
      // Anonymise question
      dom.anon = dom.createZoneCell(row);
      dom.anonCheckbox = new CheckableAction(false).appendTo(dom.anon);
      dom.anonCheckbox.tooltip = "Cocher cette case pour anonymiser le contenu de la zone";
      dom.anonCheckbox.checked = remotezone.info.anonymous;
      dom.anonCheckbox.enabled = dom.lockAction.isUnlocked();
      
      this._onnewcustom(remotezone);

      // Delete
      if (this.removable) {
        dom.remove = dom.createZoneCell(row);
        dom.removeAction = new Action('', function() {
          remotezone.run('remove');
        }).appendTo(dom.remove).setIcon('delete');
      }
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
      var self = this;
      var dom = remotezone.dom;
      dom.custom = dom.createZoneCell(dom.row);

      // Parent
      dom.moveToParentAction = new Action('', function() {
        remotezone.run('moveTo', 'parentElement');
      }).appendTo(dom.custom).setIcon('moveto-parent');
      dom.moveToParentAction.tooltip = "Déplacer la sélection vers l'élément parent."

      // Previous sibling
      dom.moveToPrevAction = new Action('', function() {
        remotezone.run('moveTo', 'previousElement');
      }).appendTo(dom.custom).setIcon('moveto-prev');
      dom.moveToPrevAction.tooltip = "Déplacer la sélection vers l'élément précédent."

      // Next sibling
      dom.moveToNextAction = new Action('', function() {
        remotezone.run('moveTo', 'nextElement');
      }).appendTo(dom.custom).setIcon('moveto-next');
      dom.moveToNextAction.tooltip = "Déplacer la sélection vers l'élément suivant."

      // First child
      dom.moveToFirstAction = new Action('', function() {
        remotezone.run('moveTo', 'firstChildElement');
      }).appendTo(dom.custom).setIcon('moveto-firstchild');
      dom.moveToFirstAction.tooltip = "Déplacer la sélection vers le premier élément fils."

      // Select
      dom.select = new Action('', function() {
        remotezone.run('toggleSelection');
      }).appendTo(dom.custom).setIcon('select');
      dom.select.tooltip = "Lancer/Stopper une sélection au curseur."

      this.ontargetchanged(remotezone);

      dom.lockAction.on('stateChanged', function() {
        self.ontargetchanged(remotezone);
      });
    },

    /**
     * Called when a zone managed by this interface got the focus
     *
     * @method onfocus
     * @public
     */
    onfocus : function(remotezone) {
      if (remotezone != Interfaces.currentZone) {
        this.action.activate();
        Interfaces.currentZone = remotezone;
      }
      if (remotezone && remotezone.dom) {
        var parent = remotezone.dom.row.element;
        var top = 0;
        while(parent && parent != Interfaces.container.element) {
          top += parent.offsetTop;
          parent = parent.offsetParent;
        }
        Interfaces.container.element.scrollTop = top - 100;
        remotezone.dom.row.addClassState('highlight');
        window.setTimeout(function() {
          remotezone.dom.row.removeClassState('highlight');
        }, 2500);
      }
    },

    /**
     * Called when a zone managed by this interface got its label changed
     *
     * @method onlabelchanged
     * @public
     * @param {RemoteZone}
     *            remotezone
     */
    onlabelchanged : function(remotezone) {
      if (this.dom) {
        remotezone.dom.labelInput.element.value = remotezone.info.label;
        remotezone.dom.lockAction.enabled = remotezone.info.allowSave;
      }
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
        var zone = remotezone.info;
        remotezone.dom.moveToParentAction.enabled = enabled && zone.hasParent;
        remotezone.dom.moveToNextAction.enabled = enabled && zone.hasNext;
        remotezone.dom.moveToPrevAction.enabled = enabled && zone.hasPrevious;
        remotezone.dom.moveToFirstAction.enabled = enabled && zone.hasFirstChild;
        remotezone.dom.select.enabled = enabled;
        remotezone.dom.lockAction.enabled = zone.allowSave;
      }
    },

    /**
     * Called when a zone managed by this interface got saved
     *
     * @method onsaved
     * @public
     * @param {RemoteZone}
     *            remotezone
     */
    onsaved : function(remotezone) {
      if (remotezone == Interfaces.currentZone) {
        this.onfocus(remotezone);
      }
      if (this.dom) {
        remotezone.dom.lockAction.lock();
      }
    },

    /**
     * Called when a zone managed by this interface failed to be saved
     *
     * @method onsaved
     * @public
     * @param {RemoteZone}
     *            remotezone
     */
    onsavefailed : function(remotezone) {
      if (remotezone == Interfaces.currentZone) {
        this.onfocus(remotezone);
      }
      if (remotezone.dom) {
        remotezone.dom.lockAction.lockFailed();
      }
    },

    /**
     * Called when a zone managed by this interface got removed
     * @method onsaved
     * @public
     * @param {RemoteZone} remotezone
     */
    onremoved : function(remotezone) {
      if (remotezone == Interfaces.currentZone) {
        this.onfocus(null);
      }
      remotezone.dom.row.remove();
    }
  };

  return ZoneInterface;
})