Loader.define('CSSDefaultTheme', ['core/CSSStyle'], function (CSSStyle) {
  CSSStyle.addRule("global", "default", {
    "font-family": "sans-serif",
    "font-size": "14px"
  });

  CSSStyle.addRule("global-div", "default", {
    "display": "block"
  });

  CSSStyle.addRule("global-table", "default", {
    "display": "table"
  });

  CSSStyle.addRule("global-tr", "default", {
    "display": "table-row"
  });

  CSSStyle.addRule("global-td", "default", {
    "display": "table-cell"
  });

  CSSStyle.addRule("global-th", "default", {
    "display": "table-cell",
    "font-weight" : "bold"
  });

  CSSStyle.addRule("global-input", "default", {
    "display": "inline-block",
    "padding": "5px",
    "height": "16px",
    "border-radius": "4px",
    "vertical-align": "middle",
    "border": "1px solid #A35F00",
    "background": "linear-gradient(to bottom, #F6E6B4 0%, #ED9017 100%) #F6E6B4"
  });

  CSSStyle.addRule("toolbar", "default", {
    "position": "fixed",
    "top": "10px",
    "left": "10px",
    "z-index": "2147483646",
    "border-radius": "4px",
    "border": "1px solid #004799",
    "background": "linear-gradient(to top, #B2E1FF 0%, #66B6FC 100%) #B2E1FF",
    "box-shadow": "0 0 7px black"
  });

  CSSStyle.addRule("toolbar-title", "default", {
      "display": "inline-block",
      "margin": "0px 5px",
      "font-weight": "bold",
      "color": "white",
      "cursor": "move"
  });

  CSSStyle.addRule("toolbar-titleitems", "default", {
    "border-bottom": "1px solid #004799",
    "padding": "5px",
    "border-radius": "4px 4px 0 0",
    "background": "linear-gradient(to bottom, #6db3f2 0%, #54a3ee 50%, #3690f0 51%, #1e69de 100%)",
    "cursor": "move"
  });

  CSSStyle.addRule("toolbar-actions", "default", {
      "padding": "5px",
      "overflow": "auto",
      "max-height": "500px"
  });

  CSSStyle.addRule("zone" , "default", {
    "position": "absolute",
    "z-index": "2147483640",
    "border-radius": "5px",
    "background-color": "rgba(32, 124, 229, 0.25)",
    "box-shadow": "0 0 15px grey, inset 0 0 7px white",
    "border": "2px solid #004799",
    "cursor": "pointer",
    "transition-property": "background-color, border-color",
    "transition-duration": "0.5s"
  });

  CSSStyle.addRule("zone", "highlight", {
    "background-color": "rgba(132, 255, 229, 0.50)",
    "border-color": "#60C7F9"
  });

  CSSStyle.addRule("zoneselection" , "default", {
    "position": "absolute",
    "z-index": "2147483640",
    "background-color": "rgba(32, 124, 229, 0.25)",
    "transition-property": "background-color",
    "transition-duration": "0.5s",
    "box-shadow": "0 0 15px grey, inset 0 0 7px white",
    "cursor": "pointer"
  });

  CSSStyle.addRule("zoneselection" , "highlight", {
    "background-color": "rgba(132, 255, 229, 0.50)"
  });

  CSSStyle.addRule("zone-overlay-actions" , "default", {
    "border-top-left-radius": "5px",
    "border-top-right-radius": "5px",
    "bottom": "100%",
    "left": "-2px",
    "padding": "2px 2px 0",
    "position": "absolute",
    "background": "linear-gradient(to bottom, #6db3f2 0%, #54a3ee 50%, #3690f0 51%, #1e69de 100%)",
    "border": "1px solid #004799",
    "white-space": "nowrap"
  });

  CSSStyle.addRule("zone-overlay-label" , "default", {
    "position" : "absolute",
    "top" : "-2px",
    "right" : "-2px",
    "font-size" : "12px",
    "padding" : "3px 4px",
    "border-bottom" : "1px solid black",
    "border-left" : "1px solid black",
    "border-top" : "2px solid #004799",
    "border-right" : "2px solid #004799",
    "background": "rgba(255,255,255,0.5)",
    "border-bottom-left-radius": "8px",
    "border-top-right-radius": "5px"
  });

  CSSStyle.addRule("action" , "default", {
    "display": "inline-block",
    "padding": "5px",
    "border-radius": "4px",
    "height": "16px",
    "border": "1px solid #A35F00",
    "background": "linear-gradient(to bottom, #F6E6B4 0%, #ED9017 100%) #F6E6B4",
    "cursor": "pointer",
    "color": "#A35F00",
    "font-weight": "bold",
    "vertical-align": "middle",
    "opacity": "1"
  });

  CSSStyle.addRule("action" , "hover", {
    "background" : "linear-gradient(to bottom, #FFD65E 0%, #FEBF04 100%) #FFD65E"
  });

  CSSStyle.addRule("action" , "disabled", {
    "opacity": "0.5"
  });

  CSSStyle.addRule("action-icon", {
    "background-size": "20px 20px",
    "background-position": "50% 50%",
    "background-repeat": "no-repeat",
    "width": "26px",
    "height": "26px",
    "margin": "-5px",
    "cursor": "pointer"
  });

  CSSStyle.addRule("action-icon-moveto-parent" , "default", {
    "background-image": CSSStyle.url("icons/upload2.svg")
  });

  CSSStyle.addRule("action-icon-moveto-next" , "default", {
    "background-image": CSSStyle.url("icons/arrow-down.svg")
  });

  CSSStyle.addRule("action-icon-moveto-prev" , "default", {
    "background-image": CSSStyle.url("icons/arrow-up.svg")
  });

  CSSStyle.addRule("action-icon-moveto-firstchild" , "default", {
    "background-image": CSSStyle.url("icons/forward.svg")
  });

  CSSStyle.addRule("action-icon-select" , "default", {
    "background-image": CSSStyle.url("icons/target.svg")
  });

  CSSStyle.addRule("action-icon-delete" , "default", {
    "background-image": CSSStyle.url("icons/close.svg")
  });

  CSSStyle.addRule("action-icon-ref" , "default", {
    "background-image": CSSStyle.url("icons/link.svg")
  });

  CSSStyle.addRule("action-icon-locked" , "default", {
    "background-image": CSSStyle.url("icons/lock.svg"),
    "background-position": "100% 50%"
  });

  CSSStyle.addRule("action-icon-locking" , "default", {
    "background-image": CSSStyle.url("icons/spinner3.svg")
  });

  CSSStyle.addRule("action-icon-unlocked" , "default", {
    "background-image": CSSStyle.url("icons/unlocked.svg")
  });

  CSSStyle.addRule("action-icon-settings" , "default", {
    "background-image": CSSStyle.url("icons/cog.svg")
  });

  CSSStyle.addRule("action-icon-checked" , "default", {
    "background-image": CSSStyle.url("icons/checkmark.svg")
  });

  CSSStyle.addRule("action-showhide" , "default", {
    "display": "inline-block",
    "padding": "0",
    "background-image": CSSStyle.url("icons/eye.svg"),
    "background-size": "16px 16px",
    "cursor": "pointer",
    "color": "#A35F00",
    "font-weight": "bold",
    "width": "16px",
    "height": "16px",
    "vertical-align": "bottom",
    "margin" : "0 0 0 4px",
    "opacity" : "1.0"
  });

  CSSStyle.addRule("action-showhide" , "hover", {
    "opacity" : "0.5"
  });

  CSSStyle.addRule("action-showhide" , "hidden", {
    "background-image": CSSStyle.url("icons/eye-blocked.svg")
  });

  CSSStyle.addRule("zoneinterface-pages" , "default", {
    "overflow": "auto",
    "max-height" : "500px"
  });

  CSSStyle.addRule("zoneinterface-table" , "default", {
    "width": "100%"
  });

  CSSStyle.addRule("zoneinterface-zonerow" , "default", {
    "background-color": "transparent",
    "transition-property": "background-color",
    "transition-duration": "0.5s"
  });

  CSSStyle.addRule("zoneinterface-zonecell" , "default", {
    "padding": "2px"
  });

  CSSStyle.addRule("zoneinterface-zonerow" , "highlight", {
    "background-color": "lime"
  });

  CSSStyle.addRule("interfaceexpert-action" , "default", {
    "margin": "0 2px"
  });

  CSSStyle.addRule("interfaceexpert-action" , "activated", {
    "color": "black"
  });

  return null;
});