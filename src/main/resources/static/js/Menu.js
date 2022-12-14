/**
 * There are a lot of magic numbers in this file
 * most of them are related to element position on canvas
 * Will be very grateful for merge request with fix of this numbers
 *
 */
const Menu = function (stage) {
    this.stage = stage;
    this.elements = [];
};

Menu.prototype.show = function () {
    this.drawBackground();
    this.drawPlayButton();
};

Menu.prototype.hide = function () {
    for (let i = 0; i < this.elements.length; i++) {
        this.stage.removeChild(this.elements[i]);
    }
    this.elements = [];
};

Menu.prototype.showMatchmaking = function () {
    this.drawBackground();
    this.showGameOverText("Matchmaking...");
    let timer = 0;
    const timerText = new createjs.Text(timer, "40px Helvetica", "#ff4444");
    timerText.x = (gCanvas.getWidthInPixel() - timerText.getMeasuredWidth()) / 2;
    timerText.y = 150;
    this.stage.addChild(timerText);
    this.elements.push(timerText);


    const self = this;
    createjs.Ticker.framerate = 1;
    createjs.Ticker.addEventListener('tick', function () {
        timerText.text = ++timer;
        self.stage.update();
    });
};

Menu.prototype.showGameOver = function (text) {
    this.show();
    this.showGameOverText(text);
};


Menu.prototype.drawBackground = function () {
    const canvasRect = new createjs.Graphics()
        .beginFill("rgba(0, 0, 0, 0.5)")
        .drawRect(0, 0, gCanvas.getWidthInPixel(), gCanvas.getHeightInPixel());

    const background = new createjs.Shape(canvasRect);
    this.stage.addChild(background);
    this.elements.push(background);
};

Menu.prototype.showGameOverText = function (text) {
    const gameOverText = new createjs.Text(text, "40px Helvetica", "#ff4444");
    gameOverText.x = (gCanvas.getWidthInPixel() - gameOverText.getMeasuredWidth()) / 2;
    gameOverText.y = 60;
    this.stage.addChild(gameOverText);
    this.elements.push(gameOverText);
};

Menu.prototype.drawPlayButton = function () {
    const buttonSize = 110;
    // counting central position for this element
    const buttonX = (gCanvas.getWidthInPixel() - buttonSize) / 2;
    const buttonY = (gCanvas.getHeightInPixel() - buttonSize) / 2;

    this.drawPlayButtonBackground(buttonX, buttonY, buttonSize);
    this.drawPlayButtonText(buttonX, buttonY, buttonSize);
    this.drawPawnIcon(buttonX, buttonY, buttonSize);
};

Menu.prototype.drawPlayButtonBackground = function (x, y, buttonSize) {
    const playButtonBackgroundGraphics = new createjs.Graphics()
        .beginFill("rgba(0, 0, 0, 0.5)")
        .drawRect(x, y, buttonSize, buttonSize);

    const background = new createjs.Shape(playButtonBackgroundGraphics);
    this.stage.addChild(background);
    this.elements.push(background);
    this.setHandCursor(background);

    background.addEventListener('click', function() {
        gGameEngine.matchmaking();
    });
};

Menu.prototype.drawPlayButtonText = function (x, y, buttonSize) {
    const playText = new createjs.Text("Play", "32px Helvetica", "#ff4444");
    // counting central position inside background
    playText.x = x + (buttonSize - playText.getMeasuredWidth()) / 2;
    const shiftFromDownside = 20;
    playText.y = (y + buttonSize) - (playText.getMeasuredHeight() + shiftFromDownside);
    this.stage.addChild(playText);
    this.elements.push(playText);
};

Menu.prototype.drawPawnIcon = function (x, y, buttonSize) {
    const singleIcon = new createjs.Bitmap(gGameEngine.asset.pawn.player);
    const pawnIconSize = 48;
    singleIcon.sourceRect = new createjs.Rectangle(0, 0, pawnIconSize, pawnIconSize);
    // counting central position inside background
    singleIcon.x = x + (buttonSize - pawnIconSize) / 2;
    const shiftFromUpside = 13;
    singleIcon.y = y + shiftFromUpside;
    gGameEngine.stage.addChild(singleIcon);
    this.elements.push(singleIcon);
};

Menu.prototype.setHandCursor = function (btn) {
    btn.addEventListener('mouseover', function () {
        document.body.style.cursor = 'pointer';
    });
    btn.addEventListener('mouseout', function () {
        document.body.style.cursor = 'auto';
    });
};
