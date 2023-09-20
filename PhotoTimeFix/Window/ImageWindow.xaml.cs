using System;
using System.IO;
using System.Windows.Media.Imaging;

namespace PhotoTimeFix.Window
{
    /// <summary>
    ///     ImageWindow.xaml 的交互逻辑
    /// </summary>
    public partial class ImageWindow : System.Windows.Window
    {
        public ImageWindow(string path)
        {
            InitializeComponent();
            Title = new FileInfo(path).Name;
            ImageView.Source = new BitmapImage(new Uri(path));
        }
    }
}