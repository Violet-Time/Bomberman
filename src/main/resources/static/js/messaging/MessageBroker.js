const MessageBroker = function () {
    this.handler = {
        'Pawn': this.handlePawn,
        'Bomb': this.handleBomb,
        'Wood': this.handleTile,
        'Wall': this.handleTile,
        'Fire': this.handleFire,
        'Bonus': this.handleBonus
    }
};

// Здесь обрабатывается реплика, хотелось бы отметить как вообще объекты должны присылаться (Их вид):
// Объекты, отправляемые с сервера имеют вид (что из них что думаю ясно):
// {"id":0,"type":"Pawn","position":{"y":20,"x":10},"alive":true,"direction":""}
// {"id":1,"type":"Bomb","position":{"y":20,"x":10}}
// {"id":2,"type":"Bonus","position":{"y":20,"x":10},"bonusType":"BOMBS"}
// {"id":3,"type":"Bonus","position":{"y":20,"x":10},"bonusType":"SPEED"}
// {"id":4,"type":"Bonus","position":{"y":20,"x":10},"bonusType":"RANGE"}
// {"id":5,"type":"Fire","position":{"y":20,"x":10}}
// {"id":6,"type":"Wall","position":{"y":20,"x":10}}
// {"id":7,"type":"Wood","position":{"y":20,"x":10}}
//
// msg при выводе в консоль должно выглядеть примерно так
// "[{\"id\":383,\"type\":\"Pawn\",\"position\":{\"x\":800,\"y\":32},\"alive\":true,\"direction\":\"\"}]"
MessageBroker.prototype.handleReplica = function (msg) {
    if (!gGameEngine.game.started) {
        gGameEngine.game.start();
    }
    gGameEngine.game.gc(msg.data);
};

MessageBroker.prototype.handleGameOver = function (msg) {
    gGameEngine.finishGame(msg.data);
};

// Реализация оставлена на разработчика сервера
MessageBroker.prototype.handlePossess = function (msg) {
    gInputEngine.possessed = parseInt(msg.data);
};

// Далее происходит обработка присланных объектов (эти функции вызываются из функции gc)
// Суть примерно одна - если нашли объект, то обновили его характеристики (Если это обычные объекты, то они не обновляются)
// Если нет, то обратились к конструктору за новым
MessageBroker.prototype.handlePawn = function(obj) {
    let player = gGameEngine.game.players.find(function (el) {
        return el.id === obj.id;
    });
    const position = gMessageBroker.mirrorPosition(obj.position);
    const direction = obj.direction;
    if (player) {
        player.bmp.x = position.x;
        player.bmp.y = position.y;
        player.direction = direction;
        player.alive = obj.alive;
    } else {
        player = new Player(obj.id, position);
        gGameEngine.game.players.push(player);
    }
};

MessageBroker.prototype.handleBomb = function(obj) {
    const bomb = gGameEngine.game.bombs.find(function (el) {
        return el.id === obj.id;
    });
    const position = gMessageBroker.mirrorPosition(obj.position);

    if (bomb) {
        bomb.bmp.x = position.x;
        bomb.bmp.y = position.y;
    } else {
        new Bomb(obj.id, position);
    }
};

MessageBroker.prototype.handleBonus = function(obj) {
    const bonus = gGameEngine.game.bonuses.find(function (el) {
        return el.id === obj.id;
    });
    const position = gMessageBroker.mirrorPosition(obj.position);

    if (bonus) {
        bonus.bmp.x = position.x;
        bonus.bmp.y = position.y;
    } else {
        new Bonus(obj.id, position, obj.bonusType);
    }
};

MessageBroker.prototype.handleTile = function (obj) {
    const tile = gGameEngine.game.tiles.find(function (el) {
        return el.id === obj.id;
    });
    const position = gMessageBroker.mirrorPosition(obj.position);
    if (tile) {
        tile.material = obj.type;
    } else {
        new Tile(obj.id, position, obj.type);
    }
};

MessageBroker.prototype.handleFire = function (obj) {
    const fire = gGameEngine.game.fires.find(function (el) {
        return el.id === obj.id;
    });

    const position = gMessageBroker.mirrorPosition(obj.position);
    if (!fire) {
        new Fire(obj.id, position);
    }
};

MessageBroker.prototype.mirrorPosition = function (origin) {
    return {
        x: origin.x,
        y: -origin.y + gCanvas.getHeightInPixel() - gCanvas.tileSize
    }
};

MessageBroker.prototype.move = function (direction) {
    const template = {
        topic: "MOVE",
        data: {
            direction: direction.toUpperCase()
        }
    };

    return JSON.stringify(template);
};

MessageBroker.prototype.plantBomb = function () {
    const template = {
        topic: "PLANT_BOMB",
        data: {}
    };

    return JSON.stringify(template);
};

// Experimental
MessageBroker.prototype.jump = function () {
    const template = {
        topic: "JUMP",
        data: {}
    };

    return JSON.stringify(template);
};

gMessageBroker = new MessageBroker();
