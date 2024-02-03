const ClusterSetting = function () {
    this.gameServer = {
        protocol: 'ws',
        host: 'violet.mercusysddns.com',
        port: '80',
        path: '/events/connect'
    };

    this.matchMaker = {
        protocol: 'http',
        host: 'violet.mercusysddns.com',
        port: '80',
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

const gClusterSettings = new ClusterSetting();
