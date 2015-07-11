using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Text;
using System.Windows.Forms;

namespace Peter
{    
    public class Radar
    {
        #region Private Fields

        // the diameter of the radar
        int _size;
        // the base image of the radar
        // this saves time/cycles, b/c we only have to draw the
        // background of the radar once (and update it as necessary)
        Image _baseImage;
        // the final rendered image of the radar
        Image _outputImage;
        // the azimuth of the radar
        int _az = 0;
        // some internally used points for drawing the fade
        // of the radar scanline
        PointF _pt = new PointF(0F, 0F);
        PointF _pt2 = new PointF(1F, 1F);
        PointF _pt3 = new PointF(2F, 2F);
        // how the scanline knows when to move
        Timer t;
        // do draw the scanline or not to draw the scanline?
        bool _scanLine = false;
        // colors to use in drawing the image
        Color _topColor = Color.FromArgb(0, 120, 0);
        Color _bottomColor = Color.FromArgb(0, 40, 0);
        Color _lineColor = Color.FromArgb(0, 255, 0);
        // list of items to draw on the radar
        List<IRadarItem> radarItemList = new List<IRadarItem>();

        #endregion

        #region Constructor

        public Radar(int diameter)
        {
            // set the diameter of the control
            // this influences the size of the output image
            _size = diameter;
            // create the base image
            CreateBaseImage();
            // create the export image
            Draw();
            // set up the default timer intervals
            // the timer is used for the scanline drawing
            // it is also disabled by default
            t = new Timer();
            t.Tick += new EventHandler(t_Tick);
            t.Interval = 40;
        }

        #endregion

        #region Public Field Accessor/Mutators

        public int DrawScanInterval
        {
            get
            {
                return t.Interval;
            }
            set
            {
                // update the timer interval
                if (value > 0)
                    t.Interval = value;
            }
        }

        public bool DrawScanLine
        {
            get
            {
                return _scanLine;
            }
            set
            {
                _scanLine = value;
                // turn the timer on/off
                t.Enabled = _scanLine;
                // if the user turns off the scanline, redraw the image
                if (!_scanLine)
                    Draw();
            }
        }

        public Color CustomGradientColorTop
        {
            get
            {
                return _topColor;
            }
            set
            {
                _topColor = value;
                // update the base image
                CreateBaseImage();
                // update the output image
                Draw();
            }
        }

        public Color CustomGradientColorBottom
        {
            get
            {
                return _bottomColor;
            }
            set
            {
                _bottomColor = value;
                // update the base image
                CreateBaseImage();
                // update the output image
                Draw();
            }
        }

        public Color CustomLineColor
        {
            get
            {
                return _lineColor;
            }
            set
            {
                _lineColor = value;
                // update the base image
                CreateBaseImage();
                // update the output image
                Draw();
            }
        }

        public Image Image
        {
            get
            {
                return _outputImage;
            }
        }

        #endregion

        #region Private Functions

        /// <summary>
        /// Creates the base radar image
        /// </summary>
        void CreateBaseImage()
        {
            // create the drawing objects
            Image i = new Bitmap(_size, _size);
            Graphics g = Graphics.FromImage(i);
            Pen p = new Pen(_lineColor);
            // set a couple of graphics properties to make the
            // output image look nice
            g.CompositingQuality = CompositingQuality.HighQuality;
            g.InterpolationMode = InterpolationMode.Bicubic;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.TextRenderingHint = TextRenderingHint.AntiAlias;
            // draw the background of the radar
            g.FillEllipse(new LinearGradientBrush(new Point((int)(_size / 2), 0), new Point((int)(_size / 2), _size - 1), _topColor, _bottomColor), 0, 0, _size - 1, _size - 1);
            // draw the outer ring (0?elevation)
            g.DrawEllipse(p, 0, 0, _size - 1, _size - 1);
            // draw the inner ring (60?elevation)
            int interval = _size / 3;
            g.DrawEllipse(p, (_size - interval) / 2, (_size - interval) / 2, interval, interval);
            // draw the middle ring (30?elevation)
            interval *= 2;
            g.DrawEllipse(p, (_size - interval) / 2, (_size - interval) / 2, interval, interval);
            // draw the x and y axis lines
            g.DrawLine(p, new Point(0, (int)(_size / 2)), new Point(_size - 1, (int)(_size / 2)));
            g.DrawLine(p, new Point((int)(_size / 2), 0), new Point((int)(_size / 2), _size - 1));
            // release the graphics object
            g.Dispose();
            // update the base image
            _baseImage = i;
        }

        /// <summary>
        /// Draws the output image and fire the event caller for ImageUpdate
        /// </summary>
        void Draw()
        {
            // create a copy of the base image to draw on
            Image i = (Image)_baseImage.Clone();
            // create the circular path for clipping the output
            Graphics g = Graphics.FromImage(i);
            GraphicsPath path = new GraphicsPath();
            path.FillMode = FillMode.Winding;
            path.AddEllipse(-1F, -1F, (float)(_size + 1), (float)(_size + 1));
            // clip the output image to the circular shape
            g.Clip = new Region(path);
            // set a couple of graphics properties to make the
            // output image look nice
            g.CompositingQuality = CompositingQuality.HighQuality;
            g.InterpolationMode = InterpolationMode.Bicubic;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.TextRenderingHint = TextRenderingHint.AntiAlias;
            // draw each RadarItem in the list
            foreach (IRadarItem item in radarItemList)
            {
                item.DrawItem(this, g);
            }
            // if the scanline is on, draw it
            if (_scanLine)
            {
                // create the fade path and gradient
                GraphicsPath gp = new GraphicsPath(FillMode.Winding);
                gp.AddLine(new PointF((float)(_size / 2), (float)(_size / 2)), _pt2);
                gp.AddCurve(new PointF[] { _pt2, _pt3, _pt });
                gp.AddLine(_pt, new PointF((float)(_size / 2), (float)(_size / 2)));
                PathGradientBrush pgb = new PathGradientBrush(gp);
                pgb.CenterPoint = _pt;
                pgb.CenterColor = Color.FromArgb(128, _lineColor);
                pgb.SurroundColors = new Color[] { Color.Empty };
                // draw the fade path
                g.FillPath(pgb, gp);
                // draw the scanline
                g.DrawLine(new Pen(_lineColor), new PointF((float)(_size / 2), (float)(_size / 2)), _pt);
            }
            // draw the outer ring once more
            // this gives the control a clearly defined edge, good for the UI
            g.DrawEllipse(new Pen(_lineColor), 0, 0, _size - 1, _size - 1);

            // release the graphics object
            g.Dispose();
            // call the event caller
            OnImageUpdate(i);

            _outputImage = i;
        }

        //Rectangle GetRectFromPointFs(PointF[] pfArray)
        //{
        //    int xMin = (int)pfArray[0].X;
        //    int xMax = (int)pfArray[0].X;
        //    int yMin = (int)pfArray[0].Y;
        //    int yMax = (int)pfArray[0].Y;

        //    foreach (PointF pF in pfArray)
        //    {
        //        if ((int)pF.X < xMin)
        //            xMin = (int)pF.X;
        //        if ((int)pF.X > xMax)
        //            xMax = (int)pF.X;
        //        if ((int)pF.Y < yMin)
        //            yMin = (int)pF.Y;
        //        if ((int)pF.Y > yMax)
        //            yMax = (int)pF.Y;
        //    }

        //    return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
        //}

        #endregion

        #region Public Functions

        public PointF AzEl2XY(int azimuth, int elevation)
        {
            // rotate coords... 90deg W = 180deg trig
            double angle = (270d + (double)azimuth);

            // turn into radians
            angle *= 0.0174532925d;

            double r, x, y;

            // determine the lngth of the radius
            r = (double)_size * 0.5d;
            r -= (r * (double)elevation / 90);

            x = (((double)_size * 0.5d) + (r * Math.Cos(angle)));
            y = (((double)_size * 0.5d) + (r * Math.Sin(angle)));

            return new PointF((float)x, (float)y);
        }

        public void AddItem(IRadarItem item)
        {
            bool bFlag = true;

            for (int i = 0; i < radarItemList.Count; i++)
            {
                if (radarItemList[i].ID == item.ID)
                    radarItemList[i] = item;
            }

            if (bFlag)
                radarItemList.Add(item);
            
            Draw();
        }

        #endregion

        #region Event Handlers

        void t_Tick(object sender, EventArgs e)
        {
            // increment the azimuth
            _az++;

            // reset the azimuth if needed
            // if not, this will cause an OverflowException
            if (_az >= 360)
                _az = 0;

            // update the fade path coordinates
            _pt = AzEl2XY(_az, 0);
            _pt2 = AzEl2XY(_az - 20, 0);
            _pt3 = AzEl2XY(_az - 10, -10);

            // redraw the output image
            Draw();
        }

        #endregion

        #region Outgoing Event Code

        /// <summary>
        /// Event caller for the ImageUpdate event
        /// </summary>
        /// <param name="i"></param>
        private void OnImageUpdate(Image i)
        {
            if (ImageUpdate != null)
                ImageUpdate(this, new ImageUpdateEventArgs(i));
        }

        /// <summary>
        /// Event fired when the output image is redrawn
        /// </summary>
        public event ImageUpdateHandler ImageUpdate;

        #endregion
    }

    /// <summary>
    /// 
    /// </summary>
    /// <param name="b"></param>
    public delegate void ImageUpdateHandler(object sender, ImageUpdateEventArgs e);

    /// <summary>
    /// 
    /// </summary>
    public class ImageUpdateEventArgs : EventArgs
    {
        Image _image;

        public Image Image
        {
            get
            {
                return _image;
            }
        }

        public ImageUpdateEventArgs(Image i)
        {
            _image = i;
        }
    }
}
