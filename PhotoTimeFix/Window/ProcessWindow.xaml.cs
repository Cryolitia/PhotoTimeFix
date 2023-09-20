using System;
using System.ComponentModel;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.Window
{
    public partial class ProcessWindow : System.Windows.Window
    {
        private bool _closable;

        public ProcessWindow()
        {
            InitializeComponent();
            MaxHeight = SystemParameters.WorkArea.Height;
            MaxWidth = SystemParameters.WorkArea.Width;
            ProcessResultList.CollectionChanged += (sender, args) =>
            {
                var view = ListView.View as GridView;
                if (view == null) return;
                foreach (var column in view.Columns)
                {
                    column.Width = column.ActualWidth;
                    column.Width = double.NaN;
                }
            };
        }

        public bool Closable
        {
            get => _closable;
            set
            {
                ProgressBar.Visibility = value ? Visibility.Collapsed : Visibility.Visible;
                _closable = value;
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