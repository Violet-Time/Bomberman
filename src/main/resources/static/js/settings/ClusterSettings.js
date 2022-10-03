const ClusterSetting = function () {
    this.gameServer = {
        protocol: 'wss',
        host: 'bomberscw.herokuapp.com/',
        path: '/events/connect'
    };

    this.matchMaker = {
        protocol: 'https',
        host: 'bomberscw.herokuapp.com/',
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
    return data['protocol'] + "://" + data['host'] + data['path']
}

const gClusterSettings = new ClusterSetting();
