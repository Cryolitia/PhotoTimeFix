using System.ComponentModel;
using System.Runtime.CompilerServices;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.ViewBinding
{
    public class SettingBinding : INotifyPropertyChanged
    {
        private bool _onlyMedia = true;

        private bool _safetyMode = true;

        private bool _showMedia = false;

        private bool _saveLog = false;

        [SettingItem("SafeMode")]
        public bool SafetyMode
        {
            get => _safetyMode;
            set
            {
                _safetyMode = value;
                OnPropertyChanged(nameof(SafetyMode));
            }
        }

        [SettingItem("MediaOnly")]
        public bool OnlyMedia
        {
            get => _onlyMedia;
            set
            {
                _onlyMedia = value;
                OnPropertyChanged(nameof(OnlyMedia));
            }
        }

        [SettingItem("OpenMedia")]
        public bool ShowMedia
        {
            get => _showMedia;
            set
            {
                _showMedia = value;
                OnPropertyChanged(nameof(ShowMedia));
            }
        }

        [SettingItem("SaveLog")]
        public bool SaveLog
        {
            get => _saveLog;
            set
            {
                _saveLog = value;
                OnPropertyChanged(nameof(SaveLog));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}