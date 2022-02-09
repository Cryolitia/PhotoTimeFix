using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Security.Principal;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Documents;
using System.Windows.Forms;
using DesktopBridge;
using ICSharpCode.AvalonEdit;
using LicensesDialog;
using LicensesDialog.Licenses;
using M2.NSudo;
using MetadataExtractor;
using MetadataExtractor.Formats.Exif;
using MimeTypes;
using PhotoTimeFix.Util;
using PhotoTimeFix.ViewBinding;
using Application = System.Windows.Forms.Application;
using Binding = System.Windows.Data.Binding;
using DataFormats = System.Windows.DataFormats;
using Directory = System.IO.Directory;
using DragDropEffects = System.Windows.DragDropEffects;
using DragEventArgs = System.Windows.DragEventArgs;
using Image = System.Drawing.Image;
using Label = System.Windows.Controls.Label;
using MessageBox = System.Windows.MessageBox;
using OpenFileDialog = Microsoft.Win32.OpenFileDialog;

namespace PhotoTimeFix.Window
{
    /// <summary>
    ///     Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : System.Windows.Window
    {
        private readonly MainWindowBinding _binding;
        private readonly SettingBinding _setting;

        private Dictionary<string, string> _exifInfos = new Dictionary<string, string>();

        public MainWindow()
        {
            InitializeComponent();
            _binding = (MainWindowBinding) Resources[nameof(MainWindowBinding)];
            ThemeHelper.InitTheme();

            InitUser();
            _setting = new SettingBinding();
            InitSetting();
        }

        private Dictionary<string, string> ExifInfos
        {
            get => _exifInfos;
            set
            {
                _exifInfos = value;
                _binding.DetailTags = value.Keys;
                DetailTextBox.Text = "";
            }
        }

        private void InitUser()
        {
            var identity = WindowsIdentity.GetCurrent();
            UserNameText.Content = identity.Name;
            var principal = new WindowsPrincipal(identity);
            var wbirFields = Enum.GetValues(typeof(WindowsBuiltInRole));
            var groups = "";
            foreach (WindowsBuiltInRole roleName in wbirFields)
                if (principal.IsInRole(roleName))
                    groups += roleName.ToString() + '\n';

            GroupText.Content = groups;

            var windowsPrincipal = new WindowsPrincipal(identity);

            if (new Helpers().IsRunningAsUwp())
            {
                RunAsAdminButton.Visibility = Visibility.Collapsed;
                RunAsTrustInstallerButton.Visibility = Visibility.Collapsed;
            }
            else if (windowsPrincipal.IsInRole(WindowsBuiltInRole.Administrator))
            {
                RunAsAdminButton.Visibility = Visibility.Collapsed;
            }
            else
            {
                RunAsTrustInstallerButton.Visibility = Visibility.Collapsed;
            }
        }

        private void InitSetting()
        {
            var i = 0;
            foreach (var info in typeof(SettingBinding).GetProperties())
            {
                var attribute = info.GetCustomAttribute<SettingItemAttribute>();
                if (attribute != null)
                {
                    var label = new Label();
                    label.Content = attribute.Name;
                    label.ToolTip = attribute.Summary;
                    label.Margin = new Thickness(5, 5, 0, 5);
                    var button = new ToggleButton();
                    button.IsChecked = (bool) info.GetValue(_setting);
                    button.Style = (Style) FindResource("MaterialDesignSwitchToggleButton");
                    button.Margin = new Thickness(0, 5, 5, 5);
                    var binding = new Binding(info.Name);
                    binding.Source = _setting;
                    button.SetBinding(ToggleButton.IsCheckedProperty, binding);
                    SettingContainer.RowDefinitions.Add(new RowDefinition());
                    SettingContainer.Children.Add(label);
                    SettingContainer.Children.Add(button);
                    Grid.SetColumn(label, 0);
                    Grid.SetRow(label, i);
                    Grid.SetColumn(button, 1);
                    Grid.SetRow(button, i);
                    i++;
                }
            }
        }

        private void FileButton_OnClick(object sender, RoutedEventArgs e)
        {
            var dialog = new OpenFileDialog();
            if (dialog.ShowDialog() ?? false) _binding.FilePath = dialog.FileName;
        }

        private void DictionaryButton_OnClick(object sender, RoutedEventArgs e)
        {
            var dialog = new FolderBrowserDialog();
            if (dialog.ShowDialog().Equals(System.Windows.Forms.DialogResult.OK))
                _binding.FilePath = dialog.SelectedPath;
        }

        private void Grid_OnLayoutUpdated(object sender, EventArgs e)
        {
            var height = DetailCard.ActualHeight - DetailListBox.ActualHeight - 30d;
            if (height > 0) DetailTextBox.Height = height;

            MinWidth = SelectStackPanel.ActualWidth + 180d;
            var minHeight = SelectStackPanel.ActualHeight + 120d;
            if (DetailCard.Visibility == Visibility.Visible) minHeight += DetailListBox.ActualHeight + 120d;
            MinHeight = minHeight;
        }

        private async void PathTextBox_OnTextChanged(object sender, TextChangedEventArgs e)
        {
            var value = PathTextBox.Text;
            if (File.Exists(value))
            {
                await ProcessBarWindow.StartTask(action =>
                {
                    Dispatcher.Invoke(async () =>
                    {
                        await UpdateCurrentTime(true, action);
                        if (File.Exists(value))
                        {
                            _binding.IsFile = Visibility.Visible;
                            _binding.PathExist = Visibility.Visible;
                            await UpdateExif(value);
                            DetailListBox.SelectAll();
                            if (_setting.ShowMedia) ShowImage(value);
                        }
                    });
                });
            }
            else
            {
                if (Directory.Exists(value))
                    _binding.PathExist = Visibility.Visible;
                else
                    _binding.PathExist = Visibility.Collapsed;
                _binding.IsFile = Visibility.Collapsed;
                ExifInfos = new Dictionary<string, string>();
                DetailTextBox.Text = "";
            }
        }

        private async Task UpdateCurrentTime(bool resetNow = true, Action action = null)
        {
            var path = PathTextBox.Text;
            if (resetNow)
            {
                _binding.NowDate = null;
                _binding.NowTime = null;
            }

            if (File.Exists(path))
            {
                var info = new FileInfo(path);

                CurrentCreateTime.Text = info.CreationTime.ToString(CultureInfo.CurrentCulture);
                CurrentModifyTime.Text = info.LastWriteTime.ToString(CultureInfo.CurrentCulture);

                try
                {
                    var dateTime = await GetDateTimeFromFileProcessor(info);
                    _binding.NowDate = dateTime;
                    _binding.NowTime = dateTime;
                }
                catch (Exception e)
                {
                    MessageBox.Show(e.Message);
                }
            }
            else
            {
                CurrentCreateTime.Text = "";
                CurrentModifyTime.Text = "";
            }

            action?.Invoke();
        }

        private async Task<DateTime?> GetDateTimeFromFileProcessor(FileInfo info)
        {
            return await Task.Run(() =>
            {
                var processor = _binding.FileProcessor;
                return processor.GetFileDateTime(info);
            });
        }

        private async Task UpdateExif(string path)
        {
            var dictionary = new Dictionary<string, string>();
            try
            {
                var directories = await Task.Run(() => ImageMetadataReader.ReadMetadata(path));
                try
                {
                    var subIfdDirectory = directories.OfType<ExifIfd0Directory>().FirstOrDefault();
                    if (subIfdDirectory?.TryGetDateTime(ExifDirectoryBase.TagDateTime, out var dateTime) == true)
                    {
                        _binding.NowDate = dateTime;
                        _binding.NowTime = dateTime;
                    }
                }
                catch (Exception e)
                {
                    MessageBox.Show(e.Message);
                }

                foreach (var directory in directories)
                {
                    var value = "";
                    foreach (var tag in directory.Tags) value += $"[{directory.Name}] {tag.Name} = {tag.Description}\n";
                    if (directory.HasError)
                        foreach (var error in directory.Errors)
                            value += $"ERROR: {error}\n";
                    var name = directory.Name;
                    var i = 2;
                    var exist = dictionary.ContainsKey(name);
                    while (dictionary.ContainsKey(name + "-" + i)) i++;
                    if (exist) name += "-" + i;
                    dictionary.Add(name, value);
                }
            }
            catch (Exception e)
            {
                dictionary.Add("Error", e.ToString());
            }

            ExifInfos = dictionary;
        }

        private static void ShowImage(string path)
        {
            try
            {
                var image = Image.FromFile(path);
                var imageWindow = new ImageWindow(path);
                imageWindow.Show();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }
        }

        private void DetailListBox_OnSelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            var value = "";
            foreach (var info in ExifInfos)
                if (DetailListBox.SelectedItems.Contains(info.Key))
                    value += info.Value;
            DetailTextBox.Text = value;
        }

        private void RunAsAdmin_OnClick(object sender, RoutedEventArgs args)
        {
            var startInfo = new ProcessStartInfo();
            startInfo.UseShellExecute = true;
            startInfo.WorkingDirectory = Environment.CurrentDirectory;
            startInfo.FileName = Application.ExecutablePath;
            startInfo.Verb = "runas";
            try
            {
                Process.Start(startInfo);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }

            Close();
            Application.Exit();
        }

        private void RunAsTrustInstaller_OnClick(object sender, RoutedEventArgs args)
        {
            try
            {
                var instance = new NSudoInstance();
                instance.CreateProcess(
                    NSUDO_USER_MODE_TYPE.TRUSTED_INSTALLER,
                    NSUDO_PRIVILEGES_MODE_TYPE.ENABLE_ALL_PRIVILEGES,
                    NSUDO_MANDATORY_LABEL_TYPE.SYSTEM,
                    NSUDO_PROCESS_PRIORITY_CLASS_TYPE.NORMAL,
                    NSUDO_SHOW_WINDOW_MODE_TYPE.DEFAULT,
                    0,
                    true,
                    Application.ExecutablePath,
                    Environment.CurrentDirectory
                );
                Close();
                Application.Exit();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }
        }

        private void Grid_DragEnter(object sender, DragEventArgs e)
        {
            var fileData = (Array) e.Data.GetData(DataFormats.FileDrop, true);
            if (fileData != null && fileData.Length == 1)
            {
                e.Effects = DragDropEffects.Link;
                e.Handled = true;
            }
            else
            {
                e.Effects = DragDropEffects.None;
            }
        }

        private void Grid_Drop(object sender, DragEventArgs e)
        {
            try
            {
                var fileData = (Array) e.Data.GetData(DataFormats.FileDrop, true);
                if (fileData != null)
                {
                    var fileName = fileData.GetValue(0).ToString();
                    if (!string.IsNullOrWhiteSpace(fileName)) PathTextBox.Text = fileName;
                    e.Handled = true;
                }
            }
            catch (Exception exception)
            {
                MessageBox.Show(exception.ToString());
            }
        }

        private void Hyperlink_Click(object sender, RoutedEventArgs e)
        {
            var link = sender as Hyperlink;
            Process.Start(new ProcessStartInfo
            {
                FileName = link?.NavigateUri.AbsoluteUri ?? throw new InvalidOperationException(),
                UseShellExecute = true
            });
        }

        private void OpenSourceLicense_Click(object sender, RoutedEventArgs e)
        {
            var noticeList = new List<Notice>();
            noticeList.Add(new Notice("PhotoTimeFix", "https://github.com/Cryolitia/PhotoTimeFix",
                "Copyroght 2018-2022 singleNeuron", new MITLicense()));
            noticeList.Add(new Notice("MetadataExtractor", "https://github.com/drewnoakes/metadata-extractor-dotnet",
                "Drew Noakes", new ApacheSoftwareLicense20()));
            noticeList.Add(new Notice("Material Design In XAML Toolkit",
                "https://github.com/MaterialDesignInXAML/MaterialDesignInXamlToolkit", "MaterialDesignInXAML",
                new MITLicense()));
            noticeList.Add(new Notice("AvalonEdit", "https://github.com/icsharpcode/AvalonEdit",
                "Copyright (c) AvalonEdit Contributors", new MITLicense()));
            noticeList.Add(new Notice("CS-Script", "https://github.com/oleg-shilo/cs-script",
                "Copyright (c) 2018 oleg-shilo", new MITLicense()));
            noticeList.Add(new Notice("MimeTypeMap", "https://github.com/samuelneff/MimeTypeMap",
                "Copyright (c) 2014 Samuel Neff", new MITLicense()));
            new LicensesDialog.LicensesDialog.Builder().SetNotices(noticeList).SetShowOwnLicense(true).Build().Show();
        }

        private async void EditCode_OnClick(object sender, RoutedEventArgs e)
        {
            await Dispatcher.BeginInvoke(new Action(() =>
            {
                var window = new CodeEditWindow();
                var binding = new Binding("FileProcessorCode");
                binding.Source = _binding;
                window.CodeEditor.SetBinding(TextEditor.DocumentProperty, binding);
                window.Binding = _binding;
                window.ShowDialog();
            }));
        }

        private async void Start_OnClick(object sender, RoutedEventArgs e)
        {
            var window = new ProcessWindow();
            await window.ShowDialogAsync();
            if (File.Exists(_binding.FilePath) && _binding.NowDate.HasValue && _binding.NowTime.HasValue)
            {
                var result = await Task.Run(() => ProcessFile(new FileInfo(_binding.FilePath),
                    new DateTime(_binding.NowDate.Value.Year, _binding.NowDate.Value.Month,
                        _binding.NowDate.Value.Day,
                        _binding.NowTime.Value.Hour, _binding.NowTime.Value.Minute, _binding.NowTime.Value.Second)));
#pragma warning disable CS4014 //调用不会等待
                Dispatcher.BeginInvoke(new Action(() => { window.ProcessResultList.Add(result); }));
#pragma warning restore CS4014
                await UpdateCurrentTime(false);
            }
            else if (Directory.Exists(_binding.FilePath))
            {
                await Task.Run(() => ProcessDirectory(new DirectoryInfo(_binding.FilePath), window.ProcessResultList));
            }

            window.Closable = true;
        }

        private void ProcessDirectory(DirectoryInfo info, ProcessResultList list)
        {
            try
            {
                foreach (var mInfo in info.EnumerateDirectories()) ProcessDirectory(mInfo, list);
                foreach (var mInfo in info.EnumerateFiles())
                {
                    var directories = ImageMetadataReader.ReadMetadata(mInfo.FullName);
                    DateTime? dateTime = null;
                    try
                    {
                        var subIfdDirectory = directories.OfType<ExifIfd0Directory>().FirstOrDefault();
                        if (subIfdDirectory?.TryGetDateTime(ExifDirectoryBase.TagDateTime, out var mDateTime) == true)
                            dateTime = mDateTime;
                        if (dateTime == null) dateTime = _binding.FileProcessor.GetFileDateTime(mInfo);
                        if (dateTime != null)
                        {
                            var result = ProcessFile(mInfo, dateTime.Value);
                            Dispatcher.BeginInvoke(new Action(() => { list.Add(result); }));
                        }
                        else
                        {
                            Dispatcher.BeginInvoke(new Action(() => { list.Add(mInfo.FullName, "Null", "Skip", ""); }));
                        }
                    }
                    catch (Exception e)
                    {
                        Dispatcher.BeginInvoke(new Action(() => { list.Add(mInfo.FullName, "", "Error", e.Message); }));
                    }
                }
            }
            catch (Exception e)
            {
                Dispatcher.BeginInvoke(new Action(() => { list.Add(info.FullName, "", "Error", e.Message); }));
            }
        }

        private ProcessResult ProcessFile(FileInfo info, DateTime dateTime)
        {
            try
            {
                var mime = MimeTypeMap.GetMimeType(info.Extension).ToLower();
                if (_setting.OnlyMedia && !mime.StartsWith("image/") && !mime.StartsWith("video/"))
                    return new ProcessResult(info.FullName, "", "Skip", "MINE unmatch: " + mime);
                if (_setting.SafetyMode && (dateTime > DateTime.Now || dateTime < new DateTime(1970, 1, 1)))
                    return new ProcessResult(info.FullName, "Skip", "",
                        "Date unbelievable: " + dateTime.ToString(CultureInfo.CurrentCulture));
                info.CreationTime = dateTime;
                info.LastWriteTime = dateTime;
                return new ProcessResult(info.FullName, dateTime.ToString(CultureInfo.CurrentCulture), "Success", "");
            }
            catch (Exception e)
            {
                return new ProcessResult(info.FullName, "", "Error", e.Message);
            }
        }
    }
}