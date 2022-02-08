using System;
using System.Windows.Forms;

namespace PhotoTimeFix.Util
{
    [AttributeUsage(AttributeTargets.Property)]
    public class SettingItemAttribute : Attribute
    {
        public SettingItemAttribute(string name, string summary)
        {
            Name = name;
            Summary = summary;
        }

        public SettingItemAttribute(string res)
        {
            try
            {
                Name = typeof(Resource.Resource).GetProperty("Setting_" + res).GetValue(null) as string;
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
            try
            {
                Summary = typeof(Resource.Resource).GetProperty("Setting_" + res+ "Summary").GetValue(null) as string;
            }
            catch (Exception e)
            {
                //ignore
            }
        }

        public string Name { get; set; }

        public string Summary { get; set; }
    }
}