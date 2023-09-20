using System.ComponentModel;
using System.Runtime.CompilerServices;
using PhotoTimeFix.Util;

namespace PhotoTimeFix.ViewBinding
{
    public class SettingBinding : INotifyPropertyChanged
    {
        private bool _onlyMedia = true;

        private bool _preferExif = true;

        private bool _safetyMode = true;

        private bool _showMedia;

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

        [SettingItem("PreferExif")]
        public bool PreferExif
        {
            get => _preferExif;
            set
            {
                _preferExif = value;
                OnPropertyChanged(nameof(PreferExif));
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

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}