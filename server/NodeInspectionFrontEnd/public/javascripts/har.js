google.load("visualization", "1");
google.setOnLoadCallback(function() {
  $(function() {
    
    window.apiUrl = window.apiUrl || "/api/1";

    var googleLoaded = false;
    var timeline, data;

    function redraw () {
      if (timeline) {
        timeline.redraw();
      }
    }

    function showHARForId (id) {
      $.ajax({
        url: apiUrl + "/" + id + "/har"
      }).done(function (values) {
        data = new google.visualization.DataTable();
        data.addColumn('datetime', 'start');
        data.addColumn('datetime', 'end');
        data.addColumn('string', 'content');

        var rows = [];

        values.forEach(function (e) {          
          rows.push([new Date(parseInt(e.ts)), , e.tag]);
        });

        data.addRows(rows);


        // specify options
        var options = {
            "width":  "100%",
            "height": "150px",
            "style": "box"
        };
        // Instantiate our timeline object.
        timeline = new links.Timeline(document.getElementById('harTimeline'));

        // Draw our timeline with the created data and options
        timeline.draw(data, options);
        google.visualization.events.addListener(timeline, 'select', onselect);
      });
    }

    function onselect (event) {
      var row = getSelectedRow();
      if (row !== undefined) {
        var ts = data.getValue(row, 0).getTime();
        var txt = 'Tag: ' + data.getValue(row, 2);

        $.ajax({
          url: apiUrl + "/" + window.currentDevId + "/gps/" + ts
        }).done(function (values) {
          window.lMap.addMarker(values.lat, values.lon, txt + ' Diff: ' + values.diff );
        });
      }
    }

    function getSelectedRow() {
      var row = undefined;
      var sel = timeline.getSelection();
      if (sel.length) {
        if (sel[0].row != undefined) {
          row = sel[0].row;
        }
      }
      return row;
    }

    window.showHARForId = showHARForId;
    window.redrawHAR = redraw;
  });
});
