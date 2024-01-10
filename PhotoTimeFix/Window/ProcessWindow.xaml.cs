using System;
using System.ComponentModel;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.Window
{
    public partial class ProcessWindow : System.Windows.Window
    {
        private bool _closable;
        private FileStream _logFile;
        private UTF8Encoding utF8Encoding = new UTF8Encoding();

        public ProcessWindow(bool saveFile)
        {
            InitializeComponent();
            MaxHeight = SystemParameters.WorkArea.Height;
            MaxWidth = SystemParameters.WorkArea.Width;

            ProcessResultList.CollectionChanged += (sender, args) =>
            {
                if (args.Action == System.Collections.Specialized.NotifyCollectionChangedAction.Add)
                {
                    StringBuilder builder = null;
                    foreach (var item in args.NewItems)
                    {
                        if (item is ProcessResult)
                        {
                            var item1 = item as ProcessResult;
                            builder = new StringBuilder();
                            builder.Append(item1.FileName);
                            if (item1.DateTime != "")
                            {
                                builder.Append("\t\t=>\t\t");
                                builder.Append(item1.DateTime);
                            }
                        }
                    }
                    Dispatcher.BeginInvoke(new Action(() => {
                        if (builder != null)
                        {
                            ProcessingNow.Content = builder.ToString();
                        }
                    }));
                }
            };

            if (saveFile)
            {
                ProcessResultList.CollectionChanged += (sender, args) =>
                {
                    if (args.Action == System.Collections.Specialized.NotifyCollectionChangedAction.Add)
                    {
                        var builder = new StringBuilder();
                        foreach (var item in args.NewItems)
                        {
                            if (item is ProcessResult)
                            {
                                var item1 = item as ProcessResult;
                                builder.Append(item1.FileName);
                                builder.Append(',');
                                builder.Append(item1.Status);
                                builder.Append(',');
                                builder.Append(item1.DateTime);
                                builder.Append(',');
                                builder.Append(item1.Detail);
                                builder.AppendLine();
                            }
                        }
                        try
                        {
                            byte[] bytes = utF8Encoding.GetBytes(builder.ToString());
                            _logFile.WriteAsync(bytes, 0, bytes.Length) ;
                        }
                        catch(Exception e)
                        {
                            //ignore
                        }
                    }
                };
            }
        }

        public string LogFile
        {
            set
            {
                _logFile = File.OpenWrite(value);
            }
        }

        public bool Closable
        {
            get => _closable;
            set
            {
                ProgressBar.Visibility = value ? Visibility.Collapsed : Visibility.Visible;
                MessageBox.Show("OK");
                _closable = value;
                ScrollList.Visibility = Visibility.Visible;
                if (_logFile != null)
                {
                    try
                    {
                        _logFile.Dispose();
                    }
                    catch(Exception e)
                    {
                        //ignore
                    }
                }
            }
        }

        public ProcessResultList ProcessResultList => FindResource("ProcessResultList") as ProcessResultList;

        public async Task ShowDialogAsync()
        {
            await Task.Run(() => { Dispatcher.BeginInvoke(new Action(() => { ShowDialog(); })); });
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            if (Closable)
                base.OnClosing(e);
            else
                e.Cancel = true;
        }
    }
}