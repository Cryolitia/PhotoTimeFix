using System.Globalization;
using System.IO;
using System.Windows.Controls;

namespace PhotoTimeFix.Util
{
    public class FileExistValidationRule : ValidationRule
    {
        public override ValidationResult Validate(object value, CultureInfo cultureInfo)
        {
            if (value == null || string.IsNullOrWhiteSpace((value ?? "").ToString()))
                return new ValidationResult(false, Resource.Resource.Rule_PathNonNull);

            var path = value.ToString();
            return File.Exists(path) || Directory.Exists(path)
                ? ValidationResult.ValidResult
                : new ValidationResult(false, Resource.Resource.Rule_PathNotExist);
        }
    }
}