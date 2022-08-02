var ClusterSetting = function () {
    this.gameServer = {
        protocol: 'ws',
        host: '192.168.1.121',
        port: '8080',
        path: '/events/connect'
    };

    this.matchMaker = {
        protocol: 'http',
        host: '192.168.1.121',
        port: '8080',
        path: '/matchmaker/join'
    };
};

ClusterSetting.prototype.gameServerUrl = function() {
    return makeUrl(this.gameServer)
};

ClusterSetting.prototype.matchMakerUrl = function() {
    return makeUrl(this.matchMaker)
};

function makeUrl(data) {
    return data['protocol'] + "://" + data['host'] + ":" + data['port'] + data['path']
}

var gClusterSettings = new ClusterSetting();
