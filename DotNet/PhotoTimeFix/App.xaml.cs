using System;
using System.Globalization;
using System.Threading.Tasks;
using System.Windows;
using Microsoft.AppCenter;
using Microsoft.AppCenter.Analytics;
using Microsoft.AppCenter.Crashes;

namespace PhotoTimeFix
{
    /// <summary>
    ///     Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
#if DEBUG
        public static readonly bool IsDebug = true;
#else
        public static readonly bool IsDebug = false;
#endif

        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);
            if (!IsDebug)
            {
                //UI线程未捕获异常处理事件
                DispatcherUnhandledException += (sender, e1) =>
                {
                    MessageBox.Show(e1.Exception.Message);
                    Crashes.TrackError(e1.Exception);
                };

                //Task线程内未捕获异常处理事件
                TaskScheduler.UnobservedTaskException += (sender, e1) =>
                {
                    MessageBox.Show(e1.Exception.Message);
                    Crashes.TrackError(e1.Exception);
                };

                //非UI线程未捕获异常处理事件
                AppDomain.CurrentDomain.UnhandledException += delegate(object sender, UnhandledExceptionEventArgs args)
                {
                    var ex = args.ExceptionObject;
                    if (ex is Exception)
                    {
                        MessageBox.Show((ex as Exception).Message);
                        Crashes.TrackError(ex as Exception);
                    }
                };
                var countryCode = RegionInfo.CurrentRegion.TwoLetterISORegionName;
                AppCenter.SetCountryCode(countryCode);
                AppCenter.Start("5aa5aa14-0bd1-45f1-bea4-72d960c9c576", typeof(Crashes), typeof(Analytics));
            }
        }
    }
}