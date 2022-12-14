const Game = function (stage) {
    this.stage = stage;

    this.players = [];
    this.tiles = [];
    this.fires = [];
    this.bombs = [];
    this.bonuses = [];

    this.started = false;


};

// when game starts we connect socket to URL in ClusterSettings
Game.prototype.start = function () {
    gInputEngine.setupBindings();

    this.drawBackground();

    createjs.Ticker.removeAllEventListeners('tick');
    const self = this;
    createjs.Ticker.framerate = 60;
    createjs.Ticker.addEventListener('tick', function () {
        self.update();
    });

    this.started = true;
};

Game.prototype.update = function () {
    let i;
    for (i = 0; i < this.players.length; i++) {
        this.players[i].update();
    }

    for (i = 0; i < this.bombs.length; i++) {
        this.bombs[i].update();
    }

    this.stage.update();
};

Game.prototype.finish = function () {
    createjs.Ticker.removeAllEventListeners('tick');
    gInputEngine.unsubscribeAll();
    this.stage.removeAllChildren();

    [this.players, this.fires, this.bombs].forEach(function (it) {
        let i = it.length;
        while (i--) {
            it[i].remove();
            it.splice(i, 1);
        }
    });
};


Game.prototype.drawBackground = function () {
    for (let i = 0; i < gCanvas.tiles.w; i++) {
        for (let j = 0; j < gCanvas.tiles.h; j++) {
            const bitmap = new createjs.Bitmap(gGameEngine.asset.tile.grass);
            bitmap.x = i * gCanvas.tileSize;
            bitmap.y = j * gCanvas.tileSize;
            this.stage.addChild(bitmap);
        }
    }
};

// Каждую реплику происходит обновление игрового состояния, проверяем каждый объект - должны ли мы его удалить
// Назвал бы его не GarbageCollector а ObjectsManager потому что он не только удаляет объекты, а так же создает и
// изменяет (Pawn и  Bomb)
Game.prototype.gc = function (gameObjects) {
    if (!this.started) {
        this.started = true;
    }

    const survivors = new Set();

    // Стоит отметить что все объекты изначально разделяются по ID
    for (let i = 0; i < gameObjects.length; i++) {
        let wasDeleted = false;
        const obj = gameObjects[i];

        // Суть в том, что Пешка и Бомба живут ровно столько, сколько мы отправляем их в реплике (В отличие от других
        // объектов, таких как ящики)
        if (obj.type === 'Pawn' || obj.type === 'Bomb') {
            gMessageBroker.handler[obj.type](obj);
            survivors.add(obj.id);
            continue;
        }

        // Ящики же и бонусы живут между двумя репликами, в которых они присутствуют
        // Если они были присланны в реплике в первый раз, то они появляются
        // Если же второй раз, то удаляются
        [this.tiles, this.bonuses].forEach(function (it) {
            let i = it.length;
            while (i--) {
                if (obj.id === it[i].id) {
                    it[i].remove();
                    it.splice(i, 1);
                    wasDeleted = true;
                }
            }
        });

        // здесь мы добавляем обычные объекты, кстати условие на проверку Pawn вроде лишнее, так как в начале мы
        // проверяем на равенство
        if (!wasDeleted && obj.type !== 'Pawn') {
            gMessageBroker.handler[obj.type](obj);
        }
    }

    // Вот как раз мы тут удаляем Pawn и Bomb
    [this.players, this.bombs].forEach(function (it) {
        let i = it.length;
        while (i--) {
            if (!survivors.has(it[i].id)) {
                it[i].remove();
                it.splice(i, 1);
            }
        }
    });
};
