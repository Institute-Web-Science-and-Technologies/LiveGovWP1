$(function() {
  $('.main.menu .item').tab({
    onTabLoad: function (tabPath, parameterArray, historyEvent) {
      if(tabPath === "map" && window.lMap) {
        window.lMap._map.invalidateSize();
      }
    }
  });


  $('.ui.dropdown').dropdown();

  $("#showBtn").click(function(eventObject) {
    var id = $('#devId .active.item').data("value");
    if(!id) { alert("Please select a device."); return;}
    window.lMap = window.lMap || new window.MyMap();
    lMap.showAllForId(id);
    showTagsForId(id);
    showAccelerometerForId(id);
    $('[data-tab="map"]').click();
  });
});

  