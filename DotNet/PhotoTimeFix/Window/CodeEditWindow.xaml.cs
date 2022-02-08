using System;
using System.Windows;
using CSScriptLib;
using PhotoTimeFix.Util;
using PhotoTimeFix.ViewBinding;

namespace PhotoTimeFix.Window
{
    public partial class CodeEditWindow : System.Windows.Window
    {
        private MainWindowBinding _binding;

        public CodeEditWindow()
        {
            InitializeComponent();
        }

        public MainWindowBinding Binding
        {
            set => _binding = value;
        }

        private async void Compile_OnClick(object sender, RoutedEventArgs e)
        {
            try
            {
                var code = CodeEditor.Text;
                await ProcessBarWindow.StartTask(action =>
                {
                    _binding.FileProcessor = (IFileProcessor) CSScript.Evaluator.LoadCode(code);
                    MessageBox.Show(Resource.Resource.CodeEditWindow_CompileSuccess);
                    action.Invoke();
                });
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }
    }
}