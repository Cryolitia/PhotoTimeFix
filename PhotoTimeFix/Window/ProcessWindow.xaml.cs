using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using MaterialDesignThemes.Wpf;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.Window
{
    public partial class ProcessWindow : System.Windows.Window
    {
        private bool _closable;
        private FileStream _logFile;
        private UTF8Encoding utF8Encoding = new UTF8Encoding();
        Dictionary<string, int> result = new Dictionary<string, int>();

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

                            if (result.ContainsKey(item1.Status))
                            {
                                result[item1.Status] += 1;
                            }
                            else
                            {
                                result[item1.Status] = 1;
                            }

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
                        if (!Closable)
                        {
                            if (builder != null)
                            {
                                ProcessingNow.Content = builder.ToString();
                            }
                        }
                        else
                        {
                            StringBuilder builder1 = new StringBuilder();
                            foreach (var item in result)
                            {
                                builder1.Append(item.Key);
                                builder1.Append(": ");
                                builder1.Append(item.Value);
                                builder1.Append("\t\t");
                            }
                            ProcessingNow.Content = builder1.ToString();
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
                _closable = value;
                ProgressBar.Visibility = value ? Visibility.Collapsed : Visibility.Visible;
                ListView.Visibility = Visibility.Visible;

                StringBuilder builder = new StringBuilder();
                foreach(var item in result)
                {
                    builder.Append(item.Key);
                    builder.Append(": ");
                    builder.Append(item.Value);
                    builder.Append("\t\t");
                }
                ProcessingNow.Content = builder.ToString();

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
                MessageBox.Show("OK");
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