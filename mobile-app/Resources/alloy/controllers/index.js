function Controller() {
    function log(msg) {
        $.log.text.length >= 5e4 && ($.log.text = "Cleared log...");
        $.log.text += "\n" + msg;
        $.logScroll.scrollToBottom();
    }
    function startCollection() {
        if (c.running) {
            c.stop();
            $.startCollector.title = "Collect Samples";
        } else {
            c.start();
            $.startCollector.title = "Stop collection";
        }
    }
    require("alloy/controllers/BaseController").apply(this, Array.prototype.slice.call(arguments));
    this.__controllerPath = "index";
    arguments[0] ? arguments[0]["__parentSymbol"] : null;
    arguments[0] ? arguments[0]["$model"] : null;
    arguments[0] ? arguments[0]["__itemTemplate"] : null;
    var $ = this;
    var exports = {};
    var __defers = {};
    $.__views.index = Ti.UI.createWindow({
        backgroundColor: "white",
        id: "index"
    });
    $.__views.index && $.addTopLevelView($.__views.index);
    $.__views.startCollector = Ti.UI.createButton({
        top: 5,
        width: Ti.UI.FILL,
        margin: 5,
        title: "Collect Samples",
        id: "startCollector"
    });
    $.__views.index.add($.__views.startCollector);
    startCollection ? $.__views.startCollector.addEventListener("click", startCollection) : __defers["$.__views.startCollector!click!startCollection"] = true;
    $.__views.logScroll = Ti.UI.createScrollView({
        id: "logScroll",
        top: "75",
        height: "80%"
    });
    $.__views.index.add($.__views.logScroll);
    $.__views.log = Ti.UI.createLabel({
        color: "#000",
        top: 0,
        width: Ti.UI.FILL,
        id: "log",
        value: ""
    });
    $.__views.logScroll.add($.__views.log);
    exports.destroy = function() {};
    _.extend($, $.__views);
    var Collector = require("collector"), upload = require("upload");
    $.log.editable = false;
    var c = new Collector(30, log);
    upload(log);
    $.index.open();
    __defers["$.__views.startCollector!click!startCollection"] && $.__views.startCollector.addEventListener("click", startCollection);
    _.extend($, exports);
}

var Alloy = require("alloy"), Backbone = Alloy.Backbone, _ = Alloy._;

module.exports = Controller;