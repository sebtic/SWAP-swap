Loader.define('main', [], function() {
  console.log("Main loaded");
  Loader.debugMode();
  //Loader.verbosity = 1;
  if (window.top === window)
    Loader.require('ExpertInterface');
  else
    Loader.require('ExpertInterfaceWorker');

});