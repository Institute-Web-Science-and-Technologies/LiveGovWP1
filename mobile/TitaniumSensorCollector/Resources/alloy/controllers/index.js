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
    function sendTag() {
        var tag = $.tag.value;
        c.sendTag(tag);
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
        title: "Collect Samples",
        id: "startCollector"
    });
    $.__views.index.add($.__views.startCollector);
    startCollection ? $.__views.startCollector.addEventListener("click", startCollection) : __defers["$.__views.startCollector!click!startCollection"] = true;
    $.__views.tag = Ti.UI.createTextField({
        top: 145,
        width: Ti.UI.FILL,
        id: "tag"
    });
    $.__views.index.add($.__views.tag);
    $.__views.sendTag = Ti.UI.createButton({
        top: 75,
        width: Ti.UI.FILL,
        title: "Send Tag",
        id: "sendTag"
    });
    $.__views.index.add($.__views.sendTag);
    sendTag ? $.__views.sendTag.addEventListener("click", sendTag) : __defers["$.__views.sendTag!click!sendTag"] = true;
    $.__views.logScroll = Ti.UI.createScrollView({
        top: 210,
        id: "logScroll"
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
    var c = new Collector(log);
    upload(c, log);
    $.index.open();
    __defers["$.__views.startCollector!click!startCollection"] && $.__views.startCollector.addEventListener("click", startCollection);
    __defers["$.__views.sendTag!click!sendTag"] && $.__views.sendTag.addEventListener("click", sendTag);
    _.extend($, exports);
}

var Alloy = require("alloy"), Backbone = Alloy.Backbone, _ = Alloy._;

module.exports = Controller;