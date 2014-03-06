var connectedDevices = {};
var sock = new SockJS('/socket');
sock.onopen = function() {
  console.log('open');
};
sock.onmessage = function(e) {
  var data = JSON.parse(e.data);
  if (!connectedDevices[data.id]) {
    addDevice(data);
  } else {
    var oldAct = connectedDevices[data.id];
    $("#"+data.id).removeClass(oldAct);
  }
  connectedDevices[data.id] = data.act;
  $("#"+data.id).addClass(data.act).text(data.id+" "+data.act);

};
sock.onclose = function() {
  console.log('close');
};

function addDevice (data) {
  var ele = '<li class="list-group-item" id="'+data.id+'">'+data.id+'</li>';
  $("#deviceList").append(ele);
}
