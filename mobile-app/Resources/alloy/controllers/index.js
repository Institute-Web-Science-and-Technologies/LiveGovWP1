function Controller() {
    function startCollection() {
        c.start();
    }
    require("alloy/controllers/BaseController").apply(this, Array.prototype.slice.call(arguments));
    arguments[0] ? arguments[0]["__parentSymbol"] : null;
    arguments[0] ? arguments[0]["$model"] : null;
    var $ = this;
    var exports = {};
    var __defers = {};
    $.__views.index = Ti.UI.createWindow({
        backgroundColor: "white",
        id: "index"
    });
    $.__views.index && $.addTopLevelView($.__views.index);
    $.__views.startCollector = Ti.UI.createButton({
        title: "Collect Samples",
        id: "startCollector"
    });
    $.__views.index.add($.__views.startCollector);
    startCollection ? $.__views.startCollector.addEventListener("click", startCollection) : __defers["$.__views.startCollector!click!startCollection"] = true;
    exports.destroy = function() {};
    _.extend($, $.__views);
    var Collector = require("collector");
    var c = new Collector(30);
    $.index.open();
    __defers["$.__views.startCollector!click!startCollection"] && $.__views.startCollector.addEventListener("click", startCollection);
    _.extend($, exports);
}

var Alloy = require("alloy"), Backbone = Alloy.Backbone, _ = Alloy._;

module.exports = Controller;