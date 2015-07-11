using System;
using System.Drawing;
using System.Drawing.Drawing2D;

namespace Peter
{
    public class TriangleRadarItem : IRadarItem
    {
        int _id;
        int _azimuth;
        int _elevation;
        int _width;
        int _height;
        DateTime _created = DateTime.Now;
        GraphicsPath _gp;

        public int ID
        {
            get
            {
                return _id;
            }
        }
        public int Azimuth
        {
            get
            {
                return _azimuth;
            }
            set
            {
                _azimuth = value;
                while (_azimuth < 0)
                {
                    _azimuth += 360;
                }
                if (_azimuth >= 360)
                    _azimuth = _azimuth % 360;
            }
        }
        public int Elevation
        {
            get
            {
                return _elevation;
            }
            set
            {
                _elevation = value;
                if (_elevation > 90)
                    _elevation = 90;
                else if (_elevation < 0)
                    _elevation = 0;
            }
        }
        public int Height
        {
            get
            {
                return _height;
            }
            set
            {
            }
        }
        public int Width
        {
            get
            {
                return _width;
            }
            set
            {
            }
        }
        public DateTime Created
        {
            get
            {
                return _created;
            }
        }

        public TriangleRadarItem(int id, int size, int az, int el)
        {
            _id = id;
            _width = size;
            _height = size;
            _azimuth = az;
            _elevation = el;
        }

        public void DrawItem(Radar radar, Graphics g)
        {
            PointF cp = radar.AzEl2XY(_azimuth, _elevation);
            PointF _topLeft = new PointF(cp.X - ((float)_width / 2), cp.Y - ((float)_height / 2));

            PointF p1 = new PointF(((float)_topLeft.X + ((float)_width / 2F)), (float)_topLeft.Y);
            PointF p2 = new PointF((float)_topLeft.X, (float)_topLeft.Y + (float)_height);
            PointF p3 = new PointF((float)_topLeft.X + (float)_width, (float)_topLeft.Y + (float)_height);

            _gp = new GraphicsPath(FillMode.Winding);
            _gp.AddPolygon(new PointF[] { p1, p2, p3 });

            g.FillPath(new SolidBrush(radar.CustomLineColor), _gp);
        }

        public int CompareTo(IRadarItem item)
        {
            return 0;
        }
    }
}
