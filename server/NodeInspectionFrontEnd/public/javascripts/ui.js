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
    showLinearAccelerationForId(id);
    showGravityForId(id);
    window.currentDevId = id;
    $('[data-tab="map"]').click();
  });

  $('.resetZoom').click(function (eventObject) {
    if(!window.currentDevId) return;
    var id = window.currentDevId;
    showAccelerometerForId(id);
    showLinearAccelerationForId(id);
    showGravityForId(id);
  });

  $('.saveWindow').click(function (eventObject) {
    if(!window.currentDevId) return;
    if(!window.currentWindow || !window.currentWindow.min || !window.currentWindow.max) return;
    $('.ui.small.modal').modal('setting', 'closable', false).modal('show');
  });

  $('#acceptSave').on('click', function (event) {
    if(!window.currentDevId) return;
    if(!window.currentWindow || !window.currentWindow.min || !window.currentWindow.max) return;
    var startDate = window.currentWindow.min + 60 * 60 * 1000 * 2;
    var endDate = window.currentWindow.max + 60*60*1000 * 2;
    var tag = $("#newTag").val();
    $.post("/api/1/" + window.currentDevId + "/window", {start: startDate, end: endDate, tag: tag}, function(data) { console.dir(data)});
    //alert("INSERT INTO raw_training_data (type, ts, x, y, z, tag) SELECT 'acc' AS type,ts,x,y,z, 'test' as tag FROM accelerometer WHERE id='"+window.currentDevId+"' AND ts>=TIMESTAMP '"+startDate+"' AND ts<=TIMESTAMP '"+endDate+"';");
  });


});

  