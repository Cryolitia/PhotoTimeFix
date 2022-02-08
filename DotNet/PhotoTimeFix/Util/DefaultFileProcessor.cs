using System;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
using PhotoTimeFix.Util;
public class DefaultFileProcessor : IFileProcessor {

        private const string regexString =
            "(19|20)[0-9]{2}[^0-9]*?(0[1-9]|1[0-2])[^0-9]*?([0-2][0-9]|[3][0-1])[^0-9]*?([01][0-9]|2[0-3])[^0-9]*?[0-5][0-9][^0-9]*?[0-5][0-9]";

        private static Regex regex = new Regex(regexString, RegexOptions.Compiled);

        private static Regex spaceRemover = new Regex(@"\D+", RegexOptions.Compiled);
        
        public DateTime? GetFileDateTime(FileInfo fileInfo)
        {
            var mathch = regex.Match(fileInfo.Name);
            if (mathch.Success)
            {
                string result = mathch.Value;
                result = spaceRemover.Replace(result, "");
                return DateTime.ParseExact(result, "yyyyMMddHHmmss", CultureInfo.InvariantCulture);
            }
            else
            {
                return null;
            }
        }
}