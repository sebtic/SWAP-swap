Loader.define('ui/LockAction', ['ui/Action', 'core/ManagedElement'], function(Action, ManagedElement) {
  'use strict';

  var LOCKED = "locked";
  var UNLOCKED = "unlocked";
  var LOCKING = "locking";

  /**
   * A lock action
   * Provided events :
   *  - locked
   *  - locking
   *  - unlocked
   *  - stateChanged
   *
   * @module ui
   * @class LockAction
   * @extends Action
   */
  var LockAction = function(locked, unlockable) {
    Action.call(this);

    this.state = locked ? LOCKED : UNLOCKED;
    this.unlockable = !!unlockable;

    this.update();

    var self = this;
    this.on('run', function() {
      if(self.isLocked())
        self.unlock();
      else
        self.locking();
    });
    this.on('stateChanged', function() {
      self.update();
    });
  };

  LockAction.prototype = Object.create(Action.prototype);
  LockAction.prototype.constructor = LockAction;

  /**
   * Locking this action
   *
   * @method locking
   */
  LockAction.prototype.locking = function() {
    if(this.isLocked() || this.isLocking())
      return;
    this.state = LOCKING;
    this.emit('locking');
    this.emit('stateChanged');
  };

  /**
   * Lock this action
   *
   * @method lock
   */
  LockAction.prototype.lock = function() {
    if(this.isLocked())
      return;
    this.state = LOCKED;
    this.emit('locked');
    this.emit('stateChanged');
  };

  /**
   * Unlock this action because lock has failed
   *
   * @method unlock
   */
  LockAction.prototype.lockFailed = function() {
    if(!this.isLocking())
      return;
    this.state = UNLOCKED;
    this.emit('unlocked');
    this.emit('stateChanged');
  };

  /**
   * Unlock this action
   *
   * @method unlock
   */
  LockAction.prototype.unlock = function() {
    if(this.isUnlocked() || this.isLocking() || !this.unlockable)
      return;
    this.state = UNLOCKED;
    this.emit('unlocked');
    this.emit('stateChanged');
  };

  /**
   * Return true if this action is in locked state, false otherwise
   * @return {Boolean}
   */
  LockAction.prototype.isLocked = function() {
    return this.state === LOCKED;
  };

  /**
   * Return true if this action is in locking state, false otherwise
   * @return {Boolean}
   */
  LockAction.prototype.isLocking = function() {
    return this.state === LOCKING;
  };

  /**
   * Return true if this action is in unlocked state, false otherwise
   * @return {Boolean}
   */
  LockAction.prototype.isUnlocked = function() {
    return this.state === UNLOCKED;
  };

  LockAction.prototype.update = function() {
    var tip;
    if (this.isLocked()) {
      this.setIcon('locked');
      tip = "Cette zone est vérouillé";
      if (this.unlockable) {
        tip += ", cliquer pour dévérouiller";
      } else {
        tip += ", vous ne pouvez pas la dévérouiller"
      }
    } else if(this.isLocking()) {
      this.setIcon('locking');
      tip = "Cette zone est en cours de vérouillage.";
    } else {
      this.setIcon('unlocked');
      tip = "Cette zone n'est pas vérouillé, cliquer pour vérouiller."
      if (!this.unlockable) {
        tip += "\nUne fois la zone vérouillé, il vous sera impossible de la modifier ultérieurement."
      }
    }
    this.tooltip = tip;
  };

  return LockAction;
});