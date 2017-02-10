Loader.define('ExpertInterfaceWorker', ['core/ProxyCommunication', 'ZonesWorker', 'Zone', 'core/Overlay', 'CSSDefaultTheme', 'ZoneLink', 'ZoneImage'],
    function (ProxyCommunication, ZonesWorker, Zone, Overlay) {
  ProxyCommunication.baseUrl = 'http://proxyapi.swap.projectsforge.org/api/org.projectsforge.swap.expertinterface.http.api/';

  Overlay.ready(function(overlay) {

    Zone.load();

  });

  return null;
});