function Controller() {
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
        top: 20,
        title: "Collect Samples",
        id: "startCollector"
    });
    $.__views.index.add($.__views.startCollector);
    startCollection ? $.__views.startCollector.addEventListener("click", startCollection) : __defers["$.__views.startCollector!click!startCollection"] = true;
    $.__views.log = Ti.UI.createLabel({
        color: "#000",
        text: "LOGGING GOES HERE",
        id: "log"
    });
    $.__views.index.add($.__views.log);
    exports.destroy = function() {};
    _.extend($, $.__views);
    var Collector = require("collector");
    var log = function(msg) {
        $.log.text = msg;
    };
    var c = new Collector(30, log);
    $.index.open();
    __defers["$.__views.startCollector!click!startCollection"] && $.__views.startCollector.addEventListener("click", startCollection);
    _.extend($, exports);
}

var Alloy = require("alloy"), Backbone = Alloy.Backbone, _ = Alloy._;

module.exports = Controller;