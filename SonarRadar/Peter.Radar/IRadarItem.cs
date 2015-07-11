using System;
using System.Drawing;

namespace Peter
{
    public interface IRadarItem : IComparable<IRadarItem>
    {
        /// <summary>
        /// Unique ID for each RadarItem
        /// 
        /// This is used to update existing RadarItems on the Radar
        /// </summary>
        int ID { get; }

        /// <summary>
        /// The Azimuth on which to display the RadarItem
        /// 
        /// Logically, the range of values is [0,359], but any
        /// valid integer will be resolved to the correct value
        /// </summary>
        int Azimuth { get; set; }
        
        /// <summary>
        /// The Elevation on the Radar of the RadarItem
        /// The range of values is [0,90]
        /// </summary>
        int Elevation { get; set; }

        /// <summary>
        /// The Height of the RadarItem
        /// </summary>
        int Height { get; set; }

        /// <summary>
        /// The Width of the RadarItem
        /// </summary>
        int Width { get; set; }

        /// <summary>
        /// The DateTime for when the RadarItem was created
        /// </summary>
        DateTime Created { get; }

        /// <summary>
        /// Specifies how to draw the RadarItem
        /// </summary>
        /// <param name="g"></param>
        void DrawItem(Radar radar, Graphics g);
    }
}
