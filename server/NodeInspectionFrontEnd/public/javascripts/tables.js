(function () {
  window.apiUrl = window.apiUrl || "/api/1";


  function showTagsForId (id) {
    $.ajax({
      url: apiUrl + "/" + id + "/tag"
    }).done(function (data) {
      var $tbody = $("#tags tbody");
      $tbody
        .find('tr')
        .remove()
        .end();
      data.forEach(function (e) {
        $tbody.append("<tr><td>"+e.ts+"</td><td>"+e.tag+"</td></tr>");
      });
      window.setMarks(data);
    });
  }

  window.showTagsForId = showTagsForId;

  $(document).ready(function () {
    $("#tripTable").tablesorter();
    $('#tripTable').filterTable();
    $.fn.editable.defaults.mode = 'inline';
    $('.tripName').editable({
      type: 'text',
      showbuttons: false,
      clear: false,
      emptytext: ''
    });
  });
})();

>>>>>>> 6ee82f5faf2a8ac279f56d27284baa0af1de44be
