package com.example.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class GameView : View, Runnable {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private val gameSize = 14 // 地图的长宽
    private var screenHeight = 0 // 屏幕的整体高度
    private var screenWidth = 0 // 屏幕的整体宽度

    private val map = arrayListOf<ArrayList<GameStyle>>() // 整个地图的元素
    private val snakeLocation = arrayListOf<Point>() // 蛇的位置
    private val snakeHead = Point(gameSize / 2, gameSize / 2) // 蛇头位置
    private var foodLocation = Point() // 食物位置

    private var moveSpeed = 4 // 移动速度
    private var snakeLength = 1 // 蛇的长度
    private var snakeDirection = Direction.UP // 移动方向

    private var eatCount = 0 // 吃的食物数量

    private val thread = Thread(this) // 游戏线程
    private var gameStart = false // 游戏是否开始

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG) // 画笔

    /**
     * 在onSizeChanged可以获取到外部给GameView设置的宽高，所以这里给先前创建的变量进行赋值
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenHeight = height
        screenWidth = width
    }
    /**
     * 初始化函数
     */
    private fun init() {
        // 地图初始化
        for (y in 0 until gameSize) {
            val styleList = arrayListOf<GameStyle>()
            for (x in 0 until gameSize) {
                styleList.add(GameStyle(Type.GRID)) // 默认全部为格子
            }
            map.add(styleList)
        }
        // 随机食物的位置
        randomCreateFood()

        // 蛇头位置更新到蛇身上
        snakeLocation.add(Point(snakeHead.x, snakeHead.y))

//        gameStart = true
//        thread.start() // 开始游戏
    }

    fun start(){
        gameStart = true
        thread.start()
    }

    fun resetGame(){
        snakeLocation.clear()// 蛇的位置

        snakeHead.x = gameSize / 2
        snakeHead.y = gameSize / 2 // 蛇头位置

        foodLocation = Point() // 食物位置

        moveSpeed = 4 // 移动速度
        snakeLength = 1 // 蛇的长度
        snakeDirection = Direction.UP // 移动方向

        eatCount = 0 // 吃的食物数量
        init()
    }

    fun reStart(){
        gameStart = true
    }

    // 第三个构造函数中调用
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }
    /**
     * 随机生成食物
     */
    private fun randomCreateFood() {
        var food = Point(Random.nextInt(gameSize), Random.nextInt(gameSize))
        var index = 0
        while (index < snakeLocation.size - 1) {
            if (food.x == snakeLocation[index].x && food.y == snakeLocation[index].y) {
                food = Point(Random.nextInt(gameSize), Random.nextInt(gameSize))
                index = 0
            }
            index++
        }

        foodLocation = food
        refreshFood()
    }
    /**
     * 食物更新到地图上
     */
    private fun refreshFood() {
        map[foodLocation.y][foodLocation.x].type = Type.FOOD
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val blockWidth = screenWidth / gameSize // 每个网格的宽度
        val blockHeight = screenHeight / gameSize // 每个网格的高度

        // 绘制地图元素
        for (y in 0 until gameSize) {
            for (x in 0 until gameSize) {
                // 每个矩形的范围
                val left = x * blockWidth.toFloat()
                val right = (x + 1f) * blockWidth
                val top = y * blockHeight.toFloat()
                val bottom = (y + 1f) * blockHeight

                // 不同的标识设置不同的画笔样式
                when (map[y][x].type) {
                    Type.GRID -> mPaint.style = Paint.Style.STROKE
                    Type.FOOD, Type.BODY -> mPaint.style = Paint.Style.FILL
                }
                // 根据标识设置画笔颜色
                mPaint.color = map[y][x].getColor()

                // 当前的位置是否为头部
                if (x == snakeHead.x && y == snakeHead.y) {
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = GameStyle(Type.HEAD).getColor()
                }

                // 绘制矩形
                canvas.drawRect(left, top, right, bottom, mPaint)
            }
        }
    }
    /**
     * 移动
     */
    private fun moveSnake() {
        when (snakeDirection) {
            Direction.LEFT -> {
                if (snakeHead.x - 1 < 0) {
                    snakeHead.x = gameSize - 1
                } else {
                    snakeHead.x = snakeHead.x - 1
                }
                snakeLocation.add(Point(snakeHead.x, snakeHead.y))
            }
            Direction.RIGHT -> {
                if (snakeHead.x + 1 >= gameSize) {
                    snakeHead.x = 0
                } else {
                    snakeHead.x = snakeHead.x + 1
                }
                snakeLocation.add(Point(snakeHead.x, snakeHead.y))
            }
            Direction.UP -> {
                if (snakeHead.y - 1 < 0) {
                    snakeHead.y = gameSize - 1
                } else {
                    snakeHead.y = snakeHead.y - 1
                }
                snakeLocation.add(Point(snakeHead.x, snakeHead.y))
            }
            Direction.DOWN -> {
                if (snakeHead.y + 1 >= gameSize) {
                    snakeHead.y = 0
                } else {
                    snakeHead.y = snakeHead.y + 1
                }
                snakeLocation.add(Point(snakeHead.x, snakeHead.y))
            }
        }
    }
    private fun drawSnakeBody() {
        var length = snakeLength
        for (i in snakeLocation.indices.reversed()) {
            if (length > 0) {
                length--
            } else {
                val body = snakeLocation[i]
                map[body.y][body.x].type = Type.GRID
            }
        }

        length = snakeLength
        for (i in snakeLocation.indices.reversed()) {
            if (length > 0) {
                length--
            } else {
                snakeLocation.removeAt(i)
            }
        }
    }
    /**
     * 身体更新到地图上
     */
    private fun refreshBody() {
        // 减1是因为不需要包括蛇头
        for (i in 0 until snakeLocation.size - 1) {
            map[snakeLocation[i].y][snakeLocation[i].x].type = Type.BODY
        }
    }
    /**
     * 吃判断
     */
    private fun judgeEat() {
        // 是否吃到自己
        val head = snakeLocation[snakeLocation.size - 1]
        for (i in 0 until snakeLocation.size - 2) {
            val body = snakeLocation[i]
            if (body.x == head.x && body.y == head.y) {
                gameStart = false // 吃到身体游戏结束
                over.gameover()
            }
        }

        // 吃到食物
        if (head.x == foodLocation.x && head.y == foodLocation.y) {
            snakeLength++ // 长度+1
            eatCount++
            onEatListener.onEatListener(eatCount)
            randomCreateFood() // 刷新食物
        }
    }

    lateinit var onEatListener:OnEatListener
    lateinit var over:Over

    override fun run() {
        while (gameStart) {
            moveSnake() // 移动蛇
            drawSnakeBody() // 绘制蛇身
            refreshBody() // 刷新蛇身
            judgeEat() // 判断吃
            postInvalidate() // 刷新视图
            Thread.sleep(1000 / moveSpeed.toLong())
        }
    }
    /**
     * 设置移动方向
     */
    fun setMove(direction: Int) {
        when {
            snakeDirection == Direction.LEFT && direction == Direction.RIGHT -> return
            snakeDirection == Direction.RIGHT && direction == Direction.LEFT -> return
            snakeDirection == Direction.UP && direction == Direction.DOWN -> return
            snakeDirection == Direction.DOWN && direction == Direction.UP -> return
        }
        snakeDirection = direction
    }

}
