 $(function() {
  $('.main.menu .item').tab({
    onTabLoad: function (tabPath, parameterArray, historyEvent) {
      if(tabPath === "graphs" && window.rMap) {
        window.rMap._map.invalidateSize();
      } else if (tabPath === "har") {
        window.lMap._map.invalidateSize();
        window.redrawHAR();
      }
    }
  });

  $('.ui.dropdown').dropdown();  

  $('.showTrip').click(function (eventObject) {
    var id = $(this).parent().data('id');
    if(!id) { alert("Please select a device."); return;}
    window.lMap = window.lMap || new window.MyMap('domMap');
    window.rMap = window.rMap || new window.MyMap('rawMap');
    lMap.showAllForId(id);
    rMap.showAllForId(id);
    showTagsForId(id);
    showAccelerometerForId(id);
    showLinearAccelerationForId(id);
    showGravityForId(id);
    showHARForId(id);
    window.currentDevId = id;
    $('[data-tab="graphs"]').click();
  });

  $("#showBtn").click(function(eventObject) {
    var id = $('#devId .active.item').data("value");
    if(!id) { alert("Please select a device."); return;}
    window.lMap = window.lMap || new window.MyMap();
    lMap.showAllForId(id);
    rMap.showAllForId(id);
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

  $('.csvDownload').click(function (eventObject) {
    if(!window.currentDevId) return;
    var tsSelect = '';
    if(window.currentWindow && window.currentWindow.min && window.currentWindow.max) {
      var startDate = window.currentWindow.min;
      var endDate = window.currentWindow.max;// + 60 * 60 * 1000;
      tsSelect = '?startTime='+startDate+'&endTime='+endDate;
    }
    var apiUrl = '/api/1/csv/' + window.currentDevId;
    switch(eventObject.currentTarget.id) {
      case('accCsv'):
        apiUrl += '/acc'
        break;
      case('lacCsv'):
        apiUrl += '/lac'
        break;
      case('graCsv'):
        apiUrl += '/gra'
        break;
    }
    apiUrl += tsSelect;
    window.location = apiUrl;
  });

  $('.saveWindow').click(function (eventObject) {
    if(!window.currentDevId) return;
    if(!window.currentWindow || !window.currentWindow.min || !window.currentWindow.max) return;
    $('.ui.small.modal').modal('setting', 'closable', false).modal('show');
  });

  $('#acceptSave').on('click', function (event) {
    if(!window.currentDevId) return;
    if(!window.currentWindow || !window.currentWindow.min || !window.currentWindow.max) return;
    var startDate = window.currentWindow.min + 60 * 60 * 1000;
    var endDate = window.currentWindow.max + 60 * 60 * 1000;
    var tag = $("#newTag").val();
    $.post("/api/1/" + window.currentDevId + "/window", {start: startDate, end: endDate, tag: tag}, function(data) { console.dir(data)});
  });


});