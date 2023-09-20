using System;
using System.Text;
using System.Windows;
using System.Windows.Media;
using Windows.Foundation.Metadata;
using Windows.UI.ViewManagement;
using MaterialDesignThemes.Wpf;
using Microsoft.Win32;

namespace PhotoTimeFix.Util
{
    public static class ThemeHelper
    {
        private static bool eventInitial;

        //https://kira-96.github.io/posts/%E5%A6%82%E4%BD%95%E8%8E%B7%E5%8F%96Windows10%E4%B8%BB%E9%A2%98%E9%A2%9C%E8%89%B2/
        public static void InitTheme()
        {
            var paletteHelper = new PaletteHelper();
            var isLight = true;
            try
            {
                var registryValue =
                    Registry.GetValue(@"HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Themes\Personalize",
                        "AppsUseLightTheme", null);
                isLight = Convert.ToBoolean(registryValue);
            }
            catch (Exception exception)
            {
                MessageBox.Show(exception.ToString());
            }

            IBaseTheme baseTheme;
            if (isLight)
                baseTheme = new MaterialDesignLightTheme();
            else
                baseTheme = new MaterialDesignDarkTheme();
            var color = isLight ? Color.FromRgb(66, 133, 244) : Color.FromRgb(130, 168, 231);
            var theme = Theme.Create(baseTheme, color, color);

            RequireSystemTheme(theme, isLight);

            if (!eventInitial)
            {
                SystemEvents.UserPreferenceChanged += (sender, args) => { InitTheme(); };
                eventInitial = true;
            }

            paletteHelper.SetTheme(theme);
        }

        public static void RequireSystemTheme(Theme theme, bool isLight)
        {
            try
            {
#pragma warning disable CA1416 // 验证平台兼容性
                if (ApiInformation.IsTypePresent("Windows.UI.ViewManagement.UISettings"))
                {
                    var uiSettings = new UISettings();
                    var accentColor = uiSettings.GetColorValue(UIColorType.Accent);
                    theme.SetPrimaryColor(Color.FromRgb(accentColor.R, accentColor.G, accentColor.B));
                    theme.SetSecondaryColor(Color.FromRgb(accentColor.R, accentColor.G, accentColor.B));
                    if (!isLight)
                    {
                        theme.CardBackground = Color.FromRgb(18, 18, 18);
                        theme.Paper = Colors.Black;
                    }
                }
#pragma warning restore CA1416 // 验证平台兼容性
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }
        }

        public static void ShowSystemColor()
        {
            var uiSettings = new UISettings();
            var builder = new StringBuilder();
            builder.Append("System Colors: " + '\n');

            foreach (UIColorType type in Enum.GetValues(typeof(UIColorType)))
                if (type != UIColorType.Complement)
                    builder.Append(type + ": " + uiSettings.GetColorValue(type) + '\n');

            builder.Append('\n');

            foreach (UIElementType type in Enum.GetValues(typeof(UIElementType)))
                builder.Append(type + ": " + uiSettings.UIElementColor(type) + '\n');

            System.Windows.Forms.MessageBox.Show(builder.ToString());
        }
    }
}