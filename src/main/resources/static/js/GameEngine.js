var GameEngine = function () {
    this.asset = {
        pawn: {},
        bomb: null,
        fire: null,
        tile: {},
        bonus: {}
    };

    this.serverProxy = new ServerProxy();
};

GameEngine.prototype.load = function () {
    this.stage = new createjs.Stage("canvas");
    this.stage.canvas.width = gCanvas.getWidthInPixel();
    this.stage.canvas.height = gCanvas.getHeightInPixel();
    this.stage.enableMouseOver();

    var queue = new createjs.LoadQueue();
    var self = this;
    queue.addEventListener("complete", function () {
        self.asset.pawn.player = queue.getResult("pawn_player");
        self.asset.pawn.rival = queue.getResult("pawn_rival");
        self.asset.tile.grass = queue.getResult("tile_grass");
        self.asset.tile.wall = queue.getResult("tile_wall");
        self.asset.tile.wood = queue.getResult("tile_wood");
        self.asset.bomb = queue.getResult("bomb");
        self.asset.fire = queue.getResult("fire");
        self.asset.bonus.speed = queue.getResult("bonus_speed");
        self.asset.bonus.bombs = queue.getResult("bonus_bomb");
        self.asset.bonus.explosion = queue.getResult("bonus_explosion");
        self.initCanvas();
    });
    queue.loadManifest([
        {id: "pawn_player", src: "img/betty.png"},
        {id: "pawn_rival", src: "img/george.png"},
        {id: "tile_grass", src: "img/tile_grass.png"},
        {id: "tile_wall", src: "img/tile_wall.png"},
        {id: "tile_wood", src: "img/tile_wood.png"},
        {id: "bomb", src: "img/bomb.png"},
        {id: "fire", src: "img/fire.png"},
        {id: "bonus_speed", src: "img/bonus_speed.png"},
        {id: "bonus_bomb", src: "img/bonus_bomb.png"},
        {id: "bonus_explosion", src: "img/bonus_explosion.png"},
    ]);
};

GameEngine.prototype.initCanvas = function () {
    this.menu = new Menu(this.stage);
    this.menu.show();
    this.stage.update();
};

GameEngine.prototype.matchmaking = function () {
    var gameId = gMatchMaker.getSessionId();
    var name = gMatchMaker.settings.data
    this.serverProxy.connectToGameServer(gameId, name);

    this.game = new Game(this.stage);

    this.menu.hide();
    this.menu.showMatchmaking();
    this.stage.update();
};

GameEngine.prototype.startGame = function () {
    this.game = new Game(this.stage);
    this.game.start();
};

GameEngine.prototype.finishGame = function (gameOverText) {
    if (this.game !== null) {
        this.game.finish();
        this.game = null;
    }
    this.menu.showGameOver(gameOverText);
    this.stage.update();
};

gGameEngine = new GameEngine();