var MatchMaker = function (clusterSetting) {
    this.settings = {
        url: clusterSetting.matchMakerUrl(),
        method: 'POST',
        crossDomain: true,
        async: false
    };
};

MatchMaker.prototype.getSessionId = function () {
    var name = "name=" + Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);

    this.settings.data = name;
    var sessionId = -1;
    $.ajax(this.settings).done(function(id) {
        console.log(id);
        sessionId = id;
    }).fail(function(status) {
        alert("Matchmaker request failed" + status);
    });

    return sessionId;
};

gMatchMaker = new MatchMaker(gClusterSettings);