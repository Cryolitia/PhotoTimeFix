using System;
using System.ComponentModel;
using System.Threading.Tasks;
using System.Windows;

namespace PhotoTimeFix.Window
{
    public partial class ProcessBarWindow : System.Windows.Window
    {
        public bool Closable;

        private ProcessBarWindow()
        {
            InitializeComponent();
        }

        public static async Task StartTask(Action<Action> action)
        {
            var window = new ProcessBarWindow();
            try
            {
                await window.Dispatcher.BeginInvoke(new Func<Task>(async () => { await window.ShowDialogAsync(); }));
                await Task.WhenAll(Task.Run(() =>
                {
                    action.Invoke(() =>
                    {
                        window.Closable = true;
                        window.Dispatcher.BeginInvoke(new Action(window.Close));
                    });
                }));
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
                window.Close();
            }
        }

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