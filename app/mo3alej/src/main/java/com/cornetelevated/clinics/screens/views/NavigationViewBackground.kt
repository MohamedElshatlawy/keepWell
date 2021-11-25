package com.cornetelevated.clinics.screens.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class NavigationViewBackground: View {

    private var mPath: Path? = null
    private var mPaint: Paint? = null

    /** the CURVE_CIRCLE_RADIUS represent the radius of the fab button  */
    private val curveCircleRadius = 60

    // the coordinates of the first curve
    private val mFirstCurveStartPoint = Point()
    private val mFirstCurveEndPoint = Point()
    private val mFirstCurveControlPoint1 = Point()
    private val mFirstCurveControlPoint2 = Point()

    //the coordinates of the second curve
    private var mSecondCurveStartPoint = Point()
    private val mSecondCurveEndPoint = Point()
    private val mSecondCurveControlPoint1 = Point()
    private val mSecondCurveControlPoint2 = Point()
    private var mNavigationBarWidth = 0
    private var mNavigationBarHeight = 0

    constructor(context: Context) : super(context) {
        // CURVE_CIRCLE_RADIUS=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, context.getResources().getDisplayMetrics());
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mPath = Path()
        mPaint = Paint()
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        mPaint!!.color = Color.WHITE
        setBackgroundColor(Color.TRANSPARENT)
    }

    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // get width and height of navigation bar
        // Navigation bar bounds (width & height)
        mNavigationBarWidth = width
        mNavigationBarHeight = (curveCircleRadius.toFloat() * 1.5f).toInt()

        // the coordinates (x,y) of the start point before curve
        mFirstCurveStartPoint[(mNavigationBarWidth / 2) - (curveCircleRadius * 2) - (curveCircleRadius / 3)] = mNavigationBarHeight
        // the coordinates (x,y) of the end point after curve
        mFirstCurveEndPoint[mNavigationBarWidth / 2] = 0
        // same thing for the second curve
        mSecondCurveStartPoint = mFirstCurveEndPoint
        mSecondCurveEndPoint[(mNavigationBarWidth / 2) + (curveCircleRadius * 2) + (curveCircleRadius / 3)] = mNavigationBarHeight

        // the coordinates (x,y)  of the 1st control point on a cubic curve
        mFirstCurveControlPoint1[mFirstCurveStartPoint.x + curveCircleRadius + curveCircleRadius / 4] = mFirstCurveStartPoint.y
        // the coordinates (x,y)  of the 2nd control point on a cubic curve
        mFirstCurveControlPoint2[mFirstCurveEndPoint.x - (curveCircleRadius.toFloat() * 1.5).toInt()] = 0
        mSecondCurveControlPoint1[mSecondCurveStartPoint.x + (curveCircleRadius.toFloat() * 1.5).toInt()] = mSecondCurveStartPoint.y
        mSecondCurveControlPoint2[mSecondCurveEndPoint.x - (curveCircleRadius + curveCircleRadius / 4)] = mSecondCurveEndPoint.y
        mPath!!.reset()
        mPath!!.moveTo(0f, mNavigationBarHeight.toFloat())
        mPath!!.lineTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat())
        mPath!!.cubicTo(
            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
        )
        mPath!!.cubicTo(
            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
        )
        mPath!!.lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat())
        mPath!!.lineTo(mNavigationBarWidth.toFloat(), height.toFloat())
        mPath!!.lineTo(0f, height.toFloat())
        mPath!!.close()
    }

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPath!!, mPaint!!)
    }
}