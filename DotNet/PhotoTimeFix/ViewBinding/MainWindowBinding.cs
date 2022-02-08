using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Windows;
using CSScriptLib;
using ICSharpCode.AvalonEdit.Document;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.ViewBinding
{
    public class MainWindowBinding : INotifyPropertyChanged
    {
        private IEnumerable<string> _detailTags;

        private string _filePath;

        private IFileProcessor _fileProcessor;

        private TextDocument _fileProcessorCode;

        private Visibility _isFile = Visibility.Collapsed;

        private DateTime? _nowDate;

        private DateTime? _nowTime;

        private Visibility _pathExist = Visibility.Collapsed;

        public bool IsDebug => App.IsDebug;

        public string FilePath
        {
            get => _filePath;
            set
            {
                _filePath = value;
                OnPropertyChanged(nameof(FilePath));
            }
        }

        public Visibility IsFile
        {
            get => _isFile;
            set
            {
                _isFile = value;
                OnPropertyChanged(nameof(IsFile));
                OnPropertyChanged(nameof(StartEnabled));
            }
        }

        public Visibility PathExist
        {
            get => _pathExist;
            set
            {
                _pathExist = value;
                OnPropertyChanged(nameof(PathExist));
                OnPropertyChanged(nameof(StartEnabled));
            }
        }

        public IEnumerable<string> DetailTags
        {
            get => _detailTags;
            set
            {
                _detailTags = value;
                OnPropertyChanged(nameof(DetailTags));
            }
        }

        public DateTime? NowDate
        {
            get => _nowDate;
            set
            {
                _nowDate = value;
                OnPropertyChanged(nameof(NowDate));
                OnPropertyChanged(nameof(StartEnabled));
            }
        }

        public DateTime? NowTime
        {
            get => _nowTime;
            set
            {
                _nowTime = value;
                OnPropertyChanged(nameof(NowTime));
                OnPropertyChanged(nameof(StartEnabled));
            }
        }

        public bool StartEnabled => NowDate != null && NowTime != null && IsFile == Visibility.Visible ||
                                    IsFile == Visibility.Collapsed && PathExist == Visibility.Visible;

        public TextDocument FileProcessorCode
        {
            get
            {
                if (_fileProcessorCode == null)
                {
                    var assembly = Assembly.GetExecutingAssembly();
                    var stream =
                        assembly.GetManifestResourceStream(assembly.GetName().Name + ".Util.DefaultFileProcessor.cs");
                    var reader = new StreamReader(stream);
                    _fileProcessorCode = new TextDocument(reader.ReadToEnd());
                    _fileProcessorCode.SetOwnerThread(Application.Current.Dispatcher.Thread);
                }

                return _fileProcessorCode;
            }
            set
            {
                _fileProcessorCode = value;
                OnPropertyChanged(nameof(FileProcessorCode));
            }
        }

        public IFileProcessor FileProcessor
        {
            get
            {
                if (_fileProcessor == null)
                {
                    var code = Application.Current.Dispatcher.Invoke(() => FileProcessorCode.Text);
                    _fileProcessor = (IFileProcessor) CSScript.Evaluator.LoadCode(code);
                }

                return _fileProcessor;
            }
            set => _fileProcessor = value;
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}